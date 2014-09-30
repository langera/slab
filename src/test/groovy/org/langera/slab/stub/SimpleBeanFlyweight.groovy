package org.langera.slab.stub

import org.langera.slab.SlabFlyweight
import org.langera.slab.SlabStorage

class SimpleBeanFlyweight implements SlabFlyweight<Bean>, Bean {

    // we add a boolean as a null flag. If certain assumptions on the values exist (as unsigned)
    // we can piggy back on those value for the null flag and reduce space.

    private final int intArrayFixedSize;

    private SlabStorage storage
    private long address = -1
    private int freeAddressOffset;
    private int byteValueOffset;
    private int intValueOffset;
    private int longValueOffset;
    private int intArrayValueOffset;

    SimpleBeanFlyweight(final int intArrayFixedSize) {
        this.intArrayFixedSize = intArrayFixedSize
    }

    @Override
    void map(final SlabStorage storage, final long address) {
        this.storage = storage
        this.freeAddressOffset = storage.booleanOffset
        this.byteValueOffset = storage.booleanOffset
        this.intValueOffset = byteValueOffset + storage.byteOffset
        this.longValueOffset = intValueOffset + storage.intOffset
        this.intArrayValueOffset = longValueOffset + storage.longOffset
        mapAddress(address)
    }

    @Override
    void mapAddress(final long address) {
        this.address = address
    }

    @Override
    long getMappedAddress() {
        return address
    }

    @Override
    void dumpToStorage(final Bean bean, final SlabStorage storage, final long address) {
        long addressWithOffset = address
        addressWithOffset = storage.setBoolean(false, addressWithOffset)
        addressWithOffset = storage.setByte(bean.getByteValue(), addressWithOffset)
        addressWithOffset = storage.setInt(bean.getIntValue(), addressWithOffset)
        addressWithOffset = storage.setLong(bean.getLongValue(), addressWithOffset)
        storage.setIntArray(bean.getIntArrayValue(), addressWithOffset)
    }

    @Override
    byte getByteValue() {
        return storage.getByte(address + byteValueOffset)
    }

    @Override
    void setByteValue(byte value) {
        storage.setByte(value, address + byteValueOffset)
    }

    @Override
    int getIntValue() {
        return storage.getInt(address + intValueOffset)
    }

    @Override
    void setIntValue(int value) {
        storage.setInt(value, address + intValueOffset)
    }

    @Override
    long getLongValue() {
        return storage.getLong(address + longValueOffset)
    }

    @Override
    void setLongValue(long value) {
        storage.setLong(value, address + longValueOffset)
    }

    int[] getIntArrayValue() {
        return getIntArrayValue(null)
    }

    @Override
    int[] getIntArrayValue(final int[] container) {
        int[] toUse = (container == null || container.length != intArrayFixedSize) ? new int[intArrayFixedSize] : container
        return storage.getIntArray(toUse, address + intArrayValueOffset)
    }

    @Override
    void setIntArrayValue(int[] value) {
        storage.setIntArray(value, address + intArrayValueOffset)
    }

    @Override
    Bean asBean() {
        return this
    }

    @Override
    int getStoredObjectSize(SlabStorage storage) {
        return storage.booleanOffset + storage.byteOffset +
                storage.intOffset + storage.longOffset +
                (intArrayFixedSize * storage.intOffset)
    }

    @Override
    boolean isNull(final SlabStorage storage, final long address) {
        return storage.getBoolean(address)
    }

    @Override
    long getNextFreeAddress(final SlabStorage storage, final long address) {
        return storage.getLong(address + storage.booleanOffset)
    }

    @Override
    void setAsFreeAddress(final SlabStorage storage, final long address, final long nextFreeAddress) {
        long addressWithOffset = storage.setBoolean(true, address)
        storage.setLong(nextFreeAddress, addressWithOffset)
    }
}
