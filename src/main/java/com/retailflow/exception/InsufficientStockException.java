package com.retailflow.exception;

public final class InsufficientStockException
        extends IllegalStateException {

    private final String sku;
    private final int requestedQuantity;
    private final int availableQuantity;

    public InsufficientStockException(
            String sku,
            int requestedQuantity,
            int availableQuantity
    ) {
        super(
                "Insufficient stock for " + sku
                        + ": requested " + requestedQuantity
                        + ", available " + availableQuantity
        );

        this.sku = sku;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public String getSku() {
        return sku;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }
}
