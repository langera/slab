package org.langera.slab.flyweight;

import org.langera.slab.SlabFlyweight;
import org.langera.slab.SlabStorage;

public abstract class AbstractSlabFlyweight<T> implements SlabFlyweight<T> {

    private SlabStorage storage;
    private long address = -1;
    private int freeAddressOffset;

    protected SlabStorage getStorage() {
        return storage;
    }

    protected void setFreeAddressOffset(final int freeAddressOffset) {
        this.freeAddressOffset = freeAddressOffset;
    }

    @Override
    public void map(final SlabStorage storage, final long address) {
        this.storage = storage;
        mapAddress(address);
    }

    @Override
    public void mapAddress(final long address) {
        this.address = address;
    }

    @Override
    public long getMappedAddress() {
        return address;
    }

    @Override
    public boolean isNull() {
        return isNull(storage, address);
    }

    @Override
    public long getNextFreeAddress(final SlabStorage storage, final long address) {
        return storage.getLong(address + freeAddressOffset);
    }

    @Override
    public void setAsFreeAddress(final SlabStorage storage, final long address, final long nextFreeAddress) {
        storage.setLong(nextFreeAddress, address + freeAddressOffset);
    }
}
