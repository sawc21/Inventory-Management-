package com.ims.app;


import com.ims.model.Item;
import com.ims.repository.InventoryRepository;
import com.ims.io.InventoryFileStorage;
import com.ims.service.InventoryService;
import com.ims.service.InventoryServiceImpl;
import java.util.*;


public class Main {
	//variable initialization to use across scopes
	static String id, name, supplier;
	static double price;
	static int quantity, count;
	static boolean loopRunning = true;
	
    public static void main(String[] args) throws InputMismatchException, InterruptedException {


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

        //continuously loop until user decides to exit
        while (true) {

            System.out.println("\n==== Inventory Menu ====");
            System.out.println("1. Add Item");
            System.out.println("2. List All Items");
            System.out.println("3. Search Item by ID");
            System.out.println("4. Delete Item");
            System.out.println("5. Replace All Items");
			System.out.println("6. Load Inventory");
			System.out.println("7. Save Inventory");
            System.out.println("8. Exit");
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

                    //keeps asking for integer value until user provides one
                    while (loopRunning) {
                    	//error handling for different data types
                    	try {
                    		System.out.print("Enter Quantity: ");
                    		quantity = scnr.nextInt();
                    		scnr.nextLine(); //flush newline from scanner, prevents skipping input
                    		loopRunning = false; // end of continuous prompting if success
                    		//catch newline character
                    		scnr.nextLine();
                    	}
                    	catch (InputMismatchException e) {
                    		System.out.println("Quantity must be an integer value. Please try again.");
                    		scnr.nextLine(); // clears bad token and new line character
                    	}
                    }
                    
                    //resets loop running back to true for use in price loop
                    loopRunning = true;

                    //keeps asking for double value until user provides one
                    while (loopRunning) {
                    	//error handling for different data types
                    	try {
                    		System.out.print("Enter Price: ");
                    		price = scnr.nextDouble();
                    		loopRunning = false;
                    		//catch newline character
                    		scnr.nextLine();
                    	}
                    	catch (InputMismatchException e) {
                    		System.out.println("Price must be a double value. Please try again.");
                    		scnr.nextLine(); // clears bad token and new line character
                    	}
                    }


                    System.out.print("Enter Supplier: ");
                    String supplier = scnr.nextLine();


                    Item newItem = new Item(id, name, quantity, price, supplier);


                    repo.save(newItem);  
                    System.out.println("Item saved!");
                    Thread.sleep(2000);
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
                    Thread.sleep(1000);
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
                    Thread.sleep(1000);
                    break;
                }


                case "5": {
                    // Replace Entire Inventory
                    System.out.println("\n-- Replace All Items --");
                    System.out.println("How many items do you want to add?");
                    
                    //keep prompting user to enter an integer, otherwise display error
                    while (loopRunning) {
                    	try {
                    		count = scnr.nextInt();
                    		scnr.nextLine(); //catch newline character
                    		loopRunning = false; // end of loop if success and no errors
                    	}
                    	catch (InputMismatchException e) {
                    		System.out.println("Item count must be an integer value. Please try again.");
                    		scnr.nextLine(); /* clears bad token and new line character, prevents
                    		error overflow */
                    	}
                    }


                    List<Item> newList = new ArrayList<>();


                    for (int i = 0; i < count; i++) {
                        System.out.println("\nItem #" + (i + 1));


                        System.out.print("Enter ID: ");
                        String id = scnr.nextLine();


                        System.out.print("Enter Name: ");
                        String name = scnr.nextLine();
                        
                      //keeps asking for integer value until user provides one
                        while (loopRunning) {
                        	//error handling for different data types
                        	try {
                        		System.out.print("Enter Quantity: ");
                        		quantity = scnr.nextInt();
                        		loopRunning = false;
                        		//catch newline character
                        		scnr.nextLine();
                        	}
                        	catch (InputMismatchException e) {
                        		System.out.println("Quantity must be an integer value. Please try again.");
                        		scnr.nextLine(); // clears bad token and new line character
                        	}
                        }
                        
                        //resets loop running back to true for use in price loop
                        loopRunning = true;

                        //keeps asking for double value until user provides one
                        while (loopRunning) {
                        	//error handling for different data types
                        	try {
                        		System.out.print("Enter Price: ");
                        		price = scnr.nextDouble();
                        		loopRunning = false;
                        		//catch newline character
                        		scnr.nextLine();
                        	}
                        	catch (InputMismatchException e) {
                        		System.out.println("Price must be a double value. Please try again.");
                        		scnr.nextLine(); // clears bad token and new line character
                        	}
                        }
                        
                        System.out.print("Enter Supplier: ");
                        supplier = scnr.nextLine();


                        newList.add(new Item(id, name, quantity, price, supplier));
                    }


                    repo.replaceAll(newList);
                    System.out.println("Inventory replaced successfully!");
                    break;
                }
				//load an existing inventory file
				case 6:
					
					break;
				//save inventory to a file
				case 7:
					
					break;
                // exit program
                case "8":
                    System.out.println("Goodbye!");
                    scnr.close();
                    return;


                default:
                    System.out.println("Invalid option — try again.");
                    Thread.sleep(1000);
                    break;
            }  
        }
    }
}
