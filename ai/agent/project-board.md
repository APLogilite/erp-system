# Project Board

Last Updated: 2026-08-24

Updated By: Software Engineer

(PRD-007 COMPLETED and merged to main. PRD-008 IN_DEVELOPMENT.)

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
| (none) | | | | |

---

## Ready For Test

| Task | Title | PRD | Priority |
|------|-------|-----|----------|
| (none) | | | |

---

## Ready For Development

| Task | Title | PRD | Priority | Depends On |
|------|-------|-----|----------|------------|
| TASK-081 | Create Commission table with metadata | PRD-008 | Medium | — |

---

## In Development

| Task | Title | PRD | Priority | Depends On |
|------|-------|-----|----------|------------|
| TASK-078 | Create Sales Territory table with metadata | PRD-008 | Medium | PRD-006, PRD-007 |

---

## Planned (awaiting dependencies)

| Task | Title | PRD | Priority | Depends On |
|------|-------|-----|----------|------------|
| TASK-079 | Create Lead table with metadata | PRD-008 | High | TASK-078 |
| TASK-080 | Create Opportunity table with metadata | PRD-008 | High | TASK-078 |
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
| PRD-006 | Sales Quotation & Price Management | 1.0.0 | **COMPLETED** | High |
| PRD-007 | Sales Order Workflow & Customer Management | 1.0.0 | **COMPLETED** | High |
| PRD-008 | CRM Pipeline & Sales Team Management | 1.0.0 | **IN_DEVELOPMENT** | Medium |
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
| TASK-060 | 2026-08-12 | 1.0.0 |
| TASK-061 | 2026-08-12 | 1.0.0 |
| TASK-062 | 2026-08-12 | 1.0.0 |
| TASK-063 | 2026-08-12 | 1.0.0 |
| TASK-064 | 2026-08-12 | 1.0.0 |
| TASK-065 | 2026-08-12 | 1.0.0 |
| TASK-066 | 2026-08-12 | 1.0.0 |
| TASK-067 | 2026-08-12 | 1.0.0 |
| TASK-068 | 2026-08-12 | 1.0.0 |
| TASK-069 | 2026-08-12 | 1.0.0 |
| TASK-070 | 2026-08-24 | 1.0.0 |
| TASK-071 | 2026-08-24 | 1.0.0 |
| TASK-072 | 2026-08-24 | 1.0.0 |
| TASK-073 | 2026-08-24 | 1.0.0 |
| TASK-074 | 2026-08-24 | 1.0.0 |
| TASK-075 | 2026-08-24 | 1.0.0 |
| TASK-076 | 2026-08-24 | 1.0.0 |
| TASK-077 | 2026-08-24 | 1.0.0 |

---



## Bugs

| Bug | Parent Task | Severity | Status | Owner | Depends On |
|-----|-------------|----------|--------|-------|------------|
| BUG-001 | TASK-001 | Medium | COMPLETED | QA Engineer | — |
| BUG-002 | TASK-007 | High | COMPLETED | QA Engineer | — |
| BUG-003 | TASK-011 | High | COMPLETED | QA Engineer | — |
| BUG-004 | TASK-070 | Medium | READY_FOR_DEV | QA Engineer | — |
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
| PRDs | 9 (7 COMPLETED, 1 IN_DEVELOPMENT, 1 APPROVED) |
| Ready For Dev | 2 (TASK-078, TASK-081) |
| Ready For Test | 0 |
| In Testing | 0 |
| In Development | 0 |
| Planned | 12 (TASK-079/080/082..086, TASK-087..092) |
| Bugs | 16 (8 COMPLETED, 1 CANCELLED, 6 RESOLVED, 1 READY_FOR_DEV) |
| Completed (PRD-001) | 27 tasks + 2 enhancements (ENH-001, ENH-003) |
| Completed (PRD-002) | 3 tasks + 1 enhancement (TASK-033/034/035 + ENH-002) |
| Completed (PRD-003) | 5 tasks (TASK-028/029/030/031/032) — merged to main |
| Completed (PRD-004) | 10 tasks (TASK-036..045) — merged to main |
| Completed (PRD-005) | 14 tasks (TASK-046..059) — BUG-013 opened, PRD-005 REOPENED |
| Completed (PRD-006) | 10 tasks (TASK-060..069) — all TESTED, 6 manual scenarios pending |
| Completed (PRD-007) | 8 tasks (TASK-070..077) — merged to main; BUG-004 unresolved |
| In Development (PRD-008) | 1 task in dev (TASK-078), 1 ready for dev (TASK-081) |
| Total Tasks | 71 completed + 23 planned = 94 total |
