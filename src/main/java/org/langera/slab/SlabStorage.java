package org.langera.slab;

public interface SlabStorage {

    boolean getBoolean(final long address);

    long setBoolean(final boolean value, final long address);

    int getBooleanOffset();

    boolean[] getBooleanArray(final boolean[] container, final long address);

    long setBooleanArray(final boolean[] value, final long address);

    int getBooleanArrayOffset(final int arraySize);

    byte getByte(final long address);

    long setByte(final byte value, final long address);

    int getByteOffset();

    byte[] getByteArray(final byte[] container, final long address);

    long setByteArray(final byte[] value, final long address);

    int getByteArrayOffset(final int arraySize);

    short getShort(final long address);

    long setShort(final short value, final long address);

    int getShortOffset();

    short[] getShortArray(final short[] container, final long address);

    long setShortArray(final short[] value, final long address);

    int getShortArrayOffset(final int arraySize);

    char getChar(final long address);

    long setChar(final char value, final long address);

    int getCharOffset();

    char[] getCharArray(final char[] container, final long address);

    long setCharArray(final char[] value, final long address);

    int getCharArrayOffset(final int arraySize);

    int getInt(final long address);

    long setInt(final int value, final long address);

    int getIntOffset();

    int[] getIntArray(final int[] container, final long address);

    long setIntArray(final int[] value, final long address);

    int getIntArrayOffset(final int arraySize);

    float getFloat(final long address);

    long setFloat(final float value, final long address);

    int getFloatOffset();

    float[] getFloatArray(final float[] container, final long address);

    long setFloatArray(final float[] value, final long address);

    int getFloatArrayOffset(final int arraySize);

    long getLong(final long address);

    long setLong(final long value, final long address);

    int getLongOffset();

    long[] getLongArray(final long[] container, final long address);

    long setLongArray(final long[] value, final long address);

    int getLongArrayOffset(final int arraySize);

    double getDouble(final long address);

    long setDouble(final double value, final long address);

    int getDoubleOffset();

    double[] getDoubleArray(final double[] container, final long address);

    long setDoubleArray(final double[] value, final long address);

    int getDoubleArrayOffset(final int arraySize);

    long capacity();

    void freeStorage();
}
