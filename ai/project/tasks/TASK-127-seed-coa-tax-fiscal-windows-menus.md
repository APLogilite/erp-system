---
id: TASK-127
title: Seed COA, tax, and fiscal calendar windows, tabs, fields, and menus
type: Feature
scope: both
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
estimated_hours: 6
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
  - TASK-126
  - TASK-128
depends_on:
  - TASK-125
  - TASK-126
blocks:
  - TASK-128
labels:
  - accounting
  - metadata
  - ui
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

Make chart of accounts, tax, and fiscal calendar forms available in the runtime UI.

---

# Description

- Create Account Type window
- Create Account window (tree/list view)
- Create Tax Category and Tax Rate windows
- Create Fiscal Year and Fiscal Period windows
- Add Accounting menu group and entries
- Add action to generate periods from fiscal year

---

# Acceptance Criteria

- [ ] `[SE]` All windows render
- [ ] `[SE]` Menu entries visible
- [ ] `[SE]` Generate periods action available
- [ ] `[QA]` User can create accounts
- [ ] `[QA]` User can create fiscal year and generate periods

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Account hierarchy may be list in v1.0

---

# Files Expected

- Flyway seed migration

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
