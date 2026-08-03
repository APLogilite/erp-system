---
id: TASK-062
title: Create Discount Rule table with metadata registration
type: Feature
scope: database
status: READY_FOR_DEV
priority: Medium
owner: developer
assigned_to:
assigned_branch:
locked: false
created: 2026-07-29
updated: 2026-07-29
started:
completed:
estimated_hours: 2
actual_hours:
parent_prd: PRD-006
prd_version: 1.0.0
prd_branch: prd/PRD-006-sales-quotation-price-management
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-065
depends_on: []
blocks:
  - TASK-065
  - TASK-066
labels:
  - database
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

Provide configurable discount rules (percentage or fixed amount) scoped by customer, product, quantity, and validity dates so the system can automatically apply the best discount on quotation and order lines.

---

# Description

Create `md_discount_rule` table via Flyway migration with metadata registration:

- `id` UUID PK
- `code` string(50) required — unique identifier
- `name` string(200) required
- `discount_type` string(20) required — enum values: `percentage`, `fixed_amount`
- `discount_value` decimal(15,2) required
- `partner_id` many2one → md_business_partner (nullable — null means all customers)
- `product_id` many2one → md_product (nullable — null means all products)
- `min_quantity` decimal(15,3) (nullable — minimum quantity to qualify)
- `valid_from` date, `valid_to` date
- plus system columns

Register table in `sys_table` and columns in `sys_column` (ensure_column pattern). Seed 1-2 sample rules (e.g., "10% off orders of 100+ units" percentage rule).

---

# Acceptance Criteria

- [ ] `[SE]` Migration creates md_discount_rule with correct columns
- [ ] `[SE]` Table and columns registered in sys_table/sys_column
- [ ] `[SE]` At least one sample discount rule seeded
- [ ] `[SE]` Migration is idempotent
- [ ] `[QA]` Table queryable via runtime CRUD API
- [ ] `[SE][QA]` Backend starts cleanly with migration applied

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Enum stored as string with check constraint or validated at service layer
- Reference PRD-006 FR-003

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__create_discount_table.sql`

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

- PRD-006 — Sales Quotation & Price Management (FR-003)
