package com.retailflow.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RegularProduct extends Product{

    public RegularProduct(
            String sku,
            String name,
            BigDecimal price,
            int quantity
    ) {
        super(sku, name, price, quantity);
    }

    @Override
    public boolean canSell(int requestedQuantity, LocalDate saleDate) {
        if (saleDate == null) {
            throw new IllegalArgumentException("sale date is required");
        }
        return hasEnoughStock(requestedQuantity);
    }
}
