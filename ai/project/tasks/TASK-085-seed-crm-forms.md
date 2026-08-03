---
id: TASK-085
title: Seed CRM forms (lead, opportunity, territory, commission)
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
estimated_hours: 4
actual_hours:
parent_prd: PRD-008
prd_version: 1.0.0
prd_branch: prd/PRD-008-crm-pipeline-sales-team
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-078
  - TASK-079
  - TASK-080
  - TASK-081
depends_on:
  - TASK-078
  - TASK-079
  - TASK-080
  - TASK-081
blocks:
  - TASK-086
labels:
  - database
  - frontend
  - prd-008
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

Make CRM entities usable through the metadata-driven runtime UI by seeding form definitions for lead, opportunity, sales territory, and commission — plus menu entries under a "CRM" menu group.

---

# Description

Flyway migration seeding metadata forms (all scope = 'global'):

| # | Form Code | Table | Layout |
|---|-----------|-------|--------|
| 1 | `lead` | tx_lead | 2-col |
| 2 | `opportunity` | tx_opportunity | 2-col |
| 3 | `sales_territory` | md_sales_territory | 2-col |
| 4 | `commission` | tx_commission | 2-col |

**lead form sections:**
- Section 1 "Lead Information": lead_number (read-only), company_name, contact_name, email, phone, source
- Section 2 "Qualification": status, score (read-only), expected_revenue, salesperson_id, territory_id
- Section 3 "Notes": notes

**opportunity form sections:**
- Section 1 "Opportunity Information": opportunity_number (read-only), name, partner_id, lead_id, expected_revenue, expected_close_date
- Section 2 "Pipeline": stage, probability, salesperson_id, territory_id
- Section 3 "Notes": notes

Menu entries under new "CRM" group: Leads, Opportunities; under "Sales": Commissions; under "Admin": Sales Territories (following PRD-004 window/menu patterns).

---

# Acceptance Criteria

- [ ] `[SE]` All 4 forms present in runtime forms list
- [ ] `[SE]` Sections render as specified; read-only fields configured
- [ ] `[SE]` Menu entries visible after login
- [ ] `[QA]` Full CRUD on each form via runtime renderer
- [ ] `[SE][QA]` Backend starts cleanly with migration applied

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reference PRD-008 FR-006

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__seed_crm_forms.sql`

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

Created Task (PLANNED) — PRD-008 approved by user

---

# Related Documents

- PRD-008 — CRM Pipeline & Sales Team Management (FR-006)
