package com.retailflow.domain;

import com.retailflow.exception.ExpiredProductException;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PerishableProduct extends Product {

    private final LocalDate expiryDate;

    public PerishableProduct(
            String sku,
            String name,
            BigDecimal price,
            int quantity,
            LocalDate expiryDate
    ) {
        super(sku, name, price, quantity);

        if (expiryDate == null) {
            throw new IllegalArgumentException("Expiry date is required");
        }

        this.expiryDate = expiryDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    @Override
    public boolean canSell(int requestedQuantity, LocalDate saleDate) {
        if (saleDate == null) {
            throw new IllegalArgumentException("Sale date is required");
        }

        boolean enoughStock = hasEnoughStock(requestedQuantity);
        boolean notExpired = !saleDate.isAfter(expiryDate);

        return enoughStock && notExpired;
    }

    @Override
    protected void validateAdditionalSaleRules(LocalDate saleDate) {
        if (saleDate.isAfter(expiryDate)) {
            throw new ExpiredProductException(
                    getSku(),
                    expiryDate,
                    saleDate
            );
        }
    }
}
