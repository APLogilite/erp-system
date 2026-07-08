---
id: TASK-004

title: Implement Table Designer CRUD APIs (Backend)

type: API

status: READY_FOR_DEV

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
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-002
  - TASK-003
  - TASK-005

depends_on:
  - TASK-002
  - TASK-003

blocks:
  - TASK-006

labels: [backend, api, rest, table-designer]

review_required: true

test_required: true

automation_required: true

change_summary:

test_report:

history:
  - created

---

# Goal

Implement the REST API endpoints for the Table Designer (FR-001 to FR-005) so System Admins can create, read, update, and deactivate table definitions and their columns.

---

# Description

Create `TableDesignerController` and `TableDesignerService` in `com.erp.core.metadata`.

## Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/metadata/tables` | List all table definitions (paginated) |
| POST | `/api/metadata/tables` | Create a new table definition |
| GET | `/api/metadata/tables/{id}` | Get table definition with columns |
| PUT | `/api/metadata/tables/{id}` | Update table definition metadata |
| DELETE | `/api/metadata/tables/{id}` | Soft-delete table definition |
| POST | `/api/metadata/tables/{id}/columns` | Add a column |
| PUT | `/api/metadata/tables/{id}/columns/{colId}` | Update column |
| DELETE | `/api/metadata/tables/{id}/columns/{colId}` | Remove column |
| PUT | `/api/metadata/tables/{id}/columns/reorder` | Reorder columns |
| GET | `/api/metadata/tables/{id}/history` | Get schema change history |

## Service Logic

### Create Table
1. Validate: table code unique, snake_case format, columns valid
2. Save to `sys_metadata_models` + each column to `sys_table_columns`
3. Call `DdlExecutorService.createTable()` to create physical PostgreSQL table
4. If DDL fails, rollback metadata save
5. Log to `sys_metadata_version`

### Add Column
1. Validate column data
2. Save to `sys_table_columns`
3. Call `DdlExecutorService.addColumn()` to alter the physical table
4. Log schema change

### Delete Column
1. Check which forms use this column (if any, warn in response)
2. Remove from `sys_table_columns` (soft-delete)
3. Call `DdlExecutorService.dropColumn()`
4. Log schema change

## Security
- All endpoints guarded with `@PreAuthorize("hasRole('SYSTEM_ADMIN')")`

## Validation
- Table codes: regex `^[a-z][a-z0-9_]*$`
- Column codes: regex `^[a-z][a-z0-9_]*$`
- Type-specific validation (e.g., decimal requires precision/scale, many2one requires relation_table)

---

# Acceptance Criteria

- [ ] All 10 endpoints work correctly with valid payloads
- [ ] Creating a table stores metadata AND creates physical PostgreSQL table
- [ ] Adding/deleting columns alters the physical table
- [ ] Invalid table/column codes are rejected with clear error messages
- [ ] Only System Admin can access these endpoints
- [ ] Pagination and search/filter work on the list endpoint
- [ ] Schema history is recorded on every table change
- [ ] All endpoints return the standard `ApiResponse<T>` envelope

---

# Technical Notes

- Use `@RestController` and `@RequestMapping("/api/metadata/tables")`
- Return `ApiResponse` with proper HTTP status codes
- Use `@Valid` for request validation
- Inject `DdlExecutorService` for DDL operations
- All DDL operations should be in `@Transactional` methods

---

# Files Expected

- `backend/src/main/java/com/erp/core/metadata/controller/TableDesignerController.java`
- `backend/src/main/java/com/erp/core/metadata/service/TableDesignerService.java`
- `backend/src/main/java/com/erp/core/metadata/dto/CreateTableRequest.java`
- `backend/src/main/java/com/erp/core/metadata/dto/UpdateTableRequest.java`
- `backend/src/main/java/com/erp/core/metadata/dto/CreateColumnRequest.java`
- `backend/src/main/java/com/erp/core/metadata/dto/UpdateColumnRequest.java`
- `backend/src/main/java/com/erp/core/metadata/dto/TableResponse.java`
- `backend/src/main/java/com/erp/core/metadata/dto/ColumnResponse.java`
