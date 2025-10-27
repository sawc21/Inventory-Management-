package com.ims.model;

public record Supplier(String id, String name, String contactEmail) {
    public Supplier {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("supplier id required");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("supplier name required");
    }
}