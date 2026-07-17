---
id: TASK-059

title: Move Frontend Pages from modules/ to routes/

type: Refactor

scope: frontend

status: READY_FOR_TEST

priority: Medium

owner: developer

assigned_to:

assigned_branch: feature/TASK-059

locked: false

created: 2026-07-16

updated: 2026-07-17

started: 2026-07-17

completed: 2026-07-17

estimated_hours: 2

actual_hours:

parent_prd: PRD-005

prd_version: 1.3.0

prd_branch: prd/PRD-005

base_branch:

merge_target:

merge_strategy:

parent_task:

related_tasks: []

depends_on: []

blocks: []

labels:
  - frontend
  - refactor
  - prd-005

review_required: true

test_required: true

automation_required: false

change_summary: ai/project/changes/CHANGE-TASK-059.md

test_report:

test_script:

history:
  - created
  - 2026-07-17: activated to READY_FOR_DEV (SE)
  - 2026-07-17: locked and started IN_DEVELOPMENT (SE)
  - 2026-07-17: merged to PRD branch, READY_FOR_TEST (SE)

---

# Goal

Move all page components from `modules/` to `routes/` so all pages follow the same convention.

---

# Description

Page components are currently split between two directories:
- `routes/` — auth, dashboard, runtime, window pages
- `modules/` — identity pages (context, preferences, profile, sessions, admin) and admin designer pages (forms, tables)

Move all pages under `modules/` into `routes/` to follow the established convention. Update `AppRoutes.tsx` imports accordingly.

**Move these 15+ pages:**

Identity pages:
- `modules/identity/context/ContextSelectPage.tsx` → `routes/identity/context/ContextSelectPage.tsx`
- `modules/identity/preferences/PreferencesPage.tsx` → `routes/identity/preferences/PreferencesPage.tsx`
- `modules/identity/profile/ProfilePage.tsx` → `routes/identity/profile/ProfilePage.tsx`
- `modules/identity/sessions/SessionsPage.tsx` → `routes/identity/sessions/SessionsPage.tsx`
- `modules/identity/admin/*` (AdminDashboardPage + 10 sub-pages) → `routes/identity/admin/*`

Admin designer pages:
- `modules/admin/forms/FormDesignerPage.tsx`, `FormListPage.tsx` → `routes/admin/forms/`
- `modules/admin/tables/CreateTablePage.tsx`, `TableDetailPage.tsx`, `TableListPage.tsx` → `routes/admin/tables/`

---

# Acceptance Criteria

- [ ] All identity pages moved to `routes/identity/`
- [ ] All admin designer pages moved to `routes/admin/`
- [ ] `modules/identity/` and `modules/admin/` directories deleted (or left empty with note)
- [ ] All imports in `AppRoutes.tsx` updated from `../modules/...` to `./identity/...` and `./admin/...`
- [ ] Any internal imports within moved files (e.g., relative imports between admin pages) are updated
- [ ] `pnpm build` / `pnpm typecheck` succeeds
- [ ] All routes work identically

---

# Technical Notes

- Pure file move — no behavioral changes
- Update import paths in `AppRoutes.tsx` only
- Check if moved files have relative imports to each other (e.g., admin pages importing from sibling subdirectories) — these will break if the directory structure changes
- Example: `modules/identity/admin/AdminDashboardPage.tsx` importing from `../branches/BranchesAdminPage.tsx` would become `./branches/BranchesAdminPage.tsx` since they're both under `routes/identity/admin/` now
- If `modules/` directory becomes empty, delete the `.gitkeep` file and the directory
- Update the module documentation in `ai/project/modules/` if it references specific page paths

---

# Files Expected

- MOVE 15+ files from `modules/` to `routes/`
- UPDATE `frontend/src/routes/AppRoutes.tsx` — 15 import path changes
