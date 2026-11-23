# RetailFlow — Design Decisions and Patterns

## Problem and scope

RetailFlow supports a single shop operator using a Java console to catalogue regular and perishable products, search inventory, restock, create multi-line sales, apply optional percentage discounts, and save updated stock to a JSON file. The project demonstrates deliberate object-oriented design rather than a production POS system.

## Object-oriented decisions

1. **Encapsulation and validity:** `Product` has private fields, validates construction, and exposes `restock`, `sell`, and `validateSale` instead of exposing a public setter for quantity.
2. **Abstraction and polymorphism:** Abstract `Product` defines common fields and methods. `RegularProduct` and `PerishableProduct` override `canSell`; the latter additionally checks expiry via `validateAdditionalSaleRules`.
3. **Collections and sorting:** `Inventory` stores `List<Product>`, checks duplicate SKUs, uses `Optional<Product>` for lookups, and returns copies for sorted views and collection snapshots.
4. **Composition and association:** `Sale` creates and owns its `SaleItem` records; each item references an existing `Product` and stores a price/quantity snapshot. `Sale` references `Inventory` for stock validation.
5. **Domain exceptions:** `InsufficientStockException` and `ExpiredProductException` are unchecked exceptions that extend `IllegalStateException` and carry useful contextual fields. This allows legacy `IllegalStateException` handlers to remain compatible.

## Required design pattern — Strategy

**Problem:** Different promotions should calculate a discount without growing a conditional chain inside `Sale`.

**Participants in source code:**
- `DiscountStrategy`: `calculateDiscount(BigDecimal subtotal)` contract
- `NoDiscountStrategy`: returns zero discount
- `PercentageDiscountStrategy`: returns a rounded percentage discount; rejects percentages outside 0–100
- `Sale`: the *context*, accepts a `DiscountStrategy` in a constructor and uses it for `getDiscountAmount()` and `getTotal()`

**Benefit:** Strategies are interchangeable through a common interface (runtime polymorphism). `Sale` can accept additional discount algorithms without replacing its existing monetary calculation or stock-checking code. The two-argument constructor chooses `NoDiscountStrategy` by default, preserving backward compatibility.

**Example:** GHS 258.50 subtotal with 10% strategy yields GHS 25.85 discount and GHS 232.65 payable. The discount does not change the number of units deducted.

## Persistence abstraction — Repository

**Participants:** `StoreRepository` declares `load()` and `save(Inventory)`; `JsonStoreRepository` implements that contract with Jackson. `Main` and `ConsoleMenu` call the interface instead of doing JSON parsing themselves. This isolates storage details and allows an alternative persistence implementation to use the same interface.

**JSON representation:** A versioned root object with a `products` array. Each item has type, SKU, name, price as a decimal string, quantity, and an optional expiry date. This file contains **inventory only**, not historical sales.

## Validation and checkout sequence

`Sale.checkout()` verifies the sale is open and nonempty. It then validates **every** line against the current inventory and product-specific rules, verifies the discount amount, and only then deducts stock. `ConsoleMenu` calls `StoreRepository.save()` after a successful checkout.

The two-pass validation avoids deducting earlier products when a later item fails eligibility. It does **not** guarantee a database-style atomic transaction. The application is single-process and in-memory; a later file-save failure may leave memory changed and disk outdated.

## Separation of concerns

- `Main`: initializes repository, loads/creates starter inventory, starts console or demo mode.
- `ConsoleMenu`: input prompts, display, and delegation to domain methods.
- `Inventory`: catalogue, lookup, sorting, and stock replenishment.
- `Sale` and `SaleItem`: totals, discount delegation, validation and checkout.
- `Product` hierarchy: product validation and expiry behavior.
- `StoreRepository` / `JsonStoreRepository`: storage contract and JSON implementation.

## Known limitations and future work

- In-memory stock changes are not rolled back if `repository.save()` fails.
- No concurrent-user transaction isolation, database, payment gateway or receipt history.
- Prices are `BigDecimal`, but user input is not globally normalized to exactly two decimal places.
- Existing demo-sale arguments and console menu are different entry modes; menu is default when no arguments are provided.
- The console has no interactive product deletion or name sorting (although domain methods exist).
- The UI can show exceptions to the operator rather than prompting repeatedly for every invalid input.

## Why these choices suit the assignment

The design demonstrates private state, protected construction, abstract classes and interfaces, overridden behaviors, association/composition with multiplicities, generics and sorting, custom exceptions, JSON persistence, testability, and at least one recognized pattern (`Strategy`). See `uml-class-diagram.md` and `user-stories.md`.
