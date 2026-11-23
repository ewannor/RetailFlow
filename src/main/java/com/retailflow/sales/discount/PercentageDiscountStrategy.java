package com.retailflow.sales.discount;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PercentageDiscountStrategy
        implements DiscountStrategy {

    private static final BigDecimal HUNDRED =
            new BigDecimal("100");

    private final BigDecimal percentage;

    public PercentageDiscountStrategy(BigDecimal percentage) {

        if (percentage == null
                || percentage.signum() < 0
                || percentage.compareTo(HUNDRED) > 0) {

            throw new IllegalArgumentException(
                    "Percentage must be between 0 and 100"
            );
        }

        this.percentage = percentage;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    @Override
    public BigDecimal calculateDiscount(BigDecimal subtotal) {

        if (subtotal == null || subtotal.signum() < 0) {
            throw new IllegalArgumentException(
                    "Subtotal must not be null or negative"
            );
        }

        int scale = Math.max(2, subtotal.scale());

        return subtotal.multiply(percentage)
                .divide(HUNDRED, scale, RoundingMode.HALF_UP);
    }
}
