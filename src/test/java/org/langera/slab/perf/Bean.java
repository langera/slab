package org.langera.slab.perf;

public interface Bean {

    byte getMyByte();

    void setMyByte(final byte myByte);

    int getMyUnsignedInt();

    void setMyUnsignedInt(final int myUnsignedInt);

    double getMyDouble();

    void setMyDouble(final double myDouble);

    long[] getMyLongArray();

    void setMyLongArray(final long[] myLongArray);

    char[] getMyCharArray();

    void setMyCharArray(final char[] myCharArray);
}
