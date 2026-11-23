package com.retailflow.exception;

import java.time.LocalDate;

public final class ExpiredProductException
        extends IllegalStateException {

    private final String sku;
    private final LocalDate expiryDate;
    private final LocalDate saleDate;

    public ExpiredProductException(
            String sku,
            LocalDate expiryDate,
            LocalDate saleDate
    ) {
        super(
                "Product " + sku
                        + " expired on " + expiryDate
                        + " and cannot be sold on " + saleDate
        );

        this.sku = sku;
        this.expiryDate = expiryDate;
        this.saleDate = saleDate;
    }

    public String getSku() {
        return sku;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }
}
