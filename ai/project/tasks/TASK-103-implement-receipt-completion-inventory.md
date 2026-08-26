---
id: TASK-103
title: Implement goods receipt completion with inventory update
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
  - TASK-104
depends_on:
  - TASK-101
  - TASK-102
blocks:
  - TASK-104
  - TASK-110
labels:
  - purchase
  - inventory
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

Complete goods receipts and update purchase order received quantities and inventory stock moves.

---

# Description

- Create `GoodsReceiptCompletionService`
- On complete:
  - Update PO line `received_qty`
  - Create RECEIPT stock move for accepted quantity (PRD-014)
  - Update inventory balance
  - Status → Completed
- Validate accepted qty does not exceed remaining open qty beyond tolerance
- Endpoint: `POST /api/v1/purchase/receipts/{id}/complete`

---

# Acceptance Criteria

- [ ] `[SE]` PO received qty updated on completion
- [ ] `[SE]` Stock move created for accepted qty
- [ ] `[SE]` Inventory balance increased
- [ ] `[SE]` Completion is transactional
- [ ] `[QA]` On-hand stock increases after receipt completion
- [ ] `[QA]` PO received qty matches accepted qty

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Single transaction for receipt + PO + inventory
- Use stock move service from PRD-014 if available; otherwise stub and integrate later

---

# Files Expected

- `GoodsReceiptCompletionService.java`
- Controller endpoint
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

- PRD-012 — Goods Receipt & Supplier Invoice Matching
- PRD-014 — Stock Movements & Logistics
