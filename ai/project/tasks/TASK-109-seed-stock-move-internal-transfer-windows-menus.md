---
id: TASK-109
title: Seed stock move and internal transfer windows, tabs, fields, and menus
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
parent_prd: PRD-014
prd_version: 1.0.0
prd_branch: prd/PRD-014-stock-movements-logistics
base_branch: main
merge_target: prd/PRD-014-stock-movements-logistics
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-108
  - TASK-110
  - TASK-111
depends_on:
  - TASK-108
blocks:
  - TASK-110
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

Make stock move inquiry and internal transfer forms available in the runtime UI.

---

# Description

- Create Stock Move window (read-only inquiry) with filters
- Create Internal Transfer window with source/destination locators
- Create Inventory Balance inquiry window
- Add menu entries under Inventory
- Configure access

---

# Acceptance Criteria

- [ ] `[SE]` Stock Move window renders
- [ ] `[SE]` Internal Transfer window renders
- [ ] `[SE]` Inventory Balance inquiry renders
- [ ] `[QA]` User can view stock moves
- [ ] `[QA]` User can create an internal transfer

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Stock Move window may be read-only in v1.0
- Internal transfer supports complete action

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

- PRD-014 — Stock Movements & Logistics
