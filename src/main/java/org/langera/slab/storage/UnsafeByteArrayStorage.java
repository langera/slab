package org.langera.slab.storage;

import org.langera.slab.SlabStorage;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeByteArrayStorage  implements SlabStorage {

    private static final int BOOLEAN_OFFSET = 1;
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

    private static final Unsafe UNSAFE;
    private static final long BOOLEAN_ARRAY_OFFSET;
    private static final long BYTE_ARRAY_OFFSET;
    private static final long SHORT_ARRAY_OFFSET;
    private static final long CHAR_ARRAY_OFFSET;
    private static final long INT_ARRAY_OFFSET;
    private static final long FLOAT_ARRAY_OFFSET;
    private static final long LONG_ARRAY_OFFSET;
    private static final long DOUBLE_ARRAY_OFFSET;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
            BOOLEAN_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(boolean[].class);
            BYTE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
            SHORT_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(short[].class);
            CHAR_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(char[].class);
            INT_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(int[].class);
            FLOAT_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(float[].class);
            LONG_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(long[].class);
            DOUBLE_ARRAY_OFFSET = UNSAFE.arrayBaseOffset(double[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final byte[] buffer;
    private final long capacity;

    public UnsafeByteArrayStorage(final int capacity) {
        this.capacity = capacity;
        buffer = new byte[capacity];
    }

    @Override
    public boolean getBoolean(final long offset) {
        return UNSAFE.getBoolean(buffer, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public long setBoolean(final boolean value, final long offset) {
        UNSAFE.putBoolean(buffer, BYTE_ARRAY_OFFSET + offset, value);
        return offset + BOOLEAN_OFFSET;
    }

    @Override
    public int getBooleanOffset() {
        return BOOLEAN_OFFSET;
    }

    @Override
    public boolean[] getBooleanArray(final boolean[] container, final long offset) {
        UNSAFE.copyMemory(buffer, BYTE_ARRAY_OFFSET + offset, container, BOOLEAN_ARRAY_OFFSET, container.length);
        return container;
    }

    @Override
    public long setBooleanArray(final boolean[] values, final long offset) {
        UNSAFE.copyMemory(values, BOOLEAN_ARRAY_OFFSET, buffer, BYTE_ARRAY_OFFSET + offset, values.length);
        return offset + values.length;
    }

    @Override
    public int getBooleanArrayOffset(final int arraySize) {
        return arraySize;
    }

    @Override
    public byte getByte(final long offset) {
        return UNSAFE.getByte(buffer, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public long setByte(final byte value, final long offset) {
        UNSAFE.putByte(buffer, BYTE_ARRAY_OFFSET + offset, value);
        return offset + BYTE_OFFSET;
    }

    @Override
    public int getByteOffset() {
        return BYTE_OFFSET;
    }

    @Override
    public byte[] getByteArray(final byte[] container, final long offset) {
        UNSAFE.copyMemory(buffer, BYTE_ARRAY_OFFSET + offset, container, BYTE_ARRAY_OFFSET, container.length);
        return container;
    }

    @Override
    public long setByteArray(final byte[] values, final long offset) {
        UNSAFE.copyMemory(values, BYTE_ARRAY_OFFSET, buffer, BYTE_ARRAY_OFFSET + offset, values.length);
        return offset + values.length;
    }

    @Override
    public int getByteArrayOffset(final int arraySize) {
        return arraySize;
    }

    @Override
    public short getShort(final long offset) {
        return UNSAFE.getShort(buffer, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public long setShort(final short value, final long offset) {
        UNSAFE.putShort(buffer, BYTE_ARRAY_OFFSET + offset, value);
        return offset + SHORT_OFFSET;
    }

    @Override
    public int getShortOffset() {
        return SHORT_OFFSET;
    }

    @Override
    public short[] getShortArray(final short[] container, final long offset) {
        long bytesToCopy = container.length << SHORT_OFFSET_POWER_OF_TWO;
        UNSAFE.copyMemory(buffer, BYTE_ARRAY_OFFSET + offset, container, SHORT_ARRAY_OFFSET, bytesToCopy);
        return container;
    }

    @Override
    public long setShortArray(final short[] values, final long offset) {
        long bytesToCopy = values.length << SHORT_OFFSET_POWER_OF_TWO;
        UNSAFE.copyMemory(values, SHORT_ARRAY_OFFSET, buffer, BYTE_ARRAY_OFFSET + offset, bytesToCopy);
        return offset + bytesToCopy;
    }

    @Override
    public int getShortArrayOffset(final int arraySize) {
        return arraySize << SHORT_OFFSET_POWER_OF_TWO;
    }

    @Override
    public char getChar(final long offset) {
        return UNSAFE.getChar(buffer, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public long setChar(final char value, final long offset) {
        UNSAFE.putChar(buffer, BYTE_ARRAY_OFFSET + offset, value);
        return offset + CHAR_OFFSET;
    }

    @Override
    public int getCharOffset() {
        return CHAR_OFFSET;
    }

    @Override
    public char[] getCharArray(final char[] container, final long offset) {
        long bytesToCopy = container.length << CHAR_OFFSET_POWER_OF_TWO;
        UNSAFE.copyMemory(buffer, BYTE_ARRAY_OFFSET + offset, container, CHAR_ARRAY_OFFSET, bytesToCopy);
        return container;
    }

    @Override
    public long setCharArray(final char[] values, final long offset) {
        long bytesToCopy = values.length << CHAR_OFFSET_POWER_OF_TWO;
        UNSAFE.copyMemory(values, CHAR_ARRAY_OFFSET, buffer, BYTE_ARRAY_OFFSET + offset, bytesToCopy);
        return offset + bytesToCopy;
    }

    @Override
    public int getCharArrayOffset(final int arraySize) {
        return arraySize << CHAR_OFFSET_POWER_OF_TWO;
    }

    @Override
    public int getInt(final long offset) {
        return UNSAFE.getInt(buffer, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public long setInt(final int value, final long offset) {
        UNSAFE.putInt(buffer, BYTE_ARRAY_OFFSET + offset, value);
        return offset + INT_OFFSET;
    }

    @Override
    public int getIntOffset() {
        return INT_OFFSET;
    }

    @Override
    public int[] getIntArray(final int[] container, final long offset) {
        long bytesToCopy = container.length << INT_OFFSET_POWER_OF_TWO;
        UNSAFE.copyMemory(buffer, BYTE_ARRAY_OFFSET + offset, container, INT_ARRAY_OFFSET, bytesToCopy);
        return container;
    }

    @Override
    public long setIntArray(final int[] values, final long offset) {
        long bytesToCopy = values.length << INT_OFFSET_POWER_OF_TWO;
        UNSAFE.copyMemory(values, INT_ARRAY_OFFSET, buffer, BYTE_ARRAY_OFFSET + offset, bytesToCopy);
        return offset + bytesToCopy;
    }

    @Override
    public int getIntArrayOffset(final int arraySize) {
        return arraySize << INT_OFFSET_POWER_OF_TWO;
    }

    @Override
    public float getFloat(final long offset) {
        return UNSAFE.getFloat(buffer, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public long setFloat(final float value, final long offset) {
        UNSAFE.putFloat(buffer, BYTE_ARRAY_OFFSET + offset, value);
        return offset + FLOAT_OFFSET;
    }

    @Override
    public int getFloatOffset() {
        return FLOAT_OFFSET;
    }

    @Override
    public float[] getFloatArray(final float[] container, final long offset) {
        long bytesToCopy = container.length << FLOAT_OFFSET_POWER_OF_TWO;
        UNSAFE.copyMemory(buffer, BYTE_ARRAY_OFFSET + offset, container, FLOAT_ARRAY_OFFSET, bytesToCopy);
        return container;
    }

    @Override
    public long setFloatArray(final float[] values, final long offset) {
        long bytesToCopy = values.length << FLOAT_OFFSET_POWER_OF_TWO;
        UNSAFE.copyMemory(values, FLOAT_ARRAY_OFFSET, buffer, BYTE_ARRAY_OFFSET + offset, bytesToCopy);
        return offset + bytesToCopy;
    }

    @Override
    public int getFloatArrayOffset(final int arraySize) {
        return arraySize << FLOAT_OFFSET_POWER_OF_TWO;
    }

    @Override
    public long getLong(final long offset) {
        return UNSAFE.getLong(buffer, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public long setLong(final long value, final long offset) {
        UNSAFE.putLong(buffer, BYTE_ARRAY_OFFSET + offset, value);
        return offset + LONG_OFFSET;
    }

    @Override
    public int getLongOffset() {
        return LONG_OFFSET;
    }

    @Override
    public long[] getLongArray(final long[] container, final long offset) {
        long bytesToCopy = container.length << LONG_OFFSET_POWER_OF_TWO;
        UNSAFE.copyMemory(buffer, BYTE_ARRAY_OFFSET + offset, container, LONG_ARRAY_OFFSET, bytesToCopy);
        return container;
    }

    @Override
    public long setLongArray(final long[] values, final long offset) {
        long bytesToCopy = values.length << LONG_OFFSET_POWER_OF_TWO;
        UNSAFE.copyMemory(values, LONG_ARRAY_OFFSET, buffer, BYTE_ARRAY_OFFSET + offset, bytesToCopy);
        return offset + bytesToCopy;
    }

    @Override
    public int getLongArrayOffset(final int arraySize) {
        return arraySize << LONG_OFFSET_POWER_OF_TWO;
    }

    @Override
    public double getDouble(final long offset) {
        return UNSAFE.getDouble(buffer, BYTE_ARRAY_OFFSET + offset);
    }

    @Override
    public long setDouble(final double value, final long offset) {
        UNSAFE.putDouble(buffer, BYTE_ARRAY_OFFSET + offset, value);
        return offset + DOUBLE_OFFSET;
    }

    @Override
    public int getDoubleOffset() {
        return DOUBLE_OFFSET;
    }

    @Override
    public double[] getDoubleArray(final double[] container, final long offset) {
        long bytesToCopy = container.length << DOUBLE_OFFSET_POWER_OF_TWO;
        UNSAFE.copyMemory(buffer, BYTE_ARRAY_OFFSET + offset, container, DOUBLE_ARRAY_OFFSET, bytesToCopy);
        return container;
    }

    @Override
    public long setDoubleArray(final double[] values, final long offset) {
        long bytesToCopy = values.length << DOUBLE_OFFSET_POWER_OF_TWO;
        UNSAFE.copyMemory(values, DOUBLE_ARRAY_OFFSET, buffer, BYTE_ARRAY_OFFSET + offset, bytesToCopy);
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
    public void freeStorage() { }

    @Override
    public boolean isDirect() {
        return false;
    }
}
