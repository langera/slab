package org.langera.slab.storage;

import org.langera.slab.SlabStorage;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

import static java.lang.Math.max;

public class DirectMemoryStorage implements SlabStorage {

    private static final int BYTE_OFFSET = 1;
    private static final int INT_OFFSET = 4;
    private static final int LONG_OFFSET = 8;
    private static final int INT_OFFSET_POWER_OF_TWO = 2;
    private static final int LONG_OFFSET_POWER_OF_TWO = 4;

    private static final Unsafe unsafe;
    private static final long intArrayOffset;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            intArrayOffset = unsafe.arrayBaseOffset(int[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final long address;
    private final long capacity;
    private long ptr;

    public DirectMemoryStorage(final long capacity) {
        this.capacity = capacity;
        address = unsafe.allocateMemory(capacity);
        ptr = 0;
    }

    @Override
    public boolean getBoolean(final long offset) {
        return getByte(offset) != 0;
    }

    @Override
    public long setBoolean(final boolean value, final long offset) {
        return setByte((byte) (value ? 1 : 0), offset);
    }

    @Override
    public int getBooleanOffset() {
        return getByteOffset();
    }

    @Override
    public byte getByte(final long offset) {
        return unsafe.getByte(address + offset);
    }

    @Override
    public long setByte(final byte value, final long offset) {
        unsafe.putByte(address + offset, value);
        final long nextAddress = offset + BYTE_OFFSET;
        ptr = max(ptr, nextAddress);
        return nextAddress;
    }

    @Override
    public int getByteOffset() {
        return BYTE_OFFSET;
    }

    @Override
    public int getInt(final long offset) {
        return unsafe.getInt(address + offset);
    }

    @Override
    public long setInt(final int value, final long offset) {
        unsafe.putInt(address + offset, value);
        final long nextAddress = offset + INT_OFFSET;
        ptr = max(ptr, nextAddress);
        return nextAddress;
    }

    @Override
    public int getIntOffset() {
        return INT_OFFSET;
    }

    @Override
    public long getLong(final long offset) {
        return unsafe.getLong(address + offset);
    }

    @Override
    public long setLong(final long value, final long offset) {
        unsafe.putLong(address + offset, value);
        final long nextAddress = offset + LONG_OFFSET;
        ptr = max(ptr, nextAddress);
        return nextAddress;
    }

    @Override
    public int getLongOffset() {
        return LONG_OFFSET;
    }

    @Override
    public int[] getIntArray(final int[] container, final long offset) {
        long bytesToCopy = container.length << INT_OFFSET_POWER_OF_TWO;
        unsafe.copyMemory(null, address + offset, container, intArrayOffset, bytesToCopy);
        return container;
    }

    @Override
    public long setIntArray(final int[] values, final long offset) {
        long bytesToCopy = values.length << INT_OFFSET_POWER_OF_TWO;
        unsafe.copyMemory(values, intArrayOffset, null, address + offset, bytesToCopy);
        final long nextAddress = offset + bytesToCopy;
        ptr = max(ptr, nextAddress);
        return nextAddress;
    }

    @Override
    public int getIntArrayOffset(final int arraySize) {
        return arraySize << INT_OFFSET_POWER_OF_TWO;
    }

    @Override
    public long getFirstAvailableAddress() {
        return ptr;
    }

    @Override
    public void setFirstAvailableAddress(final long offset) {
        ptr = offset;
    }


    @Override
    public long capacity() {
        return capacity;
    }

    @Override
    public void freeStorage() {
        unsafe.freeMemory(address);
    }
}
