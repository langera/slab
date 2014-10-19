package com.yahoo.slab.example

import com.yahoo.slab.SlabStorage

/**
 * A trivial storage backed by an Object List for testing
 */
class SimpleStorage implements SlabStorage {

    private Object[] store

    SimpleStorage(final int capacity) {
        this.store = new Object[capacity]
    }

    @Override
    boolean getBoolean(final long address) {
        return (boolean) store[(int) address]
    }

    @Override
    long setBoolean(final boolean value, final long address) {
        store[(int) address] = value
        return address + booleanOffset
    }

    @Override
    int getBooleanOffset() {
        return 1
    }

    @Override
    byte getByte(final long address) {
        return (byte) store[(int) address]
    }

    @Override
    long setByte(final byte value, final long address) {
        store[(int) address] = value
        return address + byteOffset
    }

    @Override
    int getByteOffset() {
        return 1
    }

    @Override
    int getInt(final long address) {
        return (int) store[(int) address]
    }

    @Override
    long setInt(final int value, final long address) {
        store[(int) address] = value
        return address + intOffset
    }

    @Override
    int getIntOffset() {
        return 1
    }

    @Override
    long getLong(final long address) {
        return (long) store[(int) address]
    }

    @Override
    long setLong(final long value, final long address) {
        store[(int) address] = value
        return address + longOffset
    }

    @Override
    int getLongOffset() {
        return 1
    }

    @Override
    int[] getIntArray(final int[] container, final long address) {
        for (int i = 0; i < container.length; i++) {
            container[i] = store[(int) address + i]
        }
        return container
    }

    @Override
    long setIntArray(final int[] value, final long address) {
        for (int i = 0; i < value.length; i++) {
            store[(int) address + i] = value[i]
        }
        return address + (value.length * intOffset)
    }

    @Override
    int getIntArrayOffset(final int arraySize) {
        return arraySize
    }

    @Override
    boolean[] getBooleanArray(final boolean[] container, final long address) {
        for (int i = 0; i < container.length; i++) {
            container[i] = store[(int) address + i]
        }
        return container
    }

    @Override
    long setBooleanArray(final boolean[] value, final long address) {
        for (int i = 0; i < value.length; i++) {
            store[(int) address + i] = value[i]
        }
        return address + (value.length * booleanOffset)
    }

    @Override
    int getBooleanArrayOffset(final int arraySize) {
        return arraySize
    }

    @Override
    byte[] getByteArray(final byte[] container, final long address) {
        for (int i = 0; i < container.length; i++) {
            container[i] = store[(int) address + i]
        }
        return container
    }

    @Override
    long setByteArray(final byte[] value, final long address) {
        for (int i = 0; i < value.length; i++) {
            store[(int) address + i] = value[i]
        }
        return address + (value.length * byteOffset)
    }

    @Override
    int getByteArrayOffset(final int arraySize) {
        return arraySize
    }

    @Override
    short getShort(final long address) {
        return (short) store[(int) address]
    }

    @Override
    long setShort(final short value, final long address) {
        store[(int) address] = value
        return address + shortOffset
    }

    @Override
    int getShortOffset() {
        return 1
    }

    @Override
    short[] getShortArray(final short[] container, final long address) {
        for (int i = 0; i < container.length; i++) {
            container[i] = store[(int) address + i]
        }
        return container
    }

    @Override
    long setShortArray(final short[] value, final long address) {
        for (int i = 0; i < value.length; i++) {
            store[(int) address + i] = value[i]
        }
        return address + (value.length * shortOffset)
    }

    @Override
    int getShortArrayOffset(final int arraySize) {
        return arraySize
    }

    @Override
    char getChar(final long address) {
        return (char) store[(int) address]

    }

    @Override
    long setChar(final char value, final long address) {
        store[(int) address] = value
        return address + charOffset
    }

    @Override
    int getCharOffset() {
        return 1
    }

    @Override
    char[] getCharArray(final char[] container, final long address) {
        for (int i = 0; i < container.length; i++) {
            container[i] = store[(int) address + i]
        }
        return container
    }

    @Override
    long setCharArray(final char[] value, final long address) {
        for (int i = 0; i < value.length; i++) {
            store[(int) address + i] = value[i]
        }
        return address + (value.length * charOffset)
    }

    @Override
    int getCharArrayOffset(final int arraySize) {
        return arraySize
    }

    @Override
    float getFloat(final long address) {
        return (float) store[(int) address]

    }

    @Override
    long setFloat(final float value, final long address) {
        store[(int) address] = value
        return address + floatOffset
    }

    @Override
    int getFloatOffset() {
        return 1
    }

    @Override
    float[] getFloatArray(final float[] container, final long address) {
        for (int i = 0; i < container.length; i++) {
            container[i] = store[(int) address + i]
        }
        return container
    }

    @Override
    long setFloatArray(final float[] value, final long address) {
        for (int i = 0; i < value.length; i++) {
            store[(int) address + i] = value[i]
        }
        return address + (value.length * floatOffset)
    }

    @Override
    int getFloatArrayOffset(final int arraySize) {
        return arraySize
    }

    @Override
    long[] getLongArray(final long[] container, final long address) {
        for (int i = 0; i < container.length; i++) {
            container[i] = store[(int) address + i]
        }
        return container
    }

    @Override
    long setLongArray(final long[] value, final long address) {
        for (int i = 0; i < value.length; i++) {
            store[(int) address + i] = value[i]
        }
        return address + (value.length * longOffset)
    }

    @Override
    int getLongArrayOffset(final int arraySize) {
        return arraySize
    }

    @Override
    double getDouble(final long address) {
        return (double) store[(int) address]
    }

    @Override
    long setDouble(final double value, final long address) {
        store[(int) address] = value
        return address + doubleOffset
    }

    @Override
    int getDoubleOffset() {
        return 1
    }

    @Override
    double[] getDoubleArray(final double[] container, final long address) {
        for (int i = 0; i < container.length; i++) {
            container[i] = store[(int) address + i]
        }
        return container
    }

    @Override
    long setDoubleArray(final double[] value, final long address) {
        for (int i = 0; i < value.length; i++) {
            store[(int) address + i] = value[i]
        }
        return address + (value.length * doubleOffset)
    }

    @Override
    int getDoubleArrayOffset(final int arraySize) {
        return arraySize
    }

    @Override
    long capacity() {
        return store.length
    }

    @Override
    void freeStorage() {
        store = new Object[0];
    }

    @Override
    boolean isDirect() {
        return false
    }

    @Override
    String toString() {
        Arrays.toString(store)
    }
}
