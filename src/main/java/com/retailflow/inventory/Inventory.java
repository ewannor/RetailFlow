
package com.retailflow.inventory;

import com.retailflow.domain.Product;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Inventory {

    private final List<Product> products = new ArrayList<>();

    // 1. Add a product
    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        if (findBySku(product.getSku()).isPresent()) {
            throw new IllegalArgumentException(
                    "Duplicate SKU: " + product.getSku()
            );
        }

        products.add(product);
    }

    // 2. Find a product by SKU
    public Optional<Product> findBySku(String sku) {
        validateSku(sku);

        for (Product product : products) {
            if (product.getSku().trim().equalsIgnoreCase(sku.trim())) {
                return Optional.of(product);
            }
        }

        return Optional.empty();
    }

    // 3. Search products by name
    public List<Product> searchByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException(
                    "Search keyword is required"
            );
        }

        String searchTerm = keyword.trim().toLowerCase(Locale.ROOT);
        List<Product> matches = new ArrayList<>();

        for (Product product : products) {
            if (product.getName().toLowerCase(Locale.ROOT)
                    .contains(searchTerm)) {
                matches.add(product);
            }
        }

        return List.copyOf(matches);
    }

    // 4. List all products
    public List<Product> getAllProducts() {
        return List.copyOf(products);
    }

    // 5. Sort products by price (ascending)
    public List<Product> getProductsSortedByPrice() {
        List<Product> sorted = new ArrayList<>(products);

        sorted.sort(
                Comparator.comparing(Product::getPrice)
                        .thenComparing(Product::getSku)
        );

        return List.copyOf(sorted);
    }

    // 6. Sort products by name (alphabetically)
    public List<Product> getProductsSortedByName() {
        List<Product> sorted = new ArrayList<>(products);

        sorted.sort(
                Comparator.comparing(
                        Product::getName,
                        String.CASE_INSENSITIVE_ORDER
                ).thenComparing(Product::getSku)
        );

        return List.copyOf(sorted);
    }

    // 7. Restock an existing product
    public void restockProduct(String sku, int amount) {
        Product product = findBySku(sku).orElseThrow(
                () -> new IllegalArgumentException(
                        "Product not found: " + sku
                )
        );

        product.restock(amount);
    }

    // 8. Remove a product
    public boolean removeProduct(String sku) {
        validateSku(sku);

        return products.removeIf(
                product -> product.getSku().trim()
                        .equalsIgnoreCase(sku.trim())
        );
    }

    // 9. Count distinct products
    public int getProductCount() {
        return products.size();
    }

    // Internal SKU validation
    private void validateSku(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU is required");
        }
    }
}
