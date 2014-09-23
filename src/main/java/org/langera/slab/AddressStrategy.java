package org.langera.slab;

public interface AddressStrategy {

    long getKey(long address);

    long getAddress(long key);

    long removeAddress(long key);

    long map(long existingKey, long newAddress);
}
