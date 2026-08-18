---
id: TASK-070
title: Create Payment Term table with seed data and metadata
type: Feature
scope: database
status: TESTING
priority: High
owner: developer
assigned_to: Software Engineer
assigned_branch: feature/TASK-070
locked: true
created: 2026-07-29
updated: 2026-08-14
started: 2026-08-14
completed: 2026-08-14
estimated_hours: 2
actual_hours:
parent_prd: PRD-007
prd_version: 1.0.0
prd_branch: prd/PRD-007-sales-order-workflow-customer-management
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-071
  - TASK-072
depends_on: []
blocks:
  - TASK-071
  - TASK-072
labels:
  - database
  - prd-007
review_required: true
test_required: true
automation_required: false
change_summary:
test_report: TEST-TASK-070.md
test_script:
history:
  - created
---

# Goal

Provide a payment terms master (Net 30, Net 60, etc.) so invoice due dates can be calculated automatically from a configurable term instead of manual entry.

---

# Description

Create `md_payment_term` table via Flyway migration with metadata registration:

- `id` UUID PK, `code` string(20) required, `name` string(100) required, `days` integer required, `discount_percent` decimal(5,2) nullable, `discount_days` integer nullable, plus system columns.

Seed standard terms:
| Code | Name | Days |
|------|------|------|
| NET0 | Due on Receipt | 0 |
| NET15 | Net 15 Days | 15 |
| NET30 | Net 30 Days | 30 |
| NET60 | Net 60 Days | 60 |
| NET90 | Net 90 Days | 90 |

Register table in `sys_table` / `sys_column` and seed a metadata form `payment_term` (2-col layout) so admins can manage terms via the runtime UI. Add menu entry under "Master Data".

---

# Acceptance Criteria

- [x] `[SE]` Migration creates md_payment_term and seeds 5 standard terms
- [x] `[SE]` Table/columns registered in sys_table/sys_column
- [x] `[SE]` payment_term form seeded and visible in runtime forms list
- [ ] `[QA]` CRUD on payment terms works via runtime UI
- [x] `[SE][QA]` Backend starts cleanly with migration applied

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reference PRD-007 FR-003

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__create_payment_term_table.sql`
- `backend/src/main/resources/db/migration/V{next+1}__seed_payment_term_form.sql`

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

Created Task (PLANNED) — PRD-007 approved by user

---

# Related Documents

- PRD-007 — Sales Order Workflow & Customer Management (FR-003)
