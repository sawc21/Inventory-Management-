package com.ims.model;

import java.time.Instant;

/**
 * Represents a single stock change event for an item.
 * Positive quantityChange increases stock (INBOUND),
 * negative quantityChange decreases stock (OUTBOUND),
 * and ADJUSTMENT is for corrections or manual fixes.
 */
public record StockMovement(
        String itemId,
        int quantityChange,
        MovementType movementType,
        Instant timestamp,
        String reference, // e.g. "ORDER-123", "RESTOCK-BATCH-1"
        String note // free text note, can be null
) {

    // Compact canonical constructor for validation and defaults
    public StockMovement {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("itemId must not be null or blank");
        }
        if (movementType == null) {
            throw new IllegalArgumentException("movementType must not be null");
        }

        // quantityChange can be positive or negative, but not zero
        if (quantityChange == 0) {
            throw new IllegalArgumentException("quantityChange must not be zero");
        }

        // Default timestamp to "now" if caller passes null
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    /**
     * Convenience constructor that sets timestamp to now.
     */
    public StockMovement(String itemId,
            int quantityChange,
            MovementType movementType,
            String reference,
            String note) {
        this(itemId, quantityChange, movementType, Instant.now(), reference, note);
    }
}

/**
 * Type of stock movement.
 */
enum MovementType {
    INBOUND, // purchase, restock, return to stock
    OUTBOUND, // sale, shipment, consumption
    ADJUSTMENT // manual correction, inventory count correction
}
