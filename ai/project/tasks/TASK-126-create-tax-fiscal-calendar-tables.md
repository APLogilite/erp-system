---
id: TASK-126
title: Create tax and fiscal calendar tables
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
  - TASK-125
  - TASK-127
  - TASK-128
depends_on:
  - TASK-125
blocks:
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

Create the database tables for tax configuration and fiscal calendar.

---

# Description

- Create `acc_tax_category` and `acc_tax_rate` tables
- Create `acc_fiscal_year` and `acc_fiscal_period` tables
- Register tables in metadata
- Add FKs from tax rate to GL accounts

---

# Acceptance Criteria

- [ ] `[SE]` Tax and fiscal tables created
- [ ] `[SE]` Metadata registered
- [ ] `[QA]` Tables verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Tax rate has separate sales and purchase GL accounts
- Fiscal period belongs to fiscal year

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
