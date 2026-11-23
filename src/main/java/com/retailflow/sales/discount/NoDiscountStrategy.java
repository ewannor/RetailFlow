package com.retailflow.sales.discount;

import java.math.BigDecimal;

public final class NoDiscountStrategy
        implements DiscountStrategy {

    @Override
    public BigDecimal calculateDiscount(BigDecimal subtotal) {

        if (subtotal == null || subtotal.signum() < 0) {
            throw new IllegalArgumentException(
                    "Subtotal must not be null or negative"
            );
        }

        return BigDecimal.ZERO;
    }
}
