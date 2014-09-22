package org.langera.slab;

public class SingletonSlabFlyweightFactory<T> implements SlabFlyweightFactory<T> {

    private final SlabFlyweight<T> singleton;

    public SingletonSlabFlyweightFactory(final SlabFlyweight<T> singleton) {
        this.singleton = singleton;
    }

    @Override
    public SlabFlyweight<T> getInstance() {
        return singleton;
    }
}
