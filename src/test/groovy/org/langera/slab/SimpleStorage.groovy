package org.langera.slab

class SimpleStorage implements SlabStorage {

    @Override
    boolean getBoolean(final long address) {
        return false
    }

    @Override
    void setBoolean(final boolean value, final long address) {

    }

    @Override
    int getBooleanOffset() {
        return 0
    }

    @Override
    byte getByte(final long address) {
        return 0
    }

    @Override
    void setByte(final byte value, final long address) {

    }

    @Override
    int getByteOffset() {
        return 0
    }

    @Override
    int getInt(final long address) {
        return 0
    }

    @Override
    void setInt(final int value, final long address) {

    }

    @Override
    int getIntOffset() {
        return 0
    }

    @Override
    long getLong(final long address) {
        return 0
    }

    @Override
    void setLong(final long value, final long address) {

    }

    @Override
    int getLongOffset() {
        return 0
    }

    @Override
    int[] getIntArray(final int[] container, final long address) {
        return new int[0]
    }

    @Override
    void setIntArray(final int[] value, final long address) {

    }

    @Override
    int getIntArrayOffset(final int arraySize) {
        return 0
    }
}
