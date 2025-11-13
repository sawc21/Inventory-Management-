package com.ims.io;

import com.ims.model.Item;

import java.io.IOException;
import java.util.List;

public interface InventoryFileStorage {
    List<Item> loadAll() throws IOException;

    void saveAll(List<Item> items) throws IOException;
}
