---
id: TASK-081
title: Create Commission table with metadata
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
  - TASK-084
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

Provide commission tracking storage so salesperson earnings on closed opportunities are recorded, approvable, and payable.

---

# Description

Create `tx_commission` via Flyway migration with metadata registration:
- `id` UUID PK
- `salesperson_id` UUID required (references sys_user)
- `opportunity_id` UUID required REFERENCES tx_opportunity(id)
- `order_id` UUID nullable REFERENCES tx_order(id)
- `commission_percent` decimal(5,2) required
- `commission_amount` decimal(15,2) required
- `status` string(20) required default 'pending' — enum: pending / approved / paid
- `payment_date` date nullable
- plus system columns

Register in sys_table/sys_column.

---

# Acceptance Criteria

- [ ] `[SE]` tx_commission created with metadata registration
- [ ] `[SE]` FK constraints exist (opportunity, order)
- [ ] `[SE]` Migration idempotent
- [ ] `[QA]` Commission CRUD works via runtime API
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reference PRD-008 FR-004

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__create_commission_table.sql`

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

- PRD-008 — CRM Pipeline & Sales Team Management (FR-004)
