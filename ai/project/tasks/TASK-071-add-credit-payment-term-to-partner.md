---
id: TASK-071
title: Add credit limit and payment term to Business Partner
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
parent_prd: PRD-007
prd_version: 1.0.0
prd_branch: prd/PRD-007-sales-order-workflow-customer-management
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-070
depends_on:
  - TASK-070
blocks:
  - TASK-075
  - TASK-076
labels:
  - database
  - prd-007
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

Extend the business partner master with credit management fields (credit_limit, credit_hold) and a default payment term so orders and invoices can enforce credit policy and auto-calculate due dates.

---

# Description

Flyway migration on `md_business_partner`:
- ADD COLUMN `credit_limit` decimal(15,2) NULL
- ADD COLUMN `credit_hold` boolean NOT NULL DEFAULT false
- ADD COLUMN `payment_term_id` UUID NULL REFERENCES md_payment_term(id)

Register the 3 new columns in `sys_column` for the md_business_partner table entry, and update the seeded `business_partner` form (from PRD-003) to include them in a new "Credit & Terms" section:
- credit_limit (decimal input)
- credit_hold (checkbox)
- payment_term_id (dropdown from md_payment_term)

Migration must be idempotent (ADD COLUMN IF NOT EXISTS / ensure_column guards).

---

# Acceptance Criteria

- [ ] `[SE]` Three columns added to md_business_partner
- [ ] `[SE]` Columns registered in sys_column metadata
- [ ] `[SE]` business_partner form updated with Credit & Terms section
- [ ] `[SE]` Migration idempotent
- [ ] `[QA]` Editing a partner via runtime UI saves credit_limit, credit_hold, payment_term_id
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reference PRD-007 FR-003, FR-004

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__add_credit_to_partner.sql`

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

Created Task (PLANNED) — PRD-007 approved by user

---

# Related Documents

- PRD-007 — Sales Order Workflow & Customer Management (FR-003, FR-004)
