---
id: TASK-077
title: Update Sales Order form with workflow actions and payment terms
type: Feature
scope: both
status: PLANNED
priority: High
owner: developer
assigned_to:
assigned_branch:
locked: false
created: 2026-07-29
updated: 2026-07-29
started:
completed:
estimated_hours: 4
actual_hours:
parent_prd: PRD-007
prd_version: 1.0.0
prd_branch: prd/PRD-007-sales-order-workflow-customer-management
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-072
  - TASK-073
  - TASK-074
depends_on:
  - TASK-072
  - TASK-073
  - TASK-074
blocks: []
labels:
  - frontend
  - database
  - prd-007
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

Upgrade the runtime sales_order form so reps work the order through its lifecycle from the UI: read-only computed totals, payment term selection, and action buttons (Confirm / Mark Delivered / Create Invoice / Cancel) that call the workflow API — with credit-check feedback surfaced to the user.

---

# Description

**Form metadata updates (migration):**
- Mark subtotal, tax_amount, discount_amount, grand_total as read-only on the sales_order form
- Mark status as read-only (transitions happen via buttons, not manual edits)
- Ensure payment_term_id dropdown present (from TASK-072)

**Frontend runtime enhancement:**
- Add a workflow action bar to the runtime form for sales_order: fetch `GET /api/v1/orders/{id}/allowed-transitions` and render one button per allowed target (Confirm, Mark Delivered, Create Invoice, Cancel)
- On click: call `POST /api/v1/orders/{id}/transition`; on 400 (e.g., credit failure) show the server error message in a dialog
- After line add/edit/delete in the Order Lines sub-form, call `POST /api/v1/orders/{id}/calculate` and refresh header totals live
- Show a credit status badge (Approved / Exceeded / On Hold) next to the customer field using the check-credit endpoint

Implement via the runtime renderer's action/button extension points if available; otherwise a thin wrapper component registered for the sales_order form code.

---

# Acceptance Criteria

- [ ] `[SE]` Header totals and status are read-only on the form
- [ ] `[SE]` Action buttons render based on allowed-transitions response
- [ ] `[SE]` Line changes trigger live recalculation of header totals
- [ ] `[SE]` Credit failure on Confirm shows server message to the user
- [ ] `[QA]` Full lifecycle works from UI: draft → confirmed → delivered → invoiced
- [ ] `[QA]` Invalid transitions not offered as buttons
- [ ] `[SE][QA]` Frontend typecheck + lint pass; backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Depends on workflow API (TASK-073), calculation API (TASK-074), payment term field (TASK-072)
- Reference PRD-007 FR-006

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__update_sales_order_form.sql`
- Frontend: workflow action bar component + registration (paths determined by renderer extension design)

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

Created Task (PLANNED) — PRD-007 approved by user

---

# Related Documents

- PRD-007 — Sales Order Workflow & Customer Management (FR-006)
