package org.langera.slab;

public interface AddressingStrategy {

    long getKey(long address);

    long getAddress(long key);
}
