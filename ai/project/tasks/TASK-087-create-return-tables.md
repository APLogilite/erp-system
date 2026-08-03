---
id: TASK-087
title: Create Sales Return tables with metadata
type: Feature
scope: database
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
parent_prd: PRD-009
prd_version: 1.0.0
prd_branch: prd/PRD-009-sales-return-analytics
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-088
depends_on: []
blocks:
  - TASK-089
  - TASK-091
labels:
  - database
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

Provide storage for Return Merchandise Authorizations (RMA) — header and lines — so customers can return products through a tracked process.

---

# Description

Create two tables via Flyway migration with metadata registration:

**tx_return** — header:
`id` UUID PK, `return_number` string(50) required (auto-generated RMA-0001), `return_date` date required, `partner_id` many2one → md_business_partner required, `order_id` many2one → tx_order nullable, `invoice_id` many2one → tx_invoice nullable, `reason` string(30) required (enum: defective / wrong_item / not_needed / damaged / other), `status` string(20) required default 'requested' (enum: requested / approved / received / credited / rejected), `subtotal` decimal(15,2), `tax_amount` decimal(15,2), `credit_amount` decimal(15,2), `notes` text, plus system columns.

**tx_return_line** — lines:
`id` UUID PK, `return_id` many2one → tx_return required (FK ON DELETE CASCADE), `line_number` integer required, `product_id` many2one → md_product required, `quantity` decimal(15,3) required, `uom_id` many2one → md_uom, `unit_price` decimal(15,2) required, `line_total` decimal(15,2), `reason` text, plus system columns.

Register both in sys_table/sys_column. Index on tx_return_line.return_id.

---

# Acceptance Criteria

- [ ] `[SE]` Both tables created with correct columns, FKs, and index
- [ ] `[SE]` Metadata registered in sys_table/sys_column
- [ ] `[SE]` Migration idempotent
- [ ] `[QA]` Return CRUD works via runtime API
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reference PRD-009 FR-001

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__create_return_tables.sql`

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

- PRD-009 — Sales Return & Analytics (FR-001)
