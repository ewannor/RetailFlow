
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