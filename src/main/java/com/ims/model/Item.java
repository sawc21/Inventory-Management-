package com.ims.model;

import java.util.Objects;

/** Core entity: ID, name, quantity, price, supplier. */
public record Item(
        String id,
        String name,
        int quantity,
        double price,
        String supplier) {
    public Item {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(supplier, "supplier");
        if (id.isBlank())
            throw new IllegalArgumentException("id cannot be blank");
        if (name.isBlank())
            throw new IllegalArgumentException("name cannot be blank");
        if (supplier.isBlank())
            throw new IllegalArgumentException("supplier cannot be blank");
        if (quantity < 0)
            throw new IllegalArgumentException("quantity cannot be negative");
        if (price < 0)
            throw new IllegalArgumentException("price cannot be negative");
    }

    // ---- Getters ----
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getSupplier() {
        return supplier;
    }

    /**
     * Convenience for stock change. Positive to add, negative to deduct. Could
     * possibly combine next two functions, I jsut thought it would be nice to have
     * quanity as its own
     */
    public Item withAdjustedQuantity(int delta) {
        int newQty = this.quantity + delta;
        if (newQty < 0)
            throw new IllegalArgumentException("resulting quantity cannot be negative");
        return new Item(this.id, this.name, newQty, this.price, this.supplier);
    }

    public Item withUpdatedDetails(String newName, Double newPrice, String newSupplier) {
        String nn = newName != null ? newName : this.name;
        Double np = newPrice != null ? newPrice : this.price;
        String ns = newSupplier != null ? newSupplier : this.supplier;
        return new Item(this.id, nn, this.quantity, np, ns);
    }

    public Item withQuantity(int newQuantity) {
        return new Item(id, name, newQuantity, price, supplier);
    }

    // equality and debugging
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Item))
            return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Item{id='%s', name='%s', qty=%d, price=%s, supplier='%s'}",
                id, name, quantity, price, supplier);
    }
}
