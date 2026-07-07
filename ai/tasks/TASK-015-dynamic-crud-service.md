---
id: TASK-015

title: Implement Dynamic CRUD Service (Backend)

type: Feature

status: READY_FOR_TEST

priority: Critical

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-07

updated: 2026-07-07

started: 2026-07-07

completed: 2026-07-07

estimated_hours: 12

actual_hours:

parent_prd: PRD-001

prd_version: 1.6.0
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-002
  - TASK-016

depends_on:
  - TASK-002

blocks:
  - TASK-016
  - TASK-017

labels: [backend, service, crud, runtime]

review_required: true

test_required: true

automation_required: true

change_summary: ai/changes/CHANGE-TASK-015.md

test_report:

history:
  - created
  - implemented 2026-07-07 — Developer completed Dynamic CRUD Service

---

# Goal

Implement a generic Dynamic CRUD service that can perform database operations on any dynamic table without requiring JPA entity classes.

---

# Description

Create `DynamicCrudService` that uses `JdbcTemplate` with parameterized queries to perform CRUD operations on dynamic tables.

## Methods

### `listRecords(String tableName, String whereClause, String whereValue, UUID tenantId, int page, int size, String sortField, String sortDir, List<RowFilter> rowFilters)`
- Build SELECT query with tenant isolation: `WHERE tenant_id = ?`
- Append where clause if set: `AND {field} = {value}`
- **Append role-based row filters:** For each row filter, add `AND {condition_field} {operator} {condition_value}` — dynamic variables like `{current_user_id}` are resolved from the JWT
- Support sorting and pagination with LIMIT/OFFSET
- Return `{ items: [...], page, size, total }`

### `getRecord(String tableName, UUID recordId, UUID tenantId, List<RowFilter> rowFilters)`
- `SELECT * FROM {table} WHERE id = ? AND tenant_id = ?`
- Append row filters (if the record exists but fails row filters, return empty — caller interprets as 404)
- Return single record as Map, or null if filtered out

### `createRecord(String tableName, Map<String, Object> data, UUID tenantId, UUID userId)`
- Inject `tenant_id`, `id` (UUID), `created_by`, `created_at`
- Build INSERT with parameterized columns
- Return created record

### `updateRecord(String tableName, UUID recordId, Map<String, Object> data, UUID tenantId, UUID userId)`
- Check record exists AND belongs to tenant
- Build UPDATE with only provided columns
- Update `updated_by`, `updated_at`
- Strip read-only fields (passed as parameter list)
- Return updated record

### `deleteRecord(String tableName, UUID recordId, UUID tenantId)`
- Soft-delete: `UPDATE {table} SET is_active = false, deleted_at = NOW() WHERE id = ? AND tenant_id = ?`
- Return success

### `getChildRecords(String childTableName, String relationColumn, UUID parentRecordId, UUID tenantId)`
- `SELECT * FROM {childTable} WHERE {relationColumn} = ? AND tenant_id = ?`
- Used for sub-form tab grids

## Security
- All methods require tenant_id parameter (from JWT) — never allow cross-tenant access
- Column names are validated against the table definition to prevent SQL injection
- All values use parameterized queries (no string concatenation)

## Error Handling
- Record not found → throw `RecordNotFoundException`
- Tenant mismatch → throw `AccessDeniedException`
- Validation failure → throw `ValidationException` with field-level details

---

# Acceptance Criteria

- [ ] All CRUD operations work on any dynamic table
- [ ] Tenant isolation is enforced on every query
- [ ] Read-only fields are stripped from update payloads
- [ ] Where clause filtering is applied to list queries
- [ ] Pagination and sorting work correctly
- [ ] Soft-delete is used (not hard delete)
- [ ] Parameterized queries prevent SQL injection
- [ ] Column names are validated against metadata before use
- [ ] Integration tests with Testcontainers pass

---

# Technical Notes

- Use `NamedParameterJdbcTemplate` for parameterized queries
- Column validation: load column list from `sys_table_columns` for the table, reject any unknown column in the request
- For dynamic tables, use `MapSqlParameterSource` for parameter binding
- UUID generation: `UUID.randomUUID()` for new record IDs
- Read-only field list comes from the form definition's field config

---

# Files Expected

- `backend/src/main/java/com/erp/core/runtime/service/DynamicCrudService.java`
- `backend/src/main/java/com/erp/core/runtime/exception/RecordNotFoundException.java`
- Tests in `backend/src/test/java/com/erp/core/runtime/service/DynamicCrudServiceTest.java`
