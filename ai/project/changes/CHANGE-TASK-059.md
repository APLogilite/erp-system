---
id: CHANGE-TASK-059

task_id: TASK-059

parent_prd: PRD-005

branch: feature/TASK-059

type: Refactor

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-17

completed: 2026-07-17

duration: 1.5 hours (estimated)

related_commits:
  - refactor(TASK-059): move frontend pages from modules/ to routes/

related_files:
  - MOVED 20 page files from modules/ to routes/
  - UPDATED frontend/src/routes/AppRoutes.tsx

review_required: true

test_required: true

---

# Summary

Moved all page components from `modules/` to `routes/` to follow the established convention. Identity pages (context, preferences, profile, sessions, admin) were moved to `routes/identity/`. Admin designer pages (forms, tables) were moved to `routes/admin/`. Internal import paths in the moved files were updated to `@/` aliases. `AppRoutes.tsx` import paths were updated from `../modules/...` to `./identity/...` and `./admin/...`.

---

# Scope Verification

- [x] Frontend
- [ ] Backend
- [ ] Database
- [ ] Configuration

---

# Business Requirements Implemented

- Move all identity pages to `routes/identity/` (15 pages moved)
  - ContextSelectPage, PreferencesPage, ProfilePage, SessionsPage
  - AdminDashboardPage, AdminListPage
  - AuditPage, BranchesAdminPage, CompaniesAdminPage, DepartmentsAdminPage
  - OrganizationsAdminPage, PermissionsAdminPage, RolesAdminPage
  - SessionsAdminPage, TenantsAdminPage, UsersAdminPage

- Move all admin designer pages to `routes/admin/` (5 pages moved)
  - FormDesignerPage, FormListPage
  - CreateTablePage, TableDetailPage, TableListPage

- AppRoutes.tsx import paths updated

---

# Files Modified

| File | Summary |
|------|---------|
| 20 files moved from `modules/` to `routes/` | New locations with updated import paths |
| `frontend/src/routes/AppRoutes.tsx` | 20 import paths changed from `../modules/...` to `./identity/...` and `./admin/...` |

---

# Validation

## Build

PASS — Frontend `tsc --noEmit` succeeds
