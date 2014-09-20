package org.langera.slab;

public interface SlabFlyweight<S> {

    void map(S storage, long address);

    void advanceAddress();

    void dumpToStorage();

    boolean isNull();

    void setAsNull();

    long getNextFreeAddress();

    void setAsFreeAddress(long nextFreeAddress);
}
