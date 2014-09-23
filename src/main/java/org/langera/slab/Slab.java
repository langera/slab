package org.langera.slab;

import java.util.Iterator;

public final class Slab<T> implements Iterable<T> {

    private final SlabStorageChunk[] storageChunks;
    private final AddressStrategy addressStrategy;
    private final SlabFlyweightFactory<T> factory;
    private final int objectSize;
    private final long chunkSize;
    private final SlabStorageFactory storageFactory;

    private int numberOfChunks;
    private long size = 0;

    public Slab(final SlabStorageFactory storageFactory,
                final long chunkSize,
                final AddressStrategy addressStrategy,
                final SlabFlyweightFactory<T> factory) {
        this.storageFactory = storageFactory;
        this.chunkSize = chunkSize;
        this.addressStrategy = addressStrategy;
        this.factory = factory;
        this.storageChunks = new SlabStorageChunk[10]; // TODO
        this.storageChunks[0] = new SlabStorageChunk(storageFactory, chunkSize);
        this.numberOfChunks = 1;
        this.objectSize = factory.getInstance().getStoredObjectSize(storageChunks[0].getStorage());
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
        final long realAddress = addressStrategy.getAddress(address);
        final SlabStorage storage = storageFor(realAddress).getStorage(); // TODO null check
        flyweight.map(storage, realAddress);
        return flyweight.isNull(storage, realAddress) ? null : flyweight.asBean();
    }

    public void remove(final long address) {
        final SlabFlyweight<T> flyweight = factory.getInstance();
        final long realAddress = addressStrategy.removeAddress(address);
        final SlabStorageChunk storage = storageFor(realAddress);
        if (flyweight.isNull(storage.getStorage(), realAddress)) {  // TODO null check
            throw new ArrayIndexOutOfBoundsException("Address does not exist [" + address + "]");
        } else {
            size--;
            removeFromStorage(storage, flyweight, realAddress);
        }
    }

    public long compact(final long address) {
        final SlabFlyweight<T> flyweight = factory.getInstance();
        final long realAddress = addressStrategy.getAddress(address);
        final SlabStorageChunk chunk = storageFor(realAddress);  // TODO null check
        final SlabStorage storage = chunk.getStorage();
        if (flyweight.isNull(storage, realAddress)) {
            throw new ArrayIndexOutOfBoundsException("Address does not exist [" + address + "]");
        } else {
            flyweight.map(storage, realAddress);
            T instance = flyweight.asBean();
            long newAddress = addToStorage(instance);
            long newKey = addressStrategy.map(address, newAddress);
            removeFromStorage(chunk, flyweight, realAddress);
            return newKey;
        }
    }

    public void compact() {
        // TODO
    }

    @Override
    public Iterator<T> iterator() {
        return new SlabIterator(Direction.FORWARD);
    }

    public long size() {
        return size;
    }

    public long availableCapacity() {
        return (numberOfChunks * chunkSize / objectSize) - size;
    }

    private SlabStorageChunk storageFor(final long address) {
        return storageChunks[(int) (address / chunkSize)];
    }

    private SlabStorageChunk availableStorage() {
        for (SlabStorageChunk chunk : storageChunks) {
            if (chunk.isAvailableCapacity()) {
                return chunk;
            }
        }
        return null;
    }

    private long addToFreeEntry(final T instance, final SlabStorageChunk chunk) {
        SlabFlyweight<T> flyweight = factory.getInstance();
        final SlabStorage storage = chunk.getStorage();
        long newAddress = chunk.getFreeListIndex();
        chunk.setFreeListIndex(flyweight.getNextFreeAddress(storage, newAddress));
        flyweight.dumpToStorage(instance, storage, newAddress);
        return newAddress;
    }

    private long addToLastIndex(final T instance, final SlabStorageChunk chunk) {
        final SlabStorage storage = chunk.getStorage();
        final long newAddress = storage.getFirstAvailableAddress();
        factory.getInstance().dumpToStorage(instance, storage, newAddress);
        return newAddress;
    }

    private long addToStorage(final T instance) {
        SlabStorageChunk chunk = availableStorage();
        return (chunk.getFreeListIndex() < 0) ? addToLastIndex(instance, chunk) : addToFreeEntry(instance, chunk);
    }

    private void removeFromStorage(final SlabStorageChunk chunk, final SlabFlyweight<T> flyweight, final long address) {
        final SlabStorage storage = chunk.getStorage();
        if (address == storage.getFirstAvailableAddress() - objectSize) {
            storage.remove(address, objectSize);
        } else {
            flyweight.setAsFreeAddress(storage, address, chunk.getFreeListIndex());
            chunk.setFreeListIndex(address);
        }
    }

    private enum Direction {
        FORWARD {
            @Override
            long initialPtr(long storageSize, long objectSize) {
                return -objectSize;
            }

            @Override
            long advancePtr(final long objectSize) {
                return objectSize;
            }

            @Override
            boolean done(final long storageSize, final long ptr) {
                return ptr < storageSize;
            }
        }, BACK {
            @Override
            long initialPtr(long storageSize, long objectSize) {
                return storageSize;
            }

            @Override
            long advancePtr(final long objectSize) {
                return -objectSize;
            }

            @Override
            boolean done(final long storageSize, final long ptr) {
                return ptr >= 0;
            }
        };

        abstract long initialPtr(long storageSize, long objectSize);

        abstract long advancePtr(long objectSize);

        abstract boolean done(long storageSize, long ptr);
    }

    private class SlabIterator implements Iterator<T> {

        private final Direction direction;
        private long ptr;
        private long visited = 0;
        private int chunkPtr = 0;
        private SlabStorage storage;

        private SlabIterator(final Direction direction) {
            this.direction = direction;
            this.storage = storageChunks[chunkPtr].getStorage();
            ptr = direction.initialPtr(storage.size(), objectSize);
        }

        @Override
        public boolean hasNext() {
            return visited < size;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T next() {
            visited++;
            advancePtr();
            SlabFlyweight<T> flyweight = factory.getInstance();
            flyweight.map(storage, ptr);
            while (direction.done(storage.size(), ptr) && flyweight.isNull(storage, ptr)) {
                advancePtr();
                flyweight.map(storage, ptr);
            }
            return flyweight.asBean();
        }

        private void advancePtr() {
            ptr += direction.advancePtr(objectSize);
            if (ptr == chunkSize) {
                this.storage = storageChunks[++chunkPtr].getStorage();  // TODO null
                ptr = direction.initialPtr(storage.size(), objectSize);
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove from iterator unsupported");
        }
    }
}
