package com.yahoo.slab.flyweight;

import com.yahoo.slab.SlabFlyweight;
import com.yahoo.slab.SlabFlyweightFactory;

public final class ThreadLocalSlabFlyweightFactory<T> implements SlabFlyweightFactory<T> {

    private final ThreadLocal<SlabFlyweight<T>> threadLocal;

    public ThreadLocalSlabFlyweightFactory(final SlabFlyweightFactory<T> factory) {
        threadLocal = new ThreadLocal<SlabFlyweight<T>>() {

            @Override
            protected SlabFlyweight<T> initialValue() {
                return factory.getInstance();
            }
        };
    }

    @Override
    public SlabFlyweight<T> getInstance() {
        return threadLocal.get();
    }
}
