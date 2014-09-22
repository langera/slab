package org.langera.slab;

public interface SlabStorage {

    boolean getBoolean(long address);

    void setBoolean(boolean value, long address);

    byte getByte(long address);

    void setByte(byte value, long address);

    int getInt(long address);

    void setInt(int value, long address);

    long getLong(long address);

    void setLong(long value, long address);

    int[] getIntArray(final int[] container, long address);

    void setIntArray(int[] value, long address);
}
