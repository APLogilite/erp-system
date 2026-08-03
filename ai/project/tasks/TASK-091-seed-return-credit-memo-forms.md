---
id: TASK-091
title: Seed Return and Credit Memo forms
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
estimated_hours: 3
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
  - TASK-088
depends_on:
  - TASK-087
  - TASK-088
blocks:
  - TASK-092
labels:
  - database
  - frontend
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

Make returns and credit memos usable through the runtime UI by seeding their form definitions, with a Return Lines sub-form tab on the return form and menu entries under "Sales".

---

# Description

Flyway migration seeding metadata forms (scope = 'global'):

| # | Form Code | Table | Layout | Notes |
|---|-----------|-------|--------|-------|
| 1 | `sales_return` | tx_return | 2-col header | Sub-form tab: return_line |
| 2 | `return_line` | tx_return_line | Inline grid | Parent FK hidden |
| 3 | `credit_memo` | tx_credit_memo | 2-col | |

**sales_return form sections:**
- Section 1 "Return Information": return_number (read-only), return_date, partner_id, order_id, invoice_id, reason
- Section 2 "Amounts": status, subtotal (read-only), tax_amount (read-only), credit_amount (read-only)
- Section 3 "Notes": notes

**Sub-form config:** sales_return → return_line via `return_id`, tab label "Return Lines", display_as = tab, position 1.

Menu entries under "Sales": Sales Returns, Credit Memos.

---

# Acceptance Criteria

- [ ] `[SE]` All 3 forms present in runtime forms list
- [ ] `[SE]` sales_return renders with Return Lines tab; read-only fields configured
- [ ] `[SE]` Menu entries visible
- [ ] `[QA]` Full CRUD on each form; sub-form drill-down works
- [ ] `[SE][QA]` Backend starts cleanly with migration applied

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reference PRD-009 FR-005

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__seed_return_forms.sql`

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

- PRD-009 — Sales Return & Analytics (FR-005)
