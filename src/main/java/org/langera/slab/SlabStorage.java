package org.langera.slab;

public interface SlabStorage {

    boolean getBoolean(final long address);

    long setBoolean(final boolean value, final long address);

    int getBooleanOffset();

    byte getByte(final long address);

    long setByte(final byte value, final long address);

    int getByteOffset();

    int getInt(final long address);

    long setInt(final int value, final long address);

    int getIntOffset();

    long getLong(final long address);

    long setLong(final long value, final long address);

    int getLongOffset();

    int[] getIntArray(final int[] container, final long address);

    long setIntArray(final int[] value, final long address);

    int getIntArrayOffset(final int arraySize);

    long getFirstAvailableAddress();

    long capacity();

    void remove(long address, int objectSize);

    void freeStorage();
}
