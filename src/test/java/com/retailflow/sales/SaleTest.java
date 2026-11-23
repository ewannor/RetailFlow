
package com.retailflow.sales;

import com.retailflow.domain.PerishableProduct;
import com.retailflow.domain.RegularProduct;
import com.retailflow.inventory.Inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SaleTest {

    private Inventory inventory;

    private final LocalDate saleDate =
            LocalDate.of(2026, 12, 4);

    @BeforeEach
    void setUp() {

        inventory = new Inventory();

        inventory.addProduct(new RegularProduct(
                "RF001", "Rice",
                new BigDecimal("120.00"), 5
        ));

        inventory.addProduct(new PerishableProduct(
                "RF002", "Milk",
                new BigDecimal("18.50"), 2,
                LocalDate.of(2026, 12, 5)
        ));
    }

    @Test
    void shouldCheckoutAndDeductStock() {

        Sale sale = new Sale(inventory, saleDate);

        sale.addItem("RF001", 2);
        sale.addItem("RF002", 1);

        assertEquals(
                new BigDecimal("258.50"),
                sale.getTotal()
        );

        sale.checkout();

        assertTrue(sale.isCheckedOut());

        assertEquals(
                3,
                inventory.findBySku("RF001")
                        .orElseThrow().getQuantity()
        );

        assertEquals(
                1,
                inventory.findBySku("RF002")
                        .orElseThrow().getQuantity()
        );
    }

    @Test
    void shouldRejectExpiredMilk() {

        Sale sale = new Sale(
                inventory,
                LocalDate.of(2026, 12, 6)
        );

        sale.addItem("RF002", 1);

        assertThrows(
                IllegalStateException.class,
                sale::checkout
        );

        assertEquals(
                2,
                inventory.findBySku("RF002")
                        .orElseThrow().getQuantity()
        );
    }

    @Test
    void shouldPreventPartialStockDeduction() {

        Sale sale = new Sale(inventory, saleDate);

        sale.addItem("RF001", 2);
        sale.addItem("RF002", 3);

        assertThrows(
                IllegalStateException.class,
                sale::checkout
        );

        assertEquals(
                5,
                inventory.findBySku("RF001")
                        .orElseThrow().getQuantity()
        );

        assertEquals(
                2,
                inventory.findBySku("RF002")
                        .orElseThrow().getQuantity()
        );
    }

    @Test
    void shouldPreventDoubleCheckout() {

        Sale sale = new Sale(inventory, saleDate);

        sale.addItem("RF001", 1);
        sale.checkout();

        assertThrows(
                IllegalStateException.class,
                sale::checkout
        );

        assertEquals(
                4,
                inventory.findBySku("RF001")
                        .orElseThrow().getQuantity()
        );
    }

    @Test
    void shouldRejectEmptySale() {

        Sale sale = new Sale(inventory, saleDate);

        assertThrows(
                IllegalStateException.class,
                sale::checkout
        );
    }

    @Test
    void shouldRejectDuplicateProductLines() {

        Sale sale = new Sale(inventory, saleDate);

        sale.addItem("RF001", 1);

        assertThrows(
                IllegalArgumentException.class,
                () -> sale.addItem("rf001", 2)
        );
    }
}
