package org.langera.slab

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
        if (bean == null) {
            setAsNull(storage, address)
        }
        else {
            removeNullFlag(storage, address)
            storeByteValue(bean.getByteValue(), storage, address)
            storeIntValue(bean.getIntValue(), storage, address)
            storeLongValue(bean.getLongValue(), storage, address)
            storeIntArrayValue(bean.getIntArrayValue(), storage, address)
        }
    }

    @Override
    byte getByteValue() {
        return storage.getByte(address + byteValueOffset)
    }

    @Override
    void setByteValue(byte value) {
        storeByteValue(value, storage, address)
    }

    @Override
    int getIntValue() {
        return storage.getInt(address + intValueOffset)
    }

    @Override
    void setIntValue(int value) {
        storeIntValue(value, storage, address)
    }

    @Override
    long getLongValue() {
        return storage.getLong(address + longValueOffset)
    }

    @Override
    void setLongValue(long value) {
        storeLongValue(value, storage, address)
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
        storeIntArrayValue(value, storage, address)
    }

    @Override
    int getStoredObjectSize() {
        return intArrayValueOffset + (intArrayFixedSize * 4)
    }

    @Override
    boolean isNull(final SlabStorage storage, final long address) {
        return storage.getBoolean(address)
    }

    @Override
    long getNextFreeAddress(final SlabStorage storage, final long address) {
        return storage.getLong(address + freeAddressOffset)
    }

    @Override
    void setAsFreeAddress(final SlabStorage storage, final long address, final long nextFreeAddress) {
        storage.setBoolean(true, address)
        storage.setLong(nextFreeAddress, address + freeAddressOffset)
    }

    @Override
    void setAsNull(final SlabStorage storage, final long address) {
        setAsFreeAddress(storage, address, -1)
    }

    private removeNullFlag(final SlabStorage storage, final long address) {
        storage.setBoolean(false, address)
    }

    private void storeByteValue(byte value, SlabStorage storage, long address) {
        storage.setByte(value, address + byteValueOffset)
    }

    private void storeIntValue(int value, SlabStorage storage, long address) {
        storage.setInt(value, address + intValueOffset)
    }

    private void storeLongValue(long value, SlabStorage storage, long address) {
        storage.setLong(value, address + longValueOffset)
    }

    private void storeIntArrayValue(int[] value, SlabStorage storage, long address) {
        storage.setIntArray(value, address + intArrayValueOffset)
    }
}
