---
id: CHANGE-BUG-001

task_id: BUG-001

parent_prd: PRD-001

branch: bugfix/BUG-001

type: Bug

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-13

completed: 2026-07-13

duration: ~1.5 hours

related_commits:
  - (pending merge)

related_files:
  - backend/src/test/java/com/erp/DatabaseConnectionTest.java
  - backend/src/test/resources/application.properties
  - ai/project/tasks/BUG-001-database-connection-test-failures.md
  - ai/agent/project-board.md
  - ai/agent/changelog.md

review_required: true

test_required: true

---

# Summary

Rewrote the `DatabaseConnectionTest` integration test that was producing 3 errors on every `mvn test` run, causing `BUILD FAILURE`. The root cause was two-fold: (1) Flyway auto-configuration tried to run PostgreSQL-specific migrations against the H2 test database (failing on `CREATE EXTENSION "uuid-ossp"`), and (2) the test used fragile vendor-specific SQL and incorrect table names.

The test was rewritten from scratch to use **only JDBC standard APIs** — `DatabaseMetaData.getTables()` instead of `INFORMATION_SCHEMA`, `DataSource.getConnection().isValid()` instead of `SELECT 1` (which fails on Oracle < 23c). This makes the test portable across H2, PostgreSQL, Oracle, MySQL, SQL Server, and any JDBC-compliant database.

Additionally, `spring.flyway.enabled=false` was added to the test `application.properties` to prevent Flyway from running migrations against H2 during tests.

All 36 tests now pass with **BUILD SUCCESS**.

---

# Business Requirements Implemented

- DatabaseConnectionTest must pass without errors when running `mvn test`
- Test must be database-agnostic (portable across H2, PostgreSQL, Oracle)
- Test must verify database connectivity and JPA entity table creation
- Build must produce BUILD SUCCESS (zero errors, zero failures)

---

# Files Added

None.

---

# Files Modified

| File | Summary |
|------|---------|
| `backend/src/test/java/com/erp/DatabaseConnectionTest.java` | Complete rewrite — uses JDBC DatabaseMetaData, no vendor-specific SQL, correct table names |
| `backend/src/test/resources/application.properties` | Added `spring.flyway.enabled=false` to prevent Flyway from running migrations against H2 |
| `ai/project/tasks/BUG-001-database-connection-test-failures.md` | Updated status to READY_FOR_TEST, added fix documentation |
| `ai/agent/project-board.md` | Updated Bugs and In Development sections |

---

# Files Removed

None.

---

# Database Changes

None. No schema changes — only test code and test configuration.

---

# API Changes

None.

---

# Configuration

## Changed: `backend/src/test/resources/application.properties`

Added:
```properties
spring.flyway.enabled=false
```

This explicitly disables Flyway in the test context, preventing PostgreSQL-specific `CREATE EXTENSION "uuid-ossp"` migrations from running against H2.

---

# Validation

## Build

**PASS** — `mvn clean test` completes with BUILD SUCCESS

## Existing Automated Tests

**PASS** — 36/36 tests pass:

```
DatabaseConnectionTest:       3/3  PASS
PermissionCacheTest:          6/6  PASS
PermissionEvaluatorTest:      9/9  PASS
PasswordServiceTest:         13/13 PASS
JwtProviderTest:              5/5  PASS
```

---

# Manual Verification

- [x] `mvn test` runs with BUILD SUCCESS
- [x] All 36 tests pass (0 failures, 0 errors)
- [x] Test uses `DatabaseMetaData.getTables()` — no INFORMATION_SCHEMA queries
- [x] Test uses `DataSource.getConnection().isValid()` — no `SELECT 1` (fails on old Oracle)
- [x] Test uses correct JPA entity table names from `@Table` annotations
- [x] Test tries all three cases (as-is, UPPER, lower) for cross-database table name resolution
- [x] Flyway is explicitly disabled in test configuration

---

# Breaking Changes

None. Only test infrastructure changed — no production code modified.

---

# Known Issues

None.

---

# Future Improvements

- The set of verified entity tables in `testJpaEntityTablesExist()` is hardcoded. If new JPA entities are added, this list should be updated. Could be automated via entity scanning in the future.

---

# Developer Notes

## Architecture Decision — JDBC DatabaseMetaData over INFORMATION_SCHEMA

The original test used `INFORMATION_SCHEMA.TABLES` queries, which are:
- ✓ Supported by H2 (PostgreSQL mode)
- ✓ Supported by PostgreSQL
- ✓ Supported by MySQL, SQL Server
- ✗ **NOT supported by Oracle** (Oracle uses `USER_TABLES` / `ALL_TABLES` instead)

Using `java.sql.DatabaseMetaData.getTables()` provides a database-agnostic alternative that works across all JDBC-compliant databases.

## Table Name Case Handling

Different databases handle unquoted identifier case differently:
| Database | Unquoted Identifiers |
|----------|---------------------|
| H2 | Stored as-is (UPPERCASE by default in non-PostgreSQL mode) |
| PostgreSQL | Folded to lowercase |
| Oracle | Folded to UPPERCASE |

The test tries all three forms (as-written, uppercase, lowercase) to find the table.

## Why SELECT 1 Was Removed

`SELECT 1` (without `FROM`) works in H2, PostgreSQL, MySQL, SQL Server, and Oracle 23c+ — but **fails on Oracle < 23c** which requires `SELECT 1 FROM DUAL`. Using `SELECT 1 FROM DUAL` works in all major databases except SQL Server (which doesn't have a DUAL table by default). The most portable approach is to avoid SQL entirely for connectivity checks and use `java.sql.Connection.isValid(int timeout)` instead.

---

# QA Handoff

1. Run `mvn test` in `backend/` — verify BUILD SUCCESS with 36/36 tests passing
2. Review that no vendor-specific SQL or INFORMATION_SCHEMA queries are used
3. Verify the test file compiles and runs in CI
4. If the project ever adds a PostgreSQL or Oracle test profile, the test should pass without modification

---

# Related Documents

- [BUG-001](../tasks/BUG-001-database-connection-test-failures.md)
- [PRD-001](../prd/PRD-001-dynamic-form-configuration-system.md)
