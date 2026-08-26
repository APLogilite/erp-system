---
id: TASK-105
title: Extend warehouse and create location type/locator tables
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
parent_prd: PRD-013
prd_version: 1.0.0
prd_branch: prd/PRD-013-warehouse-location-management
base_branch: main
merge_target: prd/PRD-013-warehouse-location-management
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-106
  - TASK-107
depends_on: []
blocks:
  - TASK-106
  - TASK-107
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

Extend warehouse master data and create the location type and locator hierarchy tables.

---

# Description

- Extend `m_warehouse` with branch, address, manager
- Create `m_locator_type` table
- Create `m_locator` table with self-referencing parent for hierarchy
- Register all tables in metadata
- Add indexes

---

# Acceptance Criteria

- [ ] `[SE]` Warehouse table extended
- [ ] `[SE]` Location type and locator tables created
- [ ] `[SE]` Metadata registered
- [ ] `[QA]` Tables verified in database

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Locator tree: warehouse → zone → aisle → rack → bin
- Self-referencing FK with cycle prevention

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

- PRD-013 — Warehouse Structure & Location Management
