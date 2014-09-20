package org.langera.slab;

public interface SlabCompactionEventHandler {

    void beforeCompactionMove(long oldAddress);

    void afterCompactionMove(long oldAddress, long newAddress);
}
