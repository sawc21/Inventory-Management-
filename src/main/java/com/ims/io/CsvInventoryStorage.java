package com.ims.io;

import com.ims.model.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

interface InventoryFileStorage {
    List<Item> loadAll(String fileName) throws IOException;

	void saveAll(List<Item> items, String fileName) throws IOException;
}

//class for loading and saving from/to csv file
public class CsvInventoryStorage implements InventoryFileStorage {
	@Override
	public List<Item> loadAll(String fileName) throws IOException {
		//create a new empty list object
        List<Item> items = new ArrayList<>();
        int lineCount = 0;
        
        //try-with-resources to auto-close the file when finished reading
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
        	//skip the header line
        	reader.readLine();
        	
        	String line;
            while ((line = reader.readLine()) != null) {
                //separates properties and add to list as new item
                String[] properties = line.split(",");
                Item item = new Item(properties[0], properties[1], Integer.parseInt(properties[2]), Double.parseDouble(properties[3]), properties[4]);
                items.add(item);
                //add to total lines read
            	lineCount++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[DEBUG] Successfully loaded" + lineCount + "items!");
        return items;
	}
	@Override
	public void saveAll(List<Item> items, String fileName) throws IOException {
		System.out.println("[DEBUG] Saving " + items.size() + " items (stubbed)");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
			writer.write("ID, Name, Quantity, Price, Supplier\n");
			for (Item item1 : items) {
    			writer.write(item1.getId() + "," + item1.getName() + "," + item1.getQuantity() + "," + item1.getPrice() + "," + item1.getSupplier());
                writer.newLine();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
		System.out.println("[DEBUG] Successfully saved all items!");
	}
}