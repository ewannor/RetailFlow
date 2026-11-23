package com.retailflow.ui;

import com.retailflow.domain.PerishableProduct;
import com.retailflow.domain.Product;
import com.retailflow.domain.RegularProduct;
import com.retailflow.inventory.Inventory;
import com.retailflow.persistence.StoreRepository;
import com.retailflow.sales.Sale;
import com.retailflow.sales.SaleItem;
import com.retailflow.sales.discount.DiscountStrategy;
import com.retailflow.sales.discount.NoDiscountStrategy;
import com.retailflow.sales.discount.PercentageDiscountStrategy;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class ConsoleMenu {

    private final Inventory inventory;
    private final StoreRepository repository;
    private final Scanner input;
    private final PrintStream output;
    private final Clock clock;

    public ConsoleMenu(
            Inventory inventory,
            StoreRepository repository,
            Scanner input,
            PrintStream output,
            Clock clock
    ) {
        if (inventory == null || repository == null
                || input == null || output == null || clock == null) {
            throw new IllegalArgumentException(
                    "ConsoleMenu dependencies cannot be null"
            );
        }

        this.inventory = inventory;
        this.repository = repository;
        this.input = input;
        this.output = output;
        this.clock = clock;
    }

    public void run() throws IOException {

        while (true) {
            try {
                printMenu();
                String choice = ask("Select an option: ");

                switch (choice) {
                    case "1" -> showProducts(
                            inventory.getAllProducts()
                    );

                    case "2" -> searchProduct();

                    case "3" -> addProduct();

                    case "4" -> restockProduct();

                    case "5" -> createSale();

                    case "6" -> showProducts(
                            inventory.getProductsSortedByPrice()
                    );

                    case "7" -> {
                        output.println("Goodbye from RetailFlow!");
                        return;
                    }

                    default -> output.println(
                            "Invalid menu option. Choose 1 to 7."
                    );
                }

            } catch (NoSuchElementException error) {
                output.println("Input ended. Goodbye!");
                return;

            } catch (IllegalArgumentException
                     | IllegalStateException error) {
                output.println(
                        "Operation rejected: " + error.getMessage()
                );

            } catch (IOException error) {
                output.println(
                        "Save failed: " + error.getMessage()
                );
                output.println(
                        "Application stopping. In-memory changes "
                                + "may not have been saved."
                );
                throw error;
            }
        }
    }

    private void printMenu() {
        output.println();
        output.println("========== RETAILFLOW ==========");
        output.println("1. View all products");
        output.println("2. Search for a product");
        output.println("3. Add a new product");
        output.println("4. Restock a product");
        output.println("5. Create a sale / Checkout");
        output.println("6. View products sorted by price");
        output.println("7. Exit");
    }

    private String ask(String prompt) {
        output.print(prompt);
        output.flush();

        if (!input.hasNextLine()) {
            throw new NoSuchElementException(
                    "Console input closed"
            );
        }

        return input.nextLine().trim();
    }

    private void showProducts(List<Product> products) {

        if (products.isEmpty()) {
            output.println("No products found.");
            return;
        }

        output.println("Products found: " + products.size());

        for (Product product : products) {
            showProduct(product);
        }
    }

    private void showProduct(Product product) {

        String description = product.getSku()
                + " | " + product.getName()
                + " | GHS " + product.getPrice()
                + " | Stock: " + product.getQuantity();

        if (product instanceof PerishableProduct perishable) {
            description += " | Expiry: "
                    + perishable.getExpiryDate();
        }

        output.println(description);
    }

    private void searchProduct() {

        String keyword = ask("Enter SKU or product name: ");

        if (keyword.isBlank()) {
            throw new IllegalArgumentException(
                    "Search term is required"
            );
        }

        var exactMatch = inventory.findBySku(keyword);

        if (exactMatch.isPresent()) {
            showProduct(exactMatch.get());
        } else {
            showProducts(inventory.searchByName(keyword));
        }
    }

    private void addProduct() throws IOException {

        output.println("1. Regular product");
        output.println("2. Perishable product");

        String type = ask("Choose product type: ");

        if (!type.equals("1") && !type.equals("2")) {
            throw new IllegalArgumentException(
                    "Invalid product type"
            );
        }

        String sku = ask("SKU: ");
        String name = ask("Product name: ");

        BigDecimal price = new BigDecimal(
                ask("Unit price (GHS): ")
        );

        int quantity = Integer.parseInt(
                ask("Initial stock quantity: ")
        );

        Product product;

        if (type.equals("1")) {
            product = new RegularProduct(
                    sku, name, price, quantity
            );
        } else {
            LocalDate expiryDate = readDate(
                    ask("Expiry date (yyyy-MM-dd): ")
            );

            product = new PerishableProduct(
                    sku, name, price, quantity, expiryDate
            );
        }

        inventory.addProduct(product);
        repository.save(inventory);

        output.println("Product added and saved successfully.");
    }

    private void restockProduct() throws IOException {

        String sku = ask("Enter product SKU: ");

        int quantity = Integer.parseInt(
                ask("Quantity to add: ")
        );

        inventory.restockProduct(sku, quantity);
        repository.save(inventory);

        Product product = inventory.findBySku(sku)
                .orElseThrow();

        output.println(
                "Restock saved. New quantity: "
                        + product.getQuantity()
        );
    }

    private void createSale() throws IOException {

        String discountInput = ask(
                "Discount percentage (0-100, Enter for none): "
        );

        DiscountStrategy strategy;

        if (discountInput.isBlank()) {
            strategy = new NoDiscountStrategy();
        } else {
            strategy = new PercentageDiscountStrategy(
                    new BigDecimal(discountInput)
            );
        }

        Sale sale = new Sale(
                inventory,
                LocalDate.now(clock),
                strategy
        );

        output.println(
                "Enter products for the sale. Type 'done' when finished."
        );

        while (true) {

            String sku = ask("Product SKU (or done): ");

            if (sku.equalsIgnoreCase("done")) {
                break;
            }

            if (sku.isBlank()) {
                output.println("A product SKU is required.");
                continue;
            }

            try {
                int quantity = Integer.parseInt(
                        ask("Quantity: ")
                );

                sale.addItem(sku, quantity);

                output.println("Added to sale.");

            } catch (IllegalArgumentException error) {
                output.println(
                        "Unable to add item: " + error.getMessage()
                );
            }
        }

        if (sale.getItems().isEmpty()) {
            output.println("Sale cancelled: no items added.");
            return;
        }

        output.println("----- SALE SUMMARY -----");

        for (SaleItem item : sale.getItems()) {
            output.println(
                    item.getProductName()
                            + " x " + item.getQuantity()
                            + " = GHS " + item.getLineTotal()
            );
        }

        output.println("Subtotal: GHS " + sale.getSubtotal());
        output.println("Discount: GHS " + sale.getDiscountAmount());
        output.println("Total: GHS " + sale.getTotal());

        String confirmation = ask(
                "Confirm checkout? (y/n): "
        );

        if (!confirmation.equalsIgnoreCase("y")
                && !confirmation.equalsIgnoreCase("yes")) {
            output.println("Checkout cancelled.");
            return;
        }

        sale.checkout();
        repository.save(inventory);

        output.println(
                "Checkout successful. Inventory saved to JSON."
        );
    }

    private LocalDate readDate(String value) {

        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException error) {
            throw new IllegalArgumentException(
                    "Invalid date. Use yyyy-MM-dd."
            );
        }
    }
}
