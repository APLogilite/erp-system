---
id: CHANGE-TASK-004

task_id: TASK-004

parent_prd: PRD-001

branch: feature/TASK-004

type: Feature

status: IMPLEMENTED

developer: developer

started: 2026-07-08

completed: 2026-07-08

duration: 3h

related_commits: []

related_files:
  - backend/src/main/java/com/erp/core/metadata/controller/TableDesignerController.java
  - backend/src/main/java/com/erp/core/metadata/service/TableDesignerService.java
  - backend/src/main/java/com/erp/core/metadata/repository/MetadataModelRepository.java
  - backend/src/main/java/com/erp/platform/identity/security/SecurityConfig.java
  - backend/src/main/java/com/erp/core/metadata/dto/UpdateTableRequest.java
  - backend/src/main/java/com/erp/core/metadata/dto/UpdateColumnRequest.java
  - backend/src/main/java/com/erp/core/metadata/dto/ColumnReorderRequest.java
  - backend/src/main/java/com/erp/core/metadata/dto/VersionHistoryResponse.java

review_required: true

test_required: true

---

# Summary

Completed all 10 REST API endpoints for the Table Designer (FR-001 through FR-005). Added 4 missing endpoints (PUT table update, PUT column update, PUT column reorder, GET schema history) and enhanced the 6 existing endpoints with schema history logging, search/filter on list, and method-level security via @PreAuthorize. Added 4 new DTOs for update/reorder/history payloads. Enabled @EnableMethodSecurity on SecurityConfig to support @PreAuthorize annotations.

---

# Business Requirements Implemented

- FR-001: Create Table Definition — table metadata saved + physical PostgreSQL table created + schema history logged
- FR-002: Manage Table Columns — add, edit, reorder, delete columns with DDL execution and type-specific validation
- FR-003: View Available Tables — paginated list with search by code/label
- FR-004: Deactivate/Restore Table — soft-delete with history logging
- FR-005: View Table Schema History — chronological list of changes with user and timestamp

---

# Files Added

| File | Purpose |
|------|---------|
| `backend/src/main/java/com/erp/core/metadata/dto/UpdateTableRequest.java` | DTO for updating table metadata (label, pluralLabel, description) |
| `backend/src/main/java/com/erp/core/metadata/dto/UpdateColumnRequest.java` | DTO for updating column metadata (label, type, required, constraints, position) |
| `backend/src/main/java/com/erp/core/metadata/dto/ColumnReorderRequest.java` | DTO for column reorder request (list of column UUIDs in desired order) |
| `backend/src/main/java/com/erp/core/metadata/dto/VersionHistoryResponse.java` | DTO for schema history response (version, description, changedBy, timestamp) |

---

# Files Modified

| File | Summary |
|------|---------|
| `backend/src/main/java/com/erp/core/metadata/controller/TableDesignerController.java` | Added 4 endpoints (PUT table, PUT column, PUT reorder, GET history); added @PreAuthorize on class |
| `backend/src/main/java/com/erp/core/metadata/service/TableDesignerService.java` | Added updateTable, updateColumn, reorderColumns, getHistory methods; integrated SchemaHistoryService for audit logging; added search/filter to listTables; added type-specific column validation; added currentUserId() helper via RuntimeContextHolder |
| `backend/src/main/java/com/erp/core/metadata/repository/MetadataModelRepository.java` | Added findByNameContainingIgnoreCaseOrLabelContainingIgnoreCase for search |
| `backend/src/main/java/com/erp/platform/identity/security/SecurityConfig.java` | Added @EnableMethodSecurity to enable @PreAuthorize support |

---

# Files Removed

None

---

# Database Changes

None (uses existing sys_metadata_models, sys_table_columns, sys_metadata_versions tables)

---

# API Changes

## New Endpoints

- `PUT /api/metadata/tables/{id}` — Update table definition metadata (label, pluralLabel, description)
- `PUT /api/metadata/tables/{id}/columns/{colId}` — Update column metadata + ALTER physical column
- `PUT /api/metadata/tables/{id}/columns/reorder` — Reorder columns by position
- `GET /api/metadata/tables/{id}/history` — Get schema change history (chronological)

## Updated Endpoints

- `GET /api/metadata/tables` — Now supports `search` query parameter (filters by code or label)
- All endpoints — Now require `SYSTEM_ADMIN` role via @PreAuthorize

## Request Changes

- New: `UpdateTableRequest` { label, pluralLabel, description }
- New: `UpdateColumnRequest` { label, type, required, defaultValue, maxLength, precision, scale, relationTable, enumOptions, position }
- New: `ColumnReorderRequest` { columnIds: UUID[] }

## Response Changes

- New: `VersionHistoryResponse` { id, version, tableId, description, definitionSnapshot, changedBy, createdAt }

---

# Routes

None (backend only)

---

# Classes Added

| Class | Purpose |
|--------|---------|
| UpdateTableRequest | Table metadata update DTO |
| UpdateColumnRequest | Column metadata update DTO |
| ColumnReorderRequest | Column reorder request DTO |
| VersionHistoryResponse | Schema history response DTO |

---

# Classes Updated

| Class | Summary |
|--------|---------|
| TableDesignerController | 4 new endpoints + @PreAuthorize security |
| TableDesignerService | 4 new methods + schema history + search + validation |
| MetadataModelRepository | Search query method |
| SecurityConfig | @EnableMethodSecurity for @PreAuthorize support |

---

# Methods Added

| Class | Method | Purpose |
|--------|--------|---------|
| TableDesignerController | updateTable | PUT /{id} endpoint |
| TableDesignerController | updateColumn | PUT /{id}/columns/{colId} endpoint |
| TableDesignerController | reorderColumns | PUT /{id}/columns/reorder endpoint |
| TableDesignerController | getHistory | GET /{id}/history endpoint |
| TableDesignerService | updateTable | Update table label/pluralLabel/description |
| TableDesignerService | updateColumn | Update column + DDL modifyColumn |
| TableDesignerService | reorderColumns | Reorder columns by position |
| TableDesignerService | getHistory | Retrieve schema change history |
| TableDesignerService | currentUserId | Get current user UUID from RuntimeContext |
| TableDesignerService | buildSnapshot | Build definition snapshot for history |
| TableDesignerService | validateColumnType | Type-specific validation (decimal, many2one) |
| MetadataModelRepository | findByNameContainingIgnoreCaseOrLabelContainingIgnoreCase | Search by code or label |

---

# Methods Updated

| Class | Method | Summary |
|--------|--------|---------|
| TableDesignerService | listTables | Added search query parameter support |
| TableDesignerService | createTable | Added schema history logging |
| TableDesignerService | addColumn | Added schema history logging + auto-position |
| TableDesignerService | deleteColumn | Added schema history logging + table ownership check |
| TableDesignerService | deleteTable | Added schema history logging |

---

# Models

None

---

# Services

None new; TableDesignerService significantly extended

---

# Repositories

Updated: MetadataModelRepository (added search query method)

---

# DTOs

Added: UpdateTableRequest, UpdateColumnRequest, ColumnReorderRequest, VersionHistoryResponse

Existing (unchanged): CreateTableRequest, CreateColumnRequest, TableResponse, TableColumnDto

---

# Requests

See API Changes above

---

# Policies

Added @PreAuthorize("hasRole('SYSTEM_ADMIN')") on TableDesignerController class level

Enabled @EnableMethodSecurity on SecurityConfig

---

# Events

None

---

# Jobs

None

---

# Configuration

Added @EnableMethodSecurity annotation on SecurityConfig.java

No environment variables, feature flags, or config file changes.

---

# Dependencies

None new. Uses existing dependencies: Spring Security, JdbcTemplate, RuntimeContextHolder.

---

# Validation

## Build

PASS — `mvn clean compile` completed successfully (548 source files, 0 errors)

## Lint

N/A (backend — no lint configured)

## Static Analysis

N/A

## Existing Automated Tests

PASS (33/36) — 3 pre-existing failures in DatabaseConnectionTest (H2 vs PostgreSQL Flyway incompatibility, documented in PROJECT_MEMORY.md). All identity/permission tests pass.

Executed Tests: 36 total — 33 passed, 3 pre-existing errors (unrelated to this task)

---

# Manual Verification

- [x] Compilation succeeds with zero errors
- [x] All existing unit tests pass (except pre-existing DatabaseConnectionTest failures)
- [x] New DTOs compile and integrate with service/controller
- [x] Search query method on repository compiles with Spring Data JPA derivation
- [x] @PreAuthorize annotations compile with @EnableMethodSecurity

---

# Breaking Changes

None. All existing endpoints retain backward compatibility. Additional endpoints are purely additive. Security is now enforced on this controller, but since it was previously unauthenticated (permitAll), this is an improvement, not a break.

---

# Known Issues

- Column delete does not check which forms use the column (warn-in-response is deferred to PRD iteration — requires FormFieldRepository query not yet implemented)
- @PreAuthorize requires SYSTEM_ADMIN role, which must exist in the running database
- Version history requires a valid RuntimeContext (authenticated request); currentUserId() returns null for unauthenticated calls

---

# Future Improvements

- Add form-usage warning to column delete response (depends on linking FormFieldEntity.columnId to TableColumnEntity)
- Add column-level @PreAuthorize checks if multiple admin roles are introduced
- Add request validation annotations (@Valid, @NotBlank) on DTOs

---

# Developer Notes

- SchemaHistoryService.logChange() is called on every state-changing operation (create, update, delete, add column, update column, delete column, reorder)
- DDL operations are wrapped in @Transactional(rollbackFor = Exception.class) so that if DDL fails, metadata changes also roll back
- The `search` parameter filters by table name (code) OR label using Spring Data's derived query method
- Column validation for decimal (precision/scale required) and many2one (relationTable required) is enforced via validateColumnType()
- The `position` field on addColumn auto-calculates the next position if not explicitly provided, preventing duplicate positions
- @EnableMethodSecurity was added to SecurityConfig because @PreAuthorize requires it. This enables method-level security across the entire application, which is architecturally consistent with the RBAC model.

---

# QA Handoff

Suggested test focus:
1. All 10 endpoints return correct HTTP status codes and ApiResponse envelope
2. Creating a table actually creates the physical PostgreSQL table (verify via information_schema)
3. Invalid table codes (e.g., "123", "UPPERCASE", "with-hyphen") are rejected
4. Schema history is recorded and retrievable for every table change
5. Non-SYSTEM_ADMIN users receive 403 Forbidden
6. Search/filter works on table list endpoint
7. Column reorder updates positions correctly
8. Deleting a column soft-deletes in metadata and drops from physical table

Potential risk areas:
- DDL operations on running PostgreSQL — column type changes could fail if data is incompatible
- RuntimeContext may be null in certain test scenarios
- @PreAuthorize requires the role SYSTEM_ADMIN to exist in the database

---

# Related Documents

Task: ai/tasks/TASK-004-table-designer-crud-apis.md

PRD: ai/prd/PRD-001-dynamic-form-configuration-system.md

