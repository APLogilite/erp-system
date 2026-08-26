---
id: TASK-094
title: Create vendor category and purchase price list tables
type: Feature
scope: database
status: PLANNED
priority: High
owner: planner
assigned_to:
assigned_branch:
locked: false
created: 2026-08-26
updated: 2026-08-26
started:
completed:
estimated_hours: 4
actual_hours:
parent_prd: PRD-010
prd_version: 1.0.0
prd_branch: prd/PRD-010-vendor-management-price-lists
base_branch: main
merge_target: prd/PRD-010-vendor-management-price-lists
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-093
  - TASK-095
  - TASK-096
depends_on:
  - TASK-093
blocks:
  - TASK-095
  - TASK-096
labels:
  - vendor
  - price-list
  - metadata
review_required: true
test_required: true
automation_required: true
change_summary:
test_report:
test_script:
history:
  - created
---

# Goal

Create the database tables for vendor categories and purchase price lists (header and lines).

---

# Description

- Create `pur_vendor_category` table
- Create `pur_price_list` header table
- Create `pur_price_list_line` table with FK to `pur_price_list`, `m_product`, `c_uom`
- Register all tables in `sys_table` and `sys_column`
- Add indexes for performance
- No backend services yet

---

# Acceptance Criteria

- [ ] `[SE]` Tables created with `BaseEntity` columns
- [ ] `[SE]` Tables registered in metadata
- [ ] `[SE]` Foreign keys and indexes in place
- [ ] `[QA]` Tables exist in PostgreSQL
- [ ] `[QA]` Metadata records are tenant-isolated

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Use UUID primary keys
- Soft-delete via `BaseEntity`
- Price list line supports minimum quantity breaks

---

# Files Expected

- Flyway migration for new tables
- Metadata seed entries

---

# Developer Notes

---

# Tester Notes

---

# Review Notes

---

# Task History

2026-08-26 — Product Manager — Created task

---

# Related Documents

- PRD-010 — Vendor Management & Purchase Price Lists
