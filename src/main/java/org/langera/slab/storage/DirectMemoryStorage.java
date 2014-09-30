package org.langera.slab.storage;

import org.langera.slab.SlabStorage;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class DirectMemoryStorage implements SlabStorage {

    private static final int BYTE_OFFSET = 1;
    private static final int SHORT_OFFSET = 2;
    private static final int CHAR_OFFSET = 2;
    private static final int INT_OFFSET = 4;
    private static final int FLOAT_OFFSET = 4;
    private static final int LONG_OFFSET = 8;
    private static final int DOUBLE_OFFSET = 8;
    private static final int SHORT_OFFSET_POWER_OF_TWO = 1;
    private static final int CHAR_OFFSET_POWER_OF_TWO = 1;
    private static final int INT_OFFSET_POWER_OF_TWO = 2;
    private static final int FLOAT_OFFSET_POWER_OF_TWO = 2;
    private static final int LONG_OFFSET_POWER_OF_TWO = 3;
    private static final int DOUBLE_OFFSET_POWER_OF_TWO = 3;

    private static final Unsafe unsafe;
    private static final long booleanArrayOffset;
    private static final long byteArrayOffset;
    private static final long shortArrayOffset;
    private static final long charArrayOffset;
    private static final long intArrayOffset;
    private static final long floatArrayOffset;
    private static final long longArrayOffset;
    private static final long doubleArrayOffset;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            booleanArrayOffset = unsafe.arrayBaseOffset(boolean[].class);
            byteArrayOffset = unsafe.arrayBaseOffset(byte[].class);
            shortArrayOffset = unsafe.arrayBaseOffset(short[].class);
            charArrayOffset = unsafe.arrayBaseOffset(char[].class);
            intArrayOffset = unsafe.arrayBaseOffset(int[].class);
            floatArrayOffset = unsafe.arrayBaseOffset(float[].class);
            longArrayOffset = unsafe.arrayBaseOffset(long[].class);
            doubleArrayOffset = unsafe.arrayBaseOffset(double[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final long address;
    private final long capacity;

    public DirectMemoryStorage(final long capacity) {
        this.capacity = capacity;
        address = unsafe.allocateMemory(capacity);
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
        return BYTE_OFFSET;
    }

    @Override
    public boolean[] getBooleanArray(final boolean[] container, final long offset) {
        unsafe.copyMemory(null, address + offset, container, booleanArrayOffset, container.length);
        return container;
    }

    @Override
    public long setBooleanArray(final boolean[] values, final long offset) {
        unsafe.copyMemory(values, booleanArrayOffset, null, address + offset, values.length);
        return offset + values.length;
    }

    @Override
    public int getBooleanArrayOffset(final int arraySize) {
        return arraySize;
    }

    @Override
    public byte getByte(final long offset) {
        return unsafe.getByte(address + offset);
    }

    @Override
    public long setByte(final byte value, final long offset) {
        unsafe.putByte(address + offset, value);
        return offset + BYTE_OFFSET;
    }

    @Override
    public int getByteOffset() {
        return BYTE_OFFSET;
    }

    @Override
    public byte[] getByteArray(final byte[] container, final long offset) {
        unsafe.copyMemory(null, address + offset, container, byteArrayOffset, container.length);
        return container;
    }

    @Override
    public long setByteArray(final byte[] values, final long offset) {
        unsafe.copyMemory(values, byteArrayOffset, null, address + offset, values.length);
        return offset + values.length;
    }

    @Override
    public int getByteArrayOffset(final int arraySize) {
        return arraySize;
    }

    @Override
    public short getShort(final long offset) {
        return unsafe.getShort(address + offset);
    }

    @Override
    public long setShort(final short value, final long offset) {
        unsafe.putShort(address + offset, value);
        return offset + SHORT_OFFSET;
    }

    @Override
    public int getShortOffset() {
        return SHORT_OFFSET;
    }

    @Override
    public short[] getShortArray(final short[] container, final long offset) {
        long bytesToCopy = container.length << SHORT_OFFSET_POWER_OF_TWO;
        unsafe.copyMemory(null, address + offset, container, shortArrayOffset, bytesToCopy);
        return container;
    }

    @Override
    public long setShortArray(final short[] values, final long offset) {
        long bytesToCopy = values.length << SHORT_OFFSET_POWER_OF_TWO;
        unsafe.copyMemory(values, shortArrayOffset, null, address + offset, bytesToCopy);
        return offset + bytesToCopy;
    }

    @Override
    public int getShortArrayOffset(final int arraySize) {
        return arraySize << SHORT_OFFSET_POWER_OF_TWO;
    }

    @Override
    public char getChar(final long offset) {
        return unsafe.getChar(address + offset);
    }

    @Override
    public long setChar(final char value, final long offset) {
        unsafe.putChar(address + offset, value);
        return offset + CHAR_OFFSET;
    }

    @Override
    public int getCharOffset() {
        return CHAR_OFFSET;
    }

    @Override
    public char[] getCharArray(final char[] container, final long offset) {
        long bytesToCopy = container.length << CHAR_OFFSET_POWER_OF_TWO;
        unsafe.copyMemory(null, address + offset, container, charArrayOffset, bytesToCopy);
        return container;
    }

    @Override
    public long setCharArray(final char[] values, final long offset) {
        long bytesToCopy = values.length << CHAR_OFFSET_POWER_OF_TWO;
        unsafe.copyMemory(values, charArrayOffset, null, address + offset, bytesToCopy);
        return offset + bytesToCopy;
    }

    @Override
    public int getCharArrayOffset(final int arraySize) {
        return arraySize << CHAR_OFFSET_POWER_OF_TWO;
    }

    @Override
    public int getInt(final long offset) {
        return unsafe.getInt(address + offset);
    }

    @Override
    public long setInt(final int value, final long offset) {
        unsafe.putInt(address + offset, value);
        return offset + INT_OFFSET;
    }

    @Override
    public int getIntOffset() {
        return INT_OFFSET;
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
        return offset + bytesToCopy;
    }

    @Override
    public int getIntArrayOffset(final int arraySize) {
        return arraySize << INT_OFFSET_POWER_OF_TWO;
    }

    @Override
    public float getFloat(final long offset) {
        return unsafe.getFloat(address + offset);
    }

    @Override
    public long setFloat(final float value, final long offset) {
        unsafe.putFloat(address + offset, value);
        return offset + FLOAT_OFFSET;
    }

    @Override
    public int getFloatOffset() {
        return FLOAT_OFFSET;
    }

    @Override
    public float[] getFloatArray(final float[] container, final long offset) {
        long bytesToCopy = container.length << FLOAT_OFFSET_POWER_OF_TWO;
        unsafe.copyMemory(null, address + offset, container, floatArrayOffset, bytesToCopy);
        return container;
    }

    @Override
    public long setFloatArray(final float[] values, final long offset) {
        long bytesToCopy = values.length << FLOAT_OFFSET_POWER_OF_TWO;
        unsafe.copyMemory(values, floatArrayOffset, null, address + offset, bytesToCopy);
        return offset + bytesToCopy;
    }

    @Override
    public int getFloatArrayOffset(final int arraySize) {
        return arraySize << FLOAT_OFFSET_POWER_OF_TWO;
    }

    @Override
    public long getLong(final long offset) {
        return unsafe.getLong(address + offset);
    }

    @Override
    public long setLong(final long value, final long offset) {
        unsafe.putLong(address + offset, value);
        return offset + LONG_OFFSET;
    }

    @Override
    public int getLongOffset() {
        return LONG_OFFSET;
    }

    @Override
    public long[] getLongArray(final long[] container, final long offset) {
        long bytesToCopy = container.length << LONG_OFFSET_POWER_OF_TWO;
        unsafe.copyMemory(null, address + offset, container, longArrayOffset, bytesToCopy);
        return container;
    }

    @Override
    public long setLongArray(final long[] values, final long offset) {
        long bytesToCopy = values.length << LONG_OFFSET_POWER_OF_TWO;
        unsafe.copyMemory(values, longArrayOffset, null, address + offset, bytesToCopy);
        return offset + bytesToCopy;
    }

    @Override
    public int getLongArrayOffset(final int arraySize) {
        return arraySize << LONG_OFFSET_POWER_OF_TWO;
    }

    @Override
    public double getDouble(final long offset) {
        return unsafe.getDouble(address + offset);
    }

    @Override
    public long setDouble(final double value, final long offset) {
        unsafe.putDouble(address + offset, value);
        return offset + DOUBLE_OFFSET;
    }

    @Override
    public int getDoubleOffset() {
        return DOUBLE_OFFSET;
    }

    @Override
    public double[] getDoubleArray(final double[] container, final long offset) {
        long bytesToCopy = container.length << DOUBLE_OFFSET_POWER_OF_TWO;
        unsafe.copyMemory(null, address + offset, container, doubleArrayOffset, bytesToCopy);
        return container;
    }

    @Override
    public long setDoubleArray(final double[] values, final long offset) {
        long bytesToCopy = values.length << DOUBLE_OFFSET_POWER_OF_TWO;
        unsafe.copyMemory(values, doubleArrayOffset, null, address + offset, bytesToCopy);
        return offset + bytesToCopy;
    }

    @Override
    public int getDoubleArrayOffset(final int arraySize) {
        return arraySize << DOUBLE_OFFSET_POWER_OF_TWO;
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
