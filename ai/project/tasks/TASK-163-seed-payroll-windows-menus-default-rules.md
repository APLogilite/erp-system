---
id: TASK-163
title: Seed payroll windows, menus, and default salary rules
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
estimated_hours: 5
actual_hours:
parent_prd: PRD-026
prd_version: 1.0.0
prd_branch: prd/PRD-026-payroll-rules-payslips-compliance
base_branch: main
merge_target: prd/PRD-026-payroll-rules-payslips-compliance
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-160
  - TASK-161
  - TASK-162
  - TASK-164
  - TASK-165
depends_on:
  - TASK-160
  - TASK-161
  - TASK-162
blocks:
  - TASK-164
  - TASK-165
labels:
  - hr
  - payroll
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

Make payroll configuration and run forms available in the runtime UI with default rules.

---

# Description

- Create Salary Rule Category, Salary Rule, Salary Structure windows
- Create Payroll Run and Payslip windows
- Add Human Resources menu entries
- Seed default salary rules (Basic Salary, Tax, Insurance, Net Pay)
- Configure access

---

# Acceptance Criteria

- [ ] `[SE]` Payroll windows render
- [ ] `[SE]` Menu entries visible
- [ ] `[SE]` Default rules seeded
- [ ] `[QA]` User can view salary rules

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Default rules are examples; user can customize

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

- PRD-026 — Payroll Rules, Payslips & Compliance
