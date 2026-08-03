---
id: TASK-080
title: Create Opportunity table with metadata
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
parent_prd: PRD-008
prd_version: 1.0.0
prd_branch: prd/PRD-008-crm-pipeline-sales-team
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-078
depends_on:
  - TASK-078
blocks:
  - TASK-083
  - TASK-084
  - TASK-085
labels:
  - database
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

Provide a metadata-driven opportunity table (replacing the legacy hardcoded opportunities table) with pipeline stages, probability, expected revenue/close date, and conversion links.

---

# Description

Create `tx_opportunity` via Flyway migration with metadata registration:
- `id` UUID PK
- `opportunity_number` string(50) required (auto-generated OPP-0001)
- `name` string(200) required
- `partner_id` UUID required REFERENCES md_business_partner(id)
- `lead_id` UUID nullable REFERENCES tx_lead(id)
- `stage` string(20) required default 'qualification' — enum: qualification / proposal / negotiation / closed_won / closed_lost
- `probability` integer (0-100)
- `expected_revenue` decimal(15,2) required
- `expected_close_date` date
- `salesperson_id` UUID nullable
- `territory_id` UUID nullable REFERENCES md_sales_territory(id)
- `notes` text
- `converted_quotation_id` UUID nullable
- `converted_order_id` UUID nullable
- plus system columns

Register in sys_table/sys_column.

---

# Acceptance Criteria

- [ ] `[SE]` tx_opportunity created with all columns and metadata registration
- [ ] `[SE]` FK constraints exist (partner, lead, territory)
- [ ] `[SE]` Migration idempotent
- [ ] `[QA]` Opportunity CRUD works via runtime API
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reference PRD-008 FR-002

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__create_opportunity_table.sql`

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

- PRD-008 — CRM Pipeline & Sales Team Management (FR-002)
