package com.retailflow;

import com.retailflow.domain.Product;
import com.retailflow.domain.RegularProduct;
import com.retailflow.domain.PerishableProduct;
import com.retailflow.inventory.Inventory;
import com.retailflow.sales.Sale;
import com.retailflow.sales.SaleItem;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        Inventory inventory = new Inventory();

        //Create and add products
        inventory.addProduct(new RegularProduct(
                "RF001", "Rice",
                new BigDecimal("120.00"), 20
        ));

        inventory.addProduct(new PerishableProduct(
                "RF002", "Fresh Milk",
                new BigDecimal("18.50"), 10,
                LocalDate.of(2026, 12, 5)
        ));

        inventory.addProduct(new RegularProduct(
                "RF003", "Soap",
                new BigDecimal("12.00"), 35
        ));

        // 2. Create a sale
        LocalDate saleDate = LocalDate.of(2026, 12, 4);

        Sale sale = new Sale(inventory, saleDate);

        // 3. Add items to the sale
        sale.addItem("RF001", 2);
        sale.addItem("rf002", 3);
        sale.addItem("RF003", 1);

        // 4. Display sale details

        System.out.println("=== RETAILFLOW: MILESTONE 4 ===");
        System.out.println("Sale date: " + sale.getSaleDate());
        System.out.println("Items: ");

        for (SaleItem item : sale.getItems()) {
            System.out.println(
                    item.getSku() + " | " +
                            item.getProductName() + " | " +
                            item.getQuantity() + " x GHS " +
                            item.getUnitPrice() + " = GHS " +
                            item.getLineTotal()
            );
        }

        System.out.println("Total: GHS " + sale.getTotal());

        // 5. Complete checkout
        sale.checkout();

        System.out.println(
                "Checkout successful: " + sale.isCheckedOut()
        );

        // 6. Show remaining inventory quantities
        for (String sku : new String[]{"RF001", "RF002", "RF003"}) {

            Product product = inventory.findBySku(sku)
                    .orElseThrow();

            System.out.println(
                    product.getName() +
                            " stock remaining: " + product.getQuantity()
            );
        }

        // 7. Try to sell expired milk
        Sale expiredSale = new Sale(
                inventory,
                LocalDate.of(2026, 12, 6)
        );

        expiredSale.addItem("RF002", 1);

        try {
            expiredSale.checkout();
        } catch (IllegalStateException e) {
            System.out.println(
                    "Expired sale rejected: " + e.getMessage()
            );
        }

        // 8. Test insufficient stock without partial deduction
        Sale insufficientSale = new Sale(inventory, saleDate);

        insufficientSale.addItem("RF001", 1);
        insufficientSale.addItem("RF002", 8);

        try {
            insufficientSale.checkout();
        } catch (IllegalStateException e) {
            System.out.println(
                    "Insufficient-stock sale rejected: "
                            + e.getMessage()
            );
        }

        System.out.println(
                "Rice stock after rejected sale: " +
                        inventory.findBySku("RF001")
                                .orElseThrow().getQuantity()
        );

        System.out.println(
                "Milk stock after rejected sales: " +
                        inventory.findBySku("RF002")
                                .orElseThrow().getQuantity()
        );

        // 9. Prevent a completed sale from being checked out twice
        try {
            sale.checkout();
        } catch (IllegalStateException e) {
            System.out.println(
                    "Repeat checkout rejected: " + e.getMessage()
            );
        }

    }
}