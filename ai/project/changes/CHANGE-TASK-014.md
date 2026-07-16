---
id: CHANGE-TASK-014

task_id: TASK-014

parent_prd: PRD-001

branch: feature/TASK-014

type: Feature

status: IMPLEMENTED

developer: developer

started: 2026-07-08

completed: 2026-07-08

duration: 2h

related_commits: [3e5cb7f]

related_files:
  - frontend/src/modules/admin/forms/components/GlobalFormTenantAccessTable.tsx
  - frontend/src/modules/admin/forms/FormDesignerPage.tsx

review_required: true

test_required: true

---

# Summary

Built the System Admin view for browsing all tenants' role assignments on global forms. The `GlobalFormTenantAccessTable` component fetches tenants, roles, and form-tenant-role assignments, then joins the data client-side to display tenant names, resolved role names, and a configured/not-configured status per tenant. Integrated into `FormDesignerPage` as a conditional "Tenant Access" tab visible only for global forms when the current user has the SYSTEM_ADMIN role.

---

# Business Requirements Implemented

- FR-027: System Admin can see all tenants' role assignments per global form
- Tenants with no role assignments are listed as "Not configured"
- The view is read-only (no CRUD operations exposed)
- Shows tenant name, code, and assigned role names
- Tenants sorted: configured first, then alphabetically by name
- Tab only visible for global forms (scope='global') and SYSTEM_ADMIN users
- Role names resolved from UUIDs via identity API lookup

---

# Files Added

| File | Purpose |
|------|---------|
| `frontend/src/modules/admin/forms/components/GlobalFormTenantAccessTable.tsx` | Table component: fetches tenants + roles + assignments, joins data, displays read-only tenant access overview |

---

# Files Modified

| File | Summary |
|------|---------|
| `frontend/src/modules/admin/forms/FormDesignerPage.tsx` | Added conditional "Tenant Access" tab (tab index 5) for global forms with SYSTEM_ADMIN role; imported `useAuthStore` and `GlobalFormTenantAccessTable` |

---

# Files Removed

None

---

# Database Changes

None (frontend only)

---

# API Changes

None (consumes existing endpoints)

---

# Routes

None

---

# Classes Added

None (React components)

---

# Classes Updated

None

---

# Methods Added

| Component | Export | Purpose |
|-----------|--------|---------|
| GlobalFormTenantAccessTable | `GlobalFormTenantAccessTable` | Read-only table showing all tenants' role assignments for a global form |

---

# Methods Updated

| Component | Method | Summary |
|-----------|--------|---------|
| FormDesignerPage | Render | Added conditional tab rendering for Tenant Access |

---

# Models

None

---

# Services

None

---

# Repositories

None

---

# DTOs

None

---

# Requests

None

---

# Policies

None

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

Uses existing: MUI (`Table`, `TableBody`, `TableCell`, `TableContainer`, `TableHead`, `TableRow`, `Chip`, `Box`, `Typography`, `CircularProgress`), MUI Icons (`Info`), React hooks (`useState`, `useEffect`, `useCallback`), Zustand (`useAuthStore`), `apiClient` from `@/core/api/client`, shared UI components (`ErrorState`)

---

# Validation

## Build

PASS — `tsc --noEmit` (frontend, 0 errors)

## Lint

PASS — `eslint --max-warnings=0` on new component (0 errors, 0 warnings). Pre-existing formatting errors in `FormDesignerPage.tsx` (not introduced by this change).

## Static Analysis

N/A

## Existing Automated Tests

N/A (frontend — no test framework)

---

# Manual Verification

- [x] GlobalFormTenantAccessTable renders correctly with loading state
- [x] Error state with retry button on API failure
- [x] Empty state when no tenants exist
- [x] Table shows all tenants with names, codes, and role assignments
- [x] Configured tenants show role name chips; unconfigured show "—"
- [x] Status column: green "Configured" chip or default "Not configured" chip
- [x] Tenants sorted: configured first, then alphabetically
- [x] Read-only: no edit/delete/add buttons in the table
- [x] TypeScript compilation succeeds
- [x] "Tenant Access" tab only shown for global forms + SYSTEM_ADMIN
- [x] "Tenant Access" tab hidden for tenant-scoped forms and non-admin users

---

# Breaking Changes

None. Additive change to FormDesignerPage.

---

# Known Issues

1. **Role names are UUIDs if not in roles list**: If a role ID in `TenantRoleResponse.roleIds` doesn't match any role from `/identity/roles`, it displays as "Unknown (xxxxxxxx…)". This could happen if a role was deleted after assignment.
2. **All tenants listed**: Even tenants with no users are listed. This could be noisy for environments with many tenants. A future enhancement could filter to only active tenants.
3. **Role names not tenant-scoped in lookup**: The role lookup uses a flat list of all roles. If two tenants have roles with the same name, all role chips show the same name. This is acceptable since each tenant independently assigns roles.

---

# Future Improvements

- Add ability for System Admin to filter/group tenants
- Add tenant deactivation status indicator
- Add click-through to tenant detail page
- Cache tenant/role lists per session (currently fetched on every tab open)
- Add export to CSV for compliance reporting

---

# Developer Notes

- **Client-side join pattern**: The backend `GET /metadata/forms/{formId}/global-tenant-roles` returns only UUIDs (tenantId, roleIds). The frontend fetches `/identity/tenants` and `/identity/roles` in parallel and builds lookup maps for name resolution. This avoids backend N+1 queries but means the frontend fetches all tenants/roles regardless of form assignment.
- **Conditional tab**: The "Tenant Access" tab only renders when `form.scope === 'global'` AND `user.roles.includes('SYSTEM_ADMIN')`. The tab index (5) is only used when `showTenantAccess` is true, avoiding index offset issues.
- **useCallback for loadData**: Wrapped in `useCallback` with `[formId]` dependency to satisfy React hooks exhaustive-deps rule while avoiding unnecessary re-renders.
- **Sort order**: Configured tenants appear first (success state prioritized), then alphabetical by name. This makes it easy to scan for tenants needing attention.

---

# QA Handoff

Suggested test focus:
1. Log in as System Admin, navigate to a global form in Form Designer
2. Verify "Tenant Access" tab appears as the 6th tab (after Sub-Forms)
3. Verify the table shows all tenants with correct names
4. Verify tenants with assigned roles show role chips
5. Verify tenants without assignments show "Not configured" chip
6. Verify the view is read-only (no add/edit/delete UI elements)
7. Log in as Tenant Admin — verify "Tenant Access" tab does NOT appear
8. Navigate to a tenant-scoped form — verify "Tenant Access" tab does NOT appear
9. Test error state: block API calls, verify error message and retry button
10. Test loading state: verify spinner shown during data fetch

Potential risk areas:
- Large number of tenants (100+) — table may need pagination in the future
- Role ID resolution failures display "Unknown" — verify this degradation is acceptable
- Multi-tenant data leak: verify Tenant Admin cannot access this view even by URL manipulation (backend enforces `@PreAuthorize("hasRole('SYSTEM_ADMIN')")`)
