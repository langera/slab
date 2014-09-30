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
    def 'stores a boolean in #slabStorage.class.simpleName'() {
    expect:
        slabStorage.booleanOffset ==  slabStorage.setBoolean(true, 0)
        slabStorage.getBoolean(0)
    where:
        slabStorage << [simpleStorage, directMemoryStorage]
    }

    @Unroll
    def 'stores a byte in #slabStorage.class.simpleName'() {
    expect:
        slabStorage.byteOffset ==  slabStorage.setByte((byte) 17, 0)
        17 == slabStorage.getByte(0)
    where:
        slabStorage << [simpleStorage, directMemoryStorage]
    }

    @Unroll
    def 'stores a short in #slabStorage.class.simpleName'() {
    expect:
        slabStorage.shortOffset ==  slabStorage.setShort((short) 17, 0)
        17 == slabStorage.getShort(0)
    where:
        slabStorage << [simpleStorage, directMemoryStorage]
    }

    @Unroll
    def 'stores a char in #slabStorage.class.simpleName'() {
    given:
        char c = 'x'.charAt(0)
    expect:
        slabStorage.charOffset ==  slabStorage.setChar(c, 0)
        c == slabStorage.getChar(0)
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
    def 'stores a float in #slabStorage.class.simpleName'() {
    expect:
        slabStorage.floatOffset == slabStorage.setFloat(17.19f, 0)
        17.19f == slabStorage.getFloat(0)
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
    def 'stores a double in #slabStorage.class.simpleName'() {
    expect:
        slabStorage.doubleOffset == slabStorage.setDouble(17.19, 0)
        17.19 == slabStorage.getDouble(0)
    where:
        slabStorage << [simpleStorage, directMemoryStorage]
    }

    @Unroll
    def 'stores a boolean array in #slabStorage.class.simpleName'() {
    given:
        boolean[] a = new boolean[3]; a[0] = true; a[1] = false; a[2] = true
        boolean[] container = new boolean[3]
    expect:
        slabStorage.booleanOffset * 3 == slabStorage.setBooleanArray(a, 0)
        slabStorage.getBooleanArray(container, 0)
        [true, false, true] == container
    where:
        slabStorage << [simpleStorage, directMemoryStorage]
    }

    @Unroll
    def 'stores a byte array in #slabStorage.class.simpleName'() {
    given:
        byte[] a = new byte[3]; a[0] = 17; a[1] = 19; a[2] = 23
        byte[] container = new byte[3]
    expect:
        slabStorage.byteOffset * 3 == slabStorage.setByteArray(a, 0)
        slabStorage.getByteArray(container, 0)
        [17, 19, 23] == container
    where:
        slabStorage << [simpleStorage, directMemoryStorage]
    }

    @Unroll
    def 'stores a char array in #slabStorage.class.simpleName'() {
    given:
        char[] a = new char[3]; a[0] = 'a'.charAt(0); a[1] = 'b'.charAt(0); a[2] = 'c'.charAt(0)
        char[] container = new char[3]
    expect:
        slabStorage.charOffset * 3 == slabStorage.setCharArray(a, 0)
        slabStorage.getCharArray(container, 0)
        ['a'.charAt(0), 'b'.charAt(0), 'c'.charAt(0)] == container
    where:
        slabStorage << [simpleStorage, directMemoryStorage]
    }

    @Unroll
    def 'stores a short array in #slabStorage.class.simpleName'() {
    given:
        short[] a = new short[3]; a[0] = 17; a[1] = 19; a[2] = 23
        short[] container = new short[3]
    expect:
        slabStorage.shortOffset * 3 == slabStorage.setShortArray(a, 0)
        slabStorage.getShortArray(container, 0)
        [17, 19, 23] == container
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

    @Unroll
    def 'stores a float array in #slabStorage.class.simpleName'() {
    given:
        float[] a = new float[3]; a[0] = 17.1f; a[1] = 19.2f; a[2] = 23.3f
        float[] container = new float[3]
    expect:
        slabStorage.floatOffset * 3 == slabStorage.setFloatArray(a, 0)
        slabStorage.getFloatArray(container, 0)
        [17.1f, 19.2f, 23.3f] == container
    where:
        slabStorage << [simpleStorage, directMemoryStorage]
    }

    @Unroll
    def 'stores a long array in #slabStorage.class.simpleName'() {
    given:
        long[] a = new long[3]; a[0] = 17; a[1] = 19; a[2] = 23
        long[] container = new long[3]
    expect:
        slabStorage.longOffset * 3 == slabStorage.setLongArray(a, 0)
        slabStorage.getLongArray(container, 0)
        [17, 19, 23] == container
    where:
        slabStorage << [simpleStorage, directMemoryStorage]
    }

    @Unroll
    def 'stores a double array in #slabStorage.class.simpleName'() {
    given:
        double[] a = new double[3]; a[0] = 17.1; a[1] = 19.2; a[2] = 23.3
        double[] container = new double[3]
    expect:
        slabStorage.doubleOffset * 3 == slabStorage.setDoubleArray(a, 0)
        slabStorage.getDoubleArray(container, 0)
        [17.1, 19.2, 23.3] == container
    where:
        slabStorage << [simpleStorage, directMemoryStorage]
    }            

    def cleanupSpec() {
        simpleStorage.freeStorage()
        directMemoryStorage.freeStorage()
    }
}