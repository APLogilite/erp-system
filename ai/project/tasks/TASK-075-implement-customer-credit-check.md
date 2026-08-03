---
id: TASK-075
title: Implement Customer Credit Check backend service
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
  - TASK-071
  - TASK-073
depends_on:
  - TASK-071
  - TASK-073
blocks: []
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

Prevent order confirmation for customers who are on credit hold or whose outstanding exposure (open orders + unpaid invoices) plus the new order exceeds their credit limit — with a manager override path that is audit-logged.

---

# Description

Implement `CreditCheckService` with endpoints:

```
POST /api/v1/orders/{id}/check-credit
Response (pass): { "approved": true, "creditLimit": 50000, "outstanding": 12000, "available": 38000, "orderAmount": 5000 }
Response (fail): 400 { "approved": false, "reason": "Credit limit exceeded", "creditLimit": 50000, "outstanding": 48000, "orderAmount": 5000 }
```

Calculation:
- outstanding = Σ grand_total of tx_order where partner matches AND status IN ('confirmed','delivered') + Σ due_amount of tx_invoice where partner matches AND status IN ('validated','partially_paid')
- approved = (credit_hold = false) AND (outstanding + orderAmount ≤ credit_limit OR credit_limit IS NULL)

Integration with workflow (TASK-073):
- Hook into the `draft → confirmed` transition: run credit check automatically; block transition on failure
- Add `override: true` option on the transition request — allowed only for users with a sales-manager/admin role; every override writes a row to the status log with reason "credit override"

---

# Acceptance Criteria

- [ ] `[SE]` check-credit returns correct outstanding calculation (open orders + unpaid invoices)
- [ ] `[SE]` Confirmation blocked when credit_hold = true
- [ ] `[SE]` Confirmation blocked when outstanding + order > credit_limit
- [ ] `[SE]` Customer with null credit_limit passes (no limit enforced)
- [ ] `[SE]` Override works for manager role and is audit-logged
- [ ] `[QA]` Override rejected for non-manager role (403)
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Depends on md_business_partner.credit_limit/credit_hold (TASK-071) and OrderWorkflowService (TASK-073)
- Role check via existing platform/identity permission evaluation
- Reference PRD-007 FR-004

---

# Files Expected

- `backend/src/main/java/com/erp/modules/salesflow/service/CreditCheckService.java` (new)
- `backend/src/main/java/com/erp/modules/salesflow/controller/CreditCheckController.java` (new)
- `backend/src/main/java/com/erp/modules/salesflow/dto/CreditCheckResponse.java` (new)
- Modified: OrderWorkflowService (credit hook on confirm)

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

- PRD-007 — Sales Order Workflow & Customer Management (FR-004)
