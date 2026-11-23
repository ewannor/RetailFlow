# RetailFlow v1.0.0 — Draft Release Notes

> DRAFT — publish only after tests pass, code is merged into the default branch (`master`), and the release tag has been created.

## Included in this release

- Product model with regular and perishable products
- Inventory add, lookup, search, restock, and sorting
- Interactive console menu for shop operator workflows
- Multi-item checkout with stock and expiry-date validation
- Domain-specific stock and expiry exceptions
- Discount Strategy Pattern (no discount or percentage discount)
- JSON inventory persistence using Jackson and `StoreRepository`
- JUnit test suite (41 test methods in the review branch; final pass status pending verification)
- UML, user stories, and architecture/design explanation

## Known limitations

- Console-only, single-process app; no login, payment integration, barcode reader, or receipt printout
- JSON persists inventory, not permanent sale/receipt history
- Disk-write failures after checkout can leave the in-memory inventory changed
- No multi-user or multi-terminal transaction support

## Validation before release

- [ ] `mvn clean test` passes on final commit
- [ ] `mvn clean package` succeeds
- [ ] Manual menu workflow verified
- [ ] README, docs, license and repository issue backlog complete
- [ ] Pull request merged into `master`
- [ ] `v1.0.0` tag and GitHub release created on the merged commit
