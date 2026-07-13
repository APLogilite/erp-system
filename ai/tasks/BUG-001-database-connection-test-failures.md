---
id: BUG-001

title: Fix pre-existing DatabaseConnectionTest failures (3 errors)

status: READY_FOR_DEV

priority: High

severity: Medium

owner: developer

assigned_to:

assigned_branch:

locked: false

created: 2026-07-13

updated: 2026-07-13

started:

completed:

parent_prd: PRD-001

parent_task: TASK-001

reported_by: Planner (build output)

detected_in: mvn test

related_test: DatabaseConnectionTest.java

fix_summary:

verification_report:

history:
  - 2026-07-13 — Planner — Created bug task from pre-existing test failures. Tests run: 3, Errors: 3, Failures: 0.

---

# Summary

`DatabaseConnectionTest` (at `backend/src/test/java/com/erp/DatabaseConnectionTest.java`) produces 3 errors every time `mvn test` is run. These are pre-existing failures documented in PROJECT_MEMORY.md as a known limitation ("3 pre-existing test failures in DatabaseConnectionTest (H2 vs PostgreSQL incompatibility)"). The build fails with `BUILD FAILURE` even though no functional code is broken.

---

# Problem

All 3 tests in `DatabaseConnectionTest` error out during `mvn test`:

```
[ERROR] Tests run: 3, Failures: 0, Errors: 3, Skipped: 0, Time elapsed: 3.436 s <<< FAILURE! -- in com.erp.DatabaseConnectionTest
```

This causes `BUILD FAILURE` despite all other 33 tests passing cleanly (0 failures, 0 errors across `PermissionCacheTest`, `PermissionEvaluatorTest`, `PasswordServiceTest`, `JwtProviderTest`).

---

# Expected Behaviour

All 36 tests (including `DatabaseConnectionTest`) should pass when running `mvn test`, producing `BUILD SUCCESS`. The test should either:

- Accurately validate the current database schema, OR
- Be replaced with a meaningful integration test that reflects the current metadata-driven architecture

---

# Actual Behaviour

3 errors in `DatabaseConnectionTest`. The test was written for the original JPA entity-based schema (tables `products`, `warehouses`, `orders`) but the project now uses a metadata-driven architecture with different table naming conventions.

---

# Steps To Reproduce

1. Open a terminal in the `backend/` directory
2. Run `mvn test`
3. Observe the 3 errors in `DatabaseConnectionTest`
4. Confirm `BUILD FAILURE`

---

# Root Cause

*(To be filled by Developer after investigation)*

Known contributing factors identified during analysis:

1. **INFORMATION_SCHEMA case sensitivity:** The `testTablesExist()` method queries `INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'PRODUCTS'` (uppercase). In H2 with PostgreSQL compatibility mode (`MODE=PostgreSQL`), unquoted table names are stored in lowercase, so `'PRODUCTS'` does not match `'products'`.

2. **Wrong table name for warehouse:** `testTablesExist()` checks for `'WAREHOUSES'` and `testSampleData()` queries `warehouses`, but the actual JPA entity `Warehouse.java` maps to `@Table(name = "m1_warehouses")`.

3. **Test queries tables that may not exist in H2 test context:** Flyway migrations are disabled by default in tests. The metadata-driven tables (`md_product`, `md_warehouse`, etc.) are not created during tests. Only JPA `ddl-auto=update` creates tables for `@Entity`-annotated classes. The test was written against the old JPA table names.

---

# Fix

*(To be filled by Developer)*

The fix should either:

**Option A — Update the test to match current schema:** Correct the table name references and case sensitivity, or update to validate the metadata-driven tables (e.g., `md_product`, `md_warehouse`).

**Option B — Rewrite the test as a meaningful integration test:** Replace the legacy table-name checks with a proper database connectivity + schema validation test that checks the metadata tables created by Flyway migrations.

**Option C — Remove and replace:** If the test is no longer relevant, remove it and create a new integration test that validates the dynamic metadata schema can be queried.

---

# Validation

*(To be filled by QA Engineer after fix)*

The fix will be validated when:

- [ ] `mvn test` runs with `BUILD SUCCESS`
- [ ] All 36+ tests pass with 0 errors and 0 failures
- [ ] The test accurately reflects the current database schema
- [ ] The test does not produce false negatives on future schema changes

---

# Files Changed

*(To be filled by Developer)*

Likely:

- `backend/src/test/java/com/erp/DatabaseConnectionTest.java`

---

# Related Documents

- [PROJECT_MEMORY.md](../docs/PROJECT_MEMORY.md) — Known Limitations section documents this issue
- [PRD-001 — Dynamic Form Configuration System](../prd/PRD-001-dynamic-form-configuration-system.md)
- [TASK-001 — Database Migations Metadata Tables](../tasks/TASK-001-database-migrations-metadata-tables.md)

---

# History

| Date | Agent | Action |
|------|-------|--------|
| 2026-07-13 | Planner | Created bug task from pre-existing test failures |
