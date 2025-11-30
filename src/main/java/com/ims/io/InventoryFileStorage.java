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
    public List<Item> loadAll(fileName) throws IOException {
        File file = new File(fileName);
        Scanner sc = new Scanner(file);

        //loops to read the file
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] values = line.split(",");
            System.out.println("ID" + values[0] + "Name" + values[1] + "Grade" + values[2]);
        }
        sc.close();
    }
    public void saveAll(List<Item> items) throws IOException {
        for (int i = 0; i < items; i++) {
            
        }
    }
}

//class for loading and saving from/to txt file
public class TxtInventoryStorage implements InventoryFileStorage {
    public List<Item> loadAll() throws IOException {
        
    }
    public void saveAll(List<Item> items) throws IOException {
        
    }
}
