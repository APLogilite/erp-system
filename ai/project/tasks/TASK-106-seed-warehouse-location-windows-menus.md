---
id: TASK-106
title: Seed warehouse and location windows, tabs, fields, and menus
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
estimated_hours: 5
actual_hours:
parent_prd: PRD-013
prd_version: 1.0.0
prd_branch: prd/PRD-013-warehouse-location-management
base_branch: main
merge_target: prd/PRD-013-warehouse-location-management
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-105
  - TASK-107
depends_on:
  - TASK-105
blocks:
  - TASK-107
labels:
  - inventory
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

Make warehouse, location type, and location forms available in the runtime UI.

---

# Description

- Update Warehouse window with new fields and location child tab
- Create Location Type window
- Create Location window with hierarchy/parent selection
- Add Inventory menu group and menu entries
- Configure window access

---

# Acceptance Criteria

- [ ] `[SE]` Warehouse window updated
- [ ] `[SE]` Location type and location windows render
- [ ] `[SE]` Menu entries visible
- [ ] `[QA]` User can create warehouse locations in hierarchy

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Location tree display in runtime may be simple list in v1.0

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

- PRD-013 — Warehouse Structure & Location Management
