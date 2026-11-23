
package com.retailflow.persistence;

import com.retailflow.domain.PerishableProduct;
import com.retailflow.domain.Product;
import com.retailflow.domain.RegularProduct;
import com.retailflow.inventory.Inventory;
import com.retailflow.sales.Sale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class JsonStoreRepositoryTest {

    @TempDir
    Path tempDir;

    private StoreRepository createRepository() {
        return new JsonStoreRepository(
                tempDir.resolve("store.json")
        );
    }

    @Test
    void shouldSaveAndLoadProducts() throws IOException {

        Inventory inventory = new Inventory();

        inventory.addProduct(new RegularProduct(
                "RF001",
                "Rice",
                new BigDecimal("120.00"),
                20
        ));

        inventory.addProduct(new PerishableProduct(
                "RF002",
                "Fresh Milk",
                new BigDecimal("18.50"),
                10,
                LocalDate.of(2026, 12, 5)
        ));

        StoreRepository repository = createRepository();

        repository.save(inventory);

        Inventory restored = repository.load();

        assertEquals(2, restored.getProductCount());

        Product rice = restored.findBySku("RF001")
                .orElseThrow();

        Product milk = restored.findBySku("RF002")
                .orElseThrow();

        assertInstanceOf(RegularProduct.class, rice);

        PerishableProduct restoredMilk =
                assertInstanceOf(PerishableProduct.class, milk);

        assertEquals(
                new BigDecimal("120.00"),
                rice.getPrice()
        );

        assertEquals(20, rice.getQuantity());

        assertEquals(
                new BigDecimal("18.50"),
                milk.getPrice()
        );

        assertEquals(10, milk.getQuantity());

        assertEquals(
                LocalDate.of(2026, 12, 5),
                restoredMilk.getExpiryDate()
        );
    }

    @Test
    void shouldPersistStockChangesAfterCheckout() throws IOException {

        Inventory inventory = new Inventory();

        inventory.addProduct(new RegularProduct(
                "RF001",
                "Rice",
                new BigDecimal("120.00"),
                20
        ));

        Sale sale = new Sale(
                inventory,
                LocalDate.of(2026, 12, 4)
        );

        sale.addItem("RF001", 2);
        sale.checkout();

        StoreRepository repository = createRepository();

        repository.save(inventory);

        Inventory restored = repository.load();

        assertEquals(
                18,
                restored.findBySku("RF001")
                        .orElseThrow().getQuantity()
        );
    }

    @Test
    void shouldReturnEmptyInventoryWhenFileIsMissing()
            throws IOException {

        StoreRepository repository = createRepository();

        Inventory inventory = repository.load();

        assertEquals(0, inventory.getProductCount());
    }

    @Test
    void shouldRejectInvalidJson() throws IOException {

        Path file = tempDir.resolve("store.json");

        Files.writeString(file, "{ invalid json }");

        StoreRepository repository = createRepository();

        assertThrows(
                IOException.class,
                repository::load
        );
    }

    @Test
    void shouldRejectInvalidProductData() throws IOException {

        Path file = tempDir.resolve("store.json");

        Files.writeString(file, """
                {
                  "formatVersion": 1,
                  "products": [
                    {
                      "type": "REGULAR",
                      "sku": "RF001",
                      "name": "Rice",
                      "price": "120.00",
                      "quantity": -10
                    }
                  ]
                }
                """);

        StoreRepository repository = createRepository();

        assertThrows(
                IOException.class,
                repository::load
        );
    }
}
