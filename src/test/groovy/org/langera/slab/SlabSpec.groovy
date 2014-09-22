package org.langera.slab

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class SlabSpec extends Specification {

    private static final int FIXED_ARRAY_LENGTH = 3

    @Shared
    SlabFlyweightFactory<Bean> newInstanceFactory = new NewInstanceFactory()
    @Shared
    SlabFlyweightFactory<Bean> singletonFactory = new SingletonSlabFlyweightFactory<>(new SimpleBeanFlyweight(FIXED_ARRAY_LENGTH))
    SlabStorage storage
    AddressStrategy addressStrategy
    CompactionStrategy compactionStrategy
    SlabFlyweightFactory<Bean> factory = singletonFactory
    Bean bean

    @Subject
    Slab<Bean> slab


    def setup() {
        storage = new SimpleStorage()
        addressStrategy = Mock()
        compactionStrategy = Mock()
        bean = new SimpleBean([byteValue: 17, intValue: 19, longValue: 23L, intArrayValue: [29, 31, 37]])
    }


    def 'add item to slab'() {
    given:
        slab = new Slab<Bean>(storage, addressStrategy, compactionStrategy, factory)
    when:
        long key = slab.add(bean)
    then:
        1 * addressStrategy.getKey(_) >> 1000
        slab.size() == 1
        key == 1000
    }

    def 'remove item from slab'() {
    given:
        slab = new Slab<Bean>(storage, addressStrategy, compactionStrategy, factory)
        long address
        addressStrategy.getKey(_) >> { params -> address = params[0]; return 1000 }
        long key = slab.add(bean)
    when:
        slab.remove(key)
    then:
        1 * addressStrategy.getAddress(key) >> address
        slab.size() == 0
    }

    def 'get item from slab using #factory.class.simpleName'() {
    given:
        slab = new Slab<Bean>(storage, addressStrategy, compactionStrategy, factory)
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

    def 'iterate over entire slab'() {
    }

    @Unroll
    def 'initial available capacity matches size of #slabStorage'() {
    when:
        slab = new Slab<Bean>(slabStorage, new DirectAddressStrategy(), compactionStrategy, factory)
    then:
        slab.availableCapacity() == expected
    where:
        slabStorage                                          | expected
        new SimpleStorage([])                                | 0
        new SimpleStorage([0,1,2,3,4,5,6,7,8,9,10,11,12,13]) | 2       // 7 is size of Bean (1 + 1 + 1 + 1 + (3 * 1))
    }

    def 'storage moved into a free list increases capacity'() {
    given:
        slab = new Slab<Bean>(storage, new DirectAddressStrategy(), compactionStrategy, factory)
        long key1 = slab.add(bean)
        long key2 = slab.add(bean)
    when:
        slab.remove(key1)
    then:
        slab.availableCapacity() == 1
    }

    def 'storage removed from edge does not increase capacity'() {
    given:
        slab = new Slab<Bean>(storage, new DirectAddressStrategy(), compactionStrategy, factory)
        long key1 = slab.add(bean)
        long key2 = slab.add(bean)
    when:
        slab.remove(key2)
    then:
        slab.availableCapacity() == 0
    }

    def 'cannot add null to slab'() {
    given:
        slab = new Slab<Bean>(storage, addressStrategy, compactionStrategy, factory)
    when:
        slab.add(null)
    then:
        thrown IllegalArgumentException
    }

    def 'add item to a free gap in the slab'() {
    given:
        slab = new Slab<Bean>(storage, new DirectAddressStrategy(), compactionStrategy, factory)
        long key1 = slab.add(bean)
        long key2 = slab.add(bean)
        long key3 = slab.add(bean)
        slab.remove(key2)
    when:
        long newKey = slab.add(bean)
    then:
        newKey == key2
    }

    def 'compact a slab via strategy'() {
    }

    private static class NewInstanceFactory implements SlabFlyweightFactory<Bean> {

        @Override
        SlabFlyweight<Bean> getInstance() {
            return new SimpleBeanFlyweight(FIXED_ARRAY_LENGTH)
        }
    }
}