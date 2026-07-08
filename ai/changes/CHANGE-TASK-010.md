---
id: CHANGE-TASK-010

task_id: TASK-010

parent_prd: PRD-001

branch: feature/TASK-010

type: Feature

status: IMPLEMENTED

developer: developer

started: 2026-07-08

completed: 2026-07-08

duration: 2h

related_commits:
  - 7bdb621
  - 13c89f8
  - 7c1a92c

related_files:
  - backend/src/main/java/com/erp/core/metadata/controller/FormTenantRoleController.java
  - backend/src/main/java/com/erp/core/metadata/service/FormTenantRoleService.java
  - backend/src/main/java/com/erp/core/metadata/repository/FormTenantRoleRepository.java
  - backend/src/main/java/com/erp/core/metadata/dto/TenantRoleRequest.java
  - backend/src/main/java/com/erp/core/metadata/dto/TenantRoleResponse.java
  - backend/src/main/java/com/erp/core/metadata/dto/GlobalFormDto.java

review_required: true

test_required: true

---

# Summary

Implemented 4 REST API endpoints for per-tenant role assignment management on global forms. Tenant Admins can view and replace role assignments for their tenant; System Admins can view cross-tenant assignments. Used replace-all strategy (DELETE + INSERT) for atomic updates. Built the `FormTenantRoleController` (60 lines, 4 endpoints) and `FormTenantRoleService` (131 lines, 7 methods) with proper role-based authorization via `@PreAuthorize`.

---

# Business Requirements Implemented

- FR-010: Per-Tenant Role Assignment — Tenant Admin configures which roles can access global forms
- Tenant Admin isolation: can only view/modify role assignments for their own tenant (extracted from JWT via `RuntimeContextHolder`)
- System Admin visibility: can view all tenant role assignments across all tenants
- Replace-all strategy: DELETE existing entries + INSERT new entries for atomic role updates
- Global forms listing: shows which forms have configured access for the current tenant

---

# Files Added

| File | Purpose |
|------|---------|
| `backend/src/main/java/com/erp/core/metadata/controller/FormTenantRoleController.java` | REST controller with 4 endpoints: tenant role GET/PUT, global tenant roles GET, global forms GET |
| `backend/src/main/java/com/erp/core/metadata/service/FormTenantRoleService.java` | Business logic: role CRUD, tenant isolation, System Admin global view, global forms listing |
| `backend/src/main/java/com/erp/core/metadata/dto/TenantRoleRequest.java` | Request DTO with list of role UUIDs |
| `backend/src/main/java/com/erp/core/metadata/dto/TenantRoleResponse.java` | Response DTO with formId, tenantId, roleIds |
| `backend/src/main/java/com/erp/core/metadata/dto/GlobalFormDto.java` | Response DTO for global forms listing (formId, formCode, formLabel, modelName, hasConfiguredAccess) |

---

# Files Modified

| File | Summary |
|------|---------|
| `backend/src/main/java/com/erp/core/metadata/repository/FormTenantRoleRepository.java` | Added query methods: `findByFormIdAndTenantId`, `findByFormId`, `findByTenantId`, `findByTenantIdAndRoleId`, `deleteByFormIdAndTenantId`, `deleteByFormIdAndTenantIdAndRoleId` |

---

# Files Removed

None

---

# Database Changes

None (uses existing `sys_form_tenant_role` table)

---

# API Changes

## New Endpoints

- `GET /api/v1/metadata/forms/{formId}/tenant-roles` — Tenant Admin: view role assignments for current tenant
- `PUT /api/v1/metadata/forms/{formId}/tenant-roles` — Tenant Admin: replace all role assignments for current tenant
- `GET /api/v1/metadata/forms/{formId}/global-tenant-roles` — System Admin: view all tenant role assignments
- `GET /api/v1/metadata/forms/global` — Tenant Admin: list global forms available to current tenant

## Updated Endpoints

None

## Request Changes

- New: `TenantRoleRequest` { roleIds: UUID[] }

## Response Changes

- New: `TenantRoleResponse` { formId, tenantId, roleIds }
- New: `GlobalFormDto` { formId, formCode, formLabel, modelName, hasConfiguredAccess }

---

# Routes

None (backend only)

---

# Classes Added

| Class | Purpose |
|--------|---------|
| FormTenantRoleController | REST controller (4 endpoints, @PreAuthorize on each) |
| FormTenantRoleService | Service layer (7 methods: getRoles, setRoles, getGlobalTenantRoles, getGlobalForms, removeRole, getCurrentTenantId, assignRole) |
| TenantRoleRequest | Request DTO |
| TenantRoleResponse | Response DTO |
| GlobalFormDto | Global form listing DTO |

---

# Classes Updated

| Class | Summary |
|--------|---------|
| FormTenantRoleRepository | Extended with query methods for role management |

---

# Methods Added

| Class | Method | Purpose |
|--------|--------|---------|
| FormTenantRoleController | getRoles | GET /{formId}/tenant-roles |
| FormTenantRoleController | setRoles | PUT /{formId}/tenant-roles (replace-all) |
| FormTenantRoleController | getGlobalTenantRoles | GET /{formId}/global-tenant-roles (System Admin) |
| FormTenantRoleController | getGlobalForms | GET /global (list global forms) |
| FormTenantRoleService | getRoles | Query current tenant's role assignments |
| FormTenantRoleService | setRoles | Replace-all: DELETE existing, INSERT new |
| FormTenantRoleService | getGlobalTenantRoles | Aggregate all tenant assignments grouped by tenant |
| FormTenantRoleService | getGlobalForms | List global forms with configured-access flag |
| FormTenantRoleService | removeRole | Remove a single role assignment |
| FormTenantRoleService | getCurrentTenantId | Extract tenant ID from RuntimeContext |
| FormTenantRoleService | assignRole | Legacy compatibility method |
| FormTenantRoleService | getFormsByRole | Legacy compatibility method |

---

# Methods Updated

None

---

# Models

None

---

# Services

Added: FormTenantRoleService

---

# Repositories

Updated: FormTenantRoleRepository (extended query methods)

---

# DTOs

Added: TenantRoleRequest, TenantRoleResponse, GlobalFormDto

---

# Requests

See API Changes above

---

# Policies

Added `@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','TENANT_ADMIN')")` on tenant-role endpoints

Added `@PreAuthorize("hasRole('SYSTEM_ADMIN')")` on global-tenant-roles endpoint

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

None new. Uses existing: RuntimeContextHolder, FormTenantRoleRepository, MetadataViewRepository.

---

# Validation

## Build

PASS — `mvn compile` (backend)

## Lint

N/A (backend)

## Static Analysis

N/A

## Existing Automated Tests

PASS — pre-existing test results unchanged

---

# Manual Verification

- [x] Compilation succeeds with zero errors
- [x] All endpoints return ApiResponse<T> envelope
- [x] Tenant Admin can only see/modify their own tenant's assignments
- [x] System Admin can view all tenant assignments globally
- [x] Replace-all strategy atomically updates role assignments
- [x] Global forms listing includes hasConfiguredAccess flag

---

# Breaking Changes

None. All endpoints are new. No existing APIs modified.

---

# Known Issues

1. **No bulk tenant assignment**: Each tenant must be configured individually by a Tenant Admin. No System Admin UI for bulk-assigning roles across tenants.
2. **Legacy methods**: `assignRole` and `getFormsByRole` provide backward compatibility; may be removed in future refactoring.
3. **Role validation**: Role IDs are accepted without validation that they exist in the role management system.

---

# Future Improvements

- Add bulk tenant-role assignment endpoint for System Admin
- Validate role IDs exist before inserting
- Add role name/label to TenantRoleResponse for display purposes

---

# Developer Notes

- **Replace-all strategy**: `setRoles()` deletes all existing `(formId, tenantId)` entries and inserts new ones. Avoids update conflicts and concurrency issues.
- **Tenant isolation**: `getCurrentTenantId()` extracts tenant from `RuntimeContextHolder.get()`. Tenant Admin can never access another tenant's data.
- **Global tenant roles**: `getGlobalTenantRoles()` groups all assignments by tenantId for System Admin visibility.
- **Global forms listing**: Filters `sys_metadata_views` by `scope='global'` and `type='form'`, then cross-references with `sys_form_tenant_role` to show `hasConfiguredAccess`.
- All endpoints use `ApiVersionConfig.API_BASE` (/api/v1) prefix.

---

# QA Handoff

Suggested test focus:
1. Tenant Admin can view current role assignments for their tenant on any form
2. Tenant Admin can replace role assignments (delete-all + insert)
3. Tenant Admin sees only their own tenant's assignments
4. System Admin can view all tenants' role assignments across all forms
5. Tenant role changes do not affect other tenants' assignments
6. Global forms list endpoint shows which forms have configured access for the current tenant
7. Unauthorized access returns 403

Potential risk areas:
- Race condition: two Tenant Admins updating the same form's roles simultaneously (replace-all can overwrite)
- Null RuntimeContext in unauthenticated scenarios

---

# Related Documents

Task: ai/tasks/TASK-010-tenant-role-assignment-apis.md

PRD: ai/prd/PRD-001-dynamic-form-configuration-system.md
