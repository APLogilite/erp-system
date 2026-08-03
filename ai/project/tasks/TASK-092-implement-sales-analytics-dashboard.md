---
id: TASK-092
title: Implement Sales Analytics dashboard
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
parent_prd: PRD-009
prd_version: 1.0.0
prd_branch: prd/PRD-009-sales-return-analytics
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-091
depends_on:
  - TASK-091
blocks: []
labels:
  - backend
  - frontend
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

Give sales managers visibility into performance — revenue trends, top customers, top products, and order status distribution — through a dedicated analytics dashboard.

---

# Description

**Backend** — `SalesAnalyticsService` with endpoint:

```
GET /api/v1/analytics/sales?startDate=2026-01-01&endDate=2026-12-31&groupBy=month
Response: {
  "revenue": { "total": 500000, "trend": [ { "date": "2026-01", "amount": 40000 } ] },
  "topCustomers": [ { "partnerId", "name", "totalRevenue", "orderCount" } ],   // top 10
  "topProducts":  [ { "productId", "name", "totalRevenue", "quantitySold" } ], // top 10
  "orderStatus":  { "draft": 5, "confirmed": 12, "delivered": 8, "invoiced": 10, "paid": 15 }
}
```

- Revenue from tx_order where order_type='sales' and status NOT IN ('draft','cancelled'), grouped by day/week/month per groupBy param
- topCustomers by Σ grand_total; topProducts by Σ (quantity × unit_price) from tx_order_line joined to sales orders
- Tenant-scoped

**Frontend** — custom React page under `frontend/src/routes/` (MUI cards/tables + date range filter + group-by selector); add "Sales Analytics" menu entry under "Sales". Read-only.

---

# Acceptance Criteria

- [ ] `[SE]` API returns revenue trend grouped correctly for day/week/month
- [ ] `[SE]` topCustomers and topProducts limited to 10, ordered by revenue desc
- [ ] `[SE]` orderStatus counts accurate
- [ ] `[SE]` Date filter applies; defaults to current year when omitted
- [ ] `[QA]` Dashboard page renders all sections; filters update data
- [ ] `[QA]` Tenant isolation verified (other tenant's data not visible)
- [ ] `[SE][QA]` Frontend typecheck + lint pass; backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reference PRD-009 FR-004

---

# Files Expected

- `backend/src/main/java/com/erp/modules/analytics/service/SalesAnalyticsService.java` (new)
- `backend/src/main/java/com/erp/modules/analytics/controller/SalesAnalyticsController.java` (new)
- `backend/src/main/java/com/erp/modules/analytics/dto/SalesAnalyticsResponse.java` (new)
- `frontend/src/routes/SalesAnalytics.tsx` (new)

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

- PRD-009 — Sales Return & Analytics (FR-004)
