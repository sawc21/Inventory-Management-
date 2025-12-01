package com.ims.service;

import com.ims.model.Item;
import com.ims.model.StockMovement;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface InventoryService {
    Result<Item> addItem(Item item);

    Result<Item> updateItem(Item item);

    Result<Void> deleteById(String id);

    Optional<Item> getById(String id);

    List<Item> listAll();

    /**
     * Convenience helper for concurrency simulation and reports.
     * Returns all known item ids.
     */
    List<String> getAllItemIds();

    List<Item> searchByName(String query);

    // Stock adjustments

    /**
     * Simple numeric stock adjustment.
     */
    Result<Item> adjustStock(String id, int delta);

    /**
     * Apply a domain-level stock movement (inbound, outbound, adjustment).
     * This is what your RealTimeStockUpdater will call.
     */
    Result<Item> applyMovement(StockMovement movement);

    // Low stock report
    List<Item> lowStock();

    // Persistence
    Result<Void> saveAll(List<Item> items, String fileName) throws IOException;

    Result<Void> loadAll(String fileName) throws IOException;

    /**
     * Encapsulates low-stock threshold logic so it can be swapped in tests.
     */
    interface LowStockPolicy {
        boolean isLow(Item item);
    }

    /**
     * Simple result wrapper used across the service to report success or failure
     * with an optional value.
     */
    final class Result<T> {
        private final boolean ok;
        private final String message;
        private final T value;

        private Result(boolean ok, String message, T value) {
            this.ok = ok;
            this.message = message;
            this.value = value;
        }

        public static <T> Result<T> ok(T value) {
            return new Result<>(true, null, value);
        }

        public static <T> Result<T> ok() {
            return new Result<>(true, null, null);
        }

        public static <T> Result<T> fail(String message) {
            return new Result<>(false, message, null);
        }

        public boolean isOk() {
            return ok;
        }

        public String message() {
            return message;
        }

        public T value() {
            return value;
        }
    }
}
