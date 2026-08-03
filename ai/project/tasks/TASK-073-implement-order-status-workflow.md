---
id: TASK-073
title: Implement Order status workflow backend service
type: Feature
scope: backend
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
  - TASK-075
  - TASK-077
depends_on: []
blocks:
  - TASK-075
  - TASK-077
labels:
  - backend
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

Enforce a valid order lifecycle (Draft → Confirmed → Delivered → Invoiced → Paid, with Cancellation) at the backend so users cannot jump statuses arbitrarily or corrupt document state.

---

# Description

Implement `OrderWorkflowService` with transition validation and REST endpoint:

```
POST /api/v1/orders/{id}/transition
Request:  { "targetStatus": "confirmed" }
Response: { "success": true, "newStatus": "confirmed" }
Error:    400 { "success": false, "error": "Cannot transition from delivered to draft" }
```

Allowed transitions (case-insensitive statuses on tx_order):
- draft → confirmed, cancelled
- confirmed → delivered, cancelled
- delivered → invoiced, cancelled (cancel only when no invoice exists for the order)
- invoiced → paid (auto via payment matching — manual transition also allowed for now)
- any → cancelled (blocked when a non-cancelled invoice exists)

Rules:
- Invalid transition → 400 with explicit message naming current and target status
- Every successful transition writes an audit row to a new `tx_order_status_log` table (id, order_id, from_status, to_status, transitioned_by UUID, transitioned_at timestamp, tenant_id, system columns)
- Add `GET /api/v1/orders/{id}/allowed-transitions` returning the list of valid target statuses for the frontend to render action buttons

---

# Acceptance Criteria

- [ ] `[SE]` Valid transitions succeed and update tx_order.status
- [ ] `[SE]` Invalid transitions return 400 with explicit message
- [ ] `[SE]` Every transition logged to tx_order_status_log
- [ ] `[SE]` allowed-transitions endpoint returns correct list per status
- [ ] `[QA]` Cancel blocked when an invoice exists for the order
- [ ] `[QA]` Audit log rows show user, timestamp, from/to
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Migration needed for tx_order_status_log table + metadata registration
- Reference PRD-007 FR-001

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__create_order_status_log.sql`
- `backend/src/main/java/com/erp/modules/salesflow/service/OrderWorkflowService.java` (new)
- `backend/src/main/java/com/erp/modules/salesflow/controller/OrderWorkflowController.java` (new)
- `backend/src/main/java/com/erp/modules/salesflow/dto/TransitionRequest.java` (new)
- `backend/src/main/java/com/erp/modules/salesflow/dto/TransitionResponse.java` (new)

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

- PRD-007 — Sales Order Workflow & Customer Management (FR-001)
