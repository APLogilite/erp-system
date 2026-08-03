---
id: TASK-090
title: Implement Return inventory receipt integration
type: Feature
scope: backend
status: PLANNED
priority: Medium
owner: developer
assigned_to:
assigned_branch:
locked: false
created: 2026-07-29
updated: 2026-07-29
started:
completed:
estimated_hours: 3
actual_hours:
parent_prd: PRD-009
prd_version: 1.0.0
prd_branch: prd/PRD-009-sales-return-analytics
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-089
depends_on:
  - TASK-089
blocks: []
labels:
  - backend
  - prd-009
review_required: true
test_required: true
automation_required: false
change_summary:
test_report:
test_script:
history:
  - created
---

# Goal

When returned goods physically arrive at the warehouse, record them back into inventory via a material receipt linked to the return — keeping stock levels accurate.

---

# Description

Implement return-receipt integration in `ReturnService`:

1. Add `receipt_id` column (UUID, FK → tx_material_receipt) to tx_return via migration
2. Endpoint `POST /api/v1/returns/{id}/receive`:
   - Validate status = 'approved'
   - Create tx_material_receipt header: receipt_number from sequence, partner_id, order_id (if any), status='received'
   - Create tx_mr_line rows from return lines (product, received_qty = return quantity, uom)
   - Link receipt_id on the return; transition return to 'received'
   - Single transaction
3. If an inventory/stock table exists with per-product quantities, increment stock for each received line; if no stock table exists yet, document the gap and limit scope to creating the receipt records (note in change report)

---

# Acceptance Criteria

- [ ] `[SE]` receive endpoint creates material receipt + lines from return
- [ ] `[SE]` Return linked to receipt and transitioned to received
- [ ] `[SE]` Receipt records visible in material_receipt runtime form
- [ ] `[QA]` Non-approved return returns 400
- [ ] `[QA]` Transactional: failure leaves no partial receipt
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Depends on ReturnService (TASK-089) and tx_material_receipt/tx_mr_line from PRD-003
- Stock-level update only if inventory module tables support it — otherwise receipt records only
- Reference PRD-009 FR-003

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__add_receipt_to_return.sql`
- `backend/src/main/java/com/erp/modules/returns/service/ReturnService.java` (modified)
- `backend/src/main/java/com/erp/modules/returns/controller/ReturnController.java` (modified)

---

# Developer Notes

*(maintained by SE)*

---

# Tester Notes

*(maintained by QA)*

---

# Review Notes

*(maintained by reviewer)*

---

# Task History

2026-07-29

Product Manager

Created Task (PLANNED) — PRD-009 approved by user

---

# Related Documents

- PRD-009 — Sales Return & Analytics (FR-003)
