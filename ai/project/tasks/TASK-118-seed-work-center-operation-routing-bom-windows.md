---
id: TASK-118
title: Seed work center, operation, routing, and BOM windows, tabs, fields, and menus
type: Feature
scope: both
status: PLANNED
priority: High
owner: planner
assigned_to:
assigned_branch:
locked: false
created: 2026-08-26
updated: 2026-08-26
started:
completed:
estimated_hours: 6
actual_hours:
parent_prd: PRD-016
prd_version: 1.0.0
prd_branch: prd/PRD-016-bom-work-centers-routings
base_branch: main
merge_target: prd/PRD-016-bom-work-centers-routings
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-116
  - TASK-117
  - TASK-119
depends_on:
  - TASK-116
  - TASK-117
blocks:
  - TASK-119
labels:
  - manufacturing
  - metadata
  - ui
review_required: true
test_required: true
automation_required: true
change_summary:
test_report:
test_script:
history:
  - created
---

# Goal

Make manufacturing master data forms available in the runtime UI.

---

# Description

- Create Work Center window
- Create Operation window
- Create Routing window with operations tab
- Create BOM window with component lines tab
- Add Manufacturing menu group and entries
- Configure access

---

# Acceptance Criteria

- [ ] `[SE]` All four windows render
- [ ] `[SE]` Tabs and fields seeded
- [ ] `[SE]` Menu entries visible
- [ ] `[QA]` User can create a BOM with components
- [ ] `[QA]` User can create a routing with operations

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- BOM lines use product lookup
- Routing operations use operation and work center lookups

---

# Files Expected

- Flyway seed migration

---

# Developer Notes

---

# Tester Notes

---

# Review Notes

---

# Task History

2026-08-26 — Product Manager — Created task

---

# Related Documents

- PRD-016 — Product BOM, Work Centers & Routings
