package com.yahoo.slab.perf;

import java.util.Arrays;

public class BeanPojo implements Bean {

    private byte myByte;
    private int myUnsignedInt;
    private double myDouble;
    private long[] myLongArray;
    private char[] myCharArray;

    public BeanPojo(final byte myByte,
                    final int myUnsignedInt,
                    final double myDouble,
                    final long[] myLongArray,
                    final char[] myCharArray) {
        this.myByte = myByte;
        this.myUnsignedInt = myUnsignedInt;
        this.myDouble = myDouble;
        this.myLongArray = myLongArray;
        this.myCharArray = myCharArray;
    }

    public BeanPojo(final Bean other) {
        this.myByte = other.getMyByte();
        this.myUnsignedInt = other.getMyUnsignedInt();
        this.myDouble = other.getMyDouble();
        final long[] otherLongArray = other.getMyLongArray();
        this.myLongArray = Arrays.copyOf(otherLongArray, otherLongArray.length);
        final char[] otherCharArray = other.getMyCharArray();
        this.myCharArray = Arrays.copyOf(otherCharArray, otherCharArray.length);
    }

    public byte getMyByte() {
        return myByte;
    }

    public void setMyByte(final byte myByte) {
        this.myByte = myByte;
    }

    public int getMyUnsignedInt() {
        return myUnsignedInt;
    }

    public void setMyUnsignedInt(final int myUnsignedInt) {
        this.myUnsignedInt = myUnsignedInt;
    }

    public double getMyDouble() {
        return myDouble;
    }

    public void setMyDouble(final double myDouble) {
        this.myDouble = myDouble;
    }

    public long[] getMyLongArray() {
        return myLongArray;
    }

    public void setMyLongArray(final long[] myLongArray) {
        this.myLongArray = myLongArray;
    }

    public char[] getMyCharArray() {
        return myCharArray;
    }

    public void setMyCharArray(final char[] myCharArray) {
        this.myCharArray = myCharArray;
    }
}
