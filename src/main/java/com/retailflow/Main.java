package com.retailflow;

import com.retailflow.domain.Product;
import com.retailflow.domain.RegularProduct;
import com.retailflow.domain.PerishableProduct;
import com.retailflow.inventory.Inventory;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        Inventory inventory = new Inventory();

        //Create and add products
        inventory.addProduct(new RegularProduct(
                "RF001", "Rice",
                new BigDecimal("120.00"), 20
        ));

        inventory.addProduct(new PerishableProduct(
                "RF002", "Fresh Milk",
                new BigDecimal("18.50"), 10,
                LocalDate.of(2026, 12, 5)
        ));

        inventory.addProduct(new RegularProduct(
                "RF003", "Soap",
                new BigDecimal("12.00"), 35
        ));

        inventory.addProduct(new RegularProduct(
                "RF004", "Sugar",
                new BigDecimal("40.00"), 12
        ));

        System.out.println("=== RETAILFLOW: MILESTONE 3 ===");

        //Display inventory count
        System.out.println(
                "Total products: " + inventory.getProductCount()
        );

        // Display all products
        System.out.println("\nAll products:");
        for (Product product : inventory.getAllProducts()) {
            System.out.println(
                    product.getSku() + " - " + product.getName()
            );
        }

        // Find a product using its SKU
        System.out.println("\nSearch SKU rf002:");
        inventory.findBySku("rf002").ifPresent(
                product -> System.out.println(product.getName())
        );

        // Search by product name
        System.out.println("\nSearch by name 's':");
        for (Product product : inventory.searchByName("s")) {
            System.out.println(
                    product.getSku() + " - " + product.getName()
            );
        }

        // Sort by price
        System.out.println("\nSorted by price:");
        for (Product product : inventory.getProductsSortedByPrice()) {
            System.out.println(
                    product.getName() + " - GHS " + product.getPrice()
            );
        }

        // Sort by name
        System.out.println("\nSorted by name:");
        for (Product product : inventory.getProductsSortedByName()) {
            System.out.println(product.getName());
        }

        // Restock milk
        inventory.restockProduct("RF002", 5);

        Product milk = inventory.findBySku("RF002").orElseThrow();

        System.out.println(
                "\nMilk stock after restocking: " + milk.getQuantity()
        );

        // Remove soap
        boolean removed = inventory.removeProduct("RF003");

        System.out.println("Removed RF003: " + removed);
        System.out.println(
                "Total products remaining: " + inventory.getProductCount()
        );

        // Verify that duplicate SKUs are rejected
        try {
            inventory.addProduct(new RegularProduct(
                    "rf001", "Duplicate Rice",
                    new BigDecimal("100.00"), 1
            ));
        } catch (IllegalArgumentException exception) {
            System.out.println(
                    "Duplicate rejected: " + exception.getMessage()
            );
        }

    }
}