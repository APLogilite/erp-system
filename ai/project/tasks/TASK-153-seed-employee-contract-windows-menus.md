---
id: TASK-153
title: Seed employee and contract windows, tabs, fields, and menus
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
parent_prd: PRD-024
prd_version: 1.0.0
prd_branch: prd/PRD-024-employees-contracts-organization
base_branch: main
merge_target: prd/PRD-024-employees-contracts-organization
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-151
  - TASK-152
  - TASK-154
depends_on:
  - TASK-151
  - TASK-152
blocks:
  - TASK-154
labels:
  - hr
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

Make employee and contract forms available in the runtime UI.

---

# Description

- Create Department window
- Create Job Position window
- Create Employee window with contracts tab
- Add Human Resources menu group and entries
- Configure access with PII restrictions

---

# Acceptance Criteria

- [ ] `[SE]` Employee window renders with contracts tab
- [ ] `[SE]` Department and job position windows render
- [ ] `[SE]` Menu entries visible
- [ ] `[QA]` User can create an employee
- [ ] `[QA]` User can add a contract

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- PII fields visible only to HR roles

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

- PRD-024 — Employees, Contracts & Organization
