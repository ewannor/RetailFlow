# Checkout Sequence — RetailFlow

The following shows the current interactive checkout workflow (`ConsoleMenu` option 5), including both stock-validation passes and the JSON persistence call. `StoreRepository` is the interface; `JsonStoreRepository` provides the actual JSON implementation.

```mermaid
sequenceDiagram
    actor Operator
    participant UI as ConsoleMenu
    participant Sale
    participant Inventory
    participant Product
    participant Strategy as DiscountStrategy
    participant Repo as StoreRepository

    Operator->>UI: Select 5; add SKUs and quantities
    UI->>Sale: new Sale(inventory, date, strategy)
    loop Each selected line
        UI->>Sale: addItem(sku, quantity)
        Sale->>Inventory: findBySku(sku)
        Inventory-->>Sale: Product
    end
    UI->>Sale: getSubtotal(), getDiscountAmount(), getTotal()
    Sale->>Strategy: calculateDiscount(subtotal)
    Strategy-->>Sale: discount
    Sale-->>UI: summary totals
    Operator->>UI: Confirm checkout (y)
    UI->>Sale: checkout()
    loop Validation pass — all items
        Sale->>Inventory: findBySku(sku)
        Inventory-->>Sale: Product
        Sale->>Product: validateSale(quantity, saleDate)
        Product-->>Sale: valid or domain exception
    end
    Sale->>Strategy: calculateDiscount(subtotal)
    Strategy-->>Sale: validated discount
    loop Deduction pass — only after all validations
        Sale->>Product: sell(quantity, saleDate)
    end
    Sale-->>UI: checkout completed
    UI->>Repo: save(inventory)
    Repo-->>UI: written or IOException
    UI-->>Operator: Checkout/saving result
```

**Failure paths:** If validation fails, the exception propagates to `ConsoleMenu`, which reports an operation rejection and does not call `save()`. If `save()` fails **after** checkout, the in-memory stock has already changed. The program reports the `IOException`; this file-based approach is not a fully atomic transaction across memory and disk.
