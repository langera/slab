package org.langera.slab;

import java.util.Iterator;

public class ToySlab<F extends SlabFlyweight> implements Slab<F> {

    public static final int DEFAULT_CHUNK_SIZE = 1000;

    private final SlabCompactionEventHandler eventHandler;
    private final SlabFlyweightFactory<F> factory;
    private final int chunkSize;
    private final Object[] firstChunk;
    private Object[] lastChunk;
    private final ChunkMetadata metadata;
    private long size;
    private int lastIndex;

    public ToySlab(final SlabCompactionEventHandler eventHandler,
                   final SlabFlyweightFactory<F> factory) {
        this(DEFAULT_CHUNK_SIZE, eventHandler, factory);
    }

    public ToySlab(final int chunkSize,
                   final SlabCompactionEventHandler eventHandler,
                   final SlabFlyweightFactory<F> factory) {
        this.eventHandler = eventHandler;
        this.factory = factory;
        this.chunkSize = Math.min(chunkSize, Integer.MAX_VALUE - ChunkMetadata.getObjectSize());
        this.firstChunk = new Object[this.chunkSize + ChunkMetadata.getObjectSize()];
        this.lastChunk = firstChunk;
        this.metadata = new ChunkMetadata();
        this.size = 0;
        this.lastIndex = 0;
    }

    @Override
    public long add(final F instance) {
        size++;
        metadata.setCurrentChunk(firstChunk);
        long offset = 0;
        int freeListIndex = -1;
        while (!metadata.isNull() && ((freeListIndex = metadata.getFreeListIndex()) < 0)) {
            metadata.setCurrentChunk(metadata.getNextChunk());
            offset += chunkSize;
        }
        if (metadata.isNull()) {
            return offset + addToLastChunk(instance, freeListIndex);
        } else {
            int index = fillInChunk(metadata.currentChunk, instance, freeListIndex);
            return offset + index;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public F get(final long address) {
        metadata.setCurrentChunk(firstChunk);
        long offset = address;
        while (offset > chunkSize) {
            metadata.setCurrentChunk(metadata.getNextChunk());
            offset -= chunkSize;
        }
        Object[] currentChunk = metadata.currentChunk;
        final F flyweight = factory.getInstance();
        flyweight.map(currentChunk, offset);
        return flyweight.isNull() ? null : flyweight;
    }

    @Override
    public void remove(final long address) {
        metadata.setCurrentChunk(firstChunk);
        long offset = 0;
        while (!metadata.isNull() && offset > chunkSize) {
            metadata.setCurrentChunk(metadata.getNextChunk());
            offset += chunkSize;
        }
        if (metadata.isNull()) {
            throw new ArrayIndexOutOfBoundsException("Address does not exist [ " + address + "]");
        } else {
            size--;
            final F flyweight = factory.getInstance();
            flyweight.map(metadata.currentChunk, offset);
            flyweight.setAsNull();
            if (metadata.currentChunk == lastChunk && lastIndex == offset + 1) {
                lastIndex--;
            } else {
                final int freeListIndex = metadata.getFreeListIndex();
                metadata.setFreeListIndex((int) offset);
                flyweight.setAsFreeAddress(freeListIndex);
            }
        }
    }

    @Override
    public Iterator<F> iterator() {
        return new ToySlabIterator();
    }

    public long size() {
        return size;
    }

    private int addToLastChunk(final F instance, final int freeListIndex) {
        if (freeListIndex < 0) {
            if (lastIndex == chunkSize) {
                return addToNewChunk(instance);
            } else {
                return addToLastIndex(instance);
            }
        } else {
            return addToFreeEntry(lastChunk, instance, freeListIndex);
        }
    }

    private int addToLastIndex(final F instance) {
        lastChunk[lastIndex++] = instance;
        return lastIndex - 1;
    }

    private int addToFreeEntry(final Object[] chunk, final F instance, final int freeListIndex) {
        metadata.setCurrentChunk(chunk);
        metadata.setFreeListIndex(((Integer) lastChunk[freeListIndex]).intValue());
        metadata.setFreeEntriesSize(metadata.getFreeEntriesSize() - 1);
        lastChunk[freeListIndex] = instance;
        return freeListIndex;
    }

    private int addToNewChunk(final F instance) {
        Object[] newChunk = new Object[chunkSize + ChunkMetadata.getObjectSize()];
        lastChunk = newChunk;
        lastIndex = 1;
        metadata.setNextChunk(newChunk);
        metadata.setCurrentChunk(newChunk);
        metadata.setFreeEntriesSize(0);
        metadata.setFreeListIndex(-1);
        newChunk[0] = instance;
        return chunkSize;
    }

    private int fillInChunk(final Object[] chunk, final F flyweight, final int freeListIndex) {
        metadata.setCurrentChunk(chunk);
        int nextFreeListIndex = ((Integer) chunk[freeListIndex]).intValue();
        chunk[freeListIndex] = flyweight;
        metadata.setFreeEntriesSize(metadata.getFreeEntriesSize() - 1);
        metadata.setFreeListIndex(nextFreeListIndex);
        return freeListIndex;
    }

    private long compact(final long address) {
/// TODO
return -1;
    }

    private class ToySlabIterator implements Iterator<F> {

        private long switchedArraysAt = 0;
        private long ptr = 0;
        private Object[] currentArray = firstChunk;

        @Override
        public boolean hasNext() {
            advancePointerToNextItem();
            return ptr < lastIndex;
        }

        @SuppressWarnings("unchecked")
        @Override
        public F next() {
            advancePointerToNextItem();
            return (F) currentArray[(int) (ptr++ % chunkSize)];
        }

        private void advancePointerToNextItem() {
            if (switchedArraysAt < ptr && ptr % chunkSize == 0) {
                currentArray = (Object[]) currentArray[chunkSize];
                switchedArraysAt = ptr;
                if (currentArray == null) {
                    ptr = lastIndex;
                    return;
                }
            }
            F flyweight = factory.getInstance();
            flyweight.map(currentArray, ptr % chunkSize);
            while (ptr < lastIndex && flyweight.isNull()) {
                ptr++;
                flyweight.advanceAddress();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove from iterator unsupported");
        }
    }

    private static class ChunkMetadata {

        private Object[] currentChunk;

        public int getFreeEntriesSize() {
            return ((Integer) currentChunk[currentChunk.length - 3]).intValue();
        }

        public int getFreeListIndex() {
            return ((Integer) currentChunk[currentChunk.length - 2]).intValue();
        }

        public Object[] getNextChunk() {
            return (Object[]) currentChunk[currentChunk.length - 1];
        }

        public boolean isNull() {
            return currentChunk == null;
        }

        public void setCurrentChunk(final Object[] currentArray) {
            this.currentChunk = currentArray;
        }

        public void setFreeEntriesSize(int size) {
            currentChunk[currentChunk.length - 3] = Integer.valueOf(size);
        }

        public void setFreeListIndex(int index) {
            currentChunk[currentChunk.length - 2] = Integer.valueOf(index);
        }

        public void setNextChunk(Object[] nextChunk) {
            currentChunk[currentChunk.length - 1] = nextChunk;
        }

        public static int getObjectSize() {
            return 3;
        }
    }
}
