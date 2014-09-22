package org.langera.slab

class SimpleBeanFlyweight implements SlabFlyweight<Bean>, Bean {

    private static final int BYTE_VALUE_OFFSET = 1
    private static final int INT_VALUE_OFFSET = BYTE_VALUE_OFFSET + 1
    private static final int LONG_VALUE_OFFSET = INT_VALUE_OFFSET + 4
    // offset is storage specific but that can be a method on storage (getIntOffset() ) and then this is not storage specific
    // how does it become static??? - it cant
    private static final int INT_ARRAY_VALUE_OFFSET = LONG_VALUE_OFFSET + 8

    private static final int FREE_ADDRESS_OFFSET = 1

    private final int intArrayFixedSize;

    private SlabStorage storage
    private long address = -1

    SimpleBeanFlyweight(final int intArrayFixedSize) {
        this.intArrayFixedSize = intArrayFixedSize
    }

    @Override
    void map(final SlabStorage storage, final long address) {
        this.storage = storage
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
        return storage.getByte(address + BYTE_VALUE_OFFSET)
    }

    @Override
    void setByteValue(byte value) {
        storeByteValue(value, storage, address)
    }

    @Override
    int getIntValue() {
        return storage.getInt(address + INT_VALUE_OFFSET)
    }

    @Override
    void setIntValue(int value) {
        storeIntValue(value, storage, address)
    }

    @Override
    long getLongValue() {
        return storage.getLong(address + LONG_VALUE_OFFSET)
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
        return storage.getIntArray(toUse, address + INT_ARRAY_VALUE_OFFSET)
    }

    @Override
    void setIntArrayValue(int[] value) {
        storeIntArrayValue(value, storage, address)
    }

    @Override
    int getStoredObjectSize() {
        return INT_ARRAY_VALUE_OFFSET + (intArrayFixedSize * 4)
    }

    @Override
    boolean isNull(final SlabStorage storage, final long address) {
        return storage.getBoolean(address)
    }

    @Override
    long getNextFreeAddress(final SlabStorage storage, final long address) {
        return storage.getLong(address + FREE_ADDRESS_OFFSET)
    }

    @Override
    void setAsFreeAddress(final SlabStorage storage, final long address, final long nextFreeAddress) {
        storage.setBoolean(true, address)
        storage.setLong(nextFreeAddress, address + FREE_ADDRESS_OFFSET)
    }

    @Override
    void setAsNull(final SlabStorage storage, final long address) {
        setAsFreeAddress(storage, address, -1)
    }

    private removeNullFlag(final SlabStorage storage, final long address) {
        storage.setBoolean(false, address)
    }

    private void storeByteValue(byte value, SlabStorage storage, long address) {
        storage.setByte(value, address + BYTE_VALUE_OFFSET)
    }

    private void storeIntValue(int value, SlabStorage storage, long address) {
        storage.setInt(value, address + BYTE_VALUE_OFFSET)
    }

    private void storeLongValue(long value, SlabStorage storage, long address) {
        storage.setLong(value, address + BYTE_VALUE_OFFSET)
    }

    private void storeIntArrayValue(int[] value, SlabStorage storage, long address) {
        storage.setIntArray(value, address + BYTE_VALUE_OFFSET)
    }
}
