package org.langera.slab;

public interface SlabCompactionEventHandler {

    void beforeCompactionMove(final long address);

    void afterCompactionMove(final long oldAddress, long newAddress);

    void beforeCompactionOfStorage();

    void afterCompactionOfStorage();
}
