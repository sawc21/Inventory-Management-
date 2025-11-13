package com.ims.repository;

import com.ims.model.Item;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {
    boolean existsById(String id);

    Optional<Item> findById(String id);

    List<Item> findAll();

    void save(Item item); // upsert by id

    void deleteById(String id);

    void replaceAll(List<Item> items); // clear then add all
}
