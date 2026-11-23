# RetailFlow — Verification and Test Plan

## Build prerequisites

Use JDK 26 and Maven. The repository `pom.xml` configures Java source/target 26, Jackson 2.22.4, JUnit Jupiter 5.14.2 and Maven Surefire 3.6.1 (verified on `feature/submission-prep`, 24 November 2026).

## Automated tests

From a **clean checkout**:

```bash
mvn clean test
mvn clean package
```

Verify **BUILD SUCCESS** and zero failures/errors. GitHub currently contains 41 `@Test` methods:

| Class | Expected methods |
|---|---:|
| `InventoryTest` | 5 |
| `SaleTest` | 6 |
| `SaleDomainExceptionTest` | 6 |
| `JsonStoreRepositoryTest` | 5 |
| `DiscountStrategyTest` | 9 |
| `ConsoleMenuTest` | 10 |
| **Total** | **41** |

Check `target/surefire-reports/` if Maven reports failures. Do not describe 41 passes in the final release until you actually execute the tests on the final commit.

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

**Preserve your data:** Menu actions modify `data/store.json`. Copy it to a safe location first, or demonstrate in a separate clone/test working directory. Never commit `data/store.json`.

## Evidence log — fill in after running

- Date and Java version: _to be completed_
- Git commit SHA: _to be completed_
- `mvn clean test`: _to be completed_
- `mvn clean package`: _to be completed_
- Console manual checks: _to be completed_

## Clean-checkout run command

The existing project uses IntelliJ's Run action for `com.retailflow.Main`. To make command-line execution reproducible without changing `pom.xml`, a fully qualified Maven goal can also be used (Maven may download the plugin):

```bash
mvn compile org.codehaus.mojo:exec-maven-plugin:3.7.0:java -Dexec.mainClass=com.retailflow.Main
```

For existing demo mode, supply `-Dexec.args="demo-sale discount10"` to the same goal. Confirm this command on the final checkout before copying into README.
