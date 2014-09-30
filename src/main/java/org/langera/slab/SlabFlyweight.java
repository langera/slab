package org.langera.slab;

public interface SlabFlyweight<T> {

    void map(final SlabStorage storage, final long address);

    void mapAddress(final long address);

    long getMappedAddress();

    boolean isNull();

    void dumpToStorage(final T bean, final SlabStorage storage, final long address);

    boolean isNull(final SlabStorage storage, final long address);

    long getNextFreeAddress(final SlabStorage storage, final long address);

    void setAsFreeAddress(final SlabStorage storage, final long address, final long nextFreeAddress);

    int getStoredObjectSize(final SlabStorage storage);

    T asBean();

}
