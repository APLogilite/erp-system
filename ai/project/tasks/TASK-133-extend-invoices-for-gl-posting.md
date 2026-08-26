---
id: TASK-133
title: Extend customer and supplier invoices for GL posting
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
parent_prd: PRD-020
prd_version: 1.0.0
prd_branch: prd/PRD-020-accounts-payable-receivable
base_branch: main
merge_target: prd/PRD-020-accounts-payable-receivable
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-134
  - TASK-135
  - TASK-136
depends_on:
  - TASK-131
blocks:
  - TASK-134
  - TASK-135
labels:
  - accounting
  - ar
  - ap
  - database
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

Add fields to customer and supplier invoices to track GL posting status and open balances.

---

# Description

- Extend `ar_invoice` with `posted`, `journal_entry_id`, `open_amount`, `paid_amount`
- Extend `ap_invoice` with `posted`, `journal_entry_id`, `open_amount`, `paid_amount`
- Create `acc_invoice_allocation` table
- Register new fields in metadata

---

# Acceptance Criteria

- [ ] `[SE]` Invoice tables extended
- [ ] `[SE]` Allocation table created
- [ ] `[SE]` Metadata updated
- [ ] `[QA]` Fields visible on invoice windows

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Journal entry FK links to GL

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

- PRD-020 — Accounts Payable & Receivable
