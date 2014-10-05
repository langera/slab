package com.yahoo.slab.storage;

import com.yahoo.slab.SlabStorage;

import java.nio.ByteBuffer;

public class ByteBufferStorage implements SlabStorage {

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

    private final ByteBuffer buffer;

    public ByteBufferStorage(final ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public boolean getBoolean(final long offset) {
        return buffer.get((int)offset) != 0;
    }

    @Override
    public long setBoolean(final boolean value, final long offset) {
        buffer.put((int)offset, (byte) (value ? 1 : 0));
        return offset + BOOLEAN_OFFSET;
    }

    @Override
    public int getBooleanOffset() {
        return BOOLEAN_OFFSET;
    }

    @Override
    public boolean[] getBooleanArray(final boolean[] container, final long offset) {
        for (int i = 0; i < container.length; i++) {
            container[i] = buffer.get((int)offset  + i) != 0;
        }
        return container;
    }

    @Override
    public long setBooleanArray(final boolean[] values, final long offset) {
        int index = (int) offset;
        for (boolean value : values) {
            buffer.put(index++, (byte)(value ? 1 : 0));
        }
        return offset + values.length;
    }

    @Override
    public int getBooleanArrayOffset(final int arraySize) {
        return arraySize;
    }

    @Override
    public byte getByte(final long offset) {
        return buffer.get((int)offset);
    }

    @Override
    public long setByte(final byte value, final long offset) {
        buffer.put((int)offset, value);
        return offset + BYTE_OFFSET;
    }

    @Override
    public int getByteOffset() {
        return BYTE_OFFSET;
    }

    @Override
    public byte[] getByteArray(final byte[] container, final long offset) {
        for (int i = 0; i < container.length; i++) {
            container[i] = buffer.get((int)offset + i);
        }
        return container;
    }

    @Override
    public long setByteArray(final byte[] values, final long offset) {
        int index = (int) offset;
        for (byte value : values) {
            buffer.put(index++, value);
        }
        return offset + values.length;
    }

    @Override
    public int getByteArrayOffset(final int arraySize) {
        return arraySize;
    }

    @Override
    public short getShort(final long offset) {
        return buffer.getShort((int)offset);
    }

    @Override
    public long setShort(final short value, final long offset) {
        buffer.putShort((int)offset, value);
        return offset + SHORT_OFFSET;
    }

    @Override
    public int getShortOffset() {
        return SHORT_OFFSET;
    }

    @Override
    public short[] getShortArray(final short[] container, final long offset) {
        for (int i = 0; i < container.length; i++) {
            container[i] = buffer.getShort((int) offset + (i << SHORT_OFFSET_POWER_OF_TWO));
        }
        return container;
    }

    @Override
    public long setShortArray(final short[] values, final long offset) {
        int index = (int) offset;
        for (short value : values) {
            buffer.putShort(index, value);
            index += SHORT_OFFSET;
        }
        return index;
    }

    @Override
    public int getShortArrayOffset(final int arraySize) {
        return arraySize << SHORT_OFFSET_POWER_OF_TWO;
    }

    @Override
    public char getChar(final long offset) {
        return buffer.getChar((int) offset);
    }

    @Override
    public long setChar(final char value, final long offset) {
        buffer.putChar((int) offset, value);
        return offset + CHAR_OFFSET;
    }

    @Override
    public int getCharOffset() {
        return CHAR_OFFSET;
    }

    @Override
    public char[] getCharArray(final char[] container, final long offset) {
        for (int i = 0; i < container.length; i++) {
            container[i] = buffer.getChar((int) offset + (i << CHAR_OFFSET_POWER_OF_TWO));
        }
        return container;
    }

    @Override
    public long setCharArray(final char[] values, final long offset) {
        int index = (int) offset;
        for (char value : values) {
            buffer.putChar(index, value);
            index += CHAR_OFFSET;
        }
        return index;
    }

    @Override
    public int getCharArrayOffset(final int arraySize) {
        return arraySize << CHAR_OFFSET_POWER_OF_TWO;
    }

    @Override
    public int getInt(final long offset) {
        return buffer.getInt((int) offset);
    }

    @Override
    public long setInt(final int value, final long offset) {
        buffer.putInt((int) offset, value);
        return offset + INT_OFFSET;
    }

    @Override
    public int getIntOffset() {
        return INT_OFFSET;
    }

    @Override
    public int[] getIntArray(final int[] container, final long offset) {
        for (int i = 0; i < container.length; i++) {
            container[i] = buffer.getInt((int) offset + (i << INT_OFFSET_POWER_OF_TWO));
        }
        return container;
    }

    @Override
    public long setIntArray(final int[] values, final long offset) {
        int index = (int) offset;
        for (int value : values) {
            buffer.putInt(index, value);
            index += INT_OFFSET;
        }
        return index;
    }

    @Override
    public int getIntArrayOffset(final int arraySize) {
        return arraySize << INT_OFFSET_POWER_OF_TWO;
    }

    @Override
    public float getFloat(final long offset) {
        return buffer.getFloat((int) offset);
    }

    @Override
    public long setFloat(final float value, final long offset) {
        buffer.putFloat((int) offset, value);
        return offset + FLOAT_OFFSET;
    }

    @Override
    public int getFloatOffset() {
        return FLOAT_OFFSET;
    }

    @Override
    public float[] getFloatArray(final float[] container, final long offset) {
        for (int i = 0; i < container.length; i++) {
            container[i] = buffer.getFloat((int) offset + (i << FLOAT_OFFSET_POWER_OF_TWO));
        }
        return container;
    }

    @Override
    public long setFloatArray(final float[] values, final long offset) {
        int index = (int) offset;
        for (float value : values) {
            buffer.putFloat(index, value);
            index += FLOAT_OFFSET;
        }
        return index;
    }

    @Override
    public int getFloatArrayOffset(final int arraySize) {
        return arraySize << FLOAT_OFFSET_POWER_OF_TWO;
    }

    @Override
    public long getLong(final long offset) {
        return buffer.getLong((int) offset);
    }

    @Override
    public long setLong(final long value, final long offset) {
        buffer.putLong((int) offset, value);
        return offset + LONG_OFFSET;
    }

    @Override
    public int getLongOffset() {
        return LONG_OFFSET;
    }

    @Override
    public long[] getLongArray(final long[] container, final long offset) {
        for (int i = 0; i < container.length; i++) {
            container[i] = buffer.getLong((int) offset + (i << LONG_OFFSET_POWER_OF_TWO));
        }
        return container;
    }

    @Override
    public long setLongArray(final long[] values, final long offset) {
        int index = (int) offset;
        for (long value : values) {
            buffer.putLong(index, value);
            index += LONG_OFFSET;
        }
        return index;
    }

    @Override
    public int getLongArrayOffset(final int arraySize) {
        return arraySize << LONG_OFFSET_POWER_OF_TWO;
    }

    @Override
    public double getDouble(final long offset) {
        return buffer.getDouble((int) offset);
    }

    @Override
    public long setDouble(final double value, final long offset) {
        buffer.putDouble((int) offset, value);
        return offset + DOUBLE_OFFSET;
    }

    @Override
    public int getDoubleOffset() {
        return DOUBLE_OFFSET;
    }

    @Override
    public double[] getDoubleArray(final double[] container, final long offset) {
        for (int i = 0; i < container.length; i++) {
            container[i] = buffer.getDouble((int) offset + (i << DOUBLE_OFFSET_POWER_OF_TWO));
        }
        return container;
    }

    @Override
    public long setDoubleArray(final double[] values, final long offset) {
        int index = (int) offset;
        for (double value : values) {
            buffer.putDouble(index, value);
            index += DOUBLE_OFFSET;
        }
        return index;
    }

    @Override
    public int getDoubleArrayOffset(final int arraySize) {
        return arraySize << DOUBLE_OFFSET_POWER_OF_TWO;
    }

    @Override
    public long capacity() {
        return buffer.capacity();
    }

    @Override
    public void freeStorage() {
        buffer.clear();
    }

    @Override
    public boolean isDirect() {
        return buffer.isDirect();
    }
}
