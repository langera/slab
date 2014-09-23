package org.langera.slab;

import java.util.Iterator;


// slab becomes a class which forces:

// 1. chunks
// 2. free list
// 3. compaction via strategy

// compaction logic in strategy uses storage

// storage becomes an abstract entity (OffHeap, ByteArray, ObjectArray? etc.) - flyweight linked to storage


// virtual memory added as a strategy as well


// Specific Flyweights should be generated given "bean" interface

public final class Slab<T> implements Iterable<T> {

    private final SlabStorage storage;
    private final AddressStrategy addressStrategy;
    private final CompactionStrategy compactionStrategy;
    private final SlabFlyweightFactory<T> factory;
    private final int objectSize;

    private long size = 0;
    private long freeListIndex = -1;

    public Slab(final SlabStorage storage,
                final AddressStrategy addressStrategy,
                final CompactionStrategy compactionStrategy,
                final SlabFlyweightFactory<T> factory) {
        this.storage = storage;
        this.addressStrategy = addressStrategy;
        this.compactionStrategy = compactionStrategy;
        this.factory = factory;
        this.objectSize = factory.getInstance().getStoredObjectSize(storage);
    }

    public long add(final T instance) {
        if (instance == null) {
            throw new IllegalArgumentException("Cannot add null");
        }
        size++;
        long address = (freeListIndex < 0) ? addToLastIndex(instance) : addToFreeEntry(instance);
        return addressStrategy.getKey(address);
    }

    public T get(final long address) {
        final SlabFlyweight<T> flyweight = factory.getInstance();
        flyweight.map(storage, addressStrategy.getAddress(address));
        return flyweight.isNull(storage, address) ? null : flyweight.asBean();
    }

    public void remove(final long address) {
        final SlabFlyweight<T> flyweight = factory.getInstance();
        long toUse = addressStrategy.getAddress(address);
        if (flyweight.isNull(storage, toUse)) {
            throw new ArrayIndexOutOfBoundsException("Address does not exist [" + address + "]");
        } else {
            size--;
            if (toUse == storage.getFirstAvailableAddress() - objectSize) {
                storage.remove(toUse, objectSize);
            } else {
                flyweight.setAsFreeAddress(storage, toUse, freeListIndex);
                freeListIndex = toUse;
            }
        }
    }

    public void compact() {
        // TODO
    }

    @Override
    public Iterator<T> iterator() {
        return new SlabIterator();
    }

    public long size() {
        return size;
    }

    public long availableCapacity() {
        return (storage.size() / objectSize) - size;
    }

    private long addToFreeEntry(final T instance) {
        SlabFlyweight<T> flyweight = factory.getInstance();
        long newAddress = freeListIndex;
        freeListIndex = flyweight.getNextFreeAddress(storage, freeListIndex);
        flyweight.dumpToStorage(instance, storage, newAddress);
        return newAddress;
    }

    private long addToLastIndex(final T instance) {
        final long newAddress = storage.getFirstAvailableAddress();
        factory.getInstance().dumpToStorage(instance, storage, newAddress);
        return newAddress;
    }

    private class SlabIterator implements Iterator<T> {


        private int ptr = -objectSize;
        private int visited = 0;

        @Override
        public boolean hasNext() {
            return visited < size;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T next() {
            visited++;
            ptr += objectSize;
            SlabFlyweight<T> flyweight = factory.getInstance();
            flyweight.map(storage, ptr);
            while (ptr < storage.size() && flyweight.isNull(storage, ptr)) {
                ptr += objectSize;
                flyweight.map(storage, ptr);
            }
            return flyweight.asBean();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove from iterator unsupported");
        }
    }
}
