package org.langera.slab

import spock.lang.Specification
import spock.lang.Subject

class SlabSpec extends Specification {

    SlabStorage storage = new SimpleStorage()
    AddressingStrategy addressingStrategy = Mock()
    CompactionStrategy compactionStrategy = Mock()
    SlabFlyweightFactory<Bean> factory
    Bean bean

    @Subject
    Slab<Bean> slab


    def setup() {
        bean = new SimpleBean([byteValue: 17, intValue: 19, longValue: 23L, intArrayValue: [29, 31, 37]])
    }

    def 'add item to slab '() {
    given:
        slab = new Slab<Bean>(storage, addressingStrategy, compactionStrategy, factory)
    when:
        long address = slab.add(bean)
    then:
        1 * addressingStrategy.getKey(_) >> 1000
        slab.size() == 1
        address == 1000
    where:
        factory << []
    }

    def 'remove item from slab'() {
    }

    def 'get item from slab'() {
    }

    def 'iterate over entire slab'() {
    }

    def 'compact a slab via strategy'() {
    }
}