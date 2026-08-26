---
id: TASK-130
title: Seed journal entry window, tabs, fields, and menu
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
parent_prd: PRD-019
prd_version: 1.0.0
prd_branch: prd/PRD-019-general-ledger-journal-posting
base_branch: main
merge_target: prd/PRD-019-general-ledger-journal-posting
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-129
  - TASK-131
  - TASK-132
depends_on:
  - TASK-129
blocks:
  - TASK-131
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

Make journal entry forms available in the runtime UI.

---

# Description

- Create Journal Entry window with debit/credit lines tab
- Add Post and Reverse action buttons
- Add menu entry under Accounting
- Configure access

---

# Acceptance Criteria

- [ ] `[SE]` Journal Entry window renders
- [ ] `[SE]` Lines tab supports debit/credit amounts
- [ ] `[SE]` Menu entry visible
- [ ] `[QA]` User can create a journal entry

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Total debits/credits visible in footer

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

- PRD-019 — General Ledger & Journal Posting Engine
