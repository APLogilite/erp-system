---
id: TASK-003

title: Implement DDL Executor Service for Dynamic Table Creation

type: Feature

status: PLANNED

priority: High

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-07

updated: 2026-07-07

started:

completed:

estimated_hours: 8

actual_hours:

parent_prd: PRD-001

prd_version: 1.6.0

parent_task:

related_tasks:
  - TASK-001
  - TASK-002
  - TASK-004

depends_on:
  - TASK-002

blocks:
  - TASK-004

labels: [backend, service, ddl]

review_required: true

test_required: true

automation_required: true

change_summary:

test_report:

history:
  - created

---

# Goal

Implement a backend service that can dynamically create and alter PostgreSQL tables based on the metadata stored in `sys_table_columns`.

---

# Description

Create `DdlExecutorService` in `com.erp.core.metadata.service` that uses Spring `JdbcTemplate` to execute DDL statements.

## Required Methods

### `createTable(UUID tableId)`
1. Load table definition from `MetadataModelEntity`
2. Load columns from `TableColumnRepository`
3. Generate `CREATE TABLE` SQL with:
   - `id UUID PRIMARY KEY`
   - `tenant_id UUID NOT NULL`
   - BaseEntity fields (created_at, updated_at, created_by, updated_by, is_active, deleted_at)
   - User-defined columns with proper PostgreSQL types
   - Foreign key constraints for `many2one` columns
4. Execute via `jdbcTemplate.execute(sql)`
5. On failure, rollback metadata save (throw exception)
6. Log the schema change in `sys_metadata_version`

### `addColumn(UUID tableId, UUID columnId)`
1. Generate `ALTER TABLE ... ADD COLUMN ...` SQL
2. Handle default values and NOT NULL constraints appropriately

### `dropColumn(UUID tableId, UUID columnId)`
1. Generate `ALTER TABLE ... DROP COLUMN ...` SQL
2. Warn if data will be lost (logging)

### `modifyColumn(UUID tableId, UUID columnId)`
1. Generate `ALTER TABLE ... ALTER COLUMN ...` SQL for type changes
2. Some changes may not be possible (e.g., VARCHAR to INTEGER with non-numeric data) — handle gracefully with exceptions

### `tableExists(String tableName)`
1. Query `information_schema.tables` to check if a table exists

### `getTableColumns(String tableName)`
1. Query `information_schema.columns` to return current physical columns

## PostgreSQL Type Mapping

```java
Map<String, String> TYPE_MAP = Map.of(
    "string", "VARCHAR(%d)",
    "text", "TEXT",
    "integer", "INTEGER",
    "decimal", "NUMERIC(%d, %d)",
    "boolean", "BOOLEAN",
    "date", "DATE",
    "datetime", "TIMESTAMP",
    "many2one", "UUID",
    "enum", "VARCHAR(100)"
);
```

## Error Handling
- Wrap all DDL operations in try-catch
- On DDL failure, throw a custom `DdlExecutionException` with the failed SQL and database error
- For table creation, the service must be called within a `@Transactional` context — if DDL fails, the metadata save should also rollback

---

# Acceptance Criteria

- [ ] `createTable()` successfully creates a PostgreSQL table with all columns
- [ ] `addColumn()` successfully adds a column to an existing table
- [ ] `dropColumn()` successfully removes a column
- [ ] All column types map correctly to PostgreSQL types
- [ ] `many2one` columns create proper UUID foreign keys
- [ ] Table creation includes all BaseEntity system columns
- [ ] DDL failures throw meaningful exceptions
- [ ] Service is tested with Testcontainers (PostgreSQL)
- [ ] Code compiles with `mvn clean compile`

---

# Technical Notes

- Use `JdbcTemplate` (not JPA) for DDL operations since there are no entity classes for dynamic tables
- Execute DDL with `jdbcTemplate.execute(sql)` — no prepared statements needed for DDL
- Wrap in `@Transactional(rollbackFor = Exception.class)` 
- Table naming: use the `table_name` from `sys_metadata_models` (e.g., `tx_expense_report`)
- Many2one foreign keys should reference the target table's `id` column

---

# Files Expected

- `backend/src/main/java/com/erp/core/metadata/service/DdlExecutorService.java`
- `backend/src/main/java/com/erp/core/metadata/exception/DdlExecutionException.java`
- Tests in `backend/src/test/java/com/erp/core/metadata/service/DdlExecutorServiceTest.java`
