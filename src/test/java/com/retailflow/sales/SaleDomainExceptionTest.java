package com.retailflow.sales;

import com.retailflow.domain.PerishableProduct;
import com.retailflow.domain.Product;
import com.retailflow.domain.RegularProduct;
import com.retailflow.exception.ExpiredProductException;
import com.retailflow.exception.InsufficientStockException;
import com.retailflow.inventory.Inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SaleDomainExceptionTest {

    private Inventory inventory;

    private final LocalDate saleDate =
            LocalDate.of(2026, 12, 4);

    @BeforeEach
    void setUp() {

        inventory = new Inventory();

        inventory.addProduct(new RegularProduct(
                "RF001",
                "Rice",
                new BigDecimal("120.00"),
                5
        ));

        inventory.addProduct(new PerishableProduct(
                "RF002",
                "Milk",
                new BigDecimal("18.50"),
                2,
                LocalDate.of(2026, 12, 5)
        ));
    }

    @Test
    void shouldThrowInsufficientStockException() {

        Sale sale = new Sale(inventory, saleDate);

        sale.addItem("RF001", 6);

        InsufficientStockException error = assertThrows(
                InsufficientStockException.class,
                sale::checkout
        );

        assertEquals("RF001", error.getSku());
        assertEquals(6, error.getRequestedQuantity());
        assertEquals(5, error.getAvailableQuantity());

        assertEquals(
                5,
                inventory.findBySku("RF001")
                        .orElseThrow().getQuantity()
        );

        assertFalse(sale.isCheckedOut());
    }

    @Test
    void shouldThrowExpiredProductException() {

        LocalDate lateDate = LocalDate.of(2026, 12, 6);

        Sale sale = new Sale(inventory, lateDate);

        sale.addItem("RF002", 1);

        ExpiredProductException error = assertThrows(
                ExpiredProductException.class,
                sale::checkout
        );

        assertEquals("RF002", error.getSku());

        assertEquals(
                LocalDate.of(2026, 12, 5),
                error.getExpiryDate()
        );

        assertEquals(lateDate, error.getSaleDate());

        assertEquals(
                2,
                inventory.findBySku("RF002")
                        .orElseThrow().getQuantity()
        );

        assertFalse(sale.isCheckedOut());
    }

    @Test
    void shouldNotPartiallyDeductStock() {

        Sale sale = new Sale(inventory, saleDate);

        // Rice has enough stock
        sale.addItem("RF001", 2);

        // Milk only has 2 units available
        sale.addItem("RF002", 3);

        assertThrows(
                InsufficientStockException.class,
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

        assertFalse(sale.isCheckedOut());
    }

    @Test
    void shouldRejectDirectSaleWithInsufficientStock() {

        Product rice = inventory.findBySku("RF001")
                .orElseThrow();

        assertThrows(
                InsufficientStockException.class,
                () -> rice.sell(10, saleDate)
        );

        assertEquals(5, rice.getQuantity());
    }

    @Test
    void shouldRejectDirectSaleOfExpiredProduct() {

        Product milk = inventory.findBySku("RF002")
                .orElseThrow();

        LocalDate lateDate = LocalDate.of(2026, 12, 6);

        assertThrows(
                ExpiredProductException.class,
                () -> milk.sell(1, lateDate)
        );

        assertEquals(2, milk.getQuantity());
    }

    @Test
    void shouldAllowSaleOnExpiryDate() {

        LocalDate expiryDate = LocalDate.of(2026, 12, 5);

        Sale sale = new Sale(inventory, expiryDate);

        sale.addItem("RF002", 2);

        assertDoesNotThrow(sale::checkout);

        assertTrue(sale.isCheckedOut());

        assertEquals(
                0,
                inventory.findBySku("RF002")
                        .orElseThrow().getQuantity()
        );
    }
}
