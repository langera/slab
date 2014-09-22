package org.langera.slab;

public interface SlabStorage {

    boolean getBoolean(final long address);

    void setBoolean(final boolean value, final long address);

    int getBooleanOffset();

    byte getByte(final long address);

    void setByte(final byte value, final long address);

    int getByteOffset();

    int getInt(final long address);

    void setInt(final int value, final long address);

    int getIntOffset();

    long getLong(final long address);

    void setLong(final long value, final long address);

    int getLongOffset();

    int[] getIntArray(final int[] container, final long address);

    void setIntArray(final int[] value, final long address);

    int getIntArrayOffset(final int arraySize);

}
