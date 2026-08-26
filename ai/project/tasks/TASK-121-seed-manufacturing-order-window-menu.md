---
id: TASK-121
title: Seed manufacturing order window, tabs, fields, and menu
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
parent_prd: PRD-017
prd_version: 1.0.0
prd_branch: prd/PRD-017-manufacturing-orders-mrp
base_branch: main
merge_target: prd/PRD-017-manufacturing-orders-mrp
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-120
  - TASK-122
  - TASK-123
  - TASK-124
depends_on:
  - TASK-120
blocks:
  - TASK-122
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

Make manufacturing order forms available in the runtime UI.

---

# Description

- Create Manufacturing Order window with component lines tab
- Add action buttons: Confirm, Issue Materials, Produce, Complete, Cancel
- Add menu entry under Manufacturing
- Configure access

---

# Acceptance Criteria

- [ ] `[SE]` MO window renders with lines tab
- [ ] `[SE]` Action buttons visible
- [ ] `[SE]` Menu entry visible
- [ ] `[QA]` User can create an MO
- [ ] `[QA]` Component lines auto-populate from BOM

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Component lines generated from BOM explosion service

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

- PRD-017 — Manufacturing Orders & Basic MRP
