# Project Board

Last Updated: 2026-07-30

Updated By: Software Engineer

(PRD-005 COMPLETED v1.3.1. BUG-013/014/015 RESOLVED, ENH-004 COMPLETED — ready for merge to main.)

(Sales & Customer Management module planned: PRD-006/007/008/009 APPROVED. 33 new tasks generated (TASK-060..092). PRD-006 root tasks READY_FOR_DEV.)

---

## Workflow Legend

```
PRD:  APPROVED → IN_DEVELOPMENT → TESTING → READY_FOR_DEPLOYMENT → COMPLETED
          ↓ (SE)        ↓ (SE)        ↓ (QA)        ↓ (PM+user)     ↓ (Release)
Task: PLANNED → READY_FOR_DEV → IN_DEVELOPMENT → READY_FOR_TEST → TESTING → TESTED → COMPLETED
          ↓ (PM)     ↓ (SE)           ↓ (SE)          ↓ (SE)        ↓ (QA)    ↓ (QA)   ↓ (cascade)
Bug:                                READY_FOR_DEV → IN_DEVELOPMENT → READY_FOR_TEST → TESTING → RESOLVED
```

See `ai/agent/rules/status-transitions.md` for full transition rules.

---

## Ready For Development

| Task | Title | PRD | Priority | Depends On |
|------|-------|-----|----------|------------|
| TASK-060 | Create Document Sequence table and auto-numbering service | PRD-006 | High | — |
| TASK-061 | Create Price List tables with metadata registration | PRD-006 | High | — |
| TASK-062 | Create Discount Rule table with metadata registration | PRD-006 | Medium | — |
| TASK-063 | Create Quotation tables with metadata registration | PRD-006 | High | — |

---

## Planned (awaiting dependencies)

| Task | Title | PRD | Priority | Depends On |
|------|-------|-----|----------|------------|
| TASK-064 | Implement Price Resolution backend service | PRD-006 | High | TASK-061 |
| TASK-065 | Implement Discount Resolution backend service | PRD-006 | Medium | TASK-062 |
| TASK-066 | Seed Quotation, Price List, Discount, and Sequence forms | PRD-006 | High | TASK-060/061/062/063 |
| TASK-067 | Implement Quotation calculation API | PRD-006 | High | TASK-063/064/065 |
| TASK-068 | Implement Quotation to Order conversion | PRD-006 | High | TASK-060, TASK-067 |
| TASK-069 | Deprecate hardcoded sales/ and order/ modules | PRD-006 | High | TASK-066, TASK-068 |
| TASK-070 | Create Payment Term table with seed data and metadata | PRD-007 | High | PRD-006 |
| TASK-071 | Add credit limit and payment term to Business Partner | PRD-007 | High | TASK-070 |
| TASK-072 | Add payment term to Order and Invoice tables | PRD-007 | High | TASK-070 |
| TASK-073 | Implement Order status workflow backend service | PRD-007 | High | PRD-006 |
| TASK-074 | Implement Order auto-calculation backend service | PRD-007 | High | PRD-006 |
| TASK-075 | Implement Customer Credit Check backend service | PRD-007 | High | TASK-071, TASK-073 |
| TASK-076 | Implement Customer 360 API and dashboard form | PRD-007 | Medium | TASK-071 |
| TASK-077 | Update Sales Order form with workflow actions and payment terms | PRD-007 | High | TASK-072/073/074 |
| TASK-078 | Create Sales Territory table with metadata | PRD-008 | Medium | PRD-006 |
| TASK-079 | Create Lead table with metadata | PRD-008 | High | TASK-078 |
| TASK-080 | Create Opportunity table with metadata | PRD-008 | High | TASK-078 |
| TASK-081 | Create Commission table with metadata | PRD-008 | Medium | — |
| TASK-082 | Implement Lead scoring and qualification workflow | PRD-008 | High | TASK-079 |
| TASK-083 | Implement Lead to Opportunity conversion | PRD-008 | High | TASK-079/080/082 |
| TASK-084 | Implement Opportunity pipeline workflow, quotation conversion, and commission | PRD-008 | High | TASK-080, TASK-081 |
| TASK-085 | Seed CRM forms (lead, opportunity, territory, commission) | PRD-008 | High | TASK-078/079/080/081 |
| TASK-086 | Implement CRM Dashboard API and deprecate hardcoded crm/ module | PRD-008 | Medium | TASK-085 |
| TASK-087 | Create Sales Return tables with metadata | PRD-009 | High | PRD-007 |
| TASK-088 | Create Credit Memo table with metadata | PRD-009 | High | PRD-007 |
| TASK-089 | Implement Return workflow and Credit Memo generation | PRD-009 | High | TASK-087, TASK-088 |
| TASK-090 | Implement Return inventory receipt integration | PRD-009 | Medium | TASK-089 |
| TASK-091 | Seed Return and Credit Memo forms | PRD-009 | High | TASK-087, TASK-088 |
| TASK-092 | Implement Sales Analytics dashboard | PRD-009 | Medium | TASK-091 |

---

## PRD Status

| PRD | Title | Version | Status | Priority |
|-----|-------|---------|--------|----------|
| PRD-001 | Dynamic Form Configuration System | 1.6.0 | **COMPLETED** | High |
| PRD-002 | Admin Configuration Forms | 1.1.0 | **COMPLETED** | High |
| PRD-003 | ERP Order Flow — Transaction Forms | 1.0.0 | **COMPLETED** | High |
| PRD-004 | Window Hierarchy & Menu System | 1.1.0 | **COMPLETED** | High |
| PRD-005 | Backend-Frontend Separation & Code Standardization | 1.3.1 | **COMPLETED** | High |
| PRD-006 | Sales Quotation & Price Management | 1.0.0 | **APPROVED** | High |
| PRD-007 | Sales Order Workflow & Customer Management | 1.0.0 | **APPROVED** | High |
| PRD-008 | CRM Pipeline & Sales Team Management | 1.0.0 | **APPROVED** | Medium |
| PRD-009 | Sales Return & Analytics | 1.0.0 | **APPROVED** | Medium |

---


















---

## Ready For Test

*(none)*

---

## In Development

*(none)*

---

## Testing

*(none)*

---

## Completed

| Task | Completed | Version |
|------|-----------|---------|
| TASK-001 | 2026-07-09 | 1.6.0 |
| TASK-002 | 2026-07-09 | 1.6.0 |
| TASK-003 | 2026-07-09 | 1.6.0 |
| TASK-004 | 2026-07-09 | 1.6.0 |
| TASK-005 | 2026-07-09 | 1.6.0 |
| TASK-006 | 2026-07-09 | 1.6.0 |
| TASK-007 | 2026-07-09 | 1.6.0 |
| TASK-008 | 2026-07-09 | 1.6.0 |
| TASK-009 | 2026-07-09 | 1.6.0 |
| TASK-010 | 2026-07-09 | 1.6.0 |
| TASK-011 | 2026-07-09 | 1.6.0 |
| TASK-012 | 2026-07-09 | 1.6.0 |
| TASK-013 | 2026-07-09 | 1.6.0 |
| TASK-014 | 2026-07-09 | 1.6.0 |
| TASK-015 | 2026-07-09 | 1.6.0 |
| TASK-016 | 2026-07-09 | 1.6.0 |
| TASK-017 | 2026-07-09 | 1.6.0 |
| TASK-018 | 2026-07-09 | 1.6.0 |
| TASK-019 | 2026-07-09 | 1.6.0 |
| TASK-020 | 2026-07-09 | 1.6.0 |
| TASK-021 | 2026-07-09 | 1.6.0 |
| TASK-022 | 2026-07-09 | 1.6.0 |
| TASK-023 | 2026-07-09 | 1.6.0 |
| TASK-024 | 2026-07-09 | 1.6.0 |
| TASK-025 | 2026-07-09 | 1.6.0 |
| TASK-026 | 2026-07-09 | 1.6.0 |
| TASK-027 | 2026-07-09 | 1.6.0 |
| TASK-028 | 2026-07-13 | 1.0.0 |
| TASK-029 | 2026-07-13 | 1.0.0 |
| TASK-030 | 2026-07-13 | 1.0.0 |
| TASK-031 | 2026-07-13 | 1.0.0 |
| TASK-032 | 2026-07-13 | 1.0.0 |
| TASK-033 | 2026-07-10 | 1.1.0 |
| TASK-034 | 2026-07-10 | 1.1.0 |
| TASK-035 | 2026-07-10 | 1.1.0 |
| ENH-001 | 2026-07-09 | 1.6.0 |
| ENH-002 | 2026-07-10 | 1.1.0 |
| ENH-003 | 2026-07-14 | 1.0.0 |
| TASK-036 | 2026-07-14 | 1.0.0 |
| TASK-037 | 2026-07-14 | 1.0.0 |
| TASK-038 | 2026-07-14 | 1.0.0 |
| TASK-039 | 2026-07-14 | 1.0.0 |
| TASK-040 | 2026-07-14 | 1.0.0 |
| TASK-041 | 2026-07-14 | 1.0.0 |
| TASK-042 | 2026-07-14 | 1.0.0 |
| TASK-043 | 2026-07-14 | 1.0.0 |
| TASK-044 | 2026-07-14 | 1.0.0 |
| TASK-045 | 2026-07-14 | 1.0.0 |
| TASK-046 | 2026-07-17 | 1.3.1 |
| TASK-047 | 2026-07-17 | 1.3.1 |
| TASK-048 | 2026-07-17 | 1.3.1 |
| TASK-049 | 2026-07-17 | 1.3.1 |
| TASK-050 | 2026-07-17 | 1.3.1 |
| TASK-051 | 2026-07-17 | 1.3.1 |
| TASK-052 | 2026-07-17 | 1.3.1 |
| TASK-053 | 2026-07-17 | 1.3.1 |
| TASK-054 | 2026-07-17 | 1.3.1 |
| TASK-055 | 2026-07-17 | 1.3.1 |
| TASK-056 | 2026-07-17 | 1.3.1 |
| TASK-057 | 2026-07-17 | 1.3.1 |
| TASK-058 | 2026-07-17 | 1.3.1 |
| TASK-059 | 2026-07-17 | 1.3.1 |

---



## Bugs

| Bug | Parent Task | Severity | Status | Owner | Depends On |
|-----|-------------|----------|--------|-------|------------|
| BUG-001 | TASK-001 | Medium | COMPLETED | QA Engineer | — |
| BUG-002 | TASK-007 | High | COMPLETED | QA Engineer | — |
| BUG-003 | TASK-011 | High | COMPLETED | QA Engineer | — |
| BUG-004 | TASK-025 | Medium | COMPLETED | QA Engineer | — |
| BUG-005 | TASK-025 | Medium | CANCELLED | QA Engineer | — |
| BUG-006 | TASK-007 | Critical | COMPLETED | QA Engineer | — |
| BUG-007 | TASK-036 | Critical | COMPLETED | QA Engineer | — |
| BUG-008 | TASK-041 | Medium | COMPLETED | QA Engineer | BUG-007 |
| BUG-009 | TASK-036 | Critical | COMPLETED | QA Engineer | — |
| BUG-010 | TASK-039 | Critical | **RESOLVED** | QA Engineer | — |
| BUG-011 | TASK-041 | Critical | **RESOLVED** | QA Engineer | — |
| BUG-012 | TASK-047 | Critical | **RESOLVED** | QA Engineer | — |
| BUG-013 | TASK-046 | Critical | **RESOLVED** | QA Engineer | — |
| BUG-014 | (seed) | Medium | **RESOLVED** | Software Engineer | — |
| BUG-015 | TASK-046 | Medium | **RESOLVED** | Software Engineer | — |

---

## Enhancements

| Task | Parent PRD | Parent Task | Reason | Status |
|------|------------|-------------|--------|--------|
| ENH-001 | PRD-001 | TASK-007 | Form Designer API tenant authorization | COMPLETED |
| ENH-002 | PRD-002 | TASK-034, TASK-035 | Add tenant_id to all admin forms (REQ-ISSUE-001) | COMPLETED |
| ENH-003 | PRD-001 | TASK-026 | RuntimePage not integrated with API — uses hardcoded sample bundles instead of dynamic form rendering | COMPLETED |
| ENH-004 | PRD-005 | BUG-013 (QA) | Window definition cache auto-invalidation after DB reseeds (stale ghost-UUID errors) | COMPLETED |

---

## Blocked

*(none)*

---

## Statistics

| Status | Count |
|--------|-------|
| PRDs | 9 (5 COMPLETED, 4 APPROVED) |
| Ready For Dev | 4 (TASK-060..063, PRD-006) |
| Ready For Test | 0 |
| In Testing | 0 |
| In Development | 0 |
| Planned | 29 (TASK-064..092) |
| Bugs | 15 (8 COMPLETED, 1 CANCELLED, 6 RESOLVED) |
| Completed (PRD-001) | 27 tasks + 2 enhancements (ENH-001, ENH-003) |
| Completed (PRD-002) | 3 tasks + 1 enhancement (TASK-033/034/035 + ENH-002) |
| Completed (PRD-003) | 5 tasks (TASK-028/029/030/031/032) — merged to main |
| Completed (PRD-004) | 10 tasks (TASK-036..045) — merged to main |
| Completed (PRD-005) | 14 tasks (TASK-046..059) — BUG-013 opened, PRD-005 REOPENED |
| PRD-006 (APPROVED) | 10 tasks (TASK-060..069) — 4 READY_FOR_DEV, 6 PLANNED |
| PRD-007 (APPROVED) | 8 tasks (TASK-070..077) — 8 PLANNED |
| PRD-008 (APPROVED) | 9 tasks (TASK-078..086) — 9 PLANNED |
| PRD-009 (APPROVED) | 6 tasks (TASK-087..092) — 6 PLANNED |
| Total Tasks | 61 completed + 33 new = 94 total |
| PRD-005 Status | **COMPLETED** (v1.3.1) — all tasks, bugs, and enhancements resolved; ready for merge to main |
