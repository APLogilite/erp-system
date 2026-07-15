---
id: BUG-001

title: Fix pre-existing DatabaseConnectionTest failures (3 errors)

status: COMPLETED

priority: High

severity: Medium

owner: QA Engineer

assigned_to: QA Engineer

assigned_branch: bugfix/BUG-001

locked: true

created: 2026-07-13

updated: 2026-07-13

started: 2026-07-13

completed: 2026-07-13

parent_prd: PRD-001

parent_task: TASK-001

reported_by: Product Manager (build output)

detected_in: mvn test

related_test: DatabaseConnectionTest.java

fix_summary: ai/changes/CHANGE-BUG-001.md

verification_report: ai/tests/TEST-BUG-001.md

history:
  - 2026-07-13 — Product Manager — Created bug task from pre-existing test failures. Tests run: 3, Errors: 3, Failures: 0.
  - 2026-07-13 — Software Engineer — Rewrote DatabaseConnectionTest using JDBC DatabaseMetaData; disabled Flyway in tests. 36/36 PASS.
  - 2026-07-13 — QA Engineer — All 36 tests pass. BUILD SUCCESS. Database-agnostic (H2/PostgreSQL/Oracle).

---

# Summary

`DatabaseConnectionTest` previously produced 3 errors every time `mvn test` was run, causing `BUILD FAILURE`. The test was rewritten to use only JDBC standard APIs — no vendor-specific SQL — making it portable across H2, PostgreSQL, Oracle, MySQL, and any JDBC-compliant database.

---

# Problem

All 3 tests errored out due to `ApplicationContext failure threshold (1) exceeded`. Root cause: Flyway auto-configuration tried to run PostgreSQL-specific migrations (containing `CREATE EXTENSION "uuid-ossp"`) against the H2 test database.

Additionally, the test checked incorrect table names (`warehouses` instead of `m1_warehouses`) and used uppercase INFORMATION_SCHEMA queries.

---

# Root Cause

Three interrelated issues:
1. **Flyway running against H2** — `CREATE EXTENSION "uuid-ossp"` fails on H2
2. **Missing `spring.flyway.enabled=false`** in test properties
3. **Legacy table name references** — wrong table names, case sensitivity

---

# Fix

1. `backend/src/test/resources/application.properties` — added `spring.flyway.enabled=false`
2. `backend/src/test/java/com/erp/DatabaseConnectionTest.java` — complete rewrite using JDBC `DatabaseMetaData.getTables()` (portable), `DataSource.getConnection().isValid()` (no `SELECT 1`), correct table names, case-insensitive matching

---

# Validation

`mvn test` → **BUILD SUCCESS**, 36/36 tests pass.

---

# Related Documents

- [CHANGE-BUG-001](../changes/CHANGE-BUG-001.md)
- [TEST-BUG-001](../tests/TEST-BUG-001.md)
