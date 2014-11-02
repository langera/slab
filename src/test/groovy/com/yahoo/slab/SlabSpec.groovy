package com.yahoo.slab

import com.yahoo.slab.example.*
import com.yahoo.slab.flyweight.SingletonSlabFlyweightFactory
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class SlabSpec extends Specification {

    public static final int MAX_INT = Integer.MAX_VALUE
    public static final long MAX_LONG = Long.MAX_VALUE
    private static final int FIXED_ARRAY_LENGTH = 3
    private static final int STORAGE_CAPACITY = 5 * 7
    // 7 = Bean object size = (1 + 1 + 1 + 1 + (3 * 1))

    @Shared
    SlabFlyweightFactory<Bean> newInstanceFactory = new NewInstanceFactory()
    @Shared
    SlabFlyweightFactory<Bean> singletonFactory = new SingletonSlabFlyweightFactory<>(new SimpleBeanFlyweight(FIXED_ARRAY_LENGTH))
    AddressStrategy addressStrategy
    SlabFlyweightFactory<Bean> factory = singletonFactory
    SlabStorageFactory<SimpleStorage> storageFactory
    SlabFlyweight<Byte> slabFlyweight
    SlabCompactionEventHandler compactionEventHandler
    Bean bean

    @Subject
    Slab<Bean> slab


    def setup() {
        slabFlyweight = Mock()
        addressStrategy = Mock()
        compactionEventHandler = Mock()
        bean = new SimpleBean([byteValue: 17, intValue: 19, longValue: 23L, intArrayValue: [29, 31, 37]])
        storageFactory = new SimpleStorageFactory()
        slab = new Slab<Bean>(storageFactory, STORAGE_CAPACITY, addressStrategy, factory)
    }


    def 'add item to slab'() {
    when:
        long key = slab.add(bean)
    then:
        1 * addressStrategy.getKey(_) >> 1000
        slab.size() == 1
        key == 1000
    }

    def 'remove item from slab'() {
    given:
        long address
        addressStrategy.getKey(_) >> { params -> address = params[0]; return 1000 }
        long key = slab.add(bean)
    when:
        slab.remove(key)
    then:
        1 * addressStrategy.removeAddress(key) >> address
        slab.size() == 0
    }

    def 'get item from slab'() {
    given:
        long address
        addressStrategy.getKey(_) >> { params -> address = params[0]; return 1000 }
        long key = slab.add(bean)
    when:
        Bean retrieved = slab.get(key)
    then:
        1 * addressStrategy.getAddress(key) >> address
        SlabFlyweight.isAssignableFrom(retrieved.class)
        retrieved != bean
        retrieved.byteValue == bean.byteValue
        retrieved.intValue == bean.intValue
        retrieved.longValue == bean.longValue
        retrieved.intArrayValue == bean.intArrayValue
    }

    def 'compact item maps old key to new address'() {
    given:
        slab = new Slab<Bean>(storageFactory, 2 * 7, addressStrategy, factory)
        addressStrategy.getKey(_) >> { params -> return params[0] }
        addressStrategy.removeAddress(_) >> { params -> return params[0] }
        Bean toBeCompacted = new SimpleBean([byteValue: 1, intValue: 1, longValue: 1L, intArrayValue: [1, 1, 1]])
        slab.add(bean)
        long freeEntryKey = slab.add(bean)
        long keyToBeCompacted = slab.add(toBeCompacted)
        slab.remove(freeEntryKey)
    when:
        slab.compact(compactionEventHandler)
    then:
        1 * addressStrategy.map(keyToBeCompacted, freeEntryKey) >> keyToBeCompacted
    }


    def 'compact item invokes event handler'() {
    given:
        slab = new Slab<Bean>(storageFactory, 2 * 7, addressStrategy, factory)
        addressStrategy.getKey(_) >> { params -> return params[0] }
        addressStrategy.removeAddress(_) >> { params -> return params[0] }
        Bean toBeCompacted = new SimpleBean([byteValue: 1, intValue: 1, longValue: 1L, intArrayValue: [1, 1, 1]])
        slab.add(bean)
        long freeEntryKey = slab.add(bean)
        long keyToBeCompacted = slab.add(toBeCompacted)
        slab.remove(freeEntryKey)
        addressStrategy.map(keyToBeCompacted, freeEntryKey) >> 17
    when:
        slab.compact(compactionEventHandler)
    then:
        1 * compactionEventHandler.beforeCompactionMove(keyToBeCompacted)
    then:
        1 * compactionEventHandler.afterCompactionMove(keyToBeCompacted, 17)
    then:
        1 * compactionEventHandler.beforeCompactionOfStorage()
    then:
        1 * compactionEventHandler.afterCompactionOfStorage()
    }

    @Unroll
    def 'initial available capacity is returned in units of "objectSize" for #bytesCapacity'() {
    when:
        slab = new Slab<Bean>(storageFactory, bytesCapacity, new DirectAddressStrategy(), factory)
    then:
        slab.availableCapacity() == expected
    where:
        bytesCapacity | expected
        7  | 1
        14 | 2
        28 | 4
        // 7 is size of Bean (1 + 1 + 1 + 1 + (3 * 1))
    }

    @Unroll
    def 'removed item increases capacity with chunk size of #chunkSize'() {
    given:
        slab = new Slab<Bean>(storageFactory, chunkSize, new DirectAddressStrategy(), factory)
    when:
        long key1 = slab.add(bean)
        long key2 = slab.add(bean)
    then:
        slab.availableCapacity() == expectedAfterTwoAdds
    when:
        slab.remove(key1)
    then:
        slab.availableCapacity() == expectedAfterRemove
    where:
        chunkSize | expectedAfterTwoAdds | expectedAfterRemove
        35        | 3                    | 4
        14        | 0                    | 1
        7         | 0                    | 1

    }

    def 'cannot add null to slab'() {
    when:
        slab.add(null)
    then:
        thrown IllegalArgumentException
    }

    def 'add item to a free gap in the slab'() {
    given:
        slab = new Slab<Bean>(storageFactory, STORAGE_CAPACITY, new DirectAddressStrategy(), factory)
        long key1 = slab.add(bean)
        long key2 = slab.add(bean)
        long key3 = slab.add(bean)
        slab.remove(key2)
    when:
        long newKey = slab.add(bean)
    then:
        newKey == key2
    }

    @Unroll
    def 'iterates over items in slab using flyweight pattern using #factory.class.simpleName'() {
    given:
        slab = new Slab<Bean>(storageFactory, STORAGE_CAPACITY, new DirectAddressStrategy(), factory)
        slab.add(new SimpleBean([byteValue: 1, intValue: 1, longValue: 1L, intArrayValue: [1, 1, 1]]))
        slab.add(new SimpleBean([byteValue: 2, intValue: 2, longValue: 2L, intArrayValue: [2, 2, 2]]))
        slab.add(new SimpleBean([byteValue: 3, intValue: 3, longValue: 3L, intArrayValue: [3, 3, 3]]))
    when:
        List classes = []
        List values = []
        for (Bean b : slab) {
            classes << b.class
            values << b.byteValue
        }
    then:
        classes.every { SlabFlyweight.isAssignableFrom(it) }
        values == [1, 2, 3]
    where:
        factory << [singletonFactory, newInstanceFactory]
    }

    @Unroll
    def 'iterates over items in slab using flyweight pattern using chunk of #chunkSize'() {
    given:
        slab = new Slab<Bean>(storageFactory, chunkSize, new DirectAddressStrategy(), factory)
        slab.add(new SimpleBean([byteValue: 1, intValue: 1, longValue: 1L, intArrayValue: [1, 1, 1]]))
        slab.add(new SimpleBean([byteValue: 2, intValue: 2, longValue: 2L, intArrayValue: [2, 2, 2]]))
        slab.add(new SimpleBean([byteValue: 3, intValue: 3, longValue: 3L, intArrayValue: [3, 3, 3]]))
    when:
        List values = slab.iterator().collect { it.byteValue }
        List classes = slab.iterator().collect { it.class }
    then:
        classes.every { SlabFlyweight.isAssignableFrom(it) }
        values == [1, 2, 3]
    where:
        chunkSize << [35, 14, 7]
    }

    @Unroll
    def 'iterates over all items in a slab with gaps using chunk of #chunkSize'() {
    given:
        slab = new Slab<Bean>(storageFactory, chunkSize, new DirectAddressStrategy(), factory)
        slab.add(new SimpleBean([byteValue: 1, intValue: 1, longValue: 1L, intArrayValue: [1, 1, 1]]))
        long key = slab.add(new SimpleBean([byteValue: 2, intValue: 2, longValue: 2L, intArrayValue: [2, 2, 2]]))
        slab.add(new SimpleBean([byteValue: 3, intValue: 3, longValue: 3L, intArrayValue: [3, 3, 3]]))
        slab.remove(key)
    when:
        List values = slab.iterator().collect { it.byteValue }
    then:
        values == [1, 3]
    where:
        chunkSize << [35, 14, 7]
    }

    @Unroll
    def 'iterates over all items in a slab with free entries at start using chunk of #chunkSize'() {
    given:
        slab = new Slab<Bean>(storageFactory, chunkSize, new DirectAddressStrategy(), factory)
        long key = slab.add(new SimpleBean([byteValue: 1, intValue: 1, longValue: 1L, intArrayValue: [1, 1, 1]]))
        slab.add(new SimpleBean([byteValue: 2, intValue: 2, longValue: 2L, intArrayValue: [2, 2, 2]]))
        slab.add(new SimpleBean([byteValue: 3, intValue: 3, longValue: 3L, intArrayValue: [3, 3, 3]]))
        slab.remove(key)
    when:
        List values = slab.iterator().collect { it.byteValue }
    then:
        values == [2, 3]
    where:
        chunkSize << [35, 14, 7]
    }

    @Unroll
    def 'iterates over all items in a truncated slab using chunk of #chunkSize'() {
    given:
        slab = new Slab<Bean>(storageFactory, chunkSize, new DirectAddressStrategy(), factory)
        slab.add(new SimpleBean([byteValue: 1, intValue: 1, longValue: 1L, intArrayValue: [1, 1, 1]]))
        slab.add(new SimpleBean([byteValue: 2, intValue: 2, longValue: 2L, intArrayValue: [2, 2, 2]]))
        long key = slab.add(new SimpleBean([byteValue: 3, intValue: 3, longValue: 3L, intArrayValue: [3, 3, 3]]))
        slab.remove(key)
    when:
        List values = slab.iterator().collect { it.byteValue }
    then:
        values == [1, 2]
    where:
        chunkSize << [35, 14, 7]
    }

    def 'add item to second chunk'() {
    given:
        slab = new Slab<Bean>(storageFactory, 2 * 7, new DirectAddressStrategy(), factory)
        slab.add(bean)
        slab.add(bean)
    when:
        long key = slab.add(bean)
    then:
        slab.size() == 3
        key == 14
    }

    def 'remove item from second chunk'() {
    given:
        slab = new Slab<Bean>(storageFactory, 2 * 7, new DirectAddressStrategy(), factory)
        slab.add(bean)
        slab.add(bean)
        long key = slab.add(bean)
    when:
        slab.remove(key)
    then:
        slab.size() == 2
    }

    def 'get item from second chunk'() {
    given:
        slab = new Slab<Bean>(storageFactory, 2 * 7, new DirectAddressStrategy(), factory)
        slab.add(new SimpleBean([byteValue: 1, intValue: 1, longValue: 1L, intArrayValue: [1, 1, 1]]))
        slab.add(new SimpleBean([byteValue: 2, intValue: 2, longValue: 2L, intArrayValue: [2, 2, 2]]))
        slab.add(new SimpleBean([byteValue: 3, intValue: 3, longValue: 3L, intArrayValue: [3, 3, 3]]))
        long key = slab.add(bean)
    when:
        Bean retrieved = slab.get(key)
    then:
        SlabFlyweight.isAssignableFrom(retrieved.class)
        retrieved != bean
        retrieved.byteValue == bean.byteValue
        retrieved.intValue == bean.intValue
        retrieved.longValue == bean.longValue
        retrieved.intArrayValue == bean.intArrayValue
    }

    def 'compact does nothing if there is only one chunk'() {
    given:
        slab = new Slab<Bean>(storageFactory, STORAGE_CAPACITY, new DirectAddressStrategy(), factory)
        slab.add(new SimpleBean([byteValue: 1, intValue: 1, longValue: 1L, intArrayValue: [1, 1, 1]]))
        long keyToRemove = slab.add(new SimpleBean([byteValue: 2, intValue: 2, longValue: 2L, intArrayValue: [2, 2, 2]]))
        slab.add(new SimpleBean([byteValue: 3, intValue: 3, longValue: 3L, intArrayValue: [3, 3, 3]]))
    when:
        slab.remove(keyToRemove)
    then:
        slab.size() == 2
        slab.availableCapacity() == 3
        slab.iterator().collect { it.byteValue }  == [ 1, 3 ]
    when:
        slab.compact(compactionEventHandler)
    then:
        slab.size() == 2
        slab.availableCapacity() == 3
        slab.iterator().collect { it.byteValue }  == [ 1, 3 ]
    }

    @Unroll
    def 'compact item where chunk size is #chunkSize'() {
    given:
        slab = new Slab<Bean>(storageFactory, chunkSize, new DirectAddressStrategy(), factory)
        slab.add(new SimpleBean([byteValue: 1, intValue: 1, longValue: 1L, intArrayValue: [1, 1, 1]]))
        long keyToRemove = slab.add(new SimpleBean([byteValue: 2, intValue: 2, longValue: 2L, intArrayValue: [2, 2, 2]]))
        long keyToBeCompacted = slab.add(new SimpleBean([byteValue: 3, intValue: 3, longValue: 3L, intArrayValue: [3, 3, 3]]))
    when:
        slab.remove(keyToRemove)
    then:
        slab.size() == 2
        slab.availableCapacity() == expectedAfterRemove
        slab.iterator().collect { it.byteValue }  == [ 1, 3 ]
        slab.get(keyToRemove) == null
    when:
        slab.compact(compactionEventHandler)
    then:
        slab.size() == 2
        slab.availableCapacity() == expectedAfterCompact
        slab.iterator().collect { it.byteValue }  == [ 1, 3 ]
        slab.get(keyToRemove).byteValue == 3
        1 * compactionEventHandler.beforeCompactionMove(keyToBeCompacted)
        1 * compactionEventHandler.afterCompactionMove(keyToBeCompacted, keyToRemove)
    where:
        chunkSize | expectedAfterRemove | expectedAfterCompact
        14        | 2                   | 0
        7         | 1                   | 0
    }

    def 'Throws IllegalArgumentException if chunk size exceeds storage capacity'() {
    given:
        storageFactory = Mock()
    when:
        new Slab<Bean>(storageFactory, 1000000000L, addressStrategy, factory)
    then:
        1 * storageFactory.supportsCapacity(1000000000L) >> false
        thrown IllegalArgumentException
    }

    @Unroll
    def 'Convert chunk size to nearest multiple of Bean size #objectSize for the storage type'() {
    given:
        SlabStorage storage = Mock()
        storageFactory = Mock()
        storageFactory.allocateStorage(0) >> storage
        SlabFlyweight<Byte> slabFlyweight = Mock()
        SlabFlyweightFactory<Byte> factory = Mock()
        factory.getInstance() >>  slabFlyweight
        slabFlyweight.getStoredObjectSize(storage) >> objectSize
    when:
        new Slab<Bean>(storageFactory, 1000000000L, addressStrategy, factory)
    then:
        1 * storageFactory.supportsCapacity(1000000000L) >> true
        1 * storageFactory.allocateStorage(actualChunkSize) >> storage
    where:
        actualChunkSize | objectSize
        1000000000L     | 1
        1000000000L     | 2
        999999999L      | 3
        999999993L      | 17

    }

    @Unroll
    def 'Throws IndexOutOfBoundsException if address returned from addressStrategy is #returnedAddress'() {
    when:
        slab.get(-17)
    then:
        1 * addressStrategy.getAddress(-17) >> returnedAddress
        thrown IndexOutOfBoundsException
    where:
        returnedAddress << [ -1, -35 ]
    }

    @Unroll
    def 'returns maximumCapacity in objects count for chunk size #chunkSize and objectSize #objectSize'() {
    when:
        storageFactory = Mock()
        storageFactory.supportsCapacity(_) >> true
        storageFactory.allocateStorage(_) >> new SimpleStorage(1)
        SlabFlyweightFactory<Byte> factory = Mock()
        factory.getInstance() >>  slabFlyweight
        slabFlyweight.getStoredObjectSize(_) >> objectSize
        slab = new Slab<Bean>(storageFactory, chunkSize.longValue(), addressStrategy, factory)
    then:
        slab.maximumCapacity() == maximumCapacity
        slab.chunkSizeInBytes() == actualChunkSize
    where:
        chunkSize    | objectSize | actualChunkSize | maximumCapacity
        0            | 1          | 1               | MAX_INT
        1            | 1          | 1               | MAX_INT
        10           | 1          | 10              | MAX_INT * 10L
        MAX_INT      | 1          | MAX_INT         | (MAX_INT as Long) * (MAX_INT as Long)
        MAX_LONG     | 1          | MAX_LONG        | MAX_LONG
        MAX_LONG - 1 | 1          | MAX_LONG - 1    | MAX_LONG
        0            | 2          | 2               | MAX_INT
        2            | 2          | 2               | MAX_INT
        10           | 2          | 10              | MAX_INT * 5L
        MAX_INT      | 2          | MAX_INT - 1     | ((MAX_INT - 1)/2 as Long) * (MAX_INT as Long)
        MAX_LONG     | 2          | MAX_LONG - 1    | (long) (MAX_LONG / 2)
        MAX_LONG - 1 | 2          | MAX_LONG - 1    | (long) (MAX_LONG / 2)

    }

    def 'chunk size defaults to object size if defined as smaller than objectSize'() {
    when:
        storageFactory = Mock()
        storageFactory.supportsCapacity(_) >> true
        storageFactory.allocateStorage(_) >> new SimpleStorage(1)
        SlabFlyweightFactory<Byte> factory = Mock()
        factory.getInstance() >>  slabFlyweight
        slabFlyweight.getStoredObjectSize(_) >> 2
        slab = new Slab<Bean>(storageFactory, 1, addressStrategy, factory)
    then:
        slab.chunkSizeInBytes() == 2
        slab.chunkCapacity() == 1
        slab.maximumCapacity() == MAX_INT
    }

    private static class NewInstanceFactory implements SlabFlyweightFactory<Bean> {

        @Override
        SlabFlyweight<Bean> getInstance() {
            return new SimpleBeanFlyweight(FIXED_ARRAY_LENGTH)
        }
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