---
id: TASK-084
title: Implement Opportunity pipeline workflow, quotation conversion, and commission
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
estimated_hours: 5
actual_hours:
parent_prd: PRD-008
prd_version: 1.0.0
prd_branch: prd/PRD-008-crm-pipeline-sales-team
base_branch:
merge_target:
merge_strategy:
parent_task:
related_tasks:
  - TASK-080
  - TASK-081
depends_on:
  - TASK-080
  - TASK-081
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

Enforce the opportunity pipeline stages, convert opportunities to quotations, and auto-create commission records when deals close — completing the Lead → Opportunity → Quotation → Order chain.

---

# Description

Implement `OpportunityService` (backend):

**Stage workflow endpoint** `POST /api/v1/opportunities/{id}/transition`:
- qualification → proposal → negotiation → closed_won / closed_lost
- Invalid transitions → 400

**Create quotation** `POST /api/v1/opportunities/{id}/create-quotation`:
1. Validate stage IN (proposal, negotiation)
2. Generate quote number (QT- sequence)
3. Create tx_quotation: partner_id from opportunity, grand_total defaulting from expected_revenue, status='draft'
4. Update opportunity: converted_quotation_id
5. Response: `{ "quotationId": "uuid", "quoteNumber": "QT-0007" }`

**Commission on close-won:**
- When stage transitions to closed_won: create tx_commission (salesperson_id from opportunity, opportunity_id, commission_percent from a configurable default — start with system property `crm.default-commission-percent` = 5.0, commission_amount = expected_revenue × percent / 100, status='pending')

**Auto-numbering:** opportunity_number assigned from OPP- sequence on create.

---

# Acceptance Criteria

- [ ] `[SE]` Stage transitions validated (invalid → 400)
- [ ] `[SE]` create-quotation creates quotation and links it to the opportunity
- [ ] `[SE]` Closing won auto-creates a pending commission with correct amount
- [ ] `[SE]` Closing lost creates no commission
- [ ] `[QA]` Opportunity number auto-assigned on create
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Depends on tx_opportunity (TASK-080), tx_commission (TASK-081), tx_quotation + sequence (PRD-006)
- Reference PRD-008 FR-002, FR-004

---

# Files Expected

- `backend/src/main/java/com/erp/modules/crmflow/service/OpportunityService.java` (new)
- `backend/src/main/java/com/erp/modules/crmflow/controller/OpportunityController.java` (new)
- `backend/src/main/java/com/erp/modules/crmflow/dto/CreateQuotationResponse.java` (new)

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

- PRD-008 — CRM Pipeline & Sales Team Management (FR-002, FR-004)
