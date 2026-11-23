package com.retailflow.sales.discount;

import com.retailflow.domain.PerishableProduct;
import com.retailflow.domain.RegularProduct;
import com.retailflow.exception.ExpiredProductException;
import com.retailflow.exception.InsufficientStockException;
import com.retailflow.inventory.Inventory;
import com.retailflow.sales.Sale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DiscountStrategyTest {

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
    void defaultSaleShouldHaveNoDiscount() {

        Sale sale = new Sale(inventory, saleDate);

        sale.addItem("RF001", 1);
        sale.addItem("RF002", 1);

        assertEquals(
                new BigDecimal("138.50"),
                sale.getSubtotal()
        );

        assertEquals(
                BigDecimal.ZERO,
                sale.getDiscountAmount()
        );

        assertEquals(
                new BigDecimal("138.50"),
                sale.getTotal()
        );

        sale.checkout();

        assertTrue(sale.isCheckedOut());
    }

    @Test
    void percentageDiscountShouldReduceTotal() {

        DiscountStrategy strategy =
                new PercentageDiscountStrategy(
                        new BigDecimal("10")
                );

        Sale sale = new Sale(inventory, saleDate, strategy);

        sale.addItem("RF001", 2);
        sale.addItem("RF002", 1);

        assertEquals(
                new BigDecimal("258.50"),
                sale.getSubtotal()
        );

        assertEquals(
                new BigDecimal("25.85"),
                sale.getDiscountAmount()
        );

        assertEquals(
                new BigDecimal("232.65"),
                sale.getTotal()
        );

        sale.checkout();

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
    void percentageDiscountShouldRoundCorrectly() {

        DiscountStrategy strategy =
                new PercentageDiscountStrategy(
                        new BigDecimal("15")
                );

        Sale sale = new Sale(inventory, saleDate, strategy);

        sale.addItem("RF002", 1);

        // 15% of 18.50 = 2.775, rounded to 2.78
        assertEquals(
                new BigDecimal("2.78"),
                sale.getDiscountAmount()
        );

        assertEquals(
                new BigDecimal("15.72"),
                sale.getTotal()
        );
    }

    @Test
    void shouldAcceptZeroAndFullDiscount() {

        Sale noDiscount = new Sale(
                inventory,
                saleDate,
                new PercentageDiscountStrategy(
                        BigDecimal.ZERO
                )
        );

        noDiscount.addItem("RF001", 1);

        assertEquals(
                new BigDecimal("120.00"),
                noDiscount.getTotal()
        );

        Sale fullDiscount = new Sale(
                inventory,
                saleDate,
                new PercentageDiscountStrategy(
                        new BigDecimal("100")
                )
        );

        fullDiscount.addItem("RF001", 1);

        assertEquals(
                new BigDecimal("120.00"),
                fullDiscount.getDiscountAmount()
        );

        assertEquals(
                new BigDecimal("0.00"),
                fullDiscount.getTotal()
        );
    }

    @Test
    void shouldRejectInvalidPercentages() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new PercentageDiscountStrategy(
                        new BigDecimal("-5")
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new PercentageDiscountStrategy(
                        new BigDecimal("101")
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new PercentageDiscountStrategy(null)
        );
    }

    @Test
    void shouldRejectNullDiscountStrategy() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Sale(inventory, saleDate, null)
        );
    }

    @Test
    void discountShouldNotBypassStockValidation() {

        Sale sale = new Sale(
                inventory,
                saleDate,
                new PercentageDiscountStrategy(
                        new BigDecimal("50")
                )
        );

        sale.addItem("RF001", 6);

        assertThrows(
                InsufficientStockException.class,
                sale::checkout
        );

        assertEquals(
                5,
                inventory.findBySku("RF001")
                        .orElseThrow().getQuantity()
        );

        assertFalse(sale.isCheckedOut());
    }

    @Test
    void discountShouldNotBypassExpiryValidation() {

        Sale sale = new Sale(
                inventory,
                LocalDate.of(2026, 12, 6),
                new PercentageDiscountStrategy(
                        new BigDecimal("10")
                )
        );

        sale.addItem("RF002", 1);

        assertThrows(
                ExpiredProductException.class,
                sale::checkout
        );

        assertEquals(
                2,
                inventory.findBySku("RF002")
                        .orElseThrow().getQuantity()
        );
    }

    @Test
    void invalidStrategyShouldNotDeductStock() {

        DiscountStrategy invalidStrategy =
                subtotal -> subtotal.add(BigDecimal.ONE);

        Sale sale = new Sale(
                inventory,
                saleDate,
                invalidStrategy
        );

        sale.addItem("RF001", 1);

        assertThrows(
                IllegalStateException.class,
                sale::checkout
        );

        assertEquals(
                5,
                inventory.findBySku("RF001")
                        .orElseThrow().getQuantity()
        );

        assertFalse(sale.isCheckedOut());
    }
}
