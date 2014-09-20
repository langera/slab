package org.langera.slab;

public interface Slab<F extends SlabFlyweight>  extends Iterable<F> {

    long add(final F instance);

    F get(final long address);

    void remove(final long address);

    long size();
}
