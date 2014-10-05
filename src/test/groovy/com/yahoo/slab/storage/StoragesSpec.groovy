package com.yahoo.slab.storage

import com.yahoo.slab.SlabStorage
import spock.lang.Specification
import spock.lang.Unroll

import static com.yahoo.slab.storage.Storages.StorageType.DIRECT
import static com.yahoo.slab.storage.Storages.StorageType.HEAP

class StoragesSpec extends Specification {

    @Unroll
    def 'creates factory for #clazz.simpleName with direct == #direct'() {
    when:
        SlabStorage storage = factory.newInstance().allocateStorage(17)
    then:
        storage.isDirect() == direct
        storage.class == clazz
    where:
        factory                                                                                  | clazz                  | direct
        Storages.storageFactoryFor()                                                             | DirectMemoryStorage    | true
        Storages.storageFactoryFor().maxCapacity(Long.MAX_VALUE)                                 | DirectMemoryStorage    | true
        Storages.storageFactoryFor().maxCapacity(Long.MAX_VALUE).type(DIRECT)                    | DirectMemoryStorage    | true
        Storages.storageFactoryFor().maxCapacity(Integer.MAX_VALUE).type(HEAP)                   | UnsafeByteArrayStorage | false
        Storages.storageFactoryFor().maxCapacity(Integer.MAX_VALUE).type(DIRECT)                 | DirectMemoryStorage    | true
        Storages.storageFactoryFor().maxCapacity(Integer.MAX_VALUE).type(DIRECT).usesByteBuffer()| ByteBufferStorage      | true
        Storages.storageFactoryFor().maxCapacity(Integer.MAX_VALUE).type(HEAP).usesByteBuffer()  | ByteBufferStorage      | false
    }

    @Unroll
    def 'does not create a factory with non existent requirements'() {
    when:
        factory.newInstance()
    then:
        thrown ex
    where:
        factory                                                                   | ex
        Storages.storageFactoryFor().usesByteBuffer()                             | IllegalArgumentException
        Storages.storageFactoryFor().maxCapacity(Long.MAX_VALUE).usesByteBuffer() | IllegalArgumentException
        Storages.storageFactoryFor().type(HEAP)                                   | IllegalArgumentException
        Storages.storageFactoryFor().maxCapacity(Long.MAX_VALUE).type(HEAP)       | IllegalArgumentException
    }
}
