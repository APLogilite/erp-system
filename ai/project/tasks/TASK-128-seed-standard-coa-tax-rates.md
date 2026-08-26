---
id: TASK-128
title: Seed standard chart of accounts and sample tax rates
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
  - TASK-126
  - TASK-127
depends_on:
  - TASK-125
  - TASK-126
  - TASK-127
blocks:
  - TASK-131
labels:
  - accounting
  - seed
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

Provide a starter chart of accounts and sample tax rates so accounting can begin immediately.

---

# Description

- Seed standard accounts:
  - Cash, Bank, Accounts Receivable, Inventory, Fixed Assets
  - Accounts Payable, Tax Payable, Accrued Expenses
  - Equity, Revenue, COGS, Operating Expenses
- Seed sample tax rates (e.g., Sales Tax 10%, Purchase Tax 10%)
- Link tax rates to appropriate GL accounts

---

# Acceptance Criteria

- [ ] `[SE]` Standard COA exists after migration
- [ ] `[SE]` Sample tax rates exist
- [ ] `[QA]` Accounts and tax rates visible in UI

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- COA is tenant-specific seed data
- User can customize after seed

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
