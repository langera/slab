package org.langera.slab

/**
 * A trivial storage backed by an Object List for testing
 */
class SimpleStorage implements SlabStorage {

    private List store

    SimpleStorage() {
        this([])
    }

    SimpleStorage(final List store) {
        this.store = store
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
    long getFirstAvailableAddress() {
        return store.size()
    }

    @Override
    long size() {
        return store.size()
    }

    @Override
    void remove(final long address, final int objectSize) {
        for (int i = objectSize - 1; i >= 0; i--) {
            store.remove((int)(address + i))
        }
    }
}
