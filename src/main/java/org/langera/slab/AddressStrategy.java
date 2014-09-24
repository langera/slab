package org.langera.slab;

public interface AddressStrategy {

    long getKey(final long address);

    long getAddress(final long key);

    long removeAddress(final long key);

    long map(final long existingKey, final long newAddress);
}
