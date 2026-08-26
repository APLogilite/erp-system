---
id: TASK-125
title: Create account type and account tables
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
parent_prd: PRD-018
prd_version: 1.0.0
prd_branch: prd/PRD-018-coa-tax-fiscal-calendar
base_branch: main
merge_target: prd/PRD-018-coa-tax-fiscal-calendar
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-126
  - TASK-127
  - TASK-128
depends_on: []
blocks:
  - TASK-126
  - TASK-127
  - TASK-128
labels:
  - accounting
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

Create the database tables for account types and the chart of accounts.

---

# Description

- Create `acc_account_type` table
- Create `acc_account` table with self-referencing parent for hierarchy
- Register tables in metadata
- Add indexes

---

# Acceptance Criteria

- [ ] `[SE]` Tables created with `BaseEntity` columns
- [ ] `[SE]` Metadata registered
- [ ] `[QA]` Tables verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Account types: Asset, Liability, Equity, Revenue, Expense, COGS
- Support summary accounts

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

- PRD-018 — Chart of Accounts, Tax & Fiscal Calendar
