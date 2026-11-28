# RetailFlow

RetailFlow is a Java-based retail inventory and sales management system developed to demonstrate Object-Oriented Programming (OOP) principles for the Programming 2 assignment at the University of Pécs. The project demonstrates how core Java concepts, design patterns, file handling, and automated testing can be applied to a practical retail management application.

## Problem Statement

Small retail businesses need to maintain accurate inventory records and process sales efficiently. However, manual inventory management can lead to stock discrepancies, overselling, and difficulties identifying expired products as well. 

RetailFlow aims to address these challenges by providing a Java-based inventory and sales management system that allows a single operator to manage products, monitor stock levels, and process sales. 

## Technologies

The project uses the following technologies:

- **Java 26** — Application development and OOP implementation.
- **Maven** — Project management, dependency management, and build automation.
- **Jackson JSON Library** — Serialization and deserialization of inventory data.
- **JUnit 5** — Automated unit testing.
- **IntelliJ IDEA** — Integrated Development Environment (IDE).
- **Git and GitHub** — Version control and repository management.

## Project Architecture

RetailFlow follows a modular, object-oriented design. The main components are organized into packages that separate product management, inventory operations, sales processing, and persistence.

### Domain Package

**Main classes:**

- `Product`
- `RegularProduct`
- `PerishableProduct`

This package defines the product model and its different product types.

It demonstrates inheritance, abstraction, encapsulation, and polymorphism.

### Inventory Package

**Main class:**

- `Inventory`

Manages the collection of products and provides operations for adding, searching, restocking, and sorting products.

It demonstrates the use of Java collections, generics, and business-rule validation.

### Sales Package

**Main classes:**

- `Sale`
- `SaleItem`

Handles sales transactions, checkout calculations, stock validation, and stock deduction.

It prevents transactions involving insufficient stock or expired products.

### Persistence Package

**Main components:**

- `StoreRepository`
- `JsonStoreRepository`

Provides JSON-based inventory persistence using the Jackson library.

This package separates data storage operations from the application's business logic.

## Design Patterns

RetailFlow implements the Repository and Strategy design patterns to improve code organization, maintainability, and extensibility.

### Repository Pattern

The Repository Pattern separates data storage operations from the rest of the application.

- `StoreRepository` defines the persistence operations.
- `JsonStoreRepository` implements these operations using Jackson and local JSON file storage.

This allows business logic to interact with a repository interface rather than depending directly on the JSON storage implementation.

### Strategy Pattern — Discount Calculation

RetailFlow uses the Strategy Pattern to support interchangeable discount policies.

**Participants:**

- `DiscountStrategy` — Common interface defining discount calculation behavior.
- `NoDiscountStrategy` — Applies no discount.
- `PercentageDiscountStrategy` — Applies a percentage-based discount.
- `Sale` — Context class that uses the selected discount strategy.

The `Sale` class depends on the `DiscountStrategy` interface rather than implementing individual discount algorithms.

By default, sales use `NoDiscountStrategy` to preserve their original behavior. A percentage-based discount strategy can be supplied through the `Sale` constructor.

This approach supports the **Open/Closed Principle**, allowing new discount policies to be introduced without modifying the existing sales calculation logic.

Discounts affect the amount payable but do not change the quantities deducted from inventory.

## Design Documentation

Additional documentation is available in the `docs` directory.

- [UML Class Diagram](docs/uml-class-diagram.md)
- [Checkout Sequence Diagram](docs/checkout-sequence.md)
- [User Stories and Acceptance Criteria](docs/user-stories.md)
- [Design Decisions and Patterns](docs/design-decisions.md)
- [Verification Plan](docs/test-plan.md)

These documents describe the system's class relationships, checkout process, functional requirements, design decisions, and testing approach.

## OOP Concepts

RetailFlow demonstrates the following Java and object-oriented programming concepts:

- Classes and objects
- Constructors
- Encapsulation
- Inheritance
- Abstraction
- Polymorphism
- Composition and object relationships
- Interfaces
- Collections and generics
- Exception handling and validation
- Design patterns
- File input/output
- Automated testing

These concepts are applied throughout the implementation of product management, inventory operations, sales processing, and persistence.

## JSON Storage

RetailFlow uses JSON files for local inventory persistence instead of a database.

Inventory data is stored in:

`data/store.json`

Each product record contains information such as:

- Product type
- Stock Keeping Unit (SKU)
- Product name
- Price
- Quantity
- Expiry date, where applicable

The file is automatically created when the application initializes its inventory for the first time.

Subsequent application launches restore the saved product and inventory information.

Changes resulting from product additions, restocking, and successful sales are saved to the JSON file.

**Note:** The current implementation stores inventory information, including stock changes after sales. Permanent sales transaction history is not yet supported.

## Running RetailFlow

RetailFlow can be executed through IntelliJ IDEA or the command line using Maven.

### Prerequisites

Before running the application, ensure that the following are installed:

- Java Development Kit (JDK) 26
- Apache Maven
- IntelliJ IDEA (recommended for development)

### Running in IntelliJ IDEA

1. Clone the RetailFlow repository from GitHub.
2. Open the project in IntelliJ IDEA.
3. Load or reload the Maven project.
4. Configure the Project SDK to use JDK 26.
5. Locate the `com.retailflow.Main` class.
6. Run the `Main` class.

When the application starts without program arguments, the interactive console menu is displayed.

On the first launch, starter inventory is created. Subsequent launches restore inventory from the JSON file.

### Build and Execute

To build the project from the command line, navigate to the project root directory and run:

```bash
mvn clean package
```

To start the interactive console application:

```bash
mvn compile org.codehaus.mojo:exec-maven-plugin:3.7.0:java -Dexec.mainClass=com.retailflow.Main
```

This executes the `Main` class without program arguments and opens the interactive console menu.

The Maven Exec plugin may need to download its dependencies during the first execution.

### Interactive Console Menu

RetailFlow provides the following menu options:

1. View all products.
2. Search products by SKU or product name.
3. Add regular or perishable products.
4. Restock existing products.
5. Create and complete sales with optional discounts.
6. Sort products by price.
7. Exit the application.

User input is validated using the application's business rules and exception-handling mechanisms.

Product additions, stock replenishments, and successful checkouts update the stored inventory data.

### Demonstration Modes

RetailFlow also supports predefined checkout demonstrations through program arguments.

**Normal checkout:**

```text
demo-sale
```

**Checkout with a 10% discount:**

```text
demo-sale discount10
```

These arguments can be configured in IntelliJ IDEA under the Run Configuration for the `Main` class.

Alternatively, run the discounted checkout demonstration from the terminal:

```bash
mvn compile org.codehaus.mojo:exec-maven-plugin:3.7.0:java -Dexec.mainClass=com.retailflow.Main -Dexec.args="demo-sale discount10"
```

When no program arguments are provided, RetailFlow starts in interactive console mode.

### Example Usage

The following example demonstrates a sale with a percentage discount.

1. Start RetailFlow and select menu option `5` to create a sale.
2. Enter `10` when prompted for the discount percentage.
3. Select product `RF001` and enter a quantity of `2`.
4. Select product `RF002` and enter a quantity of `1`.
5. Enter `done` to finish adding sale items.
6. Enter `y` to confirm the checkout.

With the original starter prices, the expected calculation is:

| Description | Amount (GHS) |
|---|---:|
| Subtotal | 258.50 |
| Discount (10%) | 25.85 |
| **Final Total** | **232.65** |

After successful checkout, the corresponding product quantities are deducted from inventory and the updated stock information is saved.

Actual results depend on the current inventory, product prices, stock availability, and expiry dates.

## Automated Testing

RetailFlow uses JUnit 5 to verify the correctness of its business operations and application behavior.

### Running All Tests

To execute the complete test suite, run:

```bash
mvn clean test
```

Maven executes the available tests and reports their results.

### Console Testing

`ConsoleMenuTest` verifies the interactive console using simulated user input and an in-memory repository test double.

To execute only the console tests, run:

```bash
mvn -Dtest=ConsoleMenuTest test
```

This allows console behavior to be tested without modifying the actual inventory JSON file.

### Test Coverage

The automated test suite covers:

- Product and inventory operations
- Duplicate SKU validation
- Product searching and sorting
- Sales and checkout calculations
- Discount strategy behavior
- Product expiry validation
- Insufficient stock handling
- Repeated checkout prevention
- JSON saving and loading
- Corrupted JSON data handling
- Interactive console operations

These tests help verify that the application follows its defined business rules and handles invalid operations appropriately.

## Project Milestones

RetailFlow was developed through five milestones:

1. **Product Model** — Creating the foundational product class and applying encapsulation.
2. **Product Types** — Implementing inheritance, abstraction, and polymorphism.
3. **Inventory Management** — Managing products with collections, generics, and validation.
4. **Sales and Checkout** — Implementing transactions, sale items, and stock deduction.
5. **JSON Persistence, Testing, and Documentation** — Adding file persistence, automated testing, design patterns, and project documentation.

Each milestone builds upon the previous implementation and introduces additional Java programming concepts.

## Current Limitations

RetailFlow is an educational application and is not intended to serve as a complete commercial point-of-sale system.

The current implementation has the following limitations:

- The application uses a console-based interface and does not provide a graphical user interface.
- Inventory is stored in a local JSON file rather than a database.
- Permanent sales transaction history is not supported.
- Multi-user access and concurrent checkout are not supported.
- The system does not provide authentication or external payment integration.
- Transaction rollback is not implemented for file-save failures.

Although inventory changes are saved after successful business operations, a file-write failure may leave in-memory changes unsaved.

These limitations identify areas for potential future development.

## Contributors

- **Emmanuel Wiafe Annor** — Project design, implementation, automated testing, and documentation.

## Release and License

RetailFlow is distributed under the **MIT License**.

See the [LICENSE](LICENSE) file for the applicable terms and conditions.

Project releases and release notes are available through the [GitHub Releases page](https://github.com/ewannor/RetailFlow/releases).

This initial release is `v1.0.0`.