package org.langera.slab

import static java.lang.Math.max

/**
 * A trivial storage backed by an Object List for testing
 */
class SimpleStorage implements SlabStorage {

    private int ptr = 0;
    private Object[] store

    SimpleStorage(final int capacity) {
        this.store = new Object[capacity]
    }

    @Override
    boolean getBoolean(final long address) {
        return (boolean) store[(int) address]
    }

    @Override
    long setBoolean(final boolean value, final long address) {
        store[(int) address] = value
        final long nextAddress = address + booleanOffset
        ptr = max(ptr, nextAddress)
        return nextAddress
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
        final long nextAddress = address + byteOffset
        ptr = max(ptr, nextAddress)
        return nextAddress
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
        final long nextAddress = address + intOffset
        ptr = max(ptr, nextAddress)
        return nextAddress
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
        final long nextAddress = address + longOffset
        ptr = max(ptr, nextAddress)
        return nextAddress
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
        final long nextAddress = address + (value.length * intOffset)
        ptr = max(ptr, nextAddress)
        return nextAddress
    }

    @Override
    int getIntArrayOffset(final int arraySize) {
        return arraySize
    }

    @Override
    long getFirstAvailableAddress() {
        return ptr;
    }

    @Override
    long size() {
        return store.length
    }

    @Override
    void remove(final long address, final int objectSize) {
        for (int i = objectSize - 1; i >= 0; i--) {
            store[(int)(address + i)] = null
        }
        ptr = max(ptr, address)
    }

    @Override
    void freeStorage() {
        store = new Object[0];
    }

    @Override
    String toString() {
        Arrays.toString(store)
    }
}
