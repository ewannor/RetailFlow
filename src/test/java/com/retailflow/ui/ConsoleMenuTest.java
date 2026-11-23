package com.retailflow.ui;

import com.retailflow.domain.PerishableProduct;
import com.retailflow.domain.Product;
import com.retailflow.domain.RegularProduct;
import com.retailflow.inventory.Inventory;
import com.retailflow.persistence.StoreRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleMenuTest {

    private Inventory inventory;
    private FakeRepository repository;

    private static class FakeRepository implements StoreRepository {

        int saveCount;

        @Override
        public Inventory load() {
            return new Inventory();
        }

        @Override
        public void save(Inventory inventory) {
            saveCount++;
        }
    }

    @BeforeEach
    void setUp() {

        inventory = new Inventory();
        repository = new FakeRepository();

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

    private String runMenu(String commands) throws IOException {

        ByteArrayOutputStream buffer =
                new ByteArrayOutputStream();

        Clock testClock = Clock.fixed(
                Instant.parse("2026-12-04T10:00:00Z"),
                ZoneOffset.UTC
        );

        try (
                Scanner scanner = new Scanner(commands);
                PrintStream output = new PrintStream(
                        buffer,
                        true,
                        StandardCharsets.UTF_8
                )
        ) {
            ConsoleMenu menu = new ConsoleMenu(
                    inventory,
                    repository,
                    scanner,
                    output,
                    testClock
            );

            menu.run();
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void shouldListProducts() throws IOException {

        String output = runMenu("1\n7\n");

        assertTrue(output.contains("RF001 | Rice"));
        assertTrue(output.contains("RF002 | Milk"));
        assertTrue(output.contains("Goodbye from RetailFlow!"));
    }

    @Test
    void shouldSearchBySkuAndName() throws IOException {

        String output = runMenu(
                "2\nrf001\n2\nmil\n7\n"
        );

        assertTrue(output.contains("RF001 | Rice"));
        assertTrue(output.contains("RF002 | Milk"));
    }

    @Test
    void shouldAddRegularProductAndSave() throws IOException {

        String output = runMenu(
                "3\n1\nRF003\nSoap\n12.00\n4\n7\n"
        );

        Product soap = inventory.findBySku("RF003")
                .orElseThrow();

        assertEquals("Soap", soap.getName());
        assertEquals(4, soap.getQuantity());
        assertEquals(1, repository.saveCount);

        assertTrue(output.contains(
                "Product added and saved successfully."
        ));
    }

    @Test
    void shouldAddPerishableProductAndSave() throws IOException {

        runMenu(
                "3\n2\nRF004\nYoghurt\n8.25\n7\n"
                        + "2026-12-10\n7\n"
        );

        Product product = inventory.findBySku("RF004")
                .orElseThrow();

        PerishableProduct yoghurt =
                assertInstanceOf(PerishableProduct.class, product);

        assertEquals(
                LocalDate.of(2026, 12, 10),
                yoghurt.getExpiryDate()
        );

        assertEquals(1, repository.saveCount);
    }

    @Test
    void shouldRestockProductAndSave() throws IOException {

        runMenu("4\nRF001\n3\n7\n");

        assertEquals(
                8,
                inventory.findBySku("RF001")
                        .orElseThrow().getQuantity()
        );

        assertEquals(1, repository.saveCount);
    }

    @Test
    void shouldProcessDiscountedCheckout() throws IOException {

        String output = runMenu(
                "5\n10\nRF001\n2\nRF002\n1\ndone\ny\n7\n"
        );

        assertTrue(output.contains(
                "Subtotal: GHS 258.50"
        ));

        assertTrue(output.contains(
                "Discount: GHS 25.85"
        ));

        assertTrue(output.contains(
                "Total: GHS 232.65"
        ));

        assertTrue(output.contains("Checkout successful."));

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

        assertEquals(1, repository.saveCount);
    }

    @Test
    void shouldCancelSaleWithoutDeductingStock()
            throws IOException {

        String output = runMenu(
                "5\n0\nRF001\n1\ndone\nn\n7\n"
        );

        assertTrue(output.contains("Checkout cancelled."));

        assertEquals(
                5,
                inventory.findBySku("RF001")
                        .orElseThrow().getQuantity()
        );

        assertEquals(0, repository.saveCount);
    }

    @Test
    void shouldRejectExpiredProductWithoutSaving()
            throws IOException {

        inventory.removeProduct("RF002");

        inventory.addProduct(new PerishableProduct(
                "RF002",
                "Milk",
                new BigDecimal("18.50"),
                2,
                LocalDate.of(2026, 12, 3)
        ));

        String output = runMenu(
                "5\n0\nRF002\n1\ndone\ny\n7\n"
        );

        assertTrue(output.contains(
                "Operation rejected: Product RF002 expired"
        ));

        assertEquals(
                2,
                inventory.findBySku("RF002")
                        .orElseThrow().getQuantity()
        );

        assertEquals(0, repository.saveCount);
    }

    @Test
    void shouldSortProductsByPrice() throws IOException {

        String output = runMenu("6\n7\n");

        int milkPosition = output.indexOf("RF002 | Milk");
        int ricePosition = output.indexOf("RF001 | Rice");

        assertTrue(milkPosition >= 0);
        assertTrue(ricePosition > milkPosition);
    }

    @Test
    void shouldHandleInvalidMenuOption() throws IOException {

        String output = runMenu("99\n7\n");

        assertTrue(output.contains(
                "Invalid menu option. Choose 1 to 7."
        ));

        assertTrue(output.contains("Goodbye from RetailFlow!"));
    }
}
