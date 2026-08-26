---
id: TASK-112
title: Create physical inventory and adjustment tables
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
parent_prd: PRD-015
prd_version: 1.0.0
prd_branch: prd/PRD-015-inventory-adjustments-valuation
base_branch: main
merge_target: prd/PRD-015-inventory-adjustments-valuation
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-113
  - TASK-114
  - TASK-115
depends_on:
  - TASK-108
blocks:
  - TASK-113
  - TASK-114
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

Create tables for physical inventory counts and stock adjustments.

---

# Description

- Create `m_physical_inventory` and `m_physical_inventory_line` tables
- Create `m_adjustment_reason` table
- Add `cost_type` and `average_cost` to `m_product`
- Register all tables in metadata

---

# Acceptance Criteria

- [ ] `[SE]` Tables created
- [ ] `[SE]` `m_product` extended with cost fields
- [ ] `[SE]` Metadata registered
- [ ] `[QA]` Tables verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Cost type: AVERAGE for v1.0

---

# Files Expected

- Flyway migration

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

- PRD-015 — Inventory Adjustments, Counting & Valuation
