package org.langera.slab;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;


// slab becomes a class which forces:

// 1. chunks
// 2. free list
// 3. compaction via strategy

// compaction logic in strategy uses storage

// storage becomes an abstract entity (OffHeap, ByteArray, ObjectArray? etc.) - flyweight linked to storage


// virtual memory added as a strategy as well



// Specific Flyweights should be generated given "bean" interface

public final class Slab<T>  implements Iterable<T> {

    public Slab(final SlabStorage storage,
                final AddressingStrategy addressingStrategy,
                final CompactionStrategy compactionStrategy,
                final SlabFlyweightFactory<T> factory) {
    }

    void compact() {
    }

    long add(final T instance) {
    return 0;
    }

    T get(final long address) {
    return null;
    }

    void remove(final long address) {
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public void forEach(final Consumer<? super T> action) {

    }

    @Override
    public Spliterator<T> spliterator() {
        return null;
    }

    long size() {
        return 0;
    }

    long capacity() {
        return 0;
    }
}
