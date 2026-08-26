---
id: TASK-136
title: Implement AR/AP aging reports and invoice allocation
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
parent_prd: PRD-020
prd_version: 1.0.0
prd_branch: prd/PRD-020-accounts-payable-receivable
base_branch: main
merge_target: prd/PRD-020-accounts-payable-receivable
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-133
  - TASK-134
  - TASK-135
depends_on:
  - TASK-133
  - TASK-134
  - TASK-135
  - TASK-132
blocks:
  - TASK-140
labels:
  - accounting
  - ar
  - ap
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

Provide AR and AP aging reports and track invoice open balances via allocations.

---

# Description

- Create `ARAgingReportService` and `APAgingReportService`
- Aging buckets: Current, 1-30, 31-60, 61-90, 90+
- Create `InvoiceAllocationService`
  - Apply payments/credits to invoices
  - Update open_amount and paid_amount
- Endpoints:
  - `GET /api/v1/accounting/ar/aging`
  - `GET /api/v1/accounting/ap/aging`
  - `POST /api/v1/accounting/invoices/{id}/allocate`

---

# Acceptance Criteria

- [ ] `[SE]` AR aging report implemented
- [ ] `[SE]` AP aging report implemented
- [ ] `[SE]` Allocation updates open balance
- [ ] `[QA]` Aging buckets are correct
- [ ] `[QA]` Allocation reduces open amount

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Aging based on invoice date and as-of date
- Allocation amount cannot exceed open amount

---

# Files Expected

- `ARAgingReportService.java`
- `APAgingReportService.java`
- `InvoiceAllocationService.java`
- Controller endpoints
- Report window seeds
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

- PRD-020 — Accounts Payable & Receivable
