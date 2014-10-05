# slab
======

Slab is a storage mechanism for a **"Simple Flat POJO"**
where "simple flat POJO" = Java object whose state contains ONLY primitives or fixed size arrays of primitives.

Ideas borrowed from [Martin Thompson] (http://mechanical-sympathy.blogspot.co.uk/2012/10/compact-off-heap-structurestuples-in.html) and [Richard Warburton] (https://github.com/RichardWarburton/slab)

This slab implementation offers several features:

1. Basic Collection operations (`add`, `get`, `remove`, `iterator`, `size`) that work on long keys (so Slab maximum size is `Long.MAX_VALUE`). 
2. Abstraction of the underlying storage and its access. (No need to commit to either off heap, use of Unsafe etc.)
3. Management of free gaps in the slab (created by removals) and efficient filling of those to avoid fragmentation and wasting of memory. 
4. Using a Flyweight pattern to access data inside the slab.
5. Compaction of the slab to reduce consumed memory.

## Why limit the objects being stored to "Simple flat POJO"?

The limitation of the object being stored to a "simple flat POJO" allows us to effectlvely manage the slab memory without the need to handle complex defragmentation and compaction scenarios.
Note that if we are thinking of offloading objects to a different storage mechanism, this limitation is already imposed by the situation and
object references must be handled as a special case regardless of the tool being used to offload.
This gives us a slab that can be used to store efficiently POJOs whose number of instances is very dynamic in real world applications. 
i.e additions and removals happen constantly and at arbitrary points in the slab.
One such real world example can be the block information in the Hadoop HDFS Namenode.

## Why not use a java.nio.ByteBuffer?

java.nio.ByteBuffer is optimised towards a use of a buffer and not a Collection. 
It does not support efficient removals of arbitrary sections in the buffer, does not consider issues of fragmentation and its compact() operation
assume a behaviour where everything already read can be discarded. 
It also forces its own specific serialization mechanism for the primitive values.

The Slab project does have a `ByteBufferStorage` which uses ByteBuffers as a back-end storage for the slab. 
Our performance tests (see `StoragePerfTest`) and others (see [here] (http://mechanical-sympathy.blogspot.co.uk/2012/07/native-cc-like-performance-for-java.html)) showed it is being out-performed by sun.misc.Unsafe 

## Why not make Slab implement java.util.Collection? java.util.List?

The basic use of a Slab should be when you are willing to trade off object lookup performance with a reduction in memory consumption.
This means you probably are using *a lot* of memory and need to support more than Integer.MAX_VALUE of instances.  

Therefore Slab API uses `long` as its key to the objects which makes it impossible for us to link it to the regular java.util.Collection family.
It does implement java.util.Iterable

## Thread-Safety?

Slab is not thread-safe. This allows maximum performance if you already access it from a single thread and don't need to pay any thread-safety performance penalty.
Access to the Slab state is done by the user of the stored objects (for example when calling `add` or `get`) or via a separate process which calls `compact`.
Slab API offers even hooks for the compaction operation which allows efficient concurrency control by only limiting the access to the slab at the point of a 
single move of an object inside the slab storage.


 

 
  