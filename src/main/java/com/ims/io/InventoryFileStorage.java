package com.ims.io;

import com.ims.model.Item;

import java.io.IOException;
import java.util.List;
import java.io.*;
import java.util.Scanner;

public interface InventoryFileStorage {
    List<Item> loadAll() throws IOException;

    void saveAll(List<Item> items) throws IOException;
}

//class for loading and saving from/to csv file
public class CsvInventoryStorage implements InventoryFileStorage {
    public List<Item> loadAll(String fileName) throws IOException {
        //create a new empty list object
        List<Item> items = new ArrayList<>();
        //try-with-resources to auto-close the file when finished reading
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ((line = reader.readLine()) != null) {
                //separates properties and add to list as new item
                String[] properties = line.split(",");
                Item item = new Item(properties[0], properties[1], properties[2], properties[3], properties[4]);
                items.add(item);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }
    public void saveAll(List<Item> items, fileName) throws IOException {
        for (Item item : items) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                for (Item item : items) {
                    writer.write("ID, Name, Quantity, Price, Supplier\n");
                    writer.write(item.getId() + "," + item.getName() + "," + item.getQuantity() + "," + item.getPrice() + "," + item.getSupplier());
                    writer.newLine();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
