
package com.retailflow.sales;

import com.retailflow.domain.Product;
import com.retailflow.inventory.Inventory;
import com.retailflow.sales.discount.DiscountStrategy;
import com.retailflow.sales.discount.NoDiscountStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Sale {

    private final Inventory inventory;
    private final LocalDate saleDate;
    private final List<SaleItem> items = new ArrayList<>();

    // NEW: Interchangeable discount policy
    private final DiscountStrategy discountStrategy;

    private boolean checkedOut;

    // Existing constructor: preserves no-discount behavior
    public Sale(Inventory inventory, LocalDate saleDate) {
        this(inventory, saleDate, new NoDiscountStrategy());
    }

    // NEW: Constructor with a discount strategy
    public Sale(
            Inventory inventory,
            LocalDate saleDate,
            DiscountStrategy discountStrategy
    ) {
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

        if (discountStrategy == null) {
            throw new IllegalArgumentException(
                    "Discount strategy is required"
            );
        }

        this.inventory = inventory;
        this.saleDate = saleDate;
        this.discountStrategy = discountStrategy;
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

    // 3. Calculate the subtotal before discounts
    public BigDecimal getSubtotal() {

        BigDecimal subtotal = BigDecimal.ZERO;

        for (SaleItem item : items) {
            subtotal = subtotal.add(item.getLineTotal());
        }

        return subtotal;
    }

    // 4. Calculate the discount using the selected strategy
    public BigDecimal getDiscountAmount() {

        BigDecimal subtotal = getSubtotal();

        BigDecimal discount =
                discountStrategy.calculateDiscount(subtotal);

        if (discount == null
                || discount.signum() < 0
                || discount.compareTo(subtotal) > 0) {

            throw new IllegalStateException(
                    "Discount strategy returned an invalid amount"
            );
        }

        return discount;
    }

    // 5. Calculate the final amount payable
    public BigDecimal getTotal() {

        return getSubtotal().subtract(getDiscountAmount());
    }

    // 6. Validate and complete checkout
    /**
     * Completes the sale after validating all selected products.
     *
     * Checkout uses two passes:
     * 1. Verify stock availability, expiry dates, and discount.
     * 2. Deduct stock only after all validations succeed.
     *
     * This prevents a failed validation on a later item
     * from causing partial stock deduction.
     */

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

            // Preserves domain-specific exceptions
            current.validateSale(
                    item.getQuantity(),
                    saleDate
            );
        }

        // Validate discount before reducing any stock
        getDiscountAmount();

        // Second pass: deduct stock after validation
        for (SaleItem item : items) {
            item.getProductReference().sell(
                    item.getQuantity(),
                    saleDate
            );
        }

        checkedOut = true;
    }

    // 7. Prevent changes after checkout
    private void ensureNotCheckedOut() {
        if (checkedOut) {
            throw new IllegalStateException(
                    "Sale has already been checked out"
            );
        }
    }
}
