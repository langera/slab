package org.langera.slab;

import java.util.List;

public class ToySlab<F extends SlabFlyweight<List>> { //implements Slab<F> {

//    private final SlabCompactionEventHandler eventHandler;
//    private final SlabFlyweightFactory<F> factory;
//    private final List storage;
//    private int freeListIndex;
//    private int size;
//
//    public ToySlab(final SlabCompactionEventHandler eventHandler,
//                   final SlabFlyweightFactory<F> factory) {
//        this.eventHandler = eventHandler;
//        this.factory = factory;
//        this.storage = new ArrayList();
//        this.freeListIndex = -1;
//        this.size = 0;
//    }
//
//    @Override
//    public void compact() {
//        for (int i = storage.size() - 1; i >= 0 && freeListIndex > -1; i--) {
//            F instance = factory.getInstance();
//            if (!instance.isNull(storage, i)) {
//                compactionMove(instance);
//            }
//        }
//    }
//
//    private long compactionMove(final F instance) {
//        long newAddress = -1;
//        try {
//            eventHandler.beforeCompactionMove(instance.getMappedAddress());
//            newAddress = add(instance);
//            remove(instance.getMappedAddress());
//        } finally {
//            eventHandler.afterCompactionMove(instance.getMappedAddress(), newAddress);
//        }
//        return newAddress;
//
//    }
//
//    @Override
//    public long add(final F instance) {
//        size++;
//        if (freeListIndex < 0) {
//            return addToLastIndex(instance);
//        } else {
//            return addToFreeEntry(instance);
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public F get(final long address) {
//        final F flyweight = factory.getInstance();
//        flyweight.map(storage, address);
//        return flyweight.isNull(storage, address) ? null : flyweight;
//    }
//
//    @Override
//    public void remove(final long address) {
//        final F flyweight = factory.getInstance();
//        if (flyweight.isNull(storage, address)) {
//            throw new ArrayIndexOutOfBoundsException("Address does not exist [ " + address + "]");
//        } else {
//            size--;
//            if (address == storage.size() - 1) {
//                flyweight.setAsNull(storage, address);
//            } else {
//                flyweight.setAsFreeAddress(storage, address, freeListIndex);
//                freeListIndex = (int) address;
//            }
//        }
//    }
//
//    @Override
//    public Iterator<F> iterator() {
//        return new ToySlabIterator(Direction.FORWARD);
//    }
//
//    @Override
//    public Iterator<F> reverseIterator() {
//        return new ToySlabIterator(Direction.BACK);
//    }
//
//    public long size() {
//        return size;
//    }
//
//    private int addToLastIndex(final F instance) {
//        final int lastIndex = storage.size();
//        instance.dumpToStorage(storage, lastIndex);
//        return lastIndex;
//    }
//
//    private int addToFreeEntry(final F instance) {
//        F current = factory.getInstance();
//        int newAddress = freeListIndex;
//        freeListIndex = (int) current.getNextFreeAddress(storage, freeListIndex);
//        instance.dumpToStorage(storage, newAddress);
//        return newAddress;
//    }
//
//    private enum Direction {
//        FORWARD {
//            @Override
//            int initialPtr(int storageSize) {
//                return 0;
//            }
//
//            @Override
//            int advancePtr(final int ptr) {
//                return ptr + 1;
//            }
//
//            @Override
//            boolean done(final int storageSize, final int ptr) {
//                return ptr < storageSize;
//            }
//        }, BACK {
//            @Override
//            int initialPtr(int storageSize) {
//                return storageSize - 1;
//            }
//
//            @Override
//            int advancePtr(final int ptr) {
//                return ptr - 1;
//            }
//
//            @Override
//            boolean done(final int storageSize, final int ptr) {
//                return ptr >= 0;
//            }
//        };
//
//        abstract int initialPtr(int storageSize);
//
//        abstract int advancePtr(int ptr);
//
//        abstract boolean done(int storageSize, int ptr);
//    }
//
//    private class ToySlabIterator implements Iterator<F> {
//
//        private int ptr;
//        private int visited = 0;
//        private final Direction direction;
//
//        public ToySlabIterator(final Direction direction) {
//            this.direction = direction;
//            ptr = direction.initialPtr(storage.size());
//        }
//
//        @Override
//        public boolean hasNext() {
//            return visited < size;
//        }
//
//        @SuppressWarnings("unchecked")
//        @Override
//        public F next() {
//            visited++;
//            F flyweight = factory.getInstance();
//            flyweight.map(storage, ptr);
//            while (direction.done(storage.size(), ptr) && flyweight.isNull(storage, ptr)) {
//                ptr = direction.advancePtr(ptr);
//                flyweight.map(storage, ptr);
//            }
//            return flyweight;
//        }
//
//        @Override
//        public void remove() {
//            throw new UnsupportedOperationException("remove from iterator unsupported");
//        }
//    }
}
