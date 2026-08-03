---
id: TASK-088
title: Create Credit Memo table with metadata
type: Feature
scope: database
status: PLANNED
priority: High
owner: developer
assigned_to:
assigned_branch:
locked: false
created: 2026-07-29
updated: 2026-07-29
started:
completed:
estimated_hours: 2
actual_hours:
parent_prd: PRD-009
prd_version: 1.0.0
prd_branch: prd/PRD-009-sales-return-analytics
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-087
depends_on: []
blocks:
  - TASK-089
  - TASK-091
labels:
  - database
  - prd-009
review_required: true
test_required: true
automation_required: false
change_summary:
test_report:
test_script:
history:
  - created
---

# Goal

Provide credit memo storage so approved returns produce a financial document that reduces the customer's balance or funds a refund.

---

# Description

Create `tx_credit_memo` via Flyway migration with metadata registration:
- `id` UUID PK
- `credit_memo_number` string(50) required (auto-generated CM-0001)
- `credit_memo_date` date required
- `partner_id` many2one → md_business_partner required
- `return_id` many2one → tx_return nullable
- `invoice_id` many2one → tx_invoice nullable (original invoice being credited)
- `currency` string(3)
- `subtotal` decimal(15,2), `tax_amount` decimal(15,2), `credit_amount` decimal(15,2) required
- `status` string(20) required default 'draft' (enum: draft / validated / applied / cancelled)
- `notes` text
- plus system columns

Register in sys_table/sys_column.

---

# Acceptance Criteria

- [ ] `[SE]` tx_credit_memo created with metadata registration
- [ ] `[SE]` FK constraints to tx_return and tx_invoice exist
- [ ] `[SE]` Migration idempotent
- [ ] `[QA]` Credit memo CRUD works via runtime API
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reference PRD-009 FR-002

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__create_credit_memo_table.sql`

---

# Developer Notes

*(maintained by SE)*

---

# Tester Notes

*(maintained by QA)*

---

# Review Notes

*(maintained by reviewer)*

---

# Task History

2026-07-29

Product Manager

Created Task (PLANNED) — PRD-009 approved by user

---

# Related Documents

- PRD-009 — Sales Return & Analytics (FR-002)
