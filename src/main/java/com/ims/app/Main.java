package com.ims.app;

import com.ims.model.Item;
import com.ims.repository.InventoryRepository;
import com.ims.io.InventoryFileStorage;
import com.ims.service.InventoryService;
import com.ims.service.InventoryServiceImpl;

public class Main {
    public static void main(String[] args) {

        // Temporary stub repository
        InventoryRepository repo = new InventoryRepository() {

            private final java.util.Map<String, Item> data = new java.util.HashMap<>();

            @Override
            public boolean existsById(String id) {
                return data.containsKey(id);
            }

            @Override
            public java.util.Optional<Item> findById(String id) {
                return java.util.Optional.ofNullable(data.get(id));
            }

            @Override
            public java.util.List<Item> findAll() {
                return new java.util.ArrayList<>(data.values());
            }

            @Override
            public void save(Item item) {
                data.put(item.id(), item);
            }

            @Override
            public void deleteById(String id) {
                data.remove(id);
            }

            @Override
            public void replaceAll(java.util.List<Item> items) {
                data.clear();
                for (Item it : items)
                    data.put(it.id(), it);
            }
        };

        // Temporary stub file storage
        InventoryFileStorage storage = new InventoryFileStorage() {
            @Override
            public java.util.List<Item> loadAll() {
                System.out.println("[DEBUG] Loading items (stubbed)");
                return java.util.List.of();
            }

            @Override
            public void saveAll(java.util.List<Item> items) {
                System.out.println("[DEBUG] Saving " + items.size() + " items (stubbed)");
            }
        };

        // Simple low-stock rule: quantity < 10
        InventoryService.LowStockPolicy policy = item -> item != null && item.getQuantity() < 10;

        // Build service
        InventoryService service = new InventoryServiceImpl(repo, storage, policy);

        // Add a couple of items
        service.addItem(new Item("A1", "Dog Chew", 5, 9.99, "Whitetail Naturals"));
        service.addItem(new Item("A2", "Elk Antler", 15, 14.99, "Hornix"));

        // Print results
        System.out.println("=== Inventory Items ===");
        service.listAll().forEach(System.out::println);

        System.out.println("\n=== Low Stock Items ===");
        service.lowStock().forEach(System.out::println);
    }
}