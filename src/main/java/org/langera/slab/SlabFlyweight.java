package org.langera.slab;

public interface SlabFlyweight<T> {

    void map(final SlabStorage storage, final long address);

    void mapAddress(final long address);

    long getMappedAddress();

    void dumpToStorage(final T bean, final SlabStorage storage, final long address);

    boolean isNull(final SlabStorage storage, final long address);

    long getNextFreeAddress(final SlabStorage storage, final long address);

    void setAsFreeAddress(final SlabStorage storage, final long address, final long nextFreeAddress);

    void setAsNull(final SlabStorage storage, final long address);

    int getStoredObjectSize();
}
