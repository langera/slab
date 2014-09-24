package org.langera.slab.flyweight

import org.langera.slab.SlabFlyweight
import spock.lang.Specification
import spock.lang.Subject

class SingletonSlabFlyweightFactorySpec extends Specification {

    SlabFlyweight instance = Mock()
    @Subject
    SingletonSlabFlyweightFactory factory = new SingletonSlabFlyweightFactory(instance)

    def 'factory returns instance it was initialized with'() {
    when:
        def instance1 = factory.getInstance()
    then:
        instance1.is(instance)
    }

    def 'factory returns same instance'() {
    when:
        def instance1 = factory.getInstance()
        def instance2 = factory.getInstance()
    then:
        instance1.is(instance2)
    }

}