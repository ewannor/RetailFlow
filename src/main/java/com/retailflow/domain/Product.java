package com.retailflow.domain;

import com.retailflow.exception.InsufficientStockException;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Abstract representation of a product in RetailFlow.
 *
 * Encapsulates common product information and stock
 * management operations.
 *
 * Concrete subclasses provide their own sale eligibility
 * rules through the canSell() method.
 */
public abstract class Product {

    private final String sku;
    private final String name;
    private final BigDecimal price;
    private int quantity;

    public Product(
            String sku,
            String name,
            BigDecimal price,
            int quantity
    ) {
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

    public String getSku() {
        return sku;
    }

    public String getName() {return name;}
    public BigDecimal getPrice() {return price;}
    public int getQuantity() {return quantity;}

    public void restock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        quantity = Math.addExact(quantity, amount);
    }

    protected final boolean hasEnoughStock(int requestedQuantity) {
        if (requestedQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Requested quantity must be positive"
            );
        }

        return quantity >= requestedQuantity;
    }

    public abstract boolean canSell(
            int requestedQuantity,
            LocalDate saleDate
    );

    // Validate a sale without changing stock
    public final void validateSale(
            int requestedQuantity,
            LocalDate saleDate
    ) {
        if (requestedQuantity <= 0) {
            throw new IllegalArgumentException(
                    "Requested quantity must be positive"
            );
        }

        if (saleDate == null) {
            throw new IllegalArgumentException(
                    "Sale date is required"
            );
        }

        if (!hasEnoughStock(requestedQuantity)) {
            throw new InsufficientStockException(
                    sku,
                    requestedQuantity,
                    quantity
            );
        }

        // Allow subclasses to check their own rules
        validateAdditionalSaleRules(saleDate);

        // Protect against other subclass restrictions
        if (!canSell(requestedQuantity, saleDate)) {
            throw new IllegalStateException(
                    "Product cannot be sold: " + sku
            );
        }
    }

    // Subclasses may override this method
    /**
     * Extension point for product-specific validation.
     *
     * PerishableProduct overrides this method to reject
     * sales occurring after the product's expiry date.
     */
    protected void validateAdditionalSaleRules(
            LocalDate saleDate
    ) {
        // Regular products need no additional checks.
    }

    // Deduct stock only after validation succeeds
    public final void sell(
            int requestedQuantity,
            LocalDate saleDate
    ) {
        validateSale(requestedQuantity, saleDate);

        quantity -= requestedQuantity;
    }
}