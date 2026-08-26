---
id: TASK-134
title: Implement customer invoice posting service
type: Feature
scope: backend
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
parent_prd: PRD-020
prd_version: 1.0.0
prd_branch: prd/PRD-020-accounts-payable-receivable
base_branch: main
merge_target: prd/PRD-020-accounts-payable-receivable
merge_strategy: merge
parent_task:
related_tasks:
  - TASK-133
  - TASK-135
  - TASK-136
depends_on:
  - TASK-133
  - TASK-131
blocks:
  - TASK-136
labels:
  - accounting
  - ar
  - backend
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

Generate and post GL journal entries for customer invoices.

---

# Description

- Create `CustomerInvoicePostingService`
- On invoice validation/post:
  - Debit AR account
  - Credit Revenue account
  - Credit Tax payable account
  - Create journal entry and post
  - Mark invoice as posted
- Endpoint: `POST /api/v1/accounting/ar/invoices/{id}/post`

---

# Acceptance Criteria

- [ ] `[SE]` GL entry created for customer invoice
- [ ] `[SE]` AR and revenue accounts updated
- [ ] `[SE]` Invoice marked posted
- [ ] `[QA]` Trial balance reflects invoice

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- GL account mapping configurable per document type

---

# Files Expected

- `CustomerInvoicePostingService.java`
- Controller endpoint
- Integration tests

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
