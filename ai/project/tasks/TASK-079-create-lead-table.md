---
id: TASK-079
title: Create Lead table with metadata
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
  - TASK-082
  - TASK-083
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

Provide a metadata-driven lead table (replacing the legacy hardcoded leads table) with qualification status, scoring, source tracking, and sales assignment fields.

---

# Description

Create `tx_lead` via Flyway migration with metadata registration:
- `id` UUID PK
- `lead_number` string(50) required (auto-generated LD-0001 via sequence from TASK-060)
- `company_name` string(200) required, `contact_name` string(200) required
- `email` string(100), `phone` string(30)
- `source` string(30) — enum: website / referral / cold_call / trade_show / advertisement / other
- `status` string(20) required default 'new' — enum: new / contacted / qualified / converted / lost
- `score` string(10) — enum: hot / warm / cold (auto-calculated by backend)
- `salesperson_id` UUID nullable (references sys_user)
- `territory_id` UUID nullable REFERENCES md_sales_territory(id)
- `expected_revenue` decimal(15,2)
- `notes` text
- `converted_partner_id` UUID nullable REFERENCES md_business_partner(id)
- `converted_opportunity_id` UUID nullable
- plus system columns

Register in sys_table/sys_column.

---

# Acceptance Criteria

- [ ] `[SE]` tx_lead created with all columns and metadata registration
- [ ] `[SE]` FK constraints to md_sales_territory and md_business_partner exist
- [ ] `[SE]` Migration idempotent
- [ ] `[QA]` Lead CRUD works via runtime API
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reference PRD-008 FR-001

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__create_lead_table.sql`

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

- PRD-008 — CRM Pipeline & Sales Team Management (FR-001)
