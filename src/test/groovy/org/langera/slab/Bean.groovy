package org.langera.slab

public interface Bean {

    byte getByteValue()
    void setByteValue(byte value)
    int getIntValue()
    void setIntValue(int value)
    long getLongValue()
    void setLongValue(long value)
    int[] getIntArrayValue(int[] container)
    void setIntArrayValue(int[] value)
}