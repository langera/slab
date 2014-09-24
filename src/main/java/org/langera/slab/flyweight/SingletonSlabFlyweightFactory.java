package org.langera.slab.flyweight;

import org.langera.slab.SlabFlyweight;
import org.langera.slab.SlabFlyweightFactory;

public final class SingletonSlabFlyweightFactory<T> implements SlabFlyweightFactory<T> {

    private final SlabFlyweight<T> singleton;

    public SingletonSlabFlyweightFactory(final SlabFlyweight<T> singleton) {
        this.singleton = singleton;
    }

    @Override
    public SlabFlyweight<T> getInstance() {
        return singleton;
    }
}
