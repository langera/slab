package com.yahoo.slab.perf;

import com.yahoo.slab.AddressStrategy;
import com.yahoo.slab.Slab;
import com.yahoo.slab.flyweight.ThreadLocalSlabFlyweightFactory;
import com.yahoo.slab.storage.Storages;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class SlabPerfTest extends AbstractPerfTest {

    private static final int WARMUPS = 3;
    private static final int REPITITIONS = 5;
    private static final int NUMBER_OF_ELEMENTS = 2 * 1000 * 1000;
    private static final int CHUNK_SIZE = 500 * 1000;

    public static void main(String[] args) throws Exception {
        new SlabPerfTest(args).runPerfTest();
    }


    public SlabPerfTest(final String[] args) {
        super(args, WARMUPS, REPITITIONS);
    }

    @Override
    protected PerfTestCase initPerfTestCases(final String name) {
        switch (name) {
            case "ArrayList":
                return new SlabPerfTestCase(new ListToSlabLikeListAdapter());
            case "Heap":
                return new SlabPerfTestCase(new SlabToListAdapter(Storages.StorageType.HEAP));
            case "OffHeap":
                return new SlabPerfTestCase(new SlabToListAdapter(Storages.StorageType.DIRECT));
            default:
                throw new IllegalArgumentException(name + ". Can only support [ArrayList, Heap, OffHeap]");
        }
    }


    private static final class SlabPerfTestCase implements PerfTestCase {

        private final Bean bean;
        private final List<Bean> list;

        private SlabPerfTestCase(final List<Bean> list) {
            this.list = list;
            bean = new BeanPojo((byte) 1, 0, 2.0, new long[]{ 1L, 2L, 3L }, "abcde".toCharArray());
        }

        @Override
        public void test(ResultsCollector resultsCollector) {
            resultsCollector.start();

            addTheElements(resultsCollector);

            getTheElements(resultsCollector);
        }

        private void getTheElements(final ResultsCollector resultsCollector) {
            for (int i = 0; i < 100; i++) {
                for (int expected = 0; expected < NUMBER_OF_ELEMENTS; expected++) {
                    getBean(expected);
                }
            }
            resultsCollector.end("get", 100 * NUMBER_OF_ELEMENTS);
        }

        private void addTheElements(final ResultsCollector resultsCollector) {
            for (int i = 0; i < NUMBER_OF_ELEMENTS; i++) {
                bean.setMyUnsignedInt(i);
                list.add(bean);
            }

            resultsCollector.end("add", NUMBER_OF_ELEMENTS);
        }

        private void getBean(final int expected) {
            Bean flyweight = list.get(expected);
            if (flyweight.getMyUnsignedInt() < 0) {
                throw new IllegalStateException(
                    "Should never happen but HotSpot does not know that.");
            }
        }

        @Override
        public void before() {
        }

        @Override
        public void after() {
            list.clear();
        }
    }

    private static class SlabToListAdapter extends AbstractList<Bean> {

        private final Slab<Bean> slab;
        private final int objectSize;

        private SlabToListAdapter(final Storages.StorageType type) {
            objectSize = 1 + 4 + 8 + (8 * 3) + (2 * 5);
            slab = new Slab<>(Storages.storageFactoryFor().maxCapacity(Integer.MAX_VALUE).type(type).newInstance(),
                              CHUNK_SIZE * objectSize,
                              new DirectAddressStrategy(),
                              new ThreadLocalSlabFlyweightFactory<>(new BeanFlyweightFactory()));
        }

        @Override
        public boolean add(final Bean bean) {
            slab.add(bean);
            return true;
        }

        @Override
        public Bean remove(final int index) {
            slab.remove(index * objectSize);
            return null;
        }

        @Override
        public Bean get(final int index) {
            return slab.get(index * objectSize);
        }

        @Override
        public int size() {
            return (int) slab.size();
        }

        @Override
        public void clear() {
            slab.destroy();
        }
    }

    private static class ListToSlabLikeListAdapter extends AbstractList<Bean> {

        private final List<Bean> realList = new ArrayList<>(CHUNK_SIZE);

        @Override
        public boolean add(final Bean bean) {
            realList.add(new BeanPojo(bean));
            return true;
        }

        @Override
        public Bean remove(final int index) {
            realList.set(index, null);
            return null;
        }

        @Override
        public Bean get(final int index) {
            return realList.get(index);
        }

        @Override
        public int size() {
            return realList.size();
        }

        @Override
        public void clear() {
            realList.clear();
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

