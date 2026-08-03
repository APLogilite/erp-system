---
id: TASK-082
title: Implement Lead scoring and qualification workflow
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
  - TASK-079
depends_on:
  - TASK-079
blocks:
  - TASK-083
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

Auto-score leads (hot/warm/cold) and enforce the lead qualification lifecycle (new → contacted → qualified → converted/lost) so reps focus on the best leads and leads move through a consistent process.

---

# Description

Implement `LeadService` (backend):

**Scoring (auto-calculated on create/update):**
- `hot` if expected_revenue > 50000 OR source = 'referral'
- `warm` if expected_revenue > 10000 OR source IN ('website','trade_show')
- `cold` otherwise
- Score field is read-only on the form; always recomputed server-side

**Workflow endpoint:**
```
POST /api/v1/leads/{id}/transition
Request:  { "targetStatus": "contacted" }
```
Allowed transitions: new → contacted / lost; contacted → qualified / lost; qualified → converted (via TASK-083) / lost. Invalid → 400.

**Auto-numbering:** on lead creation, assign lead_number from the `lead` sequence (LD-0001) via DocumentSequenceService (TASK-060).

---

# Acceptance Criteria

- [ ] `[SE]` Score auto-computed correctly for all rule branches
- [ ] `[SE]` Valid transitions succeed; invalid return 400 with message
- [ ] `[SE]` lead_number auto-assigned from sequence on create
- [ ] `[QA]` Score read-only via API (client-set value ignored/overwritten)
- [ ] `[SE][QA]` Backend starts cleanly

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Depends on tx_lead (TASK-079) and DocumentSequenceService (TASK-060 from PRD-006)
- Reference PRD-008 FR-001

---

# Files Expected

- `backend/src/main/java/com/erp/modules/crmflow/service/LeadService.java` (new)
- `backend/src/main/java/com/erp/modules/crmflow/controller/LeadController.java` (new)
- `backend/src/main/java/com/erp/modules/crmflow/dto/LeadTransitionRequest.java` (new)

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
