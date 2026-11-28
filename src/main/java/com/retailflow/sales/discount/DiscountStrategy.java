package com.retailflow.sales.discount;

import java.math.BigDecimal;

/**
 * Defines a common contract for discount calculations.

 * Different discount strategies can be supplied to
 * a Sale without changing the Sale class itself.

 * Implementations return the discount amount,
 * not the final amount payable.
 */
@FunctionalInterface
public interface DiscountStrategy {

    BigDecimal calculateDiscount(BigDecimal subtotal);
}
