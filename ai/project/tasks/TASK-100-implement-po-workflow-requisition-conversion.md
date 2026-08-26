---
id: TASK-100
title: Implement purchase order workflow and requisition conversion
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
parent_prd: PRD-011
prd_version: 1.0.0
prd_branch: prd/PRD-011-purchase-requisition-order
base_branch: main
merge_target: prd/PRD-011-purchase-requisition-order
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-097
  - TASK-098
  - TASK-099
depends_on:
  - TASK-097
  - TASK-098
  - TASK-099
blocks:
  - TASK-103
labels:
  - purchase
  - workflow
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

Enable status transitions for purchase requisitions and orders, and convert approved requisitions into purchase orders.

---

# Description

- Implement requisition workflow: Submit → Approve/Reject → Convert to PO
- Implement PO workflow: Draft → Ordered → Cancelled
- Create `PurchaseOrderWorkflowService` with actions:
  - `order(id)`: status → Ordered
  - `cancel(id)`: status → Cancelled (only if not received)
  - `reopen(id)`: status → Draft
- Implement requisition-to-PO conversion service
- Create endpoints for all actions

---

# Acceptance Criteria

- [ ] `[SE]` Requisition can be submitted, approved, rejected
- [ ] `[SE]` Approved requisition can be converted to PO
- [ ] `[SE]` PO can be ordered and cancelled
- [ ] `[SE]` Cancel blocked if PO has receipts
- [ ] `[QA]` End-to-end requisition → PO conversion works
- [ ] `[QA]` PO status transitions validated

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Audit log status changes
- Conversion preserves products, quantities, and notes

---

# Files Expected

- `PurchaseOrderWorkflowService.java`
- `RequisitionConversionService.java`
- Controller endpoints
- Unit/integration tests

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
