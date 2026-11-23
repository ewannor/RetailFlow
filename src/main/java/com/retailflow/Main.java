package com.retailflow;

import com.retailflow.domain.Product;
import com.retailflow.domain.RegularProduct;
import com.retailflow.domain.PerishableProduct;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        LocalDate saleDate = LocalDate.of(2026, 12, 4);

        Product rice = new RegularProduct(
                "RF001",
                "Rice",
                new BigDecimal("120.00"),
                20
        );

        Product milk = new PerishableProduct(
                "RF002",
                "Fresh Milk",
                new BigDecimal("18.50"),
                10,
                LocalDate.of(2026, 12, 5)
        );

        Product[] products = {rice, milk};

        System.out.println("=== RETAILFLOW: MILESTONE 2 ===");
        System.out.println("Sale date: " + saleDate);

        for (Product product : products) {
            System.out.println(
                    product.getSku() + " | " +
                            product.getName() + " | " +
                            "GHS " + product.getPrice() + " | " +
                            "Stock: " + product.getQuantity() + " | " +
                            "Can sell 2? " + product.canSell(2, saleDate)
            );
        }

        System.out.println(
                "Milk after expiry: " + milk.canSell(2, saleDate.plusDays(2))
        );

        System.out.println(
                "Rice: can sell 25 units? " + rice.canSell(25, saleDate)
        );


        rice.restock(5);

        System.out.println(
                "Rice stock after restocking: " + rice.getQuantity()
        );

        System.out.println(
                "Rice: can sell 25 units after restocking? " +
                        rice.canSell(25, saleDate)
        );
    }
}