---
id: TASK-151
title: Create department and job position tables
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
estimated_hours: 3
actual_hours:
parent_prd: PRD-024
prd_version: 1.0.0
prd_branch: prd/PRD-024-employees-contracts-organization
base_branch: main
merge_target: prd/PRD-024-employees-contracts-organization
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-152
  - TASK-153
  - TASK-154
depends_on: []
blocks:
  - TASK-152
  - TASK-153
labels:
  - hr
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

Create the database tables for HR departments and job positions.

---

# Description

- Create `hr_department` table with self-referencing parent
- Create `hr_job_position` table
- Register tables in metadata

---

# Acceptance Criteria

- [ ] `[SE]` Tables created
- [ ] `[SE]` Metadata registered
- [ ] `[QA]` Tables verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Department has manager and parent department

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

- PRD-024 — Employees, Contracts & Organization
