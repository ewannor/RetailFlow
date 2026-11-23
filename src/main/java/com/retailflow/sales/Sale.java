
package com.retailflow.sales;

import com.retailflow.domain.Product;
import com.retailflow.inventory.Inventory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Sale {

    private final Inventory inventory;
    private final LocalDate saleDate;
    private final List<SaleItem> items = new ArrayList<>();

    private boolean checkedOut;

    public Sale(Inventory inventory, LocalDate saleDate) {

        if (inventory == null) {
            throw new IllegalArgumentException(
                    "Inventory is required"
            );
        }

        if (saleDate == null) {
            throw new IllegalArgumentException(
                    "Sale date is required"
            );
        }

        this.inventory = inventory;
        this.saleDate = saleDate;
    }

    // 1. Add a product to the sale
    public void addItem(String sku, int quantity) {

        ensureNotCheckedOut();

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }

        Product product = inventory.findBySku(sku).orElseThrow(
                () -> new IllegalArgumentException(
                        "Product not found: " + sku
                )
        );

        // Prevent duplicate product lines in one sale
        for (SaleItem item : items) {
            if (item.getSku().equalsIgnoreCase(product.getSku())) {
                throw new IllegalArgumentException(
                        "Product already in sale: " + product.getSku()
                );
            }
        }

        items.add(new SaleItem(product, quantity));
    }

    // 2. Return an unmodifiable list of sale items
    public List<SaleItem> getItems() {
        return List.copyOf(items);
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public boolean isCheckedOut() {
        return checkedOut;
    }

    // 3. Calculate the total amount
    public BigDecimal getTotal() {

        BigDecimal total = BigDecimal.ZERO;

        for (SaleItem item : items) {
            total = total.add(item.getLineTotal());
        }

        return total;
    }

    // 4. Validate and complete the checkout
    public void checkout() {

        ensureNotCheckedOut();

        if (items.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot checkout an empty sale"
            );
        }

        // First pass: validate ALL items
        for (SaleItem item : items) {

            Product current = inventory.findBySku(
                    item.getSku()
            ).orElseThrow(
                    () -> new IllegalStateException(
                            "Product no longer in inventory: "
                                    + item.getSku()
                    )
            );

            if (current != item.getProductReference()) {
                throw new IllegalStateException(
                        "Product changed in inventory: "
                                + item.getSku()
                );
            }

            current.validateSale(
                    item.getQuantity(),
                    saleDate
            );
        }

        // Second pass: deduct stock after validation
        for (SaleItem item : items) {
            item.getProductReference().sell(
                    item.getQuantity(),
                    saleDate
            );
        }

        checkedOut = true;
    }

    // 5. Prevent changes after checkout
    private void ensureNotCheckedOut() {
        if (checkedOut) {
            throw new IllegalStateException(
                    "Sale has already been checked out"
            );
        }
    }
}
