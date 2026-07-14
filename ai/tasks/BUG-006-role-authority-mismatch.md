---
id: BUG-006

title: Backend @PreAuthorize uses SYSTEM_ADMIN but seeded role code is sys_admin — causes 403 on all admin endpoints

status: RESOLVED

priority: Critical

severity: High

owner: Software Engineer

assigned_to: Software Engineer

assigned_branch: (merged to main)

locked: false

created: 2026-07-13

updated: 2026-07-13

started:

completed:

parent_prd: PRD-001

parent_task: TASK-007

reported_by: User + Product Manager investigation

detected_in: Backend authorization (localhost:8081)

related_test:

fix_summary:

verification_report:

history:
  - 2026-07-13 — Product Manager — Created bug task. After BUG-002 fixed the 500 errors, all metadata/runtime endpoints return 403 Access Denied for admin user because role codes don't match.
  - 2026-07-14 — QA Engineer — RESOLVED. Fix verified on main: `@PreAuthorize("hasAuthority('sys_admin')")` present in all affected controllers.

---

# Summary

All metadata designer pages (Table Designer, Form Designer) and the runtime form search return **403 Access Denied** for the `admin` user. This is not a routing issue (BUG-002 already fixed that) — it's an authorization configuration mismatch.

# Problem

After BUG-002 fixed the API path (`/api` → `/api/v1`), the endpoints now reach the correct controllers but return 403:

```
GET /api/v1/metadata/tables → 403 Access Denied
GET /api/v1/metadata/forms  → 403 Access Denied
GET /api/v1/runtime/forms   → 200 with empty data (admin should see all forms)
```

The `admin` user logs in with `roles: ["sys_admin"]` but the controllers check for `SYSTEM_ADMIN`.

# Expected Behaviour

- `admin` user (role `sys_admin`) should have access to all admin endpoints
- Table Designer and Form Designer should load
- Runtime forms should list all available forms for admin

# Actual Behaviour

All endpoints with `@PreAuthorize("hasRole('SYSTEM_ADMIN')")` return 403. The runtime forms endpoint also returns empty because `RuntimeFormController.listAccessibleForms()` checks `roleCodes.contains("SYSTEM_ADMIN")` which never matches `sys_admin`.

# Root Cause

**Role seeding** (`IdentitySeedData.java`):
- Admin role is seeded with code `sys_admin` (lowercase, underscore)
- Tenant admin role is seeded with code `tnt_admin`

**Backend authorization checks** use `SYSTEM_ADMIN` / `TENANT_ADMIN` (uppercase, no underscore):
- `@PreAuthorize("hasRole('SYSTEM_ADMIN')")` — Spring Security checks for `ROLE_SYSTEM_ADMIN`, but the actual authority is `sys_admin`
- `@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','TENANT_ADMIN')")` — same mismatch
- `RuntimeFormController.listAccessibleForms()` — checks `roleCodes.contains("SYSTEM_ADMIN")`
- `FormDefinitionAssemblyService` — checks `roleCodes.contains("SYSTEM_ADMIN")`

**Affected files:**

| File | Issue |
|------|-------|
| `TableDesignerController.java` | `@PreAuthorize("hasRole('SYSTEM_ADMIN')")` |
| `FormDesignerController.java` | `@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','TENANT_ADMIN')")` |
| `FormRuleController.java` | `@PreAuthorize("hasRole('SYSTEM_ADMIN')")` |
| `FormValidationController.java` | `@PreAuthorize("hasRole('SYSTEM_ADMIN')")` |
| `FormSubFormController.java` | `@PreAuthorize("hasRole('SYSTEM_ADMIN')")` |
| `FormTenantRoleController.java` | `@PreAuthorize("hasRole('SYSTEM_ADMIN')")` |
| `ExpressionController.java` | `@PreAuthorize("hasRole('SYSTEM_ADMIN')")` |
| `RuntimeFormController.java` | `roleCodes.contains("SYSTEM_ADMIN")` (line 102) |
| `FormDefinitionAssemblyService.java` | `ROLE_SYSTEM_ADMIN = "SYSTEM_ADMIN"` (line 58) |

# Fix

**Option A — Change `@PreAuthorize` to use hasAuthority (recommended):**
Change all `hasRole('SYSTEM_ADMIN')` → `hasAuthority('sys_admin')` and `hasAnyRole('SYSTEM_ADMIN','TENANT_ADMIN')` → `hasAnyAuthority('sys_admin','tnt_admin')` in the 8 affected files. Also fix `RuntimeFormController` and `FormDefinitionAssemblyService` to use `"sys_admin"`.

**Option B — Change the seeded role codes in IdentitySeedData:**
Change `sys_admin` → `SYSTEM_ADMIN` and `tnt_admin` → `TENANT_ADMIN` in the seed data. This would require re-seeding and may break existing references.

**Option C — Prefix roles with ROLE_ in JwtAuthenticationFilter:**
Change `new SimpleGrantedAuthority(role)` → `new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())` in the filter, then fix all hasRole/hasAnyRole calls to use uppercase. More invasive.

**Option A is recommended** because it's the least invasive — it fixes 8 files with simple string changes and doesn't change the role data or the authentication flow.

# Validation

- [ ] `admin` user can access Table Designer — `GET /api/v1/metadata/tables` returns 200
- [ ] `admin` user can access Form Designer — `GET /api/v1/metadata/forms` returns 200
- [ ] `admin` user can access Form Designer tabs (Rules, Validations, Sub-Forms)
- [ ] `admin` user sees all forms in runtime — `GET /api/v1/runtime/forms` returns 11+ forms
- [ ] `jane.smith` (tnt_admin) still has appropriate access
- [ ] Regular users cannot access admin endpoints (still protected)
- [ ] `mvn test` passes with BUILD SUCCESS

# Files Changed

- `backend/src/main/java/com/erp/core/metadata/controller/TableDesignerController.java`
- `backend/src/main/java/com/erp/core/metadata/controller/FormDesignerController.java`
- `backend/src/main/java/com/erp/core/metadata/controller/FormRuleController.java`
- `backend/src/main/java/com/erp/core/metadata/controller/FormValidationController.java`
- `backend/src/main/java/com/erp/core/metadata/controller/FormSubFormController.java`
- `backend/src/main/java/com/erp/core/metadata/controller/FormTenantRoleController.java`
- `backend/src/main/java/com/erp/core/metadata/controller/ExpressionController.java`
- `backend/src/main/java/com/erp/core/runtime/controller/RuntimeFormController.java`
- `backend/src/main/java/com/erp/core/runtime/service/FormDefinitionAssemblyService.java`

# Related Documents

- [BUG-002 — API base path mismatch](../tasks/BUG-002-api-base-path-mismatch.md)
- [BUG-004 — Search bar issues](../tasks/BUG-004-search-bar-not-loading.md)
- [PRD-001 — Dynamic Form Configuration System](../prd/PRD-001-dynamic-form-configuration-system.md)
