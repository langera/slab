package org.langera.slab.perf;

import org.langera.slab.AddressStrategy;
import org.langera.slab.Slab;
import org.langera.slab.SlabStorageFactory;
import org.langera.slab.flyweight.ThreadLocalSlabFlyweightFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.langera.slab.storage.Storages.StorageType.DIRECT;
import static org.langera.slab.storage.Storages.StorageType.HEAP;
import static org.langera.slab.storage.Storages.storageFactoryFor;

public class StoragePerfTest {

    private static final int WARMUPS = 3;
    private static final int REPITITIONS = 5;
    private static final int NUMBER_OF_ELEMENTS = 1 * 1000 * 1000;
    private static final int CHUNK_SIZE = 500 * 1000;

    public static void main(String[] args) throws Exception {
        Map<String, List<Integer>> testCaseResults = new HashMap<>();
        Map<String, PerfTestCase> testCases = new HashMap<>();
        testCases.put("DirectMemoryStorage",
                      new PerfTestCase(storageFactoryFor().maxCapacity(Long.MAX_VALUE).type(DIRECT).newInstance()));
        testCases.put("UnsafeByteArrayStorage",
                      new PerfTestCase(storageFactoryFor().maxCapacity(Integer.MAX_VALUE).type(HEAP).newInstance()));
        testCases.put("DirectByteBufferStorage",
                      new PerfTestCase(storageFactoryFor().maxCapacity(Integer.MAX_VALUE).type(DIRECT).usesByteBuffer().newInstance()));
        testCases.put("HeapByteBufferStorage",
                      new PerfTestCase(storageFactoryFor().maxCapacity(Integer.MAX_VALUE).type(HEAP).usesByteBuffer().newInstance()));

        for (int i = 0; i < WARMUPS; i++) {
            runTests(testCases, null);
        }
        for (int i = 0; i < REPITITIONS; i++) {
            runTests(testCases, testCaseResults);
        }
        printResults(testCaseResults);
    }

    private static void runTests(final Map<String, PerfTestCase> testCases, final Map<String, List<Integer>> testCaseResults) throws Exception {
        for (Map.Entry<String, PerfTestCase> testCase : testCases.entrySet()) {
            System.gc();

            final String name = testCase.getKey();
            final PerfTestCase test = testCase.getValue();

            final long start = System.currentTimeMillis();
            test.run();
            final long end = System.currentTimeMillis();

            if (testCaseResults != null) {
                getResults(testCaseResults, name).add((int) (end - start));
            }
        }
    }

    private static final class PerfTestCase implements Runnable {

        private final Slab<Bean> slab;
        private final Bean bean;
        private final int objectSize;

        private PerfTestCase(final SlabStorageFactory storageFactory) {
            objectSize = 1 + 4 + 8 + (8 * 3) + (2 * 5);
            slab = new Slab<>(storageFactory, CHUNK_SIZE * objectSize, new DirectAddressStrategy(),
                              new ThreadLocalSlabFlyweightFactory<>(new BeanFlyweightFactory()));
            bean = new BeanPojo((byte) 1, 0, 2.0, new long[]{ 1L, 2L, 3L }, "abcde".toCharArray());
        }

        @Override
        public void run() {
            // add NUMBER_OF_ELEMENTS beans
            for (int i = 0; i < NUMBER_OF_ELEMENTS; i++) {
                bean.setMyUnsignedInt(i);
                slab.add(bean);
            }

            // remove half the elements (all even elements)
            for (int i = 0; i < NUMBER_OF_ELEMENTS; i += 2) {
                bean.setMyUnsignedInt(i);
                slab.remove(objectSize * i);
            }

            // add elements to the free entries
            for (int i = 0; i < NUMBER_OF_ELEMENTS; i += 2) {
                int chunkIndex = i / CHUNK_SIZE;
                final int expectedIndexOfFreeEntry = (CHUNK_SIZE * (chunkIndex + 1)) - ((i % CHUNK_SIZE) + 2);
                bean.setMyUnsignedInt(expectedIndexOfFreeEntry);
                slab.add(bean);
            }

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
        }
    }


    private static void printResults(final Map<String, List<Integer>> testCaseResults) {
        for (Map.Entry<String, List<Integer>> resultsEntry : testCaseResults.entrySet()) {
            System.out.println(resultsEntry.getKey() + " " + resultsEntry.getValue() + " msec.");
        }
    }

    private static List<Integer> getResults(final Map<String, List<Integer>> testCaseResults, final String name) {
        List<Integer> results = testCaseResults.get(name);
        if (results == null) {
            results = new ArrayList<>();
            testCaseResults.put(name, results);
        }
        return results;
    }

    private static class DirectAddressStrategy implements AddressStrategy {

        @Override
        public long getKey(final long address) {
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
