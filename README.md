slab
====
Slab is a storage mechanism for "simple flat POJO"
where "simple flat POJO" = Java objects whose state contains ONLY primitives or fixed size arrays of primitives.

Ideas borrowed from [Martin Thompson] (http://mechanical-sympathy.blogspot.co.uk/2012/10/compact-off-heap-structurestuples-in.html) and [Richard Warburton] (https://github.com/RichardWarburton/slab)

This slab implementation offers several features:

1. Abstraction of the underlying storage and its access. (No need to commit to either off heap, use of Unsafe etc.)
2. Management of free areas in the slab and efficient filling of those to avoid fragmentation.
3. Compaction of the slab to reduce consumed memory. 
4. Using a Flyweight pattern to access data inside the slab.

The limitation of the object being stored to a "simple flat POJO" allows us to effectlvely manage the slab memory without the need to handle complex defragmentation and compaction scenarios.
Note that if we are thinking of offloading objects to a different storage mechanism, this limitation is already imposed by the situation and
object references must be handled as a special case regardless.
The cost of using indexes and ids instead of references is being offset by not

This gives us a slab that can be used to store efficiently POJOs whose number of instances is very dynamic in real world applications.

One such real world example can be the block information in the Hadoop HDFS Namenode.