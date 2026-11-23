
# RetailFlow

RetailFlow is a Java-based retail inventory and sales management
system developed to demonstrate Object-Oriented Programming (OOP) princeples for the Programming 2 OOP assignment at the University of Pécs.

## Technologies
- Java 26
- Maven
- Jackson JSON Library
- JUnit 5
- IntelliJ IDEA
- Git and GitHub
## Project Architecture

### Domain Package

Contains:
- Product
- RegularProduct
- PerishableProduct

Demonstrates inheritance, abstraction, encapsulation,
and polymorphism.

### Inventory Package

Contains:
- Inventory

Manages products using collections and generics.

### Sales Package

Contains:
- Sale
- SaleItem

Handles sales calculations, checkout validation,
and stock deduction.

### Persistence Package

Contains:
- StoreRepository
- JsonStoreRepository

Implements the Repository Pattern for JSON-based
inventory persistence using Jackson.

## JSON Storage

Inventory is stored locally in:

data/store.json

The file contains:
- Product type
- SKU
- Product name
- Price
- Quantity
- Expiry date, where applicable

The file is automatically created on first launch.

The current implementation saves product and inventory
data, including stock changes after successful sales.

Permanent sales history is not yet implemented.

## Running RetailFlow

1. Clone the repository.
2. Open the project in IntelliJ IDEA.
3. Load the Maven project.
4. Configure the project JDK.
5. Run com.retailflow.Main.

On first launch, starter inventory is created.

Subsequent launches restore saved inventory from JSON.

To run the demonstration checkout, set the program
argument to:

demo-sale

After checkout, the updated inventory is saved.

### Interactive Console Menu

RetailFlow provides an interactive console interface.

Run `com.retailflow.Main` without program arguments
to open the main menu.

Available operations:

1. View all products
2. Search by SKU or product name
3. Add regular or perishable products
4. Restock products
5. Create and complete sales with optional discounts
6. Sort products by price
7. Exit

Input is validated using the existing domain and
business rules.

Product additions, stock replenishments, and
successful checkouts are saved to `data/store.json`.

Discounts use the Strategy Pattern through
DiscountStrategy and its implementations.

### Demonstration Modes

The previous program arguments remain supported:

- `demo-sale`
- `demo-sale discount10`

The interactive menu is the default when no
program arguments are provided.

### Console Testing

ConsoleMenuTest uses simulated input and an
in-memory repository test double.

Run the console tests:

    mvn -Dtest=ConsoleMenuTest test

Run all tests:

    mvn clean test

### Known Limitation

The application saves inventory after successful
business operations. A file-write failure can
leave in-memory changes unsaved.

Concurrent checkout and durable sales-history
storage are not supported.

## Automated Testing

Run all tests:

    mvn clean test

Build the project:

    mvn clean package

The test suite covers:
- Inventory operations
- Duplicate SKU validation
- Searching and sorting
- Checkout calculations
- Expiry validation
- Insufficient stock
- Repeated checkout prevention
- JSON saving and loading
- Corrupted data handling

## Design Patterns

### Repository Pattern

StoreRepository defines the persistence operations.

JsonStoreRepository implements those operations using
Jackson and local JSON file storage.

The design separates business logic from storage logic.


### Strategy Pattern — Discount Calculation

RetailFlow uses the Strategy Pattern to support
interchangeable discount policies.

Participants:
- DiscountStrategy: common discount interface
- NoDiscountStrategy: calculates no discount
- PercentageDiscountStrategy: applies a percentage
- Sale: context class that uses the selected strategy

The Sale class depends on the DiscountStrategy interface
rather than implementing individual discount algorithms.

By default, sales use NoDiscountStrategy to preserve
their original behavior.

A percentage-based strategy can be supplied through
the Sale constructor.

This design supports the Open/Closed Principle because
new discount policies can be added without modifying
the existing Sale calculation logic.

The strategy affects the amount payable, not the
quantity deducted from inventory.

To demonstrate the two options in IntelliJ IDEA:

Normal checkout:
demo-sale

Checkout with 10% discount:
demo-sale discount10

Discounted sales are not yet stored as permanent
transaction records. JSON persistence currently
stores the updated inventory.


## OOP Concepts

- Classes and objects
- Constructors
- Encapsulation
- Inheritance
- Abstraction
- Polymorphism
- Composition
- Collections and generics
- Exceptions
- Interfaces
- Repository Pattern
- File input/output
- Automated testing

## Project Milestones

1. Product Model
2. Product Types
3. Inventory Management
4. Sales and Checkout
5. JSON Persistence, Testing and Documentation

## Current Limitations

- Console-based application
- No graphical user interface
- No database
- No permanent sales transaction history
- No multi-user checkout support
- No transaction rollback after file-save failures
- No authentication or external payments

## Purpose

RetailFlow is an educational Java OOP project that
demonstrates how software engineering concepts can
be applied to a retail management application.