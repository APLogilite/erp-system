---
id: TASK-086
title: Implement CRM Dashboard API and deprecate hardcoded crm/ module
type: Feature
scope: both
status: PLANNED
priority: Medium
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
  - TASK-085
depends_on:
  - TASK-085
blocks: []
labels:
  - backend
  - frontend
  - refactor
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

Give sales managers a pipeline dashboard (stage counts, revenue, conversion rates, team performance) and remove the legacy hardcoded crm/ module now that metadata-driven tx_lead/tx_opportunity replace it.

---

# Description

**Dashboard API** — `CrmDashboardService`:

```
GET /api/v1/crm/dashboard
Response: {
  "pipeline": {
    "qualification": { "count": 12, "totalRevenue": 150000 },
    "proposal":      { "count": 8,  "totalRevenue": 200000 },
    "negotiation":   { "count": 5,  "totalRevenue": 180000 },
    "closedWon":     { "count": 15, "totalRevenue": 500000 },
    "closedLost":    { "count": 3,  "totalRevenue": 50000 }
  },
  "conversionRates": { "leadToOpportunity": 0.25, "opportunityToOrder": 0.60 },
  "teamPerformance": [ { "salespersonId", "name", "openOpportunities", "closedRevenue", "commissionEarned" } ]
}
```

**Dashboard UI** — custom React page under `frontend/src/routes/` (MUI cards + simple bar/list visuals) calling the API; add "CRM Dashboard" menu entry. (Dashboard layouts exceed the metadata engine's form model, so a custom page is acceptable per PRD-008 FR-005.)

**Deprecation:**
1. Grep for references to com.erp.modules.crm (Lead, Opportunity legacy entities) and legacy `leads` / `opportunities` tables; document findings
2. Delete `backend/src/main/java/com/erp/modules/crm/`
3. Migration: DROP TABLE IF EXISTS leads, opportunities CASCADE
4. `mvn clean compile` + `mvn test` pass
5. Server verification (start-all.sh, clean logs)

---

# Acceptance Criteria

- [ ] `[SE]` Dashboard API returns correct pipeline aggregates and conversion rates
- [ ] `[SE]` Dashboard page renders data without errors; menu entry works
- [ ] `[SE]` Reference search documented; crm/ module deleted; legacy tables dropped
- [ ] `[SE]` mvn clean compile + mvn test pass (BUILD SUCCESS)
- [ ] `[QA]` Application starts cleanly; metadata lead/opportunity forms work end-to-end
- [ ] `[SE][QA]` Frontend typecheck + lint pass

---

# Unmet Criteria

| Criteria | Reason | Resolution |
|----------|--------|------------|
| | | |

---

# Technical Notes

- Reference PRD-008 FR-005, FR-007

---

# Files Expected

- `backend/src/main/java/com/erp/modules/crmflow/service/CrmDashboardService.java` (new)
- `backend/src/main/java/com/erp/modules/crmflow/controller/CrmDashboardController.java` (new)
- `frontend/src/routes/CrmDashboard.tsx` (new)
- `backend/src/main/resources/db/migration/V{next}__drop_legacy_crm_tables.sql`
- Deleted: `backend/src/main/java/com/erp/modules/crm/**`

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

- PRD-008 — CRM Pipeline & Sales Team Management (FR-005, FR-007)
