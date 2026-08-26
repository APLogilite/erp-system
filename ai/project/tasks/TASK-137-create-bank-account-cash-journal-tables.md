---
id: TASK-137
title: Create bank account and cash journal tables
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
estimated_hours: 3
actual_hours:
parent_prd: PRD-021
prd_version: 1.0.0
prd_branch: prd/PRD-021-bank-cash-payments-reconciliation
base_branch: main
merge_target: prd/PRD-021-bank-cash-payments-reconciliation
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-138
  - TASK-139
  - TASK-140
  - TASK-141
depends_on:
  - TASK-128
blocks:
  - TASK-138
  - TASK-139
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

Create the database tables for bank accounts and cash journals.

---

# Description

- Create `acc_bank_account` table
- Create `acc_cash_journal` table
- Register tables in metadata
- Add FKs to GL accounts

---

# Acceptance Criteria

- [ ] `[SE]` Tables created
- [ ] `[SE]` Metadata registered
- [ ] `[QA]` Tables verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Each bank account/cash journal links to a GL account

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
