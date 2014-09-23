package org.langera.slab;

class SlabStorageChunk {

    private final SlabStorage storage;
    private long freeListIndex;

    SlabStorageChunk(SlabStorageFactory factory, final long capacity) {
        this.storage = factory.allocateStorage(capacity);
        this.freeListIndex = -1;
    }

    long getFreeListIndex() {
        return freeListIndex;
    }

    SlabStorage getStorage() {
        return storage;
    }

    void destory() {
        storage.freeStorage();
    }
}
