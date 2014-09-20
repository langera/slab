package org.langera.slab;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Iterator;

public class VirtualMemoryOffHeapSlab<F extends SlabFlyweight> implements Slab<F> {

    private static final Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long add(final F flyweight) {
        // set flyweight to address either in gap list
        // or in lastIndex memory
        return 0;
    }

    @Override
    public F get(final long address) {
        // 
        return null;
    }

    @Override
    public void remove(final long address) {
        // add to gaps list
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public Iterator<F> iterator() {
        return null;
    }
}
