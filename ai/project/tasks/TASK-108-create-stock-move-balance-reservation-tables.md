---
id: TASK-108
title: Create stock move, balance, and reservation tables
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
parent_prd: PRD-014
prd_version: 1.0.0
prd_branch: prd/PRD-014-stock-movements-logistics
base_branch: main
merge_target: prd/PRD-014-stock-movements-logistics
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-109
  - TASK-110
  - TASK-111
depends_on:
  - TASK-105
blocks:
  - TASK-109
  - TASK-110
  - TASK-111
labels:
  - inventory
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

Create the central tables for tracking stock movements, inventory balances, and reservations.

---

# Description

- Create `m_stock_move` table with movement types
- Create `m_inventory_balance` table
- Create `m_inventory_reservation` table
- Register all tables in metadata
- Add indexes on product, locator, status

---

# Acceptance Criteria

- [ ] `[SE]` Tables created with `BaseEntity` columns
- [ ] `[SE]` Tables registered in metadata
- [ ] `[SE]` Indexes in place
- [ ] `[QA]` Tables verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Movement types: RECEIPT, DELIVERY, TRANSFER, ADJUSTMENT, PRODUCTION, RETURN_CUSTOMER, RETURN_VENDOR
- Balance: on_hand_qty, reserved_qty, available_qty

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

- PRD-014 — Stock Movements & Logistics
