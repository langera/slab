package org.langera.slab;

public interface SlabStorageFactory<S extends SlabStorage> {

    S allocateStorage(final long capacity);
}
