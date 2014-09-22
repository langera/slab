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
    byte getByte(final long address) {
        return 0
    }

    @Override
    void setByte(final byte value, final long address) {

    }

    @Override
    int getInt(final long address) {
        return 0
    }

    @Override
    void setInt(final int value, final long address) {

    }

    @Override
    long getLong(final long address) {
        return 0
    }

    @Override
    void setLong(final long value, final long address) {

    }

    @Override
    int[] getIntArray(int[] container, final long address) {
        return container
    }

    @Override
    void setIntArray(final int[] value, final long address) {

    }
}
