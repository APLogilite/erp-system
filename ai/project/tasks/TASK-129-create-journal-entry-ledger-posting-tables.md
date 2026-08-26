---
id: TASK-129
title: Create journal entry and ledger posting tables
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
parent_prd: PRD-019
prd_version: 1.0.0
prd_branch: prd/PRD-019-general-ledger-journal-posting
base_branch: main
merge_target: prd/PRD-019-general-ledger-journal-posting
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-130
  - TASK-131
  - TASK-132
depends_on:
  - TASK-128
blocks:
  - TASK-130
  - TASK-131
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

Create the database tables for journal entries and immutable ledger postings.

---

# Description

- Create `acc_journal_entry` and `acc_journal_entry_line` tables
- Create `acc_ledger_posting` table
- Register tables in metadata
- Add document sequence `JE`
- Add FKs to accounts and fiscal periods

---

# Acceptance Criteria

- [ ] `[SE]` Tables created
- [ ] `[SE]` Metadata registered
- [ ] `[SE]` Document sequence seeded
- [ ] `[QA]` Tables verified

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Ledger postings immutable once created

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

- PRD-019 — General Ledger & Journal Posting Engine
