package com.yahoo.slab.flyweight

import com.yahoo.slab.SlabFlyweight
import com.yahoo.slab.SlabFlyweightFactory
import spock.lang.Specification
import spock.lang.Subject

import java.util.concurrent.CountDownLatch

import static java.util.concurrent.TimeUnit.SECONDS

class ThreadLocalSlabFlyweightFactorySpec extends Specification {

    SlabFlyweightFactory otherFactory = Mock()
    @Subject
    ThreadLocalSlabFlyweightFactory factory = new ThreadLocalSlabFlyweightFactory(otherFactory)

    def setup() {
        otherFactory.getInstance() >>> [ Mock(SlabFlyweight), Mock(SlabFlyweight) ]
    }

    def 'factory returns same instance for same thread'() {
    when:
        def instance1 = factory.getInstance()
        def instance2 = factory.getInstance()
    then:
        instance1.is(instance2)
    }

    def 'factory returns different instances for different threads'() {
    given:
        CountDownLatch latch = new CountDownLatch(1)
    when:
        def instance1 = factory.getInstance()
        def instance2 = null
        Thread.start { instance2 = factory.getInstance(); latch.countDown() }

        latch.await(1, SECONDS)
    then:
        !instance1.is(instance2)
    }
}
