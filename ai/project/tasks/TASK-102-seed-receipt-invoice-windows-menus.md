---
id: TASK-102
title: Seed goods receipt and supplier invoice windows, tabs, fields, and menus
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
parent_prd: PRD-012
prd_version: 1.0.0
prd_branch: prd/PRD-012-goods-receipt-supplier-invoice
base_branch: main
merge_target: prd/PRD-012-goods-receipt-supplier-invoice
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-101
  - TASK-103
  - TASK-104
depends_on:
  - TASK-101
blocks:
  - TASK-103
  - TASK-104
labels:
  - purchase
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

Make goods receipt and supplier invoice forms available in the runtime UI.

---

# Description

- Create Goods Receipt window with lines tab and "Receive All" shortcut
- Create Supplier Invoice window with lines tab and PO/receipt lookup
- Add action buttons: Complete, Validate
- Add menu entries under Purchasing
- Configure window access

---

# Acceptance Criteria

- [ ] `[SE]` Goods Receipt window renders
- [ ] `[SE]` Supplier Invoice window renders
- [ ] `[SE]` Menu entries visible
- [ ] `[QA]` User can create a receipt against a PO
- [ ] `[QA]` User can create a supplier invoice from a receipt

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Receipt lines auto-populate from PO lines
- Invoice lines auto-populate from receipt lines

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

- PRD-012 — Goods Receipt & Supplier Invoice Matching
