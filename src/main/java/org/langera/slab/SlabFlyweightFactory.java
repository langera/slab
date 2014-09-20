package org.langera.slab;

public interface SlabFlyweightFactory<F extends SlabFlyweight> {

    F getInstance();
}
