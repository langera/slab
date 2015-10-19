slab
======

Slab is a simple storage mechanism for Java objects.

Ideas borrowed from [Martin Thompson] (http://mechanical-sympathy.blogspot.co.uk/2012/10/compact-off-heap-structurestuples-in.html) blog and [Richard Warburton] (https://github.com/RichardWarburton/slab) slab project.

Unlike references above, this slab's emphasis is on providing a simple Collection-like behaviour to a huge number of objects
(> Integer.MAX_VALUE) with reasonable performance, flexible storage abstraction and memory efficient management.

The only **limitation** it sets on the stored Java object is that its seralized content size **must** be fixed and known in advance.

The main features in slab are:

1. Basic Collection operations (`add`, `get`, `remove`, `iterator`, `size`) that work on long keys (Slab maximum size is `Long.MAX_VALUE`). 
2. Abstraction of the underlying storage and its access. (No need to commit to either memory type, serialization protocol etc.)
3. Management of free gaps in the slab (that are created by removals) and efficient filling of those gaps to avoid fragmentation. 
4. Using the Flyweight pattern to access data inside the slab.
5. Compaction of the slab to reduce consumed memory.
6. Ability to control the addresses returned and abstraction of a virtual addressing scheme. 

### Documentation

[Introduction] (https://github.com/langera/slab/wiki/Introduction)

[FAQ] (https://github.com/langera/slab/wiki/FAQ)

[Getting Started] (https://github.com/langera/slab/wiki/Example-Code)

[Performance Results] (https://github.com/langera/slab/wiki/Performance-Results)

[Future Work] (https://github.com/langera/slab/wiki/Future-Work)

    
    
    



 

 
  
