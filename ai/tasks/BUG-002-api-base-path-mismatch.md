---
id: BUG-002

title: ApiVersionConfig.API_BASE uses /api instead of /api/v1 causing 500 errors on all metadata/runtime endpoints

status: READY_FOR_TEST

priority: High

severity: High

owner: Software Engineer

assigned_to: Software Engineer

assigned_branch: bugfix/BUG-002

locked: true

created: 2026-07-13

updated: 2026-07-13

started: 2026-07-13

completed: 2026-07-13

parent_prd: PRD-001

parent_task: TASK-007

reported_by: User (frontend console)

detected_in: Runtime (localhost:5173 → localhost:8081)

related_test:

fix_summary: ai/changes/CHANGE-BUG-002.md

verification_report:

history:
  - 2026-07-13 — Product Manager — Created bug task. User reported 500 errors on Table Designer, Form Designer, and all runtime form pages.
  - 2026-07-13 — Software Engineer — Fixed API_BASE from "/api" to "/api/v1" in ApiVersionConfig.java. Verified: metadata endpoints now return 403 (auth required, not 500), runtime endpoints return 200.

---

# Summary

**14 backend controllers** are mapped to `/api/...` but the frontend sends all requests to `/api/v1/...`. Every page that uses metadata designer or runtime APIs returns 500 errors. ~20+ pages are blocked.

Root cause: `ApiVersionConfig.API_BASE = "/api"` should be `"/api/v1"`.

# Problem

Every frontend page that calls a metadata or runtime endpoint shows errors:
```
GET http://localhost:8081/api/v1/metadata/tables?size=50   500
GET http://localhost:8081/api/v1/metadata/forms?size=50    500
GET http://localhost:8081/api/v1/runtime/forms             500
GET http://localhost:8081/api/v1/security/...              500
```

The backend returns:
```json
{"success":false,"message":"Unexpected error occurred. Please contact support."}
```

Actual Spring exception:
```
NoResourceFoundException: No static resource api/v1/metadata/tables.
```

# Expected Behaviour

All metadata and runtime pages load successfully — no 500 errors.

# Actual Behaviour

**20+ pages are broken.** Full list of affected endpoints and the pages they break:

| Endpoint | Controller | Affected Pages |
|----------|-----------|----------------|
| `GET /metadata/tables` | `TableDesignerController` | Table Designer list, Table Detail, Create Table |
| `GET /metadata/forms` | `FormDesignerController` | Form Designer list, Form Designer detail |
| `GET /metadata/forms/{id}/fields` | `FormDesignerController` | Fields tab in Form Designer |
| `GET /metadata/forms/{id}/rules` | `FormRuleController` | Rules tab |
| `GET /metadata/forms/{id}/validations` | `FormValidationController` | Validations tab |
| `GET /metadata/forms/{id}/subforms` | `FormSubFormController` | Sub-Forms tab |
| `GET /metadata/forms/{id}/tenant-roles` | `FormTenantRoleController` | Tenant Role Access tab |
| `GET /metadata/expressions` | `ExpressionController` | Expression validation |
| `GET /runtime/forms` | `RuntimeFormController` | Ctrl+K search bar, FormNavigationMenu sidebar |
| `GET /runtime/forms/{code}/definition` | `RuntimeFormController` | Dynamic form rendering (all runtime pages) |
| `GET/POST/PUT/DELETE /runtime/...` | `RuntimeController` | Record CRUD on all dynamic forms |
| `GET /runtime/relations` | `RelationController` | Related record lookups |
| `GET /security/...` | `PermissionController` | Permission checks |
| `GET /workflow/...` | `WorkflowController` | Workflow state transitions |

**Pages that still work** (don't depend on these endpoints):
- Login page ✅
- Dashboard ✅
- Identity admin pages (tenants, orgs, companies, branches, depts, users, roles, permissions, sessions, audit) ✅
- Profile, Preferences, Change Password, Sessions ✅

# Root Cause

**`ApiVersionConfig.java`** line 9:
```java
public static final String API_BASE = "/api";        // WRONG — used by 14 controllers
public static final String API_V1 = "/api/v1";       // CORRECT — used by manufacturing/analytics
```

The frontend `env.ts` sets:
```ts
export const apiBaseUrl = import.meta.env.VITE_API_URL ?? 'http://localhost:8081/api/v1';
```

All frontend API calls via `apiClient` prefix with `/api/v1`. But the backend controllers map to `/api/...`. Request never matches any `@RequestMapping` — falls through to static resource handler.

**No other configuration issues found.** The frontend consistently uses `/api/v1` in all API files (`runtimeApi.ts`, `useAccessibleForms.ts`, `authService.ts`, `metadataService.ts`, etc.). The fix is isolated to 1 line in 1 file.

# Fix

**Option A (recommended — 1 file, 1 line):**

In `backend/src/main/java/com/erp/config/ApiVersionConfig.java`:
```java
// Before:
public static final String API_BASE = "/api";

// After:
public static final String API_BASE = "/api/v1";
```

This fixes all 14 controllers at once. Zero frontend changes needed. Zero backend compilation errors (no controllers reference `API_BASE` outside of `@RequestMapping` annotations).

Also remove `API_V1` to avoid future confusion, or leave it for documentation:
```java
public static final String API_V1 = "/api/v1";   // keep as alias for backward compat
```

# Validation

- [ ] Table Designer page loads — `GET /api/v1/metadata/tables` returns 200
- [ ] Form Designer page loads — `GET /api/v1/metadata/forms` returns 200
- [ ] Form Designer tabs (Fields, Layout, Rules, Validations, Sub-Forms) load
- [ ] Ctrl+K search opens and lists accessible forms — `GET /api/v1/runtime/forms` returns 200
- [ ] Dynamic runtime page loads — `GET /api/v1/runtime/forms/{code}/definition` returns 200
- [ ] Manufacturing/analytics controllers still work (already use `API_V1`)
- [ ] `mvn test` passes with BUILD SUCCESS

# Files Changed

- `backend/src/main/java/com/erp/config/ApiVersionConfig.java` (1 line)

# Related Documents

- [PRD-001 — Dynamic Form Configuration System](../prd/PRD-001-dynamic-form-configuration-system.md)
- [BUG-003 — Sidebar content overlap](../tasks/BUG-003-sidebar-content-overlap.md)
- [BUG-004 — FormSearchBar not loading](../tasks/BUG-004-search-bar-not-loading.md)
