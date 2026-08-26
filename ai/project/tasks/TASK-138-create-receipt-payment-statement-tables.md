---
id: TASK-138
title: Create receipt, payment, and bank statement tables
type: Feature
scope: database
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
parent_prd: PRD-021
prd_version: 1.0.0
prd_branch: prd/PRD-021-bank-cash-payments-reconciliation
base_branch: main
merge_target: prd/PRD-021-bank-cash-payments-reconciliation
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-137
  - TASK-139
  - TASK-140
  - TASK-141
depends_on:
  - TASK-137
blocks:
  - TASK-139
  - TASK-140
labels:
  - accounting
  - bank
  - metadata
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

Create the database tables for customer receipts, vendor payments, and bank statements.

---

# Description

- Create `ar_receipt` and `ar_receipt_line` tables
- Create `ap_payment` and `ap_payment_line` tables
- Create `acc_bank_statement` and `acc_bank_statement_line` tables
- Register tables in metadata
- Add document sequences `REC`, `PAY`, `STMT`

---

# Acceptance Criteria

- [ ] `[SE]` Tables created
- [ ] `[SE]` Metadata registered
- [ ] `[SE]` Document sequences seeded
- [ ] `[QA]` Tables verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Receipt/payment lines link to invoices
- Statement lines track reconciled flag

---

# Files Expected

- Flyway migration

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
