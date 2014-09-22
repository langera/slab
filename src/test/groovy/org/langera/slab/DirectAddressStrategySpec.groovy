package org.langera.slab

import spock.lang.Specification
import spock.lang.Subject

class DirectAddressStrategySpec extends Specification {

    @Subject
    DirectAddressStrategy strategy = new DirectAddressStrategy()

    def 'returned key is same as address'() {
    expect:
        17 == strategy.getKey(17)
    }

    def 'returned address is same as key'() {
    expect:
        19 == strategy.getAddress(19)
    }

}