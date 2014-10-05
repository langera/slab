slab
======

Slab is a storage mechanism for a **"Simple Flat POJO"** == A Java object whose state contains ONLY 
primitives or fixed size arrays of primitives.

Ideas borrowed from [Martin Thompson] (http://mechanical-sympathy.blogspot.co.uk/2012/10/compact-off-heap-structurestuples-in.html) blog and [Richard Warburton] (https://github.com/RichardWarburton/slab) slab project.

This project emphasis is on providing a simple Collection-like behaviour to a huge number of objects
(> Integer.MAX_VALUE) with reasonable performance, flexible implementaiton and management of 
the memory being used.


This slab implementation offers several features:

1. Basic Collection operations (`add`, `get`, `remove`, `iterator`, `size`) that work on long keys (so Slab maximum size is `Long.MAX_VALUE`). 
2. Abstraction of the underlying storage and its access. (No need to commit to either off heap, use of Unsafe etc.)
3. Management of free gaps in the slab (created by removals) and efficient filling of those to avoid fragmentation and wasting of memory. 
4. Using the Flyweight pattern to access data inside the slab.
5. Compaction of the slab to reduce consumed memory.
6. Ability to control the addresses returned and implement any type of Virtual addressing scheme. 

### Why limit the objects being stored to "Simple flat POJO"?

The limitation of the object being stored to a "simple flat POJO" allows us to effectlvely manage the slab memory without the need to handle complex defragmentation and compaction scenarios.
Note that if we are thinking of offloading objects to a different storage mechanism, this limitation is already imposed by the situation and
object references must be handled as a special case regardless of the tool being used to offload.
This gives us a slab that can be used to store efficiently POJOs whose number of instances is very dynamic in real world applications. 
i.e additions and removals happen constantly and at arbitrary points in the slab.


One such real world example can be the block information in the Hadoop HDFS Namenode.

### Why not use a java.nio.ByteBuffer?

java.nio.ByteBuffer is optimised towards a use of a buffer and not a Collection. 
It does not support efficient removals of arbitrary sections in the buffer, does not consider issues of fragmentation and its compact() operation
assume a behaviour where everything already read can be discarded. 
It also forces its own specific serialization mechanism for the primitive values.

The Slab project does have a `ByteBufferStorage` which uses ByteBuffers as a back-end storage for the slab. 
Our performance tests (see `StoragePerfTest`) and others (see [here] (http://mechanical-sympathy.blogspot.co.uk/2012/07/native-cc-like-performance-for-java.html)) showed it is being out-performed by sun.misc.Unsafe 

### Why not make Slab implement java.util.Collection? java.util.List?

The basic use of a Slab should be when you are willing to trade off object lookup performance with a reduction in memory consumption.
This means you probably are using **a lot** of memory and need to support more than `Integer.MAX_VALUE` of instances.  

Therefore Slab API uses `long` as its key to the objects which makes it impossible for us to link it to the regular java.util.Collection family.
It does implement java.util.Iterable

### Thread-Safety?

Slab is not thread-safe. This allows maximum performance if you already access it from a single thread and don't need to pay any thread-safety performance penalty.


In a multi-threaded env. access to the Slab state is done by the users of the stored objects (for example when calling `add` or `get`) and possibly via a separate caller which calls `compact`. 
We assume the application code can protect the user calls and the Slab API offers event hooks for the compaction operation which allows efficient concurrency control by only limiting the access to the slab at the point of a 
single move of an object inside the slab storage.


### Future Work

1. Publish the performance graphs (coming soon)
2. Automatically generate the Flyweight class given an interface (or a protobuf-like file).
3. Specify threshold for the compaction operation.
4. Explore performance improvements (?) by copying of entire object memory where possible.
5. 8-byte alignment? Might bypass a JVM bug on solaris machines: JDK-8021574. Explore.



### Code Example?
      
                   
*Your data type*
```
public interface Bean {
        
    int getMyUnsignedInt();

    void setMyUnsignedInt(final int myUnsignedInt);

    long[] getMyLongArray();

    void setMyLongArray(final long[] myLongArray);            
}
```
*Your POJO (so far - nothing new here except that your object implements an interface so the data type can also have a flyweight impl.)*
```
public class BeanPojo implements Bean {

    private int myUnsignedInt;
    private long[] myLongArray;
    
    @Override
    public int getMyUnsignedInt() { return myUnsignedInt; }

    @Override
    public void setMyUnsignedInt(final int myUnsignedInt) { 
        if (myUnsignedInt < 0) {
            throw new IllegalArgumentException();
        } 
        this.myUnsignedInt = myUnsignedInt; 
    }

    @Override
    public long[] getMyLongArray() { return myLongArray; }

    @Override
    public void setMyLongArray(final long[] myLongArray) { this.myLongArray = myLongArray; }                     
}
```
*code snippet that creates a Slab for Bean instances*

   *Define the storage to use by the slab. In this example we ask for a Storage using DIRECT memory (i.e off-heap) which can handle Long.MAX_VALUE for every chunk of slab (i.e Initial size).*
```
    SlabStorageFactory storageFactory = 
        Storages.storageFactoryFor().maxCapacity(Long.MAX_VALUE).type(DIRECT).newInstance();
```
   *Define how and when to create a flyweight. In this example we will have one flyweight instance per Thread stored in ThreadLocal. For BeanFlyweightFactory - see below.*
```    
    FlyweightFactory flyweightFactory = 
        new ThreadLocalSlabFlyweightFactory<>(new BeanFlyweightFactory()));            
```
   *Define the Address Strategy In this example we use the trivial DirectAddressStrategy which does not translate the addresses. This does mean that we probably don't store the address as a reference to a specific object state. If we are, we'll have to keep changing it when it is moved to a different address (could happen due to compaction).*
   
   *The strategy contains the hooks for translating between virtual and real slab addresses and the hooks when a real address moves.*
```    
    AddressStrategy addressStrategy = new DirectAddressStrategy()
```    

   *create a slab.*
```    
    Slab<Bean> slab = 
        new Slab<>(storageFactory, INITIAL_SIZE_IN_BYTES, addressStrategy, flyweightFactory);
```
   *add, remove and access the bean is trivial (which is what you do most of the time).*
```    
    long address = slab.add(myBean)
    
    Bean thisIsTheFlyweightInstance = slab.get(address);
    
    slab.remove(address);
    
    for (Bean alsoTheFlyweight : slab) {
        System.out.println(alsoTheFlyweight.getMyUnsignedInt());
    }
```
    
*Bean Flyweight - The real stuff.... To be used by the slab to write the data, returned to access the data and manage free entries*
```
public class BeanFlyweight  extends AbstractSlabFlyweight<Bean> implements Bean {

    private final int myLongArrayFixedSize;   
    private int myUnsignedIntOffset;
    private int myLongArrayOffset;

    public BeanFlyweight(final int myLongArrayFixedSize) {
        this.myLongArrayFixedSize = myLongArrayFixedSize;
    }

    /**
     * map this flyweight instance to a specific storage and address.
    **/
    @Override
    public void map(final SlabStorage storage, final long address) {
        super.map(storage, address);
        this.myUnsignedIntOffset = 0;
        this.myLongArrayOffset= this.myUnsignedIntOffset + storage.getIntOffset();
         // we use the longArray memory when the slab is empty to manage the free address
        setFreeAddressOffset(this.myLongArrayOffset);
    }
    
    /**
     * return true iff the position represented by storage and address is empty.
     * 
     * This example shows why managing the state of an empty slab position is done here. 
     * It allows us a much more optimised use of the memory by piggy backing on the same 
     * memory used by a valid "Bean".
     * Here, we count on the fact that our "myUnsignedInt" property is unsigned and so a 
     * negative value can work as a null flag. 
    **/
    @Override
    public boolean isNull(final SlabStorage storage, final long address) {
        // piggy back on unsigned int to reduce memory for null flag
        return storage.getInt(address + myUnsignedIntOffset) < 0;  
    }              
    
    /**
     * sets the state represented by the storage and address to be null 
     * (i.e empty position in the slab)
    **/        
    @Override
    public void setAsFreeAddress(final SlabStorage storage, 
                                 final long address, 
                                 final long nextFreeAddress) {
        super.setAsFreeAddress(storage, address, nextFreeAddress);
        // piggy back on unsigned int to reduce memory for null flag
        storage.setInt(-1, address + myUnsignedIntOffset); 
    }                 

    /**
     * sets the state represented by the storage and address to that of 'bean'.
    **/    
    @Override
    public void dumpToStorage(final Bean bean, final SlabStorage storage, final long address) {
        long offset = storage.setInt(bean.getMyUnsignedInt(), address);
        storage.setLongArray(bean.getMyLongArray(), offset);
    }

    /**
     * The slab has to know the memory size needed for the state of a 'Bean'. 
     * This method calculates and returns it (per storage). 
    **/    
    @Override
    public int getStoredObjectSize(final SlabStorage storage) {
        return storage.getIntOffset() + storage.getLongArrayOffset(myLongArrayFixedSize);
    }

    /**
     * convenience method for the code to view the flyweight as a Bean. 
    **/
    @Override
    public Bean asBean() {
        return this;
    }

    //////////////////////////////////////////////////////////
    // Bean implementation uses backend storage for its state.
    //////////////////////////////////////////////////////////
    
    @Override
    public int getMyUnsignedInt() {
        return getStorage().getInt(getMappedAddress() + myUnsignedIntOffset);
    }

    @Override
    public void setMyUnsignedInt(final int myUnsignedInt) {
        getStorage().setInt(myUnsignedInt, getMappedAddress() + myUnsignedIntOffset);
    }

    @Override
    public long[] getMyLongArray() {
        long[] container = new long[myLongArrayFixedSize];
        return getStorage().getLongArray(container, getMappedAddress() + myLongArrayOffset);
    }

    @Override
    public void setMyLongArray(final long[] myLongArray) {
        getStorage().setLongArray(myLongArray, getMappedAddress() + myLongArrayOffset);
    }     
}
```
*Factory to create specific BeanFlyweight instances for Beans with a long array of size = 3 see also factories like 'ThreadLocalSlabFlyweightFactory' or 'SingletonSlabFlyweightFactory' to control the number of flyweight instances created.*
```
public class BeanFlyweightFactory implements SlabFlyweightFactory<Bean> {

    @Override
    public SlabFlyweight<Bean> getInstance() { return new BeanFlyweight(3); }
}    
```



    
    
    



 

 
  
