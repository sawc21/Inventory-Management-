package com.ims.service.policies;

import com.ims.model.Item;
import com.ims.service.InventoryService;

public final class GlobalThresholdPolicy implements InventoryService.LowStockPolicy {

    private final int threshold;

    public GlobalThresholdPolicy(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("threshold must be >= 0");
        }
        this.threshold = threshold;
    }

    @Override
    public boolean isLow(Item item) {
        if (item == null) {
            return false;
        }
        return item.getQuantity() < threshold;
    }

    public int threshold() {
        return threshold;
    }

}
