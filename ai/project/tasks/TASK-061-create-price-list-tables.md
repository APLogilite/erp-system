---
id: TASK-061
title: Create Price List tables with metadata registration
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
  - TASK-064
depends_on: []
blocks:
  - TASK-064
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

Provide price list storage so products can have customer-specific and volume-based pricing, replacing the single static `unit_price` on `md_product`.

---

# Description

Create two tables via Flyway migration following PRD-003's metadata pattern (physical table + sys_table/sys_column registration):

**md_price_list** — header:
`id` UUID PK, `code` string(50) required, `name` string(200) required, `currency` string(3) required, `valid_from` date, `valid_to` date, `is_default` boolean, `partner_id` many2one → md_business_partner (nullable — customer-specific list), plus system columns.

**md_price_list_line** — lines:
`id` UUID PK, `price_list_id` many2one → md_price_list required, `product_id` many2one → md_product required, `min_quantity` decimal(15,3), `unit_price` decimal(15,2) required, plus system columns.

Register both tables in `sys_table` and all business columns in `sys_column` (using the `ensure_column` pattern from V5 migration). Seed one default price list (code: `STANDARD`, name: `Standard Price List`, currency: USD, is_default: true) with a few sample lines referencing existing demo products if any exist.

---

# Acceptance Criteria

- [ ] `[SE]` Migration creates both tables with correct columns and types
- [ ] `[SE]` Both tables registered in sys_table; all columns registered in sys_column
- [ ] `[SE]` Default STANDARD price list seeded (is_default = true)
- [ ] `[SE]` Migration is idempotent (safe to re-run — DROP IF EXISTS / WHERE NOT EXISTS guards)
- [ ] `[QA]` Tables queryable via PRD-001 runtime CRUD API (`/api/v1/runtime/...`)
- [ ] `[SE][QA]` Backend starts cleanly with migration applied

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Follow V5 migration conventions (ensure_column function pattern)
- System columns auto-added: id, tenant_id, created_at, updated_at, created_by, updated_by, is_active, deleted_at
- Reference PRD-006 FR-002

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__create_price_list_tables.sql`

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

- PRD-006 — Sales Quotation & Price Management (FR-002)
