# RetailFlow — User Stories and Acceptance Criteria

## Problem statement and user

A small retail shop needs to keep product records, check stock, avoid selling expired items, calculate a customer's bill, and retain inventory changes between program runs. The primary user is a **shop operator** using a single-terminal, console-based application. Currency is GHS. This describes the implemented `feature/submission-prep` application, not a planned full POS.

## Implemented stories

| ID | Priority | User story | Acceptance criteria / implemented evidence |
|---|---|---|---|
| US-01 | Must | As an operator, I want to view all products so that I can see current stock. | Main menu option **1** lists SKU, name, price and quantity; perishable products show expiry date; empty inventory reports no products. |
| US-02 | Must | As an operator, I want to search by SKU or name so that I can locate an item. | Option **2** matches SKU case-insensitively; otherwise performs a case-insensitive partial-name search. |
| US-03 | Must | As an operator, I want to register a regular product so that I can expand the catalogue. | Option **3 → 1** takes SKU, name, positive price, nonnegative stock; duplicate SKU is rejected and successful addition is saved. |
| US-04 | Must | As an operator, I want to register a perishable product so that expiry rules can be enforced. | Option **3 → 2** also requires an ISO `yyyy-MM-dd` expiry date; successful addition is saved. |
| US-05 | Must | As an operator, I want to restock a product so that available inventory remains accurate. | Option **4** requires an existing SKU and positive quantity; updates stock and calls repository save. |
| US-06 | Must | As an operator, I want to add multiple items to a sale so that a customer pays for several products together. | Option **5** accepts successive SKU/quantity pairs until `done`, displays line totals and subtotal, and refuses duplicate lines. |
| US-07 | Must | As an operator, I want checkout to reject unavailable or expired products so that stock rules are respected. | `Sale.checkout()` validates all items before stock deduction; insufficient stock and expired items throw distinct domain exceptions; an invalid checkout does not deduct stock. |
| US-08 | Must | As an operator, I want to give an optional percentage discount so that promotions can be applied correctly. | Option **5** accepts 0–100% or blank; Strategy classes compute discount; final total = subtotal − discount; stock deduction remains based on quantities. |
| US-09 | Must | As an operator, I want to confirm or cancel checkout so that stock is not reduced accidentally. | Option **5** asks `y/n`; cancellation leaves stock unchanged; successful confirmation deducts stock and saves inventory. |
| US-10 | Must | As an operator, I want inventory changes to persist so that I can restart the application without resetting stock. | `JsonStoreRepository` loads/saves `data/store.json`; first launch creates starter products; saved quantities are restored on next launch. |
| US-11 | Should | As an operator, I want to view products ordered by price so that I can compare low-priced and high-priced items. | Option **6** lists products sorted by ascending price via `Comparator`. |
| US-12 | Should | As an operator, I want helpful errors so that invalid actions do not silently change the system. | Duplicate SKU, bad quantity, expired product and insufficient stock are reported; invalid/corrupt JSON causes an `IOException`; save failure stops the console menu. |

## Out of scope / known limitations

- Sales are **not** saved as historical transactions or receipts; only inventory is persisted.
- No employee login, payment processing, tax model, barcode scanning, database, multi-terminal locking, or GUI is implemented.
- JSON save failures after in-memory checkout do not restore the pre-sale in-memory stock. The app is educational, not production-transactional.
- The `Inventory` class supports removal and name sorting, but the **console menu does not expose** removal or name sorting. Do not claim those operations are current menu options.

## Testing traceability

| Feature | Main JUnit test class |
|---|---|
| Product collection, SKU uniqueness, sorting | `InventoryTest` |
| Successful checkout, rejected sales | `SaleTest` |
| Stock/expiry exceptions | `SaleDomainExceptionTest` |
| Discount policies and rounding | `DiscountStrategyTest` |
| Persistence and malformed JSON | `JsonStoreRepositoryTest` |
| Console menu options, input and save calls | `ConsoleMenuTest` |

As verified on GitHub on 24 November 2026, these classes contain **41 `@Test` methods** in total (5 + 6 + 6 + 9 + 5 + 10). Execution results must be confirmed using `mvn clean test` on the submission commit; source inspection alone does not prove passing tests.
