package com.retailflow.sales.discount;

import java.math.BigDecimal;

@FunctionalInterface
public interface DiscountStrategy {

    BigDecimal calculateDiscount(BigDecimal subtotal);
}
