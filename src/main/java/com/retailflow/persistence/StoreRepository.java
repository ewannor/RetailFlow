package com.retailflow.persistence;

import com.retailflow.inventory.Inventory;
import java.io.IOException;

public interface StoreRepository {
    Inventory load() throws IOException;
    void save(Inventory inventory) throws IOException;
}
