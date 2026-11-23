# RetailFlow — UML Class Diagram

**Source:** `feature/submission-prep` (verified 24 November 2026). This diagram reflects the main production classes in `src/main/java/com/retailflow` and excludes JUnit test classes and library internals.

The diagram below is **viewable directly on GitHub** and the matching `.mmd` file is editable Mermaid source.

```mermaid
classDiagram
    direction TB

    class Main {
        +main(String[] args) void
    }

    class ConsoleMenu {
        -Inventory inventory
        -StoreRepository repository
        -Scanner input
        -PrintStream output
        -Clock clock
        +run() void
        -searchProduct() void
        -addProduct() void
        -restockProduct() void
        -createSale() void
    }

    class Product {
        <<abstract>>
        -String sku
        -String name
        -BigDecimal price
        -int quantity
        +getSku() String
        +getName() String
        +getPrice() BigDecimal
        +getQuantity() int
        +restock(int amount) void
        +canSell(int quantity, LocalDate date) boolean
        +validateSale(int quantity, LocalDate date) void
        +sell(int quantity, LocalDate date) void
        #validateAdditionalSaleRules(LocalDate date) void
    }
    class RegularProduct {
        +canSell(int quantity, LocalDate date) boolean
    }
    class PerishableProduct {
        -LocalDate expiryDate
        +getExpiryDate() LocalDate
        +canSell(int quantity, LocalDate date) boolean
        #validateAdditionalSaleRules(LocalDate date) void
    }

    class Inventory {
        -List~Product~ products
        +addProduct(Product product) void
        +findBySku(String sku) Optional~Product~
        +searchByName(String keyword) List~Product~
        +getAllProducts() List~Product~
        +getProductsSortedByPrice() List~Product~
        +getProductsSortedByName() List~Product~
        +restockProduct(String sku, int amount) void
        +removeProduct(String sku) boolean
        +getProductCount() int
    }

    class Sale {
        -Inventory inventory
        -LocalDate saleDate
        -List~SaleItem~ items
        -DiscountStrategy discountStrategy
        -boolean checkedOut
        +addItem(String sku, int quantity) void
        +getItems() List~SaleItem~
        +getSubtotal() BigDecimal
        +getDiscountAmount() BigDecimal
        +getTotal() BigDecimal
        +checkout() void
        +isCheckedOut() boolean
    }

    class SaleItem {
        -Product product
        -int quantity
        -BigDecimal unitPrice
        -BigDecimal lineTotal
        +getSku() String
        +getProductName() String
        +getQuantity() int
        +getUnitPrice() BigDecimal
        +getLineTotal() BigDecimal
    }

    class DiscountStrategy {
        <<interface>>
        +calculateDiscount(BigDecimal subtotal) BigDecimal
    }
    class NoDiscountStrategy {
        +calculateDiscount(BigDecimal subtotal) BigDecimal
    }
    class PercentageDiscountStrategy {
        -BigDecimal percentage
        +getPercentage() BigDecimal
        +calculateDiscount(BigDecimal subtotal) BigDecimal
    }

    class StoreRepository {
        <<interface>>
        +load() Inventory
        +save(Inventory inventory) void
    }
    class JsonStoreRepository {
        -Path file
        -ObjectMapper mapper
        +load() Inventory
        +save(Inventory inventory) void
    }

    class InsufficientStockException {
        -String sku
        -int requestedQuantity
        -int availableQuantity
        +getSku() String
        +getRequestedQuantity() int
        +getAvailableQuantity() int
    }
    class ExpiredProductException {
        -String sku
        -LocalDate expiryDate
        -LocalDate saleDate
        +getSku() String
        +getExpiryDate() LocalDate
        +getSaleDate() LocalDate
    }

    Product <|-- RegularProduct
    Product <|-- PerishableProduct
    Inventory "1" o-- "0..*" Product : manages
    Sale "1" *-- "0..*" SaleItem : contains
    Sale "0..*" --> "1" Inventory : checks
    SaleItem "0..*" --> "1" Product : references

    DiscountStrategy <|.. NoDiscountStrategy
    DiscountStrategy <|.. PercentageDiscountStrategy
    Sale "0..*" --> "1" DiscountStrategy : uses

    StoreRepository <|.. JsonStoreRepository
    JsonStoreRepository ..> Inventory : reads and writes
    ConsoleMenu --> Inventory : uses
    ConsoleMenu --> StoreRepository : saves
    ConsoleMenu ..> Sale : creates
    Main ..> ConsoleMenu : starts
    Main ..> JsonStoreRepository : constructs

    Product ..> InsufficientStockException : throws
    PerishableProduct ..> ExpiredProductException : throws
```

## Relationships and multiplicities

| Relationship | UML interpretation |
|---|---|
| `Product` ⇢ `RegularProduct` / `PerishableProduct` | Abstract superclass and two concrete subclasses; overridden eligibility rules enable runtime polymorphism. |
| `Inventory` ◇— `Product` | Shared aggregation: one inventory manages **0..\*** product objects. Products are also referenced by sale items. |
| `Sale` ◆— `SaleItem` | Composition: one sale creates and owns **0..\*** line items (an empty sale cannot check out). |
| `SaleItem` → `Product` | Each line item references exactly one product; the same product can appear in different sales. |
| `Sale` → `Inventory` | Each sale uses exactly one inventory for validation and deduction. |
| `Sale` → `DiscountStrategy` | Each sale has exactly one strategy; a strategy implementation can be shared by multiple sales. |
| `NoDiscountStrategy` / `PercentageDiscountStrategy` ⇢ `DiscountStrategy` | Interface realization (Strategy Pattern). |
| `JsonStoreRepository` ⇢ `StoreRepository` | Interface realization (Repository abstraction). |
| `ConsoleMenu` → `Inventory` / `StoreRepository` | Console delegates to existing business and storage objects. |
| `Product` / `PerishableProduct` ⇢ custom exceptions | Dependencies: methods throw specific exceptions on stock or expiry violations. |

Notes: The UML focuses on important operations, not every private helper or every external Java/Jackson class. It depicts current in-memory object relationships; saved JSON contains inventory, not historical `Sale` objects. The `0..*` on SaleItem reflects an in-progress sale; checkout rejects an empty list.
