---
id: TASK-097
title: Create purchase requisition and purchase order tables
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
estimated_hours: 5
actual_hours:
parent_prd: PRD-011
prd_version: 1.0.0
prd_branch: prd/PRD-011-purchase-requisition-order
base_branch: main
merge_target: prd/PRD-011-purchase-requisition-order
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-098
  - TASK-099
  - TASK-100
depends_on:
  - TASK-096
blocks:
  - TASK-098
  - TASK-099
labels:
  - purchase
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

Create the database tables for purchase requisitions and purchase orders (headers and lines).

---

# Description

- Create `pur_requisition` and `pur_requisition_line` tables
- Create `pur_order` and `pur_order_line` tables
- Register all tables in `sys_table` and `sys_column`
- Add document sequences `PRQ` and `PO`
- Add foreign keys to `c_business_partner`, `m_product`, `c_uom`, `pur_requisition`

---

# Acceptance Criteria

- [ ] `[SE]` Tables created with `BaseEntity` columns
- [ ] `[SE]` Tables registered in metadata
- [ ] `[SE]` Document sequences seeded
- [ ] `[QA]` Tables and sequences verified in database

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- PO line tracks `received_qty` and `invoiced_qty`
- Requisition line tracks requested qty and notes

---

# Files Expected

- Flyway migration
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

- PRD-011 — Purchase Requisition & Purchase Order
