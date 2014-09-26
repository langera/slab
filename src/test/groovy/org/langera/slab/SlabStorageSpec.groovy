package org.langera.slab

import org.langera.slab.storage.DirectMemoryStorage
import org.langera.slab.stub.SimpleStorage
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class SlabStorageSpec extends Specification {

    @Subject SlabStorage slabStorage

    @Shared SimpleStorage simpleStorage = new SimpleStorage(10)
    @Shared DirectMemoryStorage directMemoryStorage = new DirectMemoryStorage(10)

    @Unroll
    def 'stores a byte in #slabStorage.class.simpleName'() {
    expect:
        slabStorage.byteOffset ==  slabStorage.setByte((byte) 17, 0)
        17 == slabStorage.getByte(0)
    where:
        slabStorage << [simpleStorage, directMemoryStorage]
    }

    @Unroll
    def 'stores an int in #slabStorage.class.simpleName'() {
    expect:
        slabStorage.intOffset == slabStorage.setInt(17, 0)
        17 == slabStorage.getInt(0)
    where:
        slabStorage << [simpleStorage, directMemoryStorage]
    }

    @Unroll
    def 'stores a long in #slabStorage.class.simpleName'() {
    expect:
        slabStorage.longOffset == slabStorage.setLong(17L, 0)
        17L == slabStorage.getLong(0)
    where:
        slabStorage << [simpleStorage, directMemoryStorage]
    }

    @Unroll
    def 'stores an int array in #slabStorage.class.simpleName'() {
    given:
        int[] a = new int[3]; a[0] = 17; a[1] = 19; a[2] = 23
        int[] container = new int[3]
    expect:
        slabStorage.intOffset * 3 == slabStorage.setIntArray(a, 0)
        slabStorage.getIntArray(container, 0)
        [17, 19, 23] == container
    where:
        slabStorage << [simpleStorage, directMemoryStorage]
    }

    def cleanupSpec() {
        simpleStorage.freeStorage()
        directMemoryStorage.freeStorage()
    }
}