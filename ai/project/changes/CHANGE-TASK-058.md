---
id: CHANGE-TASK-058

task_id: TASK-058

parent_prd: PRD-005

branch: feature/TASK-058

type: Refactor

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-17

completed: 2026-07-17

duration: 1.5 hours (estimated)

related_commits:
  - refactor(TASK-058): move window schema from modules/metadata/ to core/layout/

related_files:
  - MOVED 21 files from modules/metadata/ to core/layout/
  - UPDATED imports in 4 consumer files in core/runtime/

review_required: true

test_required: true

---

# Summary

Moved the window schema entities, repositories, and services from `modules/metadata/` to `core/layout/`. This structural refactor places the layout configuration (Sys* entities defining windows, tabs, fields, menus) alongside their consumers in the core layer. All 21 files had their package declarations updated from `com.erp.modules.metadata.*` to `com.erp.core.layout.*`. Imports in 4 consumer files (`WindowDefinitionAssemblyService`, `WindowDataService`, `MenuController`, `WindowDefinitionController`) were updated accordingly.

---

# Scope Verification

- [ ] Frontend
- [x] Backend
- [ ] Database
- [ ] Configuration

---

# Business Requirements Implemented

- Move Window Schema from modules/metadata/ to core/layout/
  - 7 entities moved: SysColumn, SysMenu, SysTab, SysTable, SysWindowAccess, SysWindowField, SysWindow
  - 7 repositories moved: SysColumnRepository, SysMenuRepository, SysTabRepository, SysTableRepository, SysWindowAccessRepository, SysWindowFieldRepository, SysWindowRepository
  - 7 services moved: SysColumnService, SysMenuService, SysTabService, SysTableService, SysWindowAccessService, SysWindowFieldService, SysWindowService
  - `modules/metadata/` directory deleted
  - 4 consumer imports updated

---

# Files Modified

| File | Summary |
|------|---------|
| 21 files moved from `modules/metadata/` to `core/layout/` | Package declarations updated |
| 4 consumer files in `core/runtime/` | Imports updated from `modules.metadata.*` to `core.layout.*` |

---

# Validation

## Build

PASS — `mvn clean compile` succeeds

---

## Existing Automated Tests

PASS — All 36 backend tests pass

---

# Breaking Changes

None. Pure structural refactor — no behavioral changes.
