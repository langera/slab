package org.langera.slab;

public class SimpleStorageFactory implements SlabStorageFactory<SimpleStorage> {

    @Override
    public SimpleStorage allocateStorage(final long capacity) {
        return new SimpleStorage((int) capacity);
    }
}
