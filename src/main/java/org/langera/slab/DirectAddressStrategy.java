package org.langera.slab;

public class DirectAddressStrategy implements AddressStrategy {

    @Override
    public long getKey(final long address) {
        return address;
    }

    @Override
    public long getAddress(final long key) {
        return key;
    }

    @Override
    public long removeAddress(final long key) {
        return key;
    }

    @Override
    public long map(final long existingKey, final long newAddress) {
        return newAddress;
    }
}
