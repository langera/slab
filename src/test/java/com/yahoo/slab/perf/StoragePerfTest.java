package com.yahoo.slab.perf;

import com.yahoo.slab.AddressStrategy;
import com.yahoo.slab.Slab;
import com.yahoo.slab.SlabStorageFactory;
import com.yahoo.slab.flyweight.ThreadLocalSlabFlyweightFactory;

import static com.yahoo.slab.storage.Storages.StorageType.DIRECT;
import static com.yahoo.slab.storage.Storages.StorageType.HEAP;
import static com.yahoo.slab.storage.Storages.storageFactoryFor;

public class StoragePerfTest extends AbstractPerfTest {

    private static final int WARMUPS = 3;
    private static final int REPITITIONS = 5;
    private static final int NUMBER_OF_ELEMENTS = 2 * 1000 * 1000;
    private static final int CHUNK_SIZE = 500 * 1000;

    public static void main(String[] args) throws Exception {
        new StoragePerfTest(args).runPerfTest();
    }

    public StoragePerfTest(final String[] args) {
        super(args, WARMUPS, REPITITIONS);
    }

    @Override
    protected PerfTestCase initPerfTestCases(final String name) {
        switch (name) {
            case "Direct":
                return new StoragePerfTestCase(storageFactoryFor().maxCapacity(Long.MAX_VALUE).type(DIRECT).newInstance());
            case "Heap":
                return new StoragePerfTestCase(storageFactoryFor().maxCapacity(Integer.MAX_VALUE).type(HEAP).newInstance());
            case "DirectByteBuffer":
                return new StoragePerfTestCase(storageFactoryFor().maxCapacity(Integer.MAX_VALUE).type(DIRECT).usesByteBuffer().newInstance());
            case "HeapByteBuffer":
                return new StoragePerfTestCase(storageFactoryFor().maxCapacity(Integer.MAX_VALUE).type(HEAP).usesByteBuffer().newInstance());
            default:
                throw new IllegalArgumentException(name + ". Can only support [Heap, Direct, HeapByteBuffer, DirectByteBuffer]");
        }
    }

    private static final class StoragePerfTestCase implements PerfTestCase {

        private final Slab<Bean> slab;
        private final Bean bean;
        private final int objectSize;

        private StoragePerfTestCase(final SlabStorageFactory storageFactory) {
            objectSize = 1 + 4 + 8 + (8 * 3) + (2 * 5);
            slab = new Slab<>(storageFactory, CHUNK_SIZE * objectSize, new DirectAddressStrategy(),
                              new ThreadLocalSlabFlyweightFactory<>(new BeanFlyweightFactory()));
            bean = new BeanPojo((byte) 1, 0, 2.0, new long[]{ 1L, 2L, 3L }, "abcde".toCharArray());
        }

        @Override
        public void test(ResultsCollector result) {
            result.start();

            addTheElements(result);

            getTheElements(result);

            removeTheElements(result);

            addElementsToFreeEntries(result);

            getTheElements(result);
        }

        private void removeTheElements(final ResultsCollector result) {
            // remove half the elements (all even elements)
            for (int i = 0; i < NUMBER_OF_ELEMENTS; i += 2) {
                slab.remove(objectSize * i);
            }

            result.end("remove", NUMBER_OF_ELEMENTS / 2);
        }

        private void addElementsToFreeEntries(final ResultsCollector result) {
            // add elements to the free entries
            for (int i = 0; i < NUMBER_OF_ELEMENTS; i += 2) {
                int chunkIndex = i / CHUNK_SIZE;
                final int expectedIndexOfFreeEntry = (CHUNK_SIZE * (chunkIndex + 1)) - ((i % CHUNK_SIZE) + 2);
                bean.setMyUnsignedInt(expectedIndexOfFreeEntry);
                slab.add(bean);
            }

            result.end("add-to-free-entries", NUMBER_OF_ELEMENTS / 2);
            // 100 x get all elements and match expectations (getMyUnsignedInt()).
        }

        private void getTheElements(final ResultsCollector result) {
            // 100 x get all elements and match expectations (getMyUnsignedInt()).
            for (int i = 0; i < 100; i++) {
                for (int expected = 0; expected < NUMBER_OF_ELEMENTS; expected++) {
                    Bean flyweight = slab.get(objectSize * expected);
                    if (expected != flyweight.getMyUnsignedInt()) {
                        throw new IllegalStateException(
                            "Correctness issue. Expected [" + expected + "]. Got [" + flyweight.getMyUnsignedInt() + "]");
                    }
                }
            }

            result.end("get", 100 * NUMBER_OF_ELEMENTS);
        }

        private void addTheElements(final ResultsCollector result) {
            // add NUMBER_OF_ELEMENTS beans
            for (int i = 0; i < NUMBER_OF_ELEMENTS; i++) {
                bean.setMyUnsignedInt(i);
                slab.add(bean);
            }

            result.end("add", NUMBER_OF_ELEMENTS);
        }

        @Override
        public void before() {
        }

        @Override
        public void after() {
            slab.destroy();
        }
    }

    private static class DirectAddressStrategy implements AddressStrategy<Bean> {

        @Override
        public long createKey(final long address, final Bean instance) {
            return address;
        }

        @Override
        public long getAddress(final long key) {
            return key;
        }

        @Override
        public long removeAddress(final long key) {
            return key;
        }

        @Override
        public long map(final long existingKey, final long newAddress) {
            return newAddress;
        }
    }
}
