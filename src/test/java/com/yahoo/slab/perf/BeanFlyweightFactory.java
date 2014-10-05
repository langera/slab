package com.yahoo.slab.perf;

import com.yahoo.slab.SlabFlyweight;
import com.yahoo.slab.SlabFlyweightFactory;

public class BeanFlyweightFactory implements SlabFlyweightFactory<Bean> {

    @Override
    public SlabFlyweight<Bean> getInstance() {
        return new BeanFlyweight(3, 5);
    }
}
