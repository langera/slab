package org.langera.slab

import spock.lang.Specification
import spock.lang.Subject

class ToySlabSpec extends Specification {

    @Subject
    Slab slab = new ToySlab()

    def 'add item to slab'() {
    }

    private static class DummySlabFlyweight implements SlabFlyweight {

        private long nextFreeAddress

        @Override
        boolean isNull() {
            return false
        }

        @Override
        long getNextFreeAddress() {
            return nextFreeAddress
        }

        @Override
        void setAsFreeAddress(final long nextFreeAddress) {
            this.nextFreeAddress = nextFreeAddress
        }
    }

}