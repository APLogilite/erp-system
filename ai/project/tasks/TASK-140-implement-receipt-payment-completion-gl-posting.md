---
id: TASK-140
title: Implement receipt and payment completion with GL posting
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
  - TASK-141
depends_on:
  - TASK-138
  - TASK-139
  - TASK-136
blocks:
  - TASK-141
labels:
  - accounting
  - bank
  - backend
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

Complete customer receipts and vendor payments, update invoice open balances, and post to GL.

---

# Description

- Create `CustomerReceiptCompletionService`
  - On complete: post GL (Debit Bank/Cash, Credit AR) and update invoice open balances
- Create `VendorPaymentCompletionService`
  - On complete: post GL (Debit AP, Credit Bank/Cash) and update invoice open balances
- Endpoints:
  - `POST /api/v1/accounting/receipts/{id}/complete`
  - `POST /api/v1/accounting/payments/{id}/complete`

---

# Acceptance Criteria

- [ ] `[SE]` Receipt completion posts GL and reduces AR
- [ ] `[SE]` Payment completion posts GL and reduces AP
- [ ] `[SE]` Invoice open balances updated
- [ ] `[QA]` AR/AP aging reflects payment

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Use allocation service from PRD-020
- Use GL posting engine from PRD-019

---

# Files Expected

- `CustomerReceiptCompletionService.java`
- `VendorPaymentCompletionService.java`
- Controller endpoints
- Integration tests

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
