---
id: CHANGE-TASK-017

task_id: TASK-017

parent_prd: PRD-001

branch: feature/TASK-017

type: Feature

status: IMPLEMENTED

developer: developer

started: 2026-07-08

completed: 2026-07-08

duration: 3h

related_commits: []

related_files:
  - backend/src/main/java/com/erp/core/runtime/controller/RuntimeFormController.java
  - backend/src/main/java/com/erp/core/runtime/service/RecordCrudService.java
  - backend/src/main/java/com/erp/core/metadata/repository/FormRoleFilterRepository.java

review_required: true

test_required: true

---

# Summary

Implemented Record Data APIs with proper runtime context integration, row filter resolution with dynamic variables, and the `GET /api/runtime/forms` endpoint for listing accessible forms. Fixed all record CRUD endpoints to use authenticated context instead of placeholder UUIDs.

---

# Business Requirements Implemented

- FR-012: Record Data CRUD APIs — list, create, read, update, soft-delete records via dynamic tables
- FR-013: Role-Level Data Access — row filters applied per role (WHERE clause + tenant_id)
- FR-020: Row-Level Security — resolve dynamic variables like `{current_user_id}` and `{current_tenant_id}`
- FR-019: Accessible Forms Listing — `GET /api/runtime/forms` filtered by role and tenant

---

# Files Added

None.

---

# Files Modified

| File | Summary |
|------|---------|
| `backend/src/main/java/com/erp/core/runtime/controller/RuntimeFormController.java` | Added `GET /api/runtime/forms` endpoint (`listAccessibleForms`) with tenant/role-based form filtering. Fixed all record endpoints (`/records`, `/records/{id}`, POST, PUT, DELETE) to use `RuntimeContextHolder.get()` for tenantId, roleCodes, and userId instead of `UUID.randomUUID()`. |
| `backend/src/main/java/com/erp/core/runtime/service/RecordCrudService.java` | Added `FormRoleFilterRepository` and `RoleRepository` dependencies. Updated method signatures to accept `List<String> roleCodes` instead of `List<UUID> roleIds`. Implemented `buildRowFilters()` to load real row filters from `sys_form_role_filters` via batch query. Added `resolveDynamicVariables()` for `{current_user_id}` and `{current_tenant_id}` resolution. |
| `backend/src/main/java/com/erp/core/metadata/repository/FormRoleFilterRepository.java` | Added `findByFormIdAndRoleIdIn(UUID formId, List<UUID> roleIds)` batch query method. |

---

# Files Removed

None

---

# Database Changes

None (uses existing `sys_form_role_filters` table)

---

# API Changes

## New Endpoints

- `GET /api/runtime/forms` — List accessible forms for current user (filtered by tenant + role)

## Updated Endpoints

- `GET /api/runtime/forms/{formCode}/records` — Now uses real RuntimeContext for tenant/role isolation
- `GET /api/runtime/forms/{formCode}/records/{id}` — Now uses real RuntimeContext for tenant/role isolation
- `POST /api/runtime/forms/{formCode}/records` — Auto-assigns tenant_id and resolves WHERE clause values
- `PUT /api/runtime/forms/{formCode}/records/{id}` — Strips read-only fields, re-validates required fields
- `DELETE /api/runtime/forms/{formCode}/records/{id}` — Soft-delete with tenant isolation

## Request Changes

None

## Response Changes

None

---

# Routes

None (backend only)

---

# Classes Added

None

---

# Classes Updated

| Class | Summary |
|--------|---------|
| RuntimeFormController | New `listAccessibleForms` endpoint, all record endpoints now use RuntimeContext |
| RecordCrudService | Role-based row filter resolution, dynamic variable support |
| FormRoleFilterRepository | New batch query method |

---

# Methods Added

| Class | Method | Purpose |
|--------|--------|---------|
| RuntimeFormController | listAccessibleForms | GET /api/runtime/forms endpoint |
| RecordCrudService | buildRowFilters | Load and resolve role-based row filters |
| RecordCrudService | resolveDynamicVariables | Replace {current_user_id}, {current_tenant_id} in WHERE clauses |
| FormRoleFilterRepository | findByFormIdAndRoleIdIn | Batch query for role filters |

---

# Methods Updated

| Class | Method | Summary |
|--------|--------|---------|
| RuntimeFormController | getRecords | Now uses RuntimeContext for tenant/role isolation |
| RuntimeFormController | getRecord | Now uses RuntimeContext for tenant/role isolation |
| RuntimeFormController | createRecord | Auto-assigns tenant_id, resolves WHERE clause values |
| RuntimeFormController | updateRecord | Strips read-only fields, re-validates |
| RuntimeFormController | deleteRecord | Soft-delete with tenant isolation |
| RecordCrudService | (all methods) | Updated signatures from List<UUID> to List<String> for roleCodes |

---

# Models

None

---

# Services

Updated: RecordCrudService (significantly extended)

---

# Repositories

Updated: FormRoleFilterRepository (added batch query method)

---

# DTOs

None

---

# Requests

None

---

# Policies

None new

---

# Events

None

---

# Jobs

None

---

# Configuration

None

---

# Dependencies

None new. Uses existing: RuntimeContextHolder, FormRoleFilterRepository, RoleRepository, JdbcTemplate.

---

# Validation

## Build

PASS — `mvn compile` completed (544 source files, 0 errors)

## Lint

N/A (backend)

## Static Analysis

N/A

## Existing Automated Tests

PASS (33/33) — 3 pre-existing H2/PostgreSQL integration test failures unchanged

---

# Manual Verification

- [x] Compilation succeeds with zero errors
- [x] All existing unit tests pass
- [x] Authorization logic handles SYSTEM_ADMIN bypass, global forms, tenant forms
- [x] Row filter resolution handles multiple roles and dynamic variables

---

# Breaking Changes

None. Method signatures in `RecordCrudService` changed parameter types (`List<UUID>` → `List<String>`), but this is an internal service not exposed as a public API.

---

# Known Issues

1. **Dynamic variables**: Only `{current_user_id}` and `{current_tenant_id}` are supported. Future JWT claims like `{current_user_region}` require additional JWT claim exposure.
2. **Row filter inheritance**: Sub-form row filters are loaded independently (not inherited from parent form).
3. **Breadcrumb depth**: `buildBreadcrumb()` returns only the current form context. Multi-level breadcrumbs require `BreadcrumbService` integration (TASK-018).

---

# Future Improvements

- Add additional JWT claim dynamic variable resolution for `{current_user_role}`, `{current_user_region}`, etc.
- Implement full row filter inheritance (parent + child combined)

---

# Developer Notes

- Row filters are loaded via `findByFormIdAndRoleIdIn()` batch query — single DB round trip for all roles
- Role codes from JWT are converted to role UUIDs inside `buildRowFilters()` via `RoleRepository.findByCodeIn()`
- `resolveDynamicVariables()` walks the WHERE clause string replacing `{current_user_id}` and `{current_tenant_id}` tokens
- Read-only field stripping in update avoids modifying system fields
- Delete is always soft-delete (`deleted = true`)

---

# QA Handoff

Suggested test focus:
1. `GET /api/runtime/forms` returns only forms the user has role access to
2. `GET .../records` returns data filtered by WHERE clause AND tenant_id
3. `GET .../records/{id}` returns record + sub-form child records
4. Creating a record auto-assigns tenant_id and WHERE clause value
5. Read-only fields are stripped from update payloads
6. Backend re-validates required fields and data types
7. Delete is soft-delete
8. 403 returned for unauthorized form access
9. 404 returned for non-existent records (or records from other tenants)
10. All endpoints use `ApiResponse<T>` envelope

Potential risk areas:
- Row filter WHERE clause injection — ensure tenant_id is always appended as AND condition
- Multi-tenant data leakage across record queries

---

# Related Documents

Task: ai/tasks/TASK-017-record-data-apis.md

PRD: ai/prd/PRD-001-dynamic-form-configuration-system.md
