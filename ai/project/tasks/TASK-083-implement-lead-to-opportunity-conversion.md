---
id: TASK-083
title: Implement Lead to Opportunity conversion
type: Feature
scope: backend
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
  - TASK-079
  - TASK-080
  - TASK-082
depends_on:
  - TASK-079
  - TASK-080
  - TASK-082
blocks: []
labels:
  - backend
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

Convert a qualified lead into a business partner (if new) plus an opportunity in one action — so reps never re-enter prospect data and the pipeline stays linked end-to-end.

---

# Description

Add `LeadService.convertToOpportunity(UUID leadId)` with endpoint:

```
POST /api/v1/leads/{id}/convert
Response: { "partnerId": "uuid", "opportunityId": "uuid", "opportunityNumber": "OPP-0001" }
```

Logic (single transaction):
1. Validate lead status = 'qualified' — otherwise 400
2. Validate converted_opportunity_id IS NULL — otherwise 400 "Lead already converted"
3. If lead has no converted_partner_id: create md_business_partner from lead data (code = lead_number, name = company_name, email, phone, is_customer = true); link to lead
4. Generate opportunity number (OPP-0001 via sequence)
5. Create tx_opportunity: name = company_name + " Opportunity", partner_id, lead_id, expected_revenue from lead, salesperson_id, territory_id, stage = 'qualification'
6. Update lead: status = 'converted', converted_partner_id, converted_opportunity_id
7. Return ids

---

# Acceptance Criteria

- [ ] `[SE]` Qualified lead converts: partner + opportunity created, lead updated
- [ ] `[SE]` Opportunity number auto-generated from sequence
- [ ] `[SE]` Non-qualified lead returns 400; already-converted returns 400
- [ ] `[SE]` Transactional: failure leaves no partial records
- [ ] `[QA]` Converted opportunity visible in runtime form with correct links
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reference PRD-008 FR-002 (conversion flow)

---

# Files Expected

- `backend/src/main/java/com/erp/modules/crmflow/service/LeadService.java` (modified)
- `backend/src/main/java/com/erp/modules/crmflow/controller/LeadController.java` (modified)
- `backend/src/main/java/com/erp/modules/crmflow/dto/LeadConvertResponse.java` (new)

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
