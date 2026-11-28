# RetailFlow — Verification and Test Plan

## Build prerequisites

Use JDK 26 and Maven. The repository `../pom.xml` configures Java source/target 26, Jackson 2.22.4, JUnit Jupiter 5.14.2 and Maven Surefire 3.6.1.

## Automated tests

From a **clean checkout**:

```bash
mvn clean test
mvn clean package
```

Verify **BUILD SUCCESS** and zero failures/errors. The automated test suite consists of 41 `test` methods:

| Class | Expected methods |
|---|---:|
| `InventoryTest` | 5 |
| `SaleTest` | 6 |
| `SaleDomainExceptionTest` | 6 |
| `JsonStoreRepositoryTest` | 5 |
| `DiscountStrategyTest` | 9 |
| `ConsoleMenuTest` | 10 |
| **Total** | **41** |

Check `../target/surefire-reports` if Maven reports failures. 

## Manual acceptance demonstration

1. Start `Main` **without arguments** (interactive console).
2. Menu option 1: view starter product inventory.
3. Menu option 2: search `RF001` and a partial name such as `mil`.
4. Menu option 3: add a regular item with a fresh SKU; verify it is listed.
5. Menu option 4: restock a product and confirm the new quantity.
6. Menu option 5: add two products, set 10% discount, inspect subtotal/discount/total; cancel and verify no stock change.
7. Menu option 5 again: confirm an eligible checkout; verify stock decreased.
8. Exit and restart; verify the saved stock and new product remain after reload.
9. Attempt excessive quantity or expired item; verify rejection and unchanged stock.
10. Menu option 6: verify ascending price order.

**Preserve your data:** Menu actions modify `../data/store.json`. Copy it to a safe location first, or demonstrate in a separate clone/test working directory. Never commit `data/store.json`.

## Evidence log

- Verification Date: _27 November 2027_
- Java version: _Java 26.0.2.1_
- Git commit SHA: _6772d2d3a37e87adc90a27688f1ccf989c5e8a3c_
- `mvn clean test`: _BUILD SUCCESS - 41 tests run, Failures: 0, Errors: 0, Skipped: 0_
- `mvn clean package`: _BUILD SUCCESS - JAR file generated successfully_
- Console manual checks: _10/10 passed.
  All inventory, search, product creation,
  restocking, checkout, persistence, validation,
  sorting, and exit operations worked correctly._

## Clean-Checkout Run Command

RetailFlow can be executed from the command line using Maven. From the project's root directory, run:

```bash
mvn compile org.codehaus.mojo:exec-maven-plugin:3.7.0:java -Dexec.mainClass=com.retailflow.Main
```

This command launches the application's interactive console menu without requiring IntelliJ IDEA.

### Discount Demonstration Verification

To verify the percentage-based discount functionality, run:

```bash
mvn compile org.codehaus.mojo:exec-maven-plugin:3.7.0:java -Dexec.mainClass=com.retailflow.Main -Dexec.args="demo-sale discount10"
```

**Expected result:** The application processes the demonstration sale using a 10% discount and displays the subtotal, discount amount, and final payable amount.
