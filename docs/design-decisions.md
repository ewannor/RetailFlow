# RetailFlow — Design Decisions and Patterns

## Problem and Scope

RetailFlow is a Java console application designed for a single retail shop operator. It supports regular and perishable products, inventory searching, restocking, multi-item sales, optional percentage discounts, and JSON-based inventory persistence.

The project focuses on demonstrating object-oriented programming principles and software design patterns rather than developing a complete commercial point-of-sale system.

## Object-Oriented Decisions

1. **Encapsulation and Validation:** The `Product` class uses private fields and constructor validation to maintain valid product information. Stock quantities are modified through `restock()` and `sell()`, while `validateSale()` checks whether a sale is permitted.

2. **Abstraction and Polymorphism:** The abstract `Product` class defines common product attributes and methods. `RegularProduct` and `PerishableProduct` override `canSell()`, while `PerishableProduct` also implements expiry validation through `validateAdditionalSaleRules()`.

3. **Collections and Sorting:** `Inventory` manages products using `List<Product>`, prevents duplicate SKUs, and uses `Optional<Product>` for product lookups. It provides sorted results and unmodifiable list snapshots without duplicating the underlying product objects.

4. **Composition and Association:** `Sale` creates and owns its `SaleItem` objects. Each sale item references an existing `Product` and stores its quantity and unit price at the time of creation. `Sale` also references `Inventory` for stock validation and deduction.

5. **Domain Exceptions:** `InsufficientStockException` and `ExpiredProductException` extend `IllegalStateException`. They provide specific error information while remaining compatible with general `IllegalStateException` handling.

## Required Design Pattern — Strategy

**Problem:** Different discount policies should be supported without introducing multiple conditional statements into the `Sale` class.

**Participants:**

- `DiscountStrategy`: Defines the `calculateDiscount(BigDecimal subtotal)` method.
- `NoDiscountStrategy`: Returns a zero discount.
- `PercentageDiscountStrategy`: Calculates percentage discounts using `BigDecimal` and `RoundingMode.HALF_UP`, with percentages restricted to 0–100.
- `Sale`: Acts as the context and uses the selected strategy to calculate discounts and final totals.

**Design Benefit:** The Strategy Pattern allows different discount algorithms to be used through a common interface. Additional discount policies can be implemented without modifying the existing sales calculations.

The two-argument `Sale` constructor uses `NoDiscountStrategy` by default, preserving the original behavior.

**Example:** For a subtotal of GHS 258.50, a 10% discount produces GHS 25.85 in savings and a final payable amount of GHS 232.65. The discount does not affect the quantity deducted from inventory.

## Persistence Abstraction — Repository

The Repository Pattern separates inventory persistence from the application's business logic.

- `StoreRepository` defines the `load()` and `save(Inventory)` operations.
- `JsonStoreRepository` implements these operations using Jackson.
- `Main` initializes the repository, while `ConsoleMenu` interacts with the repository interface.

This separation allows the persistence implementation to be changed without requiring the console interface to handle JSON operations directly.

**JSON Representation:** Inventory data is stored in a versioned JSON object containing a `products` array. Each product record contains its type, SKU, name, price, quantity, and expiry date where applicable.

The JSON file stores inventory information only; historical sales transactions are not persisted.

## Validation and Checkout Sequence

The `Sale.checkout()` method first verifies that the sale has not already been completed and contains at least one item.

Checkout then follows two main stages:

1. **Validation:** Every sale item is checked against the inventory, including stock availability, product-specific restrictions, and discount validity.
2. **Stock Deduction:** After validation succeeds, the required quantities are deducted from the corresponding products.

Following successful checkout, `ConsoleMenu` saves the updated inventory through `StoreRepository`.

This approach reduces the risk of partial stock deduction when an item fails validation. However, it does not provide complete transaction atomicity. A file-save failure may leave the in-memory inventory changed while the stored JSON data remains outdated.

## Separation of Concerns

The application separates responsibilities across its principal components:

- **`Main`:** Initializes the application, repository, inventory, and execution mode.
- **`ConsoleMenu`:** Manages user input, console output, and interaction with business operations.
- **`Inventory`:** Handles product collections, searching, sorting, and restocking.
- **`Sale` and `SaleItem`:** Manage sale items, calculations, validation, and checkout.
- **`Product` hierarchy:** Defines product attributes, stock rules, and expiry validation.
- **`StoreRepository` and `JsonStoreRepository`:** Define and implement inventory persistence.

This separation improves code organization, maintainability, and testability.

## Known Limitations

- Inventory changes are not rolled back if saving to JSON fails.
- The application does not support concurrent users or database transactions.
- Payment processing and permanent sales or receipt history are not implemented.
- Prices use `BigDecimal`, but user input is not consistently restricted to two decimal places.
- Product deletion and name sorting are supported by `Inventory` but are not exposed through the console menu.
- Some invalid inputs display an error and return to the main menu rather than repeating the input prompt.

## Why These Choices Suit the Assignment

RetailFlow demonstrates key object-oriented programming concepts, including encapsulation, constructor validation, abstraction, inheritance, polymorphism, and object relationships.

The application also incorporates collections, generics, sorting, custom exceptions, JSON persistence, automated testing, and the Strategy Design Pattern.

These decisions demonstrate how Java OOP principles can be applied to a practical retail management application.

For additional details, see the [UML Class Diagram](uml-class-diagram.md) and [User Stories and Acceptance Criteria](user-stories.md).