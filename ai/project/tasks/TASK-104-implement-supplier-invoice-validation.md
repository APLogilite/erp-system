---
id: TASK-104
title: Implement supplier invoice validation and matching
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
estimated_hours: 5
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
  - TASK-102
  - TASK-103
depends_on:
  - TASK-101
  - TASK-102
  - TASK-103
blocks:
  - TASK-135
labels:
  - purchase
  - ap
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

Validate supplier invoices against receipts and prevent over-invoicing.

---

# Description

- Create `SupplierInvoiceValidationService`
- On validate:
  - Check invoice qty ≤ received qty (configurable override)
  - Status → Validated
  - Lock invoice from editing
- Endpoint: `POST /api/v1/purchase/invoices/{id}/validate`
- Optional endpoint: create invoice from receipt

---

# Acceptance Criteria

- [ ] `[SE]` Validation blocks over-invoicing
- [ ] `[SE]` Validated invoice becomes read-only
- [ ] `[SE]` Endpoint returns `ApiResponse<T>`
- [ ] `[QA]` Over-invoicing is rejected
- [ ] `[QA]` Valid invoice is accepted

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Allow manual override flag per tenant in future
- Prepare GL posting hook for PRD-020

---

# Files Expected

- `SupplierInvoiceValidationService.java`
- Controller endpoint
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

- PRD-012 — Goods Receipt & Supplier Invoice Matching
