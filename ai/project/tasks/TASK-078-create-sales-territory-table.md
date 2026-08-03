---
id: TASK-078
title: Create Sales Territory table with metadata
type: Feature
scope: database
status: PLANNED
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
parent_prd: PRD-008
prd_version: 1.0.0
prd_branch: prd/PRD-008-crm-pipeline-sales-team
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks: []
depends_on: []
blocks:
  - TASK-079
  - TASK-080
  - TASK-085
labels:
  - database
  - prd-008
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

Provide a sales territory master so leads, opportunities, and customers can be assigned to geographic/market territories with a responsible manager.

---

# Description

Create `md_sales_territory` via Flyway migration with metadata registration:
- `id` UUID PK, `code` string(20) required, `name` string(100) required, `manager_id` UUID nullable (references sys_user), plus system columns.

Also add `territory_id` UUID NULL REFERENCES md_sales_territory(id) to `md_business_partner` and register in sys_column.

Seed 2 sample territories (e.g., WEST / EAST). Register table metadata in sys_table/sys_column.

---

# Acceptance Criteria

- [ ] `[SE]` md_sales_territory created with metadata registration
- [ ] `[SE]` territory_id added to md_business_partner with metadata
- [ ] `[SE]` 2 sample territories seeded
- [ ] `[QA]` Territory CRUD works via runtime API
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reference PRD-008 FR-003

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__create_sales_territory_table.sql`

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

Created Task (PLANNED) — PRD-008 approved by user

---

# Related Documents

- PRD-008 — CRM Pipeline & Sales Team Management (FR-003)
