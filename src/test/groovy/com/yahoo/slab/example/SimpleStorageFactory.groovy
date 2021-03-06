package com.yahoo.slab.example

import com.yahoo.slab.SlabStorageFactory

class SimpleStorageFactory implements SlabStorageFactory<SimpleStorage> {

    @Override
    public SimpleStorage allocateStorage(final long capacity) {
        return new SimpleStorage((int) capacity)
    }

    @Override
    public boolean supportsCapacity(final long capacity) {
        return capacity <= Integer.MAX_VALUE
    }
}

