package com.retailflow;

import com.retailflow.domain.Product;
import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {

        Product rice = new Product(
                "RF001",
                "Rice",
                new BigDecimal("120.00"),
                20
        );

        System.out.println("=== WELCOME TO RETAILFLOW ===");
        System.out.println("Product name: " + rice.getName());
        System.out.println("SKU:" + rice.getSku());
        System.out.println("Product price: " + rice.getPrice());
        System.out.println("Stock: " + rice.getQuantity());

        rice.restock(5);

        System.out.println("Stock after restocking: " + rice.getQuantity());
    }
}