package org.langera.slab

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class SlabSpec extends Specification {

    private static final int FIXED_ARRAY_LENGTH = 3
    public static final int STORAGE_CAPACITY = 5 * 7 // 7 = Bean object size

    @Shared
    SlabFlyweightFactory<Bean> newInstanceFactory = new NewInstanceFactory()
    @Shared
    SlabFlyweightFactory<Bean> singletonFactory = new SingletonSlabFlyweightFactory<>(new SimpleBeanFlyweight(FIXED_ARRAY_LENGTH))
    AddressStrategy addressStrategy
    SlabFlyweightFactory<Bean> factory = singletonFactory
    SlabStorageFactory<SimpleStorage> storageFactory
    Bean bean

    @Subject
    Slab<Bean> slab


    def setup() {
        addressStrategy = Mock()
        bean = new SimpleBean([byteValue: 17, intValue: 19, longValue: 23L, intArrayValue: [29, 31, 37]])
        storageFactory = new SimpleStorageFactory()
        slab = new Slab<Bean>(this.storageFactory, STORAGE_CAPACITY, addressStrategy, factory)
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

    def 'get item from slab using #factory.class.simpleName'() {
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

    def 'compact item inside slab'() {
    given:
        slab = new Slab<Bean>(storageFactory, STORAGE_CAPACITY, new DirectAddressStrategy(), factory)
        Bean toMove = new SimpleBean([byteValue: 1, intValue: 1, longValue: 1L, intArrayValue: [1, 1, 1]])
        slab.add(bean)
        long keyToRemove = slab.add(bean)
        long keyToMove = slab.add(toMove)
    when:
        slab.remove(keyToRemove)
    then:
        slab.size() == 2
        slab.availableCapacity() == 3
    when:
        long newKeyForMovedItem = slab.compact(keyToMove)
    then:
        slab.size() == 2
        slab.availableCapacity() == 3
        newKeyForMovedItem == keyToRemove
        slab.get(newKeyForMovedItem).byteValue == 1
    }

    def 'compact item inside slab maps old key to new address'() {
    given:
        addressStrategy.getKey(_) >> { params -> return params[0] }
        addressStrategy.removeAddress(_) >> { params -> return params[0] }
        Bean toMove = new SimpleBean([byteValue: 1, intValue: 1, longValue: 1L, intArrayValue: [1, 1, 1]])
        slab.add(bean)
        long freeEntryKey = slab.add(bean)
        long keyToMove = slab.add(toMove)
        slab.remove(freeEntryKey)
    when:
        long newKeyForMovedItem = slab.compact(keyToMove)
    then:
        1 * addressStrategy.getAddress(keyToMove) >> keyToMove
        1 * addressStrategy.map(keyToMove, freeEntryKey) >> keyToMove
        newKeyForMovedItem == keyToMove
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

    def 'storage moved into a free list increases capacity'() {
    given:
        slab = new Slab<Bean>(storageFactory, STORAGE_CAPACITY, new DirectAddressStrategy(), factory)
    when:
        long key1 = slab.add(bean)
        long key2 = slab.add(bean)
    then:
        slab.availableCapacity() == 3
    when:
        slab.remove(key1)
    then:
        slab.availableCapacity() == 4
    }

    def 'storage removed from edge increase capacity'() {
    given:
        slab = new Slab<Bean>(storageFactory, STORAGE_CAPACITY, new DirectAddressStrategy(), factory)
    when:
        long key1 = slab.add(bean)
        long key2 = slab.add(bean)
    then:
        slab.availableCapacity() == 3
    when:
        slab.remove(key2)
    then:
        slab.availableCapacity() == 4
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
    def 'iterates over items in slab using flyweight pattern using flyweight #factory.class.simpleName'() {
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
    def 'iterates over all items in a slab with gaps using flyweight #factory.class.simpleName'() {
    given:
        slab = new Slab<Bean>(storageFactory, STORAGE_CAPACITY, new DirectAddressStrategy(), factory)
        slab.add(new SimpleBean([byteValue: 1, intValue: 1, longValue: 1L, intArrayValue: [1, 1, 1]]))
        long key = slab.add(new SimpleBean([byteValue: 2, intValue: 2, longValue: 2L, intArrayValue: [2, 2, 2]]))
        slab.add(new SimpleBean([byteValue: 3, intValue: 3, longValue: 3L, intArrayValue: [3, 3, 3]]))
        slab.remove(key)
    when:
        List values = []
        for (Bean b : slab) {
            values << b.byteValue
        }
    then:
        values == [1, 3]
    where:
        factory << [singletonFactory, newInstanceFactory]
    }

    @Unroll
    def 'iterates over all items in a slab with free entries at start using flyweight #factory.class.simpleName'() {
    given:
        slab = new Slab<Bean>(storageFactory, STORAGE_CAPACITY, new DirectAddressStrategy(), factory)
        long key = slab.add(new SimpleBean([byteValue: 1, intValue: 1, longValue: 1L, intArrayValue: [1, 1, 1]]))
        slab.add(new SimpleBean([byteValue: 2, intValue: 2, longValue: 2L, intArrayValue: [2, 2, 2]]))
        slab.add(new SimpleBean([byteValue: 3, intValue: 3, longValue: 3L, intArrayValue: [3, 3, 3]]))
        slab.remove(key)
    when:
        List values = []
        for (Bean b : slab) {
            values << b.byteValue
        }
    then:
        values == [2, 3]
    where:
        factory << [singletonFactory, newInstanceFactory]
    }

    @Unroll
    def 'iterates over all items in a truncated slab using flyweight #factory.class.simpleName'() {
    given:
        slab = new Slab<Bean>(storageFactory, STORAGE_CAPACITY, new DirectAddressStrategy(), factory)
        slab.add(new SimpleBean([byteValue: 1, intValue: 1, longValue: 1L, intArrayValue: [1, 1, 1]]))
        slab.add(new SimpleBean([byteValue: 2, intValue: 2, longValue: 2L, intArrayValue: [2, 2, 2]]))
        long key = slab.add(new SimpleBean([byteValue: 3, intValue: 3, longValue: 3L, intArrayValue: [3, 3, 3]]))
        slab.remove(key)
    when:
        List values = []
        for (Bean b : slab) {
            values << b.byteValue
        }
    then:
        values == [1, 2]
    where:
        factory << [singletonFactory, newInstanceFactory]
    }


    private static class NewInstanceFactory implements SlabFlyweightFactory<Bean> {

        @Override
        SlabFlyweight<Bean> getInstance() {
            return new SimpleBeanFlyweight(FIXED_ARRAY_LENGTH)
        }
    }
}