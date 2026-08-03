---
id: TASK-069
title: Deprecate hardcoded sales/ and order/ modules
type: Refactor
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
estimated_hours: 3
actual_hours:
parent_prd: PRD-006
prd_version: 1.0.0
prd_branch: prd/PRD-006-sales-quotation-price-management
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-068
depends_on:
  - TASK-066
  - TASK-068
blocks: []
labels:
  - backend
  - database
  - refactor
  - prd-006
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

Remove the legacy hardcoded `sales/` and `order/` Java modules and their database tables now that all sales order functionality lives in the metadata-driven `tx_order` table and PRD-006 quotation flow — eliminating duplicate code paths.

---

# Description

1. **Search for references** — grep the entire codebase for usages of `SalesOrder`, `SalesOrderLine`, `Order`, `OrderLine` (com.erp.modules.sales.* and com.erp.modules.order.*) in other modules, frontend API calls, and tests. Document findings before deleting.
2. **Delete backend modules**:
   - `backend/src/main/java/com/erp/modules/sales/` (entity, dto, repository, service, controller)
   - `backend/src/main/java/com/erp/modules/order/` (entity, dto, repository, service, controller)
3. **Drop legacy tables** via migration: `sales_orders`, `sales_order_lines`, `orders`, `order_lines` (DROP TABLE IF EXISTS ... CASCADE)
4. **Remove frontend references** — delete any hardcoded API calls to `/api/v1/sales-orders` or `/api/v1/orders` endpoints in the frontend (search `frontend/src/`); the metadata forms replace them
5. **Verify build** — `mvn clean compile` and `mvn test` pass with no references to deleted classes
6. **Server verification** — run `bash start-all.sh`, check `/tmp/erp-backend.log` for clean startup and `/tmp/erp-frontend.log` for clean Vite startup

---

# Acceptance Criteria

- [ ] `[SE]` Reference search completed and documented in change report (list every file that referenced the modules)
- [ ] `[SE]` sales/ and order/ module directories deleted
- [ ] `[SE]` Migration drops sales_orders, sales_order_lines, orders, order_lines tables
- [ ] `[SE]` `mvn clean compile` and `mvn test` pass (BUILD SUCCESS)
- [ ] `[SE]` No frontend references to deleted endpoints remain
- [ ] `[QA]` Application starts cleanly (backend + frontend logs checked per server verification rules)
- [ ] `[QA]` Metadata-driven sales_order form (tx_order) still works end-to-end

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- MUST run after TASK-066 and TASK-068 so metadata forms and conversion flow exist as replacements
- If external references are found that cannot be removed, document them and set task to PENDING_APPROVAL rather than deleting blindly
- Reference PRD-006 FR-007

---

# Files Expected

- Deleted: `backend/src/main/java/com/erp/modules/sales/**`
- Deleted: `backend/src/main/java/com/erp/modules/order/**`
- `backend/src/main/resources/db/migration/V{next}__drop_legacy_sales_tables.sql`
- Modified: any files referencing the deleted modules

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

Created Task (PLANNED) — PRD-006 approved by user

---

# Related Documents

- PRD-006 — Sales Quotation & Price Management (FR-007)
