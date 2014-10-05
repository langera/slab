package com.yahoo.slab;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public final class Slab<T> implements Iterable<T> {

    public static final int INITIAL_CHUNKS_ARRAY_SIZE = 10;
    private final AddressStrategy addressStrategy;
    private final SlabFlyweightFactory<T> factory;
    private final int objectSize;
    private final long chunkSize;
    private final SlabStorageFactory storageFactory;

    private List<SlabStorageChunk> storageChunks;
    private long size = 0;

    public Slab(final SlabStorageFactory storageFactory,
                final long chunkSize,
                final AddressStrategy addressStrategy,
                final SlabFlyweightFactory<T> factory) {
        if (!storageFactory.supportsCapacity(chunkSize)) {
            throw new IllegalArgumentException("SlabStorage type does not support capacity [" + chunkSize + "]");
        }
        this.storageFactory = storageFactory;
        this.chunkSize = chunkSize;
        this.addressStrategy = addressStrategy;
        this.factory = factory;
        this.storageChunks = new ArrayList<>(INITIAL_CHUNKS_ARRAY_SIZE);
        this.storageChunks.add(new SlabStorageChunk(storageFactory, chunkSize, 0));
        this.objectSize = factory.getInstance().getStoredObjectSize(storageChunks.get(0).getStorage());
    }

    public long add(final T instance) {
        if (instance == null) {
            throw new IllegalArgumentException("Cannot add null");
        }
        size++;
        long address = addToStorage(instance);
        return addressStrategy.getKey(address);
    }

    public T get(final long address) {
        final SlabFlyweight<T> flyweight = factory.getInstance();
        long realAddress = addressStrategy.getAddress(address);
        SlabStorageChunk chunk = storageFor(realAddress);
        realAddress = chunk.noOffsetAddress(realAddress);
        final SlabStorage storage = chunk.getStorage();
        flyweight.map(storage, realAddress);
        return chunk.getFirstAvailableAddress() <= realAddress || flyweight.isNull() ? null : flyweight.asBean();
    }

    public void remove(final long address) {
        final SlabFlyweight<T> flyweight = factory.getInstance();
        long realAddress = addressStrategy.removeAddress(address);
        final SlabStorageChunk chunk = storageFor(realAddress);
        realAddress = chunk.noOffsetAddress(realAddress);
        if (chunk.getFirstAvailableAddress() <= realAddress || flyweight.isNull(chunk.getStorage(), realAddress)) {
            throw new ArrayIndexOutOfBoundsException("Address does not exist [" + address + "]");
        } else {
            chunk.decrementSize();
            removeFromStorage(chunk, flyweight, realAddress);
            size--;
        }
    }

    public void compact(SlabCompactionEventHandler eventHandler) {
        if (storageChunks.size() > 1) {
            SlabStorageChunk lastChunk = storageChunks.get(storageChunks.size() - 1);
            Iterator<SlabFlyweight<T>> iterator = new StorageChunkIterator(lastChunk, Direction.BACK);
            while (iterator.hasNext() && canCompactToPreviousChunks(lastChunk)) {
                compact(eventHandler, lastChunk, iterator.next());
            }
            boolean lastChunkRemoved = false;
            try {
                eventHandler.beforeCompactionOfStorage();
                if (lastChunk.size() == 0) {
                    lastChunk.destroy();
                    storageChunks.remove(lastChunk);
                    lastChunkRemoved = true;
                }
            } finally {
                eventHandler.afterCompactionOfStorage();
            }
            if (lastChunkRemoved) {
                compact(eventHandler);
            }
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new SlabIterator(Direction.FORWARD);
    }

    public void destroy() {
        for (SlabStorageChunk chunk : storageChunks) {
            chunk.destroy();
        }
        storageChunks.clear();
    }

    public long size() {
        return size;
    }

    public long availableCapacity() {
        return (storageChunks.size() * chunkSize / objectSize) - size;
    }

    private boolean canCompactToPreviousChunks(final SlabStorageChunk lastChunk) {
        return storageChunks.size() > 1 && size - lastChunk.size() < ((storageChunks.size() - 1) * chunkSize / objectSize);
    }

    private void compact(final SlabCompactionEventHandler eventHandler,
                         final SlabStorageChunk chunk,
                         final SlabFlyweight<T> flyweight) {
        final long existingKey = chunk.offsetAddress(flyweight.getMappedAddress());
        long newKey = -1;
        try {
            eventHandler.beforeCompactionMove(existingKey);
            if (!flyweight.isNull()) {
                long newAddress = addToStorage(flyweight.asBean());
                newKey = addressStrategy.map(existingKey, newAddress);
                chunk.decrementSize();
                removeFromStorage(chunk, flyweight, flyweight.getMappedAddress());
            }
        } finally {
            eventHandler.afterCompactionMove(existingKey, newKey);
        }
    }

    private SlabStorageChunk storageFor(final long address) {
        int index = (int) (address / chunkSize);
        if (index >= storageChunks.size() || storageChunks.get(index) == null) {
            throw new ArrayIndexOutOfBoundsException("Address does not exist [" + address + "]");
        }
        return storageChunks.get(index);
    }

    private SlabStorageChunk availableStorage() {
        for (SlabStorageChunk chunk : storageChunks) {
            if (chunk.isAvailableCapacity()) {
                return chunk;
            }
        }
        SlabStorageChunk newChunk = new SlabStorageChunk(storageFactory, chunkSize, chunkSize * storageChunks.size());
        storageChunks.add(newChunk);
        return newChunk;
    }

    private long addToFreeEntry(final T instance, final SlabStorageChunk chunk) {
        SlabFlyweight<T> flyweight = factory.getInstance();
        final SlabStorage storage = chunk.getStorage();
        long newAddress = chunk.getFreeListIndex();
        chunk.setFreeListIndex(flyweight.getNextFreeAddress(storage, newAddress));
        flyweight.dumpToStorage(instance, storage, newAddress);
        chunk.addedUpTo(newAddress + objectSize);
        return chunk.offsetAddress(newAddress);
    }

    private long addToLastIndex(final T instance, final SlabStorageChunk chunk) {
        final long newAddress = chunk.getFirstAvailableAddress();
        factory.getInstance().dumpToStorage(instance, chunk.getStorage(), newAddress);
        chunk.addedUpTo(newAddress + objectSize);
        return chunk.offsetAddress(newAddress);
    }

    private long addToStorage(final T instance) {
        SlabStorageChunk chunk = availableStorage();
        chunk.incrementSize();
        return (chunk.getFreeListIndex() < 0) ? addToLastIndex(instance, chunk) : addToFreeEntry(instance, chunk);
    }

    private void removeFromStorage(final SlabStorageChunk chunk, final SlabFlyweight<T> flyweight, final long address) {
        if (address == chunk.getFirstAvailableAddress() - objectSize) {
            chunk.removedDownTo(address);
        } else {
            flyweight.setAsFreeAddress(chunk.getStorage(), address, chunk.getFreeListIndex());
            chunk.setFreeListIndex(address);
        }
    }

    private enum Direction {
        FORWARD {
            @Override
            long initialPtr(SlabStorageChunk chunk, long objectSize) {
                return -objectSize;
            }

            @Override
            long advancePtr(final long objectSize) {
                return objectSize;
            }

            @Override
            boolean done(final SlabStorageChunk chunk, final long ptr) {
                return ptr >= chunk.getFirstAvailableAddress();
            }
        }, BACK {
            @Override
            long initialPtr(SlabStorageChunk chunk, long objectSize) {
                return chunk.getFirstAvailableAddress();
            }

            @Override
            long advancePtr(final long objectSize) {
                return -objectSize;
            }

            @Override
            boolean done(final SlabStorageChunk chunk, final long ptr) {
                return ptr < 0;
            }
        };

        abstract long initialPtr(SlabStorageChunk chunk, long objectSize);

        abstract long advancePtr(long objectSize);

        abstract boolean done(SlabStorageChunk chunk, long ptr);
    }

    private class SlabIterator implements Iterator<T> {

        private long iterationCounter = size;
        private final Direction direction;
        private int chunkPtr = 0;
        private StorageChunkIterator currentIterator;

        private SlabIterator(final Direction direction) {
            this.direction = direction;
            currentIterator = new StorageChunkIterator(storageChunks.get(0), direction);
        }

        @Override
        public boolean hasNext() {
            return iterationCounter > 0;
        }

        @Override
        public T next() {
            if (currentIterator.hasNext()) {
                iterationCounter--;
                return currentIterator.next().asBean();
            }
            if (++chunkPtr >= storageChunks.size()) {
                throw new NoSuchElementException("Already iterated over [" + iterationCounter + "] elements");
            }
            currentIterator = new StorageChunkIterator(storageChunks.get(chunkPtr), direction);
            return next();
        }
    }

    private class StorageChunkIterator implements Iterator<SlabFlyweight<T>> {

        private final SlabStorageChunk chunk;
        private final Direction direction;
        private final SlabStorage storage;

        private long iterationCounter;
        private long ptr;

        private StorageChunkIterator(final SlabStorageChunk chunk, final Direction direction) {
            this.chunk = chunk;
            this.storage = chunk.getStorage();
            this.direction = direction;
            ptr = direction.initialPtr(chunk, objectSize);
            iterationCounter = chunk.size();
        }

        @Override
        public boolean hasNext() {
            return iterationCounter > 0;
        }

        @SuppressWarnings("unchecked")
        @Override
        public SlabFlyweight<T> next() {
            ptr += direction.advancePtr(objectSize);
            SlabFlyweight<T> flyweight = factory.getInstance();
            flyweight.map(storage, ptr);
            while (!direction.done(chunk, ptr) && flyweight.isNull()) {
                ptr += direction.advancePtr(objectSize);
                flyweight.map(storage, ptr);
            }
            iterationCounter--;
            return flyweight;
        }
    }

    private static class SlabStorageChunk {

        private final long offset;
        private final SlabStorage storage;

        private long size;
        private long freeListIndex;
        private long ptr;

        SlabStorageChunk(SlabStorageFactory factory, final long capacity, final long offset) {
            this.offset = offset;
            this.storage = factory.allocateStorage(capacity);
            this.freeListIndex = -1;
            this.size = 0;
            this.ptr = 0;

        }

        long offsetAddress(long address) {
            return offset + address;
        }

        long noOffsetAddress(long address) {
            return address - offset;
        }

        long getFreeListIndex() {
            return freeListIndex;
        }

        void setFreeListIndex(final long freeListIndex) {
            this.freeListIndex = freeListIndex;
        }

        SlabStorage getStorage() {
            return storage;
        }

        void destroy() {
            storage.freeStorage();
        }

        boolean isAvailableCapacity() {
            return freeListIndex > -1 || ptr < storage.capacity();
        }

        long size() {
            return size;
        }

        void incrementSize() {
            size++;
        }

        void decrementSize() {
            size--;
        }

        long getFirstAvailableAddress() {
            return ptr;
        }

        public void removedDownTo(final long offset) {
            ptr = offset;
        }

        public void addedUpTo(final long offset) {
            ptr = Math.max(ptr, offset);
        }
    }
}
