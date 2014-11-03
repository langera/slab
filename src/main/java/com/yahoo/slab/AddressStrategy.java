package com.yahoo.slab;

public interface AddressStrategy<T> {

    long createKey(final long address, final T instance);

    long getAddress(final long key);

    long removeAddress(final long key);

    long map(final long existingKey, final long newAddress);
}
