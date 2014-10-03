package org.langera.slab.perf;

import org.langera.slab.SlabFlyweight;
import org.langera.slab.SlabFlyweightFactory;

public class BeanFlyweightFactory implements SlabFlyweightFactory<Bean> {

    @Override
    public SlabFlyweight<Bean> getInstance() {
        return new BeanFlyweight(3, 5);
    }
}
