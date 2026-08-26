---
id: TASK-113
title: Seed physical inventory and stock adjustment windows, tabs, fields, and menus
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
parent_prd: PRD-015
prd_version: 1.0.0
prd_branch: prd/PRD-015-inventory-adjustments-valuation
base_branch: main
merge_target: prd/PRD-015-inventory-adjustments-valuation
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-112
  - TASK-114
  - TASK-115
depends_on:
  - TASK-112
blocks:
  - TASK-114
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

Make physical inventory count and stock adjustment forms available in the runtime UI.

---

# Description

- Create Physical Inventory window with lines tab showing system vs counted qty
- Create Stock Adjustment window
- Create Adjustment Reason window
- Add menu entries under Inventory
- Seed default adjustment reasons

---

# Acceptance Criteria

- [ ] `[SE]` Physical Inventory window renders
- [ ] `[SE]` Stock Adjustment window renders
- [ ] `[SE]` Menu entries visible
- [ ] `[QA]` User can enter counted quantities
- [ ] `[QA]` User can create a direct adjustment

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Physical inventory line auto-populates system qty from balance

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

- PRD-015 — Inventory Adjustments, Counting & Valuation
