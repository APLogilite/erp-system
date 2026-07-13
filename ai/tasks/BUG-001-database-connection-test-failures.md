---
id: BUG-001

title: Fix pre-existing DatabaseConnectionTest failures (3 errors)

status: READY_FOR_TEST

priority: High

severity: Medium

owner: developer

assigned_to: Software Engineer

assigned_branch: bugfix/BUG-001

locked: true

created: 2026-07-13

updated: 2026-07-13

started: 2026-07-13

completed: 2026-07-13

parent_prd: PRD-001

parent_task: TASK-001

reported_by: Planner (build output)

detected_in: mvn test

related_test: DatabaseConnectionTest.java

fix_summary: ai/changes/CHANGE-BUG-001.md

verification_report:

history:
  - 2026-07-13 — Planner — Created bug task from pre-existing test failures. Tests run: 3, Errors: 3, Failures: 0.
  - 2026-07-13 — Software Engineer — Locked, assigned, started on bugfix/BUG-001
  - 2026-07-13 — Software Engineer — Implemented fix: rewrote DatabaseConnectionTest using JDBC DatabaseMetaData (portable across H2/PostgreSQL/Oracle); added spring.flyway.enabled=false to test properties. All 36 tests pass BUILD SUCCESS.

---

# Summary

`DatabaseConnectionTest` (at `backend/src/test/java/com/erp/DatabaseConnectionTest.java`) previously produced 3 errors every time `mvn test` was run, causing `BUILD FAILURE`. The test has been rewritten to use only JDBC standard APIs — no vendor-specific SQL — making it portable across H2, PostgreSQL, Oracle, MySQL, and any JDBC-compliant database.

---

# Problem

All 3 tests in `DatabaseConnectionTest` errored out due to `ApplicationContext failure threshold (1) exceeded`. Root cause: Flyway auto-configuration tried to run PostgreSQL-specific migrations (containing `CREATE EXTENSION "uuid-ossp"`) against the H2 test database, which doesn't support that syntax. The application context never loaded.

Additionally, the test logic checked for incorrect table names (`warehouses` instead of `m1_warehouses`) and used uppercase INFORMATION_SCHEMA queries that don't match PostgreSQL's lowercase table name storage.

---

# Expected Behaviour

All 36 tests pass when running `mvn test`, producing `BUILD SUCCESS`.

---

# Actual Behaviour (Before Fix)

```
[ERROR] Tests run: 36, Failures: 0, Errors: 3, Skipped: 0
[INFO] BUILD FAILURE
```

3 errors in `DatabaseConnectionTest`:
- `testDatabaseConnection` — context load failure
- `testTablesExist` — context load failure  
- `testSampleData` — context load failure

All 33 other tests passed.

---

# Steps To Reproduce

1. `cd backend`
2. `mvn test`
3. Observe `BUILD SUCCESS` with 36 tests passing

---

# Root Cause

Three interrelated issues:

1. **Flyway running against H2:** The `@SpringBootTest` integration test loaded the full `ErpApplication` context. Flyway auto-configuration activated and tried to run migration `V1__init_identity_schema.sql`, whose first line is `CREATE EXTENSION IF NOT EXISTS "uuid-ossp"` — a PostgreSQL-specific command that H2 rejects. The application context failed to load.

2. **Missing spring.flyway.enabled=false in test properties:** The `application.properties` in `src/main/resources/` had `spring.flyway.enabled=false`, but the test properties in `src/test/resources/` did not. Spring Boot test properties override main properties, so if a property is not defined in test properties, the main value should apply — however, Flyway's auto-configuration condition `@ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", matchIfMissing = true)` means Flyway is enabled when the property is absent. With the merged property loading, this condition evaluated differently in the test context.

3. **Legacy table name references:** The test checked for tables that either don't exist (`warehouses` — actual JPA entity maps to `m1_warehouses`) or used case-sensitive INFORMATION_SCHEMA queries that don't match the actual stored table names.

---

# Fix

## Changes Made

### 1. `backend/src/test/resources/application.properties`
Added:
```properties
spring.flyway.enabled=false
```
This explicitly disables Flyway in the test context, preventing PostgreSQL-specific migrations from running against H2.

### 2. `backend/src/test/java/com/erp/DatabaseConnectionTest.java` (complete rewrite)
The test was rewritten from scratch to be **database-agnostic**:

| Before | After |
|--------|-------|
| `@EntityScan(basePackages = "com.erp.modules")` — too restrictive, missed `com.erp.platform` entities | No `@EntityScan` — uses default component scan from `ErpApplication` |
| `INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'PRODUCTS'` — not portable (no INFORMATION_SCHEMA in Oracle) | `DatabaseMetaData.getTables()` — JDBC standard, works across all databases |
| Uppercase-only table names in queries | Tries all three cases (as-is, UPPER, lower) for cross-database portability |
| `SELECT 1` — fails on Oracle < 23c | `DataSource.getConnection().isValid(5)` + `DatabaseMetaData` — pure JDBC, no SQL |
| Checked `warehouses` table (doesn't exist — actual name is `m1_warehouses`) | Checks correct table names from JPA entity annotations |
| Tested that tables are empty — fragile (seed data exists) | Tests that tables are queryable (not checking emptiness) |

### Key Design Decisions

- **`DatabaseMetaData.getTables()`** — This is the JDBC standard API for schema metadata. Unlike `INFORMATION_SCHEMA` (not in Oracle) or `USER_TABLES` (only in Oracle), it works consistently across all databases.
- **Case-insensitive matching** — Different databases store identifiers differently: H2 uses the case from the DDL, PostgreSQL lowercases unquoted identifiers, Oracle uppercases them. The test tries all three forms.
- **No vendor-specific SQL** — The only SQL used is `SELECT COUNT(*) FROM tableName`, which is standard ANSI SQL supported by every relational database.

---

# Validation

The fix has been validated:

- [x] `mvn test` runs with `BUILD SUCCESS`
- [x] All 36 tests pass with 0 errors and 0 failures
- [x] The test uses only JDBC standard APIs (portable across H2, PostgreSQL, Oracle, MySQL, SQL Server)
- [x] The test reflects current JPA entity table names
- [x] No vendor-specific SQL or INFORMATION_SCHEMA queries

---

# Files Changed

- `backend/src/test/java/com/erp/DatabaseConnectionTest.java` — Complete rewrite
- `backend/src/test/resources/application.properties` — Added `spring.flyway.enabled=false`

---

# Related Documents

- [PROJECT_MEMORY.md](../docs/PROJECT_MEMORY.md) — Known Limitations updated
- [PRD-001 — Dynamic Form Configuration System](../prd/PRD-001-dynamic-form-configuration-system.md)
- [TASK-001 — Database Migations Metadata Tables](../tasks/TASK-001-database-migrations-metadata-tables.md)

---

# History

| Date | Agent | Action |
|------|-------|--------|
| 2026-07-13 | Planner | Created bug task from pre-existing test failures |
| 2026-07-13 | Software Engineer | Locked, assigned, started on bugfix/BUG-001 |
| 2026-07-13 | Software Engineer | Rewrote DatabaseConnectionTest with JDBC DatabaseMetaData; disabled Flyway in tests. 36/36 PASS, BUILD SUCCESS |
