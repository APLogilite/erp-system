---
id: TASK-141
title: Implement bank reconciliation and cash position report
type: Feature
scope: backend
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
  - TASK-139
  - TASK-140
depends_on:
  - TASK-138
  - TASK-139
  - TASK-140
blocks: []
labels:
  - accounting
  - bank
  - backend
  - reporting
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

Match bank statement lines to system transactions and provide a cash position report.

---

# Description

- Create `BankReconciliationService`
  - Auto-match by reference/amount
  - Manual match support
  - Mark statement line and transaction as reconciled
- Create `CashPositionReportService`
  - Bank/cash balances and unreconciled items
- Endpoints:
  - `POST /api/v1/accounting/bank-statements/{id}/reconcile`
  - `GET /api/v1/accounting/cash-position`

---

# Acceptance Criteria

- [ ] `[SE]` Auto-match finds matching transactions
- [ ] `[SE]` Manual match marks both sides reconciled
- [ ] `[SE]` Cash position report shows correct balances
- [ ] `[QA]` Reconciliation updates reconciled flags

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Manual bank statement entry in v1.0; CSV import future

---

# Files Expected

- `BankReconciliationService.java`
- `CashPositionReportService.java`
- Controller endpoints
- Report window seed
- Unit tests

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
