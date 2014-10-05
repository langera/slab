package com.yahoo.slab.stub

class SimpleBean implements Bean {

    byte byteValue
    int intValue
    long longValue
    int[] intArrayValue

    @Override
    int[] getIntArrayValue(final int[] container) {
        if (container == null || container.length != intArrayValue.length) {
            return intArrayValue
        }
        else {
            System.arraycopy(intArrayValue, 0, container, 0, intArrayValue.length)
            return container
        }
    }
}
