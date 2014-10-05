package com.yahoo.slab.storage;

import com.yahoo.slab.SlabStorageFactory;

import java.nio.ByteBuffer;

public class Storages {

    public enum StorageType {

        DIRECT, HEAP
    }

    public static Builder storageFactoryFor() {
        return new Builder();
    }

    public static class Builder {

        private long maxCapacity = Long.MAX_VALUE;
        private StorageType type = StorageType.DIRECT;
        private boolean usesByteBuffer = false;


        public SlabStorageFactory newInstance() {
            if (maxCapacity > Integer.MAX_VALUE) {
                if (type == StorageType.HEAP || usesByteBuffer) {
                    throw new IllegalArgumentException(
                    "Cannot create storage factory for max capacity["+maxCapacity+"], type ["+type+"], usesByteBuffer ["+usesByteBuffer+"]");
                }
                return DirectMemoryStorageFactory.FACTORY;
            }
            if (type.equals(StorageType.HEAP)) {
                return usesByteBuffer ? HeapByteBufferStorageFactory.FACTORY : UnsafeByteArrayStorageFactory.FACTORY;
            }
            return usesByteBuffer ? DirectByteBufferStorageFactory.FACTORY : DirectMemoryStorageFactory.FACTORY;
        }

        public Builder maxCapacity(final long maxCapacity) {
            this.maxCapacity = maxCapacity;
            return this;
        }

        public Builder type(final StorageType type) {
            this.type = type;
            return this;
        }

        public Builder usesByteBuffer() {
            this.usesByteBuffer = true;
            return this;
        }
    }

    private static final class DirectMemoryStorageFactory implements SlabStorageFactory<DirectMemoryStorage> {

        private static final DirectMemoryStorageFactory FACTORY = new DirectMemoryStorageFactory();

        @Override
        public DirectMemoryStorage allocateStorage(final long capacity) {
            return new DirectMemoryStorage(capacity);
        }

        @Override
        public boolean supportsCapacity(final long capacity) {
            return true;
        }
    }

    private static final class UnsafeByteArrayStorageFactory implements SlabStorageFactory<UnsafeByteArrayStorage> {

        private static final UnsafeByteArrayStorageFactory FACTORY = new UnsafeByteArrayStorageFactory();

        @Override
        public UnsafeByteArrayStorage allocateStorage(final long capacity) {
            return new UnsafeByteArrayStorage((int) capacity);
        }

        @Override
        public boolean supportsCapacity(final long capacity) {
            return capacity <= Integer.MAX_VALUE;
        }
    }

    private static final class HeapByteBufferStorageFactory implements SlabStorageFactory<ByteBufferStorage> {

        private static final HeapByteBufferStorageFactory FACTORY = new HeapByteBufferStorageFactory();

        @Override
        public ByteBufferStorage allocateStorage(final long capacity) {
            return new ByteBufferStorage((ByteBuffer.allocate((int) capacity)));
        }

        @Override
        public boolean supportsCapacity(final long capacity) {
            return capacity <= Integer.MAX_VALUE;
        }
    }

    private static final class DirectByteBufferStorageFactory implements SlabStorageFactory<ByteBufferStorage> {

        private static final DirectByteBufferStorageFactory FACTORY = new DirectByteBufferStorageFactory();

        @Override
        public ByteBufferStorage allocateStorage(final long capacity) {
            return new ByteBufferStorage((ByteBuffer.allocateDirect((int) capacity)));
        }

        @Override
        public boolean supportsCapacity(final long capacity) {
            return capacity <= Integer.MAX_VALUE;
        }
    }
}
