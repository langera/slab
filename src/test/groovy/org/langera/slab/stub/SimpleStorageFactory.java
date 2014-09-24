package org.langera.slab.stub;

import org.langera.slab.SlabStorageFactory;

public class SimpleStorageFactory implements SlabStorageFactory<SimpleStorage> {

    @Override
    public SimpleStorage allocateStorage(final long capacity) {
        return new SimpleStorage((int) capacity);
    }
}
