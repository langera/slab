package com.yahoo.slab.perf;

import com.yahoo.slab.SlabStorage;
import com.yahoo.slab.flyweight.AbstractSlabFlyweight;

public class BeanFlyweight extends AbstractSlabFlyweight<Bean> implements Bean {

    private final int myLongArrayFixedSize;
    private final int myCharArrayFixedSize;

    private int myUnsignedIntOffset;
    private int myByteOffset;
    private int myDoubleOffset;
    private int myLongArrayOffset;
    private int myCharArrayOffset;


    public BeanFlyweight(final int myLongArrayFixedSize, final int myCharArrayFixedSize) {
        this.myLongArrayFixedSize = myLongArrayFixedSize;
        this.myCharArrayFixedSize = myCharArrayFixedSize;
    }

    @Override
    public void map(final SlabStorage storage, final long address) {
        super.map(storage, address);
        this.myUnsignedIntOffset = 0;
        this.myByteOffset = storage.getIntOffset();
        this.myDoubleOffset= this.myByteOffset + storage.getByteOffset();
        this.myLongArrayOffset= this.myDoubleOffset + storage.getDoubleOffset();
        this.myCharArrayOffset= this.myLongArrayOffset + storage.getLongArrayOffset(myLongArrayFixedSize);
        setFreeAddressOffset(this.myByteOffset);
    }

    @Override
    public boolean isNull(final SlabStorage storage, final long address) {
        return storage.getInt(address) < 0; // piggy back on unsigned int to reduce memory for null flag
    }

    @Override
    public void setAsFreeAddress(final SlabStorage storage, final long address, final long nextFreeAddress) {
        super.setAsFreeAddress(storage, address, nextFreeAddress);
        storage.setInt(-1, address + myUnsignedIntOffset);  // piggy back on unsigned int to reduce memory for null flag
    }

    @Override
    public void dumpToStorage(final Bean bean, final SlabStorage storage, final long address) {
        long offset = storage.setInt(bean.getMyUnsignedInt(), address);
        offset = storage.setByte(bean.getMyByte(), offset);
        offset = storage.setDouble(bean.getMyDouble(), offset);
        offset = storage.setLongArray(bean.getMyLongArray(), offset);
        storage.setCharArray(bean.getMyCharArray(), offset);
    }

    @Override
    public int getStoredObjectSize(final SlabStorage storage) {
        return storage.getIntOffset() + storage.getByteOffset() + storage.getDoubleOffset() +
        storage.getLongArrayOffset(myLongArrayFixedSize) + storage.getCharArrayOffset(myCharArrayFixedSize);
    }

    @Override
    public Bean asBean() {
        return this;
    }

//////////////////////// Bean impl.

    @Override
    public int getMyUnsignedInt() {
        return getStorage().getInt(getMappedAddress() + myUnsignedIntOffset);
    }

    @Override
    public void setMyUnsignedInt(final int myUnsignedInt) {
        getStorage().setInt(myUnsignedInt, getMappedAddress() + myUnsignedIntOffset);
    }


    @Override
    public byte getMyByte() {
        return getStorage().getByte(getMappedAddress() + myByteOffset);
    }

    @Override
    public void setMyByte(final byte myByte) {
        getStorage().setByte(myByte, getMappedAddress() + myByteOffset);
    }

    @Override
    public double getMyDouble() {
        return getStorage().getDouble(getMappedAddress() + myDoubleOffset);
    }

    @Override
    public void setMyDouble(final double myDouble) {
        getStorage().setDouble(myDouble, getMappedAddress() + myDoubleOffset);
    }

    @Override
    public long[] getMyLongArray() {
        long[] container = new long[myLongArrayFixedSize];
        return getStorage().getLongArray(container, getMappedAddress() + myLongArrayOffset);
    }

    @Override
    public void setMyLongArray(final long[] myLongArray) {
        getStorage().setLongArray(myLongArray, getMappedAddress() + myLongArrayOffset);
    }

    @Override
    public char[] getMyCharArray() {
        char[] container = new char[myCharArrayOffset];
        return getStorage().getCharArray(container, getMappedAddress() + myCharArrayOffset);
    }

    @Override
    public void setMyCharArray(final char[] myCharArray) {
        getStorage().setCharArray(myCharArray, getMappedAddress() + myCharArrayOffset);
    }
}
