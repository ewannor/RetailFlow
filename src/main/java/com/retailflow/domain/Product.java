package com.retailflow.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class Product {

    // sku - stock keeping unit

    private final String sku;
    private final String name;
    private final BigDecimal price;
    private int quantity;

    public Product(String sku, String name, BigDecimal price, int quantity) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getSku() {return sku;}
    public String getName() {
        return name;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public int getQuantity() {
        return quantity;
    }

    public void restock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        quantity = Math.addExact(quantity, amount);
    }

    protected final boolean hasEnoughStock (int requestedQuantity) {
        if (requestedQuantity <= 0) {
            throw new IllegalArgumentException("Requested quantity must be positive");
        }

        return quantity >= requestedQuantity;
    }

    public abstract boolean canSell(
            int requestedQuantity,
            LocalDate saleDate
    );

    // Implement: Deduct stock after validating sale eligiblity
    public final void sell(int requestedQuantity, LocalDate saleDate) {
        if (requestedQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Requested quantity must be positive"
            );
        }

        if (saleDate == null) {
            throw new IllegalArgumentException("Sale date is required");
        }

        if (!canSell(requestedQuantity, saleDate)) {
            throw new IllegalStateException(
                    "Product cannot be sold: " + sku
            );
        }

        quantity -= requestedQuantity;
    }
}