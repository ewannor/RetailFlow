
package com.retailflow.sales;

import com.retailflow.domain.Product;
import java.math.BigDecimal;

public final class SaleItem {

    private final Product product;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal lineTotal;

    SaleItem(Product product, int quantity) {

        if (product == null) {
            throw new IllegalArgumentException("Product is required");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be positive"
            );
        }

        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();

        this.lineTotal = unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }

    public String getSku() {
        return product.getSku();
    }

    public String getProductName() {
        return product.getName();
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    Product getProductReference() {
        return product;
    }
}
