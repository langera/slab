package com.yahoo.slab;

public interface SlabStorageFactory<S extends SlabStorage> {

    S allocateStorage(final long capacity);

    boolean supportsCapacity(final long capacity);
}
