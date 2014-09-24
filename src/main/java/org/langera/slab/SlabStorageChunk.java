package org.langera.slab;

class SlabStorageChunk {

    private final long offset;
    private final SlabStorage storage;
    private long size;
    private long freeListIndex;

    SlabStorageChunk(SlabStorageFactory factory, final long capacity, final long offset) {
        this.offset = offset;
        this.storage = factory.allocateStorage(capacity);
        this.freeListIndex = -1;
        this.size = 0;
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

    void destory() {
        storage.freeStorage();
    }

    boolean isAvailableCapacity() {
        return freeListIndex > -1 || storage.getFirstAvailableAddress() < storage.capacity();
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
}
