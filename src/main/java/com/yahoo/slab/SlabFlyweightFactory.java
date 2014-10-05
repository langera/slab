package com.yahoo.slab;

public interface SlabFlyweightFactory<T> {

    SlabFlyweight<T> getInstance();
}
