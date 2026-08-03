---
id: TASK-076
title: Implement Customer 360 API and dashboard form
type: Feature
scope: both
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
estimated_hours: 5
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
depends_on:
  - TASK-071
blocks: []
labels:
  - backend
  - frontend
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

Give customer-facing staff a single screen showing everything about a customer — profile, credit status, recent orders, invoices, payments, and open quotations — without opening five different forms.

---

# Description

**Backend** — `Customer360Service` with endpoint:

```
GET /api/v1/customers/{partnerId}/360
Response: {
  "partner": { "id", "code", "name", "email", "phone", "creditLimit", "creditHold", "outstanding" },
  "recentOrders":     [ { "id", "orderNumber", "orderDate", "grandTotal", "status" } ],      // top 5
  "recentInvoices":   [ { "id", "invoiceNumber", "invoiceDate", "grandTotal", "dueAmount", "status" } ],
  "recentPayments":   [ { "id", "paymentNumber", "paymentDate", "amount", "status" } ],
  "openQuotations":   [ { "id", "quoteNumber", "quoteDate", "grandTotal", "validUntil", "status" } ]
}
```
(outstanding reuses the CreditCheckService calculation; each section limited to 5 most recent records)

**Frontend** — seed a metadata-driven `customer_360` form rendered as a read-only dashboard (sections per entity group), plus a "Customer 360" action/link from the business_partner form that navigates to the dashboard for the current record. If the metadata engine cannot express the dashboard layout, implement a single custom React page under `frontend/src/routes/` that calls the API and renders MUI cards/tables — keep it read-only.

---

# Acceptance Criteria

- [ ] `[SE]` API returns all five sections with correct data and 5-record limits
- [ ] `[SE]` outstanding matches credit check calculation
- [ ] `[SE]` Dashboard accessible from business partner form
- [ ] `[QA]` New customer with no history shows empty sections (no errors)
- [ ] `[QA]` Unknown partner id returns 404
- [ ] `[SE][QA]` Backend starts cleanly; frontend builds (typecheck + lint pass)

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- openOpportunities section from PRD-008 will be appended later — design response to be extensible
- Reference PRD-007 FR-005

---

# Files Expected

- `backend/src/main/java/com/erp/modules/customer/service/Customer360Service.java` (new)
- `backend/src/main/java/com/erp/modules/customer/controller/Customer360Controller.java` (new)
- `backend/src/main/java/com/erp/modules/customer/dto/Customer360Response.java` (new)
- `backend/src/main/resources/db/migration/V{next}__seed_customer_360_form.sql` (or custom React page if needed)

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

- PRD-007 — Sales Order Workflow & Customer Management (FR-005)
