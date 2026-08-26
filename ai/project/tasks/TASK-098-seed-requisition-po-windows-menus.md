---
id: TASK-098
title: Seed requisition and purchase order windows, tabs, fields, and menus
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
parent_prd: PRD-011
prd_version: 1.0.0
prd_branch: prd/PRD-011-purchase-requisition-order
base_branch: main
merge_target: prd/PRD-011-purchase-requisition-order
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-097
  - TASK-099
  - TASK-100
depends_on:
  - TASK-097
blocks:
  - TASK-099
  - TASK-100
labels:
  - purchase
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

Make requisition and purchase order forms available in the runtime UI.

---

# Description

- Create Purchase Requisition window with lines tab
- Create Purchase Order window with lines tab and totals footer
- Add workflow action buttons (Submit, Approve, Order, Cancel)
- Add "Convert to PO" action on requisition
- Add menu entries under Purchasing

---

# Acceptance Criteria

- [ ] `[SE]` Requisition window renders with lines tab
- [ ] `[SE]` Purchase Order window renders with lines tab
- [ ] `[SE]` Menu entries visible
- [ ] `[QA]` User can create a requisition
- [ ] `[QA]` User can create a purchase order
- [ ] `[QA]` Lines can be added to PO

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- PO line unit price defaults from price resolution service
- Use existing runtime window framework

---

# Files Expected

- Flyway seed migration for windows/tabs/fields/menus/access

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

- PRD-011 — Purchase Requisition & Purchase Order
