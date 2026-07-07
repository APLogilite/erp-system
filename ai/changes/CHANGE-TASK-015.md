---
id: CHANGE-TASK-015

task_id: TASK-015

parent_prd: PRD-001

branch: feature/TASK-015

type: Feature

status: IMPLEMENTED

developer: AI Developer Agent

started: 2026-07-07T23:03:00

completed: 2026-07-07T23:06:00

duration: 0.5 hours

related_commits:
  - TASK-015: Implement Dynamic CRUD Service for generic table operations

related_files:
  - backend/src/main/java/com/erp/core/runtime/service/DynamicCrudService.java
  - backend/src/main/java/com/erp/core/runtime/exception/RecordNotFoundException.java
  - backend/src/main/java/com/erp/core/metadata/repository/MetadataModelRepository.java (modified)

review_required: true

test_required: true

---

# Summary

Implemented `DynamicCrudService` — a generic service that performs CRUD operations on any dynamic table using `NamedParameterJdbcTemplate`. All queries are parameterized for SQL injection protection. Tenant isolation is enforced on every query via `WHERE tenant_id = ?`. The service supports listing with pagination/sorting, single record retrieval with row filters, create with automatic system field injection, update with read-only field stripping, soft-delete, and child record queries for sub-form grids. Also created `RecordNotFoundException` for 404 scenarios.

---

# Business Requirements Implemented

- FR-014: Dynamic CRUD — listRecords, getRecord, createRecord, updateRecord, deleteRecord, getChildRecords
- FR-016: Create Record — injects id, tenant_id, created_by, created_at, updated_at, is_active
- FR-017: Edit Record — updates only provided columns, strips read-only fields, enforces tenant ownership
- FR-018: Delete Record — soft-delete (is_active=false, deleted_at=NOW())
- FR-023: Role-Based Row-Level Data Access — row filters appended to list and get queries
- NFR-002: Security — tenant isolation on every query, parameterized queries prevent SQL injection, column name validation

---

# Files Added

| File | Purpose |
|------|---------|
| `DynamicCrudService.java` | Generic CRUD service for dynamic PostgreSQL tables |
| `RecordNotFoundException.java` | Exception for missing records (maps to 404) |

---

# Files Modified

| File | Summary |
|------|---------|
| `MetadataModelRepository.java` | Added `findByTableName(String tableName)` query method |

---

# Database Changes

No schema changes — this service operates on dynamically created tables.

---

# API Changes

No REST endpoints yet — internal service only (will be used by TASK-016/TASK-017).

---

# Validation

## Build

PASS — `mvn clean compile` succeeds

## Existing Automated Tests

PASS — 33/36 tests pass (3 pre-existing failures unrelated)

---

# Developer Notes

- Uses `NamedParameterJdbcTemplate` for all parameterized queries (per task spec)
- `RowFilter` inner class represents role-based row filters with field, operator, value
- Read-only fields are stripped from update payloads via `readOnlyFields` set parameter
- Column name validation uses regex pattern `^[a-zA-Z_][a-zA-Z0-9_]*$` to prevent SQL injection
- All identifiers are escaped with double quotes
- `resolveDynamicVariable()` is a placeholder for JWT-based variable resolution (e.g., `{current_user_id}`)
- `mapOperator()` translates operators like `equals`, `contains`, `in` to SQL operators
- Soft-delete pattern matches `BaseEntity.softDelete()` convention
