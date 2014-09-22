package org.langera.slab;

public interface AddressStrategy {

    long getKey(long address);

    long getAddress(long key);
}
