
package com.retailflow.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.retailflow.domain.PerishableProduct;
import com.retailflow.domain.Product;
import com.retailflow.domain.RegularProduct;
import com.retailflow.inventory.Inventory;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

public final class JsonStoreRepository implements StoreRepository {

    private static final int FORMAT_VERSION = 1;

    private final Path file;
    private final ObjectMapper mapper;

    public JsonStoreRepository(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("File path is required");
        }

        this.file = file.toAbsolutePath().normalize();
        this.mapper = new ObjectMapper();
    }

    @Override
    public void save(Inventory inventory) throws IOException {

        if (inventory == null) {
            throw new IllegalArgumentException("Inventory is required");
        }

        ObjectNode root = mapper.createObjectNode();
        root.put("formatVersion", FORMAT_VERSION);

        ArrayNode products = root.putArray("products");

        for (Product product : inventory.getAllProducts()) {

            ObjectNode item = products.addObject();

            if (product instanceof PerishableProduct perishable) {

                item.put("type", "PERISHABLE");

                item.put(
                        "expiryDate",
                        perishable.getExpiryDate().toString()
                );

            } else if (product instanceof RegularProduct) {

                item.put("type", "REGULAR");

            } else {
                throw new IOException(
                        "Unsupported product type: "
                                + product.getClass().getName()
                );
            }

            item.put("sku", product.getSku());
            item.put("name", product.getName());
            item.put("price", product.getPrice().toPlainString());
            item.put("quantity", product.getQuantity());
        }

        Path directory = file.getParent();

        Files.createDirectories(directory);

        // Write to a temporary file first
        Path tempFile = Files.createTempFile(
                directory,
                "retailflow-",
                ".tmp"
        );

        try {
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(tempFile.toFile(), root);

            try {
                Files.move(
                        tempFile,
                        file,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );

            } catch (AtomicMoveNotSupportedException e) {

                Files.move(
                        tempFile,
                        file,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Override
    public Inventory load() throws IOException {

        // First launch: no saved inventory yet
        if (Files.notExists(file)) {
            return new Inventory();
        }

        JsonNode root = mapper.readTree(file.toFile());

        if (root == null || !root.isObject()) {
            throw new IOException("Invalid store JSON structure");
        }

        JsonNode version = root.get("formatVersion");

        if (version == null
                || !version.isInt()
                || version.intValue() != FORMAT_VERSION) {

            throw new IOException(
                    "Unsupported or missing JSON format version"
            );
        }

        JsonNode products = root.get("products");

        if (products == null || !products.isArray()) {
            throw new IOException(
                    "Missing or invalid products array"
            );
        }

        Inventory inventory = new Inventory();

        for (int i = 0; i < products.size(); i++) {

            JsonNode item = products.get(i);

            try {
                if (!item.isObject()) {
                    throw new IllegalArgumentException(
                            "Product entry must be an object"
                    );
                }

                String type = requiredText(item, "type");
                String sku = requiredText(item, "sku");
                String name = requiredText(item, "name");

                BigDecimal price = new BigDecimal(
                        requiredText(item, "price")
                );

                int quantity = requiredInt(item, "quantity");

                Product product;

                switch (type) {

                    case "REGULAR" -> product = new RegularProduct(
                            sku,
                            name,
                            price,
                            quantity
                    );

                    case "PERISHABLE" -> product = new PerishableProduct(
                            sku,
                            name,
                            price,
                            quantity,
                            LocalDate.parse(
                                    requiredText(item, "expiryDate")
                            )
                    );

                    default -> throw new IllegalArgumentException(
                            "Unknown product type: " + type
                    );
                }

                inventory.addProduct(product);

            } catch (RuntimeException e) {

                throw new IOException(
                        "Invalid product at JSON index " + i,
                        e
                );
            }
        }

        return inventory;
    }

    private static String requiredText(
            JsonNode node,
            String field
    ) {

        JsonNode value = node.get(field);

        if (value == null || !value.isTextual()) {
            throw new IllegalArgumentException(
                    "Missing or invalid field: " + field
            );
        }

        return value.textValue();
    }

    private static int requiredInt(
            JsonNode node,
            String field
    ) {

        JsonNode value = node.get(field);

        if (value == null || !value.isInt()) {
            throw new IllegalArgumentException(
                    "Missing or invalid integer: " + field
            );
        }

        return value.intValue();
    }
}
