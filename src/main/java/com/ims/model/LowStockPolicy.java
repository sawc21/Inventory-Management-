package com.ims.model;

@FunctionalInterface
public interface LowStockPolicy {

    boolean isLow(Item i);

    static LowStockPolicy thtreshold(int min) {
        return new LowStockPolicy() {
            @Override
            public boolean isLow(Item i) {
                return i.quantity() <= min;
            }
        };
    }

}