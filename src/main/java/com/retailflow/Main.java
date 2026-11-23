package com.retailflow;

import com.retailflow.domain.Product;
import com.retailflow.domain.RegularProduct;
import com.retailflow.domain.PerishableProduct;
import com.retailflow.inventory.Inventory;
import com.retailflow.sales.Sale;
import com.retailflow.sales.SaleItem;
import com.retailflow.persistence.StoreRepository;
import com.retailflow.persistence.JsonStoreRepository;
import com.retailflow.sales.discount.PercentageDiscountStrategy;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws IOException {

        Path dataFile = Path.of("data", "store.json");
        StoreRepository repository =
                new JsonStoreRepository(dataFile);

        boolean firstRun = Files.notExists(dataFile);

        Inventory inventory = repository.load();

        if (firstRun) {
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
                    LocalDate.now().plusDays(30)
            ));
            inventory.addProduct(new RegularProduct(
                    "RF003",
                    "Soap",
                    new BigDecimal("12.00"),
                    35
            ));
            repository.save(inventory);

            System.out.println("Created starter inventory and saved JSON.");
        } else {
            System.out.println("Loaded inventory from JSON.");
        }

        System.out.println("=== RETAILFLOW: MILESTONE 5 ===");

        // Run a demo sale only when requested
        if (args.length > 0
                && args[0].equalsIgnoreCase("demo-sale")) {


            LocalDate saleDate = LocalDate.now();

            Sale sale;

            if (args.length > 1
                    && args[1].equalsIgnoreCase("discount10")) {

                sale = new Sale(
                        inventory,
                        saleDate,
                        new PercentageDiscountStrategy(
                                new BigDecimal("10")
                        )
                );

            } else {

                sale = new Sale(inventory, saleDate);
            }


            sale.addItem("RF001", 2);
            sale.addItem("RF002", 3);
            sale.addItem("RF003", 1);

            try {
                sale.checkout();

                // Save stock after checkout
                repository.save(inventory);

                System.out.println("Checkout successful.");

                for (SaleItem item : sale.getItems()) {
                    System.out.println(
                            item.getProductName()
                                    + " x " + item.getQuantity()
                                    + " = GHS " + item.getLineTotal()
                    );
                }

                System.out.println(
                        "Subtotal: GHS " + sale.getSubtotal()
                );

                System.out.println(
                        "Discount: GHS " + sale.getDiscountAmount()
                );

                System.out.println(
                        "Amount payable: GHS " + sale.getTotal()
                );


                System.out.println(
                        "Updated inventory saved to JSON."
                );

            } catch (IllegalStateException error) {

                System.out.println(
                        "Checkout rejected: " + error.getMessage()
                );
            }
        }

        // Display the current stock
        System.out.println("Current inventory:");

        for (Product product : inventory.getAllProducts()) {

            System.out.println(
                    product.getSku()
                            + " | " + product.getName()
                            + " | GHS " + product.getPrice()
                            + " | Stock: " + product.getQuantity()
            );
        }
    }
}