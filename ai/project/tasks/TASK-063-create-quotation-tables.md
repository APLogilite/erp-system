---
id: TASK-063
title: Create Quotation tables with metadata registration
type: Feature
scope: database
status: READY_FOR_DEV
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
  - TASK-067
depends_on: []
blocks:
  - TASK-066
  - TASK-067
  - TASK-068
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

Provide storage for sales quotations (header + lines) so sales reps can create formal price quotes that later convert to sales orders.

---

# Description

Create two tables via Flyway migration with metadata registration:

**tx_quotation** — header:
`id` UUID PK, `quote_number` string(50) required, `quote_date` date required, `valid_until` date, `partner_id` many2one → md_business_partner required, `price_list_id` many2one → md_price_list (nullable), `currency` string(3), `subtotal` decimal(15,2), `discount_amount` decimal(15,2), `tax_amount` decimal(15,2), `grand_total` decimal(15,2), `status` string(20) required default 'draft' (enum: draft / sent / accepted / rejected / converted / expired), `notes` text, `converted_order_id` many2one → tx_order (nullable), plus system columns.

**tx_quotation_line** — lines:
`id` UUID PK, `quotation_id` many2one → tx_quotation required (FK with ON DELETE CASCADE), `line_number` integer required, `product_id` many2one → md_product required, `description` text, `quantity` decimal(15,3) required, `uom_id` many2one → md_uom, `unit_price` decimal(15,2) required, `discount_percent` decimal(5,2), `discount_amount` decimal(15,2), `line_total` decimal(15,2), `tax_rate` decimal(5,2), plus system columns.

Register both tables in `sys_table` / `sys_column`. Add index on `tx_quotation_line.quotation_id`.

---

# Acceptance Criteria

- [ ] `[SE]` Migration creates both tables with correct columns and FK constraints
- [ ] `[SE]` Both tables registered in sys_table/sys_column
- [ ] `[SE]` Index exists on tx_quotation_line.quotation_id
- [ ] `[SE]` Migration is idempotent
- [ ] `[QA]` Tables queryable via runtime CRUD API
- [ ] `[SE][QA]` Backend starts cleanly with migration applied

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- status field stores enum as string; workflow enforcement handled at service layer (future PRD-007 pattern)
- Reference PRD-006 FR-004

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__create_quotation_tables.sql`

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

- PRD-006 — Sales Quotation & Price Management (FR-004)
