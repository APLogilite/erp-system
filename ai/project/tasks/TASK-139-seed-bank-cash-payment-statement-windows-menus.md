---
id: TASK-139
title: Seed bank, cash, payment, and statement windows, tabs, fields, and menus
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
parent_prd: PRD-021
prd_version: 1.0.0
prd_branch: prd/PRD-021-bank-cash-payments-reconciliation
base_branch: main
merge_target: prd/PRD-021-bank-cash-payments-reconciliation
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-137
  - TASK-138
  - TASK-140
  - TASK-141
depends_on:
  - TASK-137
  - TASK-138
blocks:
  - TASK-140
  - TASK-141
labels:
  - accounting
  - bank
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

Make bank accounts, cash journals, receipts, payments, and bank statement forms available in the runtime UI.

---

# Description

- Create Bank Account and Cash Journal windows
- Create Customer Receipt window with invoice allocation lines
- Create Vendor Payment window with invoice allocation lines
- Create Bank Statement window with lines tab
- Add menu entries under Accounting
- Configure access

---

# Acceptance Criteria

- [ ] `[SE]` All windows render
- [ ] `[SE]` Menu entries visible
- [ ] `[QA]` User can create a receipt
- [ ] `[QA]` User can create a payment

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Receipt/payment lines auto-populate open invoices

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

- PRD-021 — Bank, Cash & Payments Reconciliation
