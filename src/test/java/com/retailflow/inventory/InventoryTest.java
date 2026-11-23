
package com.retailflow.inventory;

import com.retailflow.domain.Product;
import com.retailflow.domain.RegularProduct;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory inventory;

    @BeforeEach
    void setUp() {

        inventory = new Inventory();

        inventory.addProduct(new RegularProduct(
                "RF001", "Rice",
                new BigDecimal("120.00"), 20
        ));

        inventory.addProduct(new RegularProduct(
                "RF002", "Soap",
                new BigDecimal("12.00"), 35
        ));
    }

    @Test
    void shouldFindProductIgnoringSkuCase() {

        Product product = inventory.findBySku("rf001")
                .orElseThrow();

        assertEquals("Rice", product.getName());
    }

    @Test
    void shouldRejectDuplicateSku() {

        assertThrows(
                IllegalArgumentException.class,
                () -> inventory.addProduct(new RegularProduct(
                        "rf001", "Duplicate Rice",
                        new BigDecimal("50.00"), 5
                ))
        );
    }

    @Test
    void shouldSortByPrice() {

        List<Product> sorted =
                inventory.getProductsSortedByPrice();

        assertEquals("Soap", sorted.get(0).getName());
        assertEquals("Rice", sorted.get(1).getName());
    }

    @Test
    void shouldRestockAndRemoveProducts() {

        inventory.restockProduct("RF001", 5);

        assertEquals(
                25,
                inventory.findBySku("RF001")
                        .orElseThrow().getQuantity()
        );

        assertTrue(inventory.removeProduct("RF002"));

        assertEquals(1, inventory.getProductCount());
    }

    @Test
    void shouldReturnUnmodifiableProductList() {

        List<Product> products =
                inventory.getAllProducts();

        assertThrows(
                UnsupportedOperationException.class,
                products::clear
        );
    }
}
