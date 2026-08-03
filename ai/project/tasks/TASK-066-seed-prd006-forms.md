---
id: TASK-066
title: Seed Quotation, Price List, Discount, and Sequence forms
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
estimated_hours: 5
actual_hours:
parent_prd: PRD-006
prd_version: 1.0.0
prd_branch: prd/PRD-006-sales-quotation-price-management
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-060
  - TASK-061
  - TASK-062
  - TASK-063
depends_on:
  - TASK-060
  - TASK-061
  - TASK-062
  - TASK-063
blocks:
  - TASK-069
labels:
  - database
  - frontend
  - prd-006
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

Make all PRD-006 entities usable through the metadata-driven runtime UI by seeding 6 form definitions: sales_quotation (with quotation lines sub-form), quotation_line, price_list, price_list_line, discount_rule, and document_sequence.

---

# Description

Create Flyway migration(s) that insert form metadata into `sys_metadata_views`, `sys_form_fields`, `sys_form_layout_sections`, `sys_form_section_fields`, and `sys_form_sub_forms`, following PRD-003's seeding patterns.

**Forms to seed (all scope = 'global'):**

| # | Form Code | Table | Layout | Notes |
|---|-----------|-------|--------|-------|
| 1 | `sales_quotation` | tx_quotation | 2-col header | Sub-form tab: quotation_line |
| 2 | `quotation_line` | tx_quotation_line | Inline grid | Parent FK hidden |
| 3 | `price_list` | md_price_list | 2-col | |
| 4 | `price_list_line` | md_price_list_line | Inline grid | Parent FK hidden |
| 5 | `discount_rule` | md_discount_rule | 2-col | |
| 6 | `document_sequence` | sys_document_sequence | 2-col | Admin-only |

**sales_quotation form sections:**
- Section 1 "Quote Information": quote_number (read-only), quote_date, valid_until, partner_id, price_list_id, currency
- Section 2 "Amounts": status, subtotal (read-only), discount_amount (read-only), tax_amount (read-only), grand_total (read-only)
- Section 3 "Notes": notes

**Sub-form config:** sales_quotation → quotation_line via `quotation_id`, tab label "Quotation Lines", display_as = tab, position 1.

Also register price_list → price_list_line sub-form via `price_list_id`, tab label "Price Lines", position 1.

Add menu entries under a "Sales" menu group for Sales Quotations, Price Lists, Discount Rules; and under "Admin" for Document Sequences (following PRD-004 window/menu patterns).

---

# Acceptance Criteria

- [ ] `[SE]` All 6 forms present in `GET /api/v1/runtime/forms` (or equivalent metadata endpoint)
- [ ] `[SE]` sales_quotation form renders with 3 sections and Quotation Lines tab
- [ ] `[SE]` price_list form renders with Price Lines tab
- [ ] `[SE]` Read-only fields configured (quote_number, subtotal, discount_amount, tax_amount, grand_total)
- [ ] `[SE]` Menu entries added for Sales and Admin groups
- [ ] `[QA]` Full CRUD works on each form through the runtime renderer
- [ ] `[QA]` Sub-form drill-down works (add/edit lines within quotation)
- [ ] `[SE][QA]` Backend starts cleanly with migration applied

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Follow V5/PRD-003 seeding conventions (idempotent delete-and-reinsert)
- Forms are rendered by PRD-001 engine — zero new UI code
- Reference PRD-006 FR-005

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__seed_pricing_forms.sql`
- `backend/src/main/resources/db/migration/V{next+1}__seed_quotation_forms.sql`

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

Created Task (PLANNED) — PRD-006 approved by user

---

# Related Documents

- PRD-006 — Sales Quotation & Price Management (FR-005)
