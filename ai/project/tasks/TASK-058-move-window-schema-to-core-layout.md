---
id: TASK-058

title: Move Window Schema from modules/metadata/ to core/layout/

type: Refactor

scope: backend

status: READY_FOR_DEV

priority: High

owner: developer

assigned_to:

assigned_branch:

locked: false

created: 2026-07-16

updated: 2026-07-17

started:

completed:

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
  - backend
  - refactor
  - prd-005

review_required: true

test_required: true

automation_required: false

change_summary:

test_report:

test_script:

history:
  - created
  - 2026-07-17: activated to READY_FOR_DEV (SE)

---

# Goal

Move the window schema entities, repositories, and services from `modules/metadata/` to `core/layout/` so the layout configuration lives alongside its consumers in the core layer.

---

# Description

The `Sys*` entities (SysTable, SysColumn, SysWindow, SysTab, SysWindowField, SysWindowAccess, SysMenu) define the **core layout configuration** — what windows, tabs, fields, and menus exist in the system. They currently live under `modules/metadata/` but every consumer is in `core/runtime/`.

This is a structural move only — no logic changes. Move 21 files and update 5 consumer imports.

**Move:**
- `modules/metadata/entity/*` → `core/layout/entity/*`
- `modules/metadata/repository/*` → `core/layout/repository/*`
- `modules/metadata/service/*` → `core/layout/service/*`

**Update imports in:**
- `core/runtime/service/WindowDefinitionAssemblyService.java`
- `core/runtime/service/WindowDataService.java`
- `core/runtime/controller/WindowDefinitionController.java`
- `core/runtime/controller/MenuController.java`
- `core/runtime/controller/RuntimeFormController.java`

---

# Acceptance Criteria

- [ ] 7 entities moved to `core/layout/entity/`
- [ ] 7 repositories moved to `core/layout/repository/`
- [ ] 7 services moved to `core/layout/service/`
- [ ] `modules/metadata/` directory deleted
- [ ] All 5 consumer imports updated to `com.erp.core.layout.*`
- [ ] `mvn clean compile` succeeds
- [ ] All 36 existing tests pass
- [ ] All runtime window/menu features work identically

---

# Technical Notes

- Pure file move — no behavioral changes
- Update package declarations in all 21 moved files from `com.erp.modules.metadata` to `com.erp.core.layout`
- Update imports in the 5 consumer files
- Check `application.properties` or `@SpringBootApplication` scan config — if it uses `com.erp.modules` in scan base packages, the move to `com.erp.core` is still within the default scan scope, so no config changes needed
- After move, update the module documentation in `ai/project/modules/`

---

# Files Expected

- MOVE 21 files from `modules/metadata/` to `core/layout/`
- UPDATE 5 consumer files in `core/runtime/`
