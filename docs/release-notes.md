# RetailFlow v1.0.0


## Release Overview

RetailFlow v1.0.0 is the initial release of a Java-based retail inventory and sales management application developed for the Programming 2 OOP assignment at the University of Pécs.

The application demonstrates object-oriented programming principles through inventory management, sales processing, discount calculation, and JSON persistence.

## Included Features

- Regular and perishable product management.
- Inventory management, including product searching, restocking, and sorting.
- Interactive console menu for retail operations.
- Multi-item sales and checkout processing.
- Stock availability and expiry-date validation.
- Custom exceptions for insufficient stock and expired products.
- Strategy Pattern for percentage-based and no-discount calculations.
- JSON inventory persistence using Jackson and the Repository Pattern.
- Automated testing using JUnit 5.
- UML diagrams, user stories, and technical documentation.

## Known Limitations

- Console-based application without a graphical interface.
- Designed for a single operator; multi-user transactions are not supported.
- No barcode scanning, payment integration, or receipt printing.
- JSON persistence stores inventory but not permanent sales history.
- File-write failures may leave in-memory inventory changes unsaved.

## Verification
RetailFlow v1.0.0 was verified through automated testing, manual acceptance testing, and Maven build execution.

- **Automated Tests:** 41 passed, 0 failures, 0 errors.
- **Manual Acceptance Checks:** 10/10 passed.
- **Maven Build:** BUILD SUCCESS.
- **Java Version:** Java 26.

Detailed verification results are available in the [Verification and Test Plan](test-plan.md).