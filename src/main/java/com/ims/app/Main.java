package com.ims.app;


import com.ims.model.Item;
import com.ims.repository.InventoryRepository;
import com.ims.io.InventoryFileStorage;
import com.ims.service.InventoryService;
import com.ims.service.InventoryServiceImpl;
import java.util.*;


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


        Scanner scnr = new Scanner(System.in);


        while (true) {


            System.out.println("\n==== Inventory Menu ====");
            System.out.println("1. Add Item");
            System.out.println("2. List All Items");
            System.out.println("3. Search Item by ID");
            System.out.println("4. Delete Item");
            System.out.println("5. Replace All Items");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");


            String choice = scnr.nextLine();


            switch (choice) {


                case "1": {
                    // Add Item
                    System.out.println("\n-- Add Item --");


                    System.out.print("Enter ID: ");
                    String id = scnr.nextLine();


                    System.out.print("Enter Name: ");
                    String name = scnr.nextLine();


                    System.out.print("Enter Quantity: ");
                    //parse convert types String -> Int
                    int qty = Integer.parseInt(scnr.nextLine());


                    System.out.print("Enter Price: ");
                    double price = Double.parseDouble(scnr.nextLine());


                    System.out.print("Enter Supplier: ");
                    String supplier = scnr.nextLine();


                    Item newItem = new Item(id, name, qty, price, supplier);


                    repo.save(newItem);  
                    System.out.println("Item saved!");
                    break;
                }


                case "2": {
                    // List All Items
                    System.out.println("\n-- All Items --");
                    List<Item> items = repo.findAll();
                    if (items.isEmpty()) {
                        System.out.println("No items in inventory.");
                    } else {
                        items.forEach(System.out::println);
                    }
                    break;
                }


                case "3": {
                    // Search By ID
                    System.out.println("\n-- Search Item --");
                    System.out.print("Enter ID: ");
                    String searchId = scnr.nextLine();


                    if (repo.existsById(searchId)) {
                        System.out.println("Item Found:");
                        System.out.println(repo.findById(searchId).get());
                    } else {
                        System.out.println("Item with ID '" + searchId + "' not found.");
                    }
                    break;
                }


                case "4": {
                    // Delete Item
                    System.out.println("\n-- Delete Item --");
                    System.out.print("Enter ID to delete: ");
                    String delId = scnr.nextLine();


                    if (repo.existsById(delId)) {
                        repo.deleteById(delId);
                        System.out.println("Item deleted!");
                    } else {
                        System.out.println("No item with that ID exists.");
                    }
                    break;
                }


                case "5": {
                    // Replace Entire Inventory
                    System.out.println("\n-- Replace All Items --");
                    System.out.println("How many items do you want to add?");
                    int count = Integer.parseInt(scnr.nextLine());


                    List<Item> newList = new ArrayList<>();


                    for (int i = 0; i < count; i++) {
                        System.out.println("\nItem #" + (i + 1));


                        System.out.print("Enter ID: ");
                        String id = scnr.nextLine();


                        System.out.print("Enter Name: ");
                        String name = scnr.nextLine();


                        System.out.print("Enter Quantity: ");
                        int qty = Integer.parseInt(scnr.nextLine());


                        System.out.print("Enter Price: ");
                        double price = Double.parseDouble(scnr.nextLine());


                        System.out.print("Enter Supplier: ");
                        String supplier = scnr.nextLine();


                        newList.add(new Item(id, name, qty, price, supplier));
                    }


                    repo.replaceAll(newList);
                    System.out.println("Inventory replaced successfully!");
                    break;
                }
                // exit program
                case "6":
                    System.out.println("Goodbye!");
                    scnr.close();
                    return;


                default:
                    System.out.println("Invalid option — try again.");
                    break;
            }  
        }
    }
}
