# README additions — paste into root README.md

The project already has most feature and running instructions. Before submission, add these sections and correct the existing typo `princeples` → `principles`.

## Problem statement

RetailFlow supports a single retail shop operator who needs to manage stock and process simple sales while preventing overselling and expired-product checkout. The console interface is designed for a single operator, uses GHS for prices, and persists inventory to a local JSON file.

## Design documentation

- [UML class diagram](docs/uml-class-diagram.md)
- [Checkout sequence diagram](docs/checkout-sequence.md)
- [User stories and acceptance criteria](docs/user-stories.md)
- [Design decisions and patterns](docs/design-decisions.md)
- [Verification plan](docs/test-plan.md)

The **Strategy Pattern** applies different discount algorithms through `DiscountStrategy`, `NoDiscountStrategy`, `PercentageDiscountStrategy`, and the `Sale` context. The `StoreRepository` interface and `JsonStoreRepository` isolate JSON persistence.

## Build and execute

Requires JDK 26 and Maven. From a clean clone:

```bash
mvn clean test
mvn clean package
mvn compile org.codehaus.mojo:exec-maven-plugin:3.7.0:java -Dexec.mainClass=com.retailflow.Main
```

This starts the interactive console. To run a 10% discount demo instead:

```bash
mvn compile org.codehaus.mojo:exec-maven-plugin:3.7.0:java -Dexec.mainClass=com.retailflow.Main -Dexec.args="demo-sale discount10"
```

The Maven exec plugin may need to download the first time. Verify the command on your final clean checkout before submitting.

## Example usage

Select menu option `5`, enter discount `10`, product `RF001` quantity `2`, product `RF002` quantity `1`, then `done` and `y`. With starter prices, subtotal = GHS 258.50, discount = GHS 25.85, total = GHS 232.65. Actual stock depends on prior use and product expiry dates.

## Contributors

- **[Replace with your name]** — design, development, tests and documentation.

## Release and license

See the repository's `LICENSE` file for the selected terms, and the GitHub `v1.0.0` release for the final version and release notes. **Add this paragraph only after the license and release actually exist.**
