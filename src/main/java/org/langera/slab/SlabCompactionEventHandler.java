package org.langera.slab;

public interface SlabCompactionEventHandler {

    void beforeCompactionMove(final long oldAddress);

    void afterCompactionMove(final long oldAddress, final long newAddress);
}
