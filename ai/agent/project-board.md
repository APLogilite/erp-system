# Project Board

Last Updated: 2026-07-17

Updated By: Software Engineer

(PRD-005 IN_DEVELOPMENT v1.3.0 — TASK-046 and TASK-047 READY_FOR_TEST, 12 tasks READY_FOR_DEV including newly activated TASK-049)

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

## PRD Status

| PRD | Title | Version | Status | Priority |
|-----|-------|---------|--------|----------|
| PRD-001 | Dynamic Form Configuration System | 1.6.0 | **COMPLETED** | High |
| PRD-002 | Admin Configuration Forms | 1.1.0 | **COMPLETED** | High |
| PRD-003 | ERP Order Flow — Transaction Forms | 1.0.0 | **COMPLETED** | High |
| PRD-004 | Window Hierarchy & Menu System | 1.1.0 | **COMPLETED** | High |
| PRD-005 | Backend-Frontend Separation & Code Standardization | 1.3.0 | **IN_DEVELOPMENT** | High |

---

## Ready For Development

| Task | Title | Priority | Scope |
|------|-------|----------|-------|

| TASK-048 | Backend Pre-Filters and Pre-Sorts Fields | Medium | backend |
| TASK-049 | Backend Type Coercion on Save | Medium | backend |
| TASK-050 | Guarantee _display on Every Record | High | both |
| TASK-051 | Backend Returns RuntimeMetadataBundle Directly | High | both |
| TASK-052 | Backend Search Endpoint for Ctrl+K | Medium | both |
| TASK-053 | Backend Guarantees Non-Empty Sections | Low | backend |
| TASK-054 | Remove Dead modules/auth/ Package | High | backend |
| TASK-055 | Remove Dead core/security/ Package | High | backend |
| TASK-056 | Move customerService.ts Out of core/api/services/ | Low | frontend |
| TASK-057 | Audit and Remove Stale Frontend API Endpoints | Low | frontend |
| TASK-058 | Move Window Schema to core/layout/ | High | backend |
| TASK-059 | Move Frontend Pages to routes/ | Medium | frontend |

---

## Ready For Test

| Task | Title | Priority | Scope | Branch |
|------|-------|----------|-------|--------|
| TASK-046 | Add childTabIds to TabDefinitionResponse | High | backend | feature/TASK-046 |
| TASK-047 | Add htmlType and lookupOptions to FieldDefinitionResponse | High | both | feature/TASK-047 |

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

---

## Enhancements

| Task | Parent PRD | Parent Task | Reason | Status |
|------|------------|-------------|--------|--------|
| ENH-001 | PRD-001 | TASK-007 | Form Designer API tenant authorization | COMPLETED |
| ENH-002 | PRD-002 | TASK-034, TASK-035 | Add tenant_id to all admin forms (REQ-ISSUE-001) | COMPLETED |
| ENH-003 | PRD-001 | TASK-026 | RuntimePage not integrated with API — uses hardcoded sample bundles instead of dynamic form rendering | COMPLETED |

---

## Blocked

*(none)*

---

## Statistics

| Status | Count |
|--------|-------|
| PRDs | 5 (4 COMPLETED: PRD-001/002/003/004, 1 IN_DEVELOPMENT: PRD-005) |
| Ready For Dev | 12 (PRD-005: TASK-048/049/050/051/052/053/054/055/056/057/058/059) |
| Ready For Test | 2 (PRD-005: TASK-046, TASK-047) |
| In Development | 0 |
| In Testing | 0 |
| Planning | 0 |
| Bugs | 11 (8 COMPLETED, 1 CANCELLED, 2 RESOLVED) |
| Completed (PRD-001) | 27 tasks + 2 enhancements (ENH-001, ENH-003) |
| Completed (PRD-002) | 3 tasks + 1 enhancement (TASK-033/034/035 + ENH-002) |
| Completed (PRD-003) | 5 tasks (TASK-028/029/030/031/032) — merged to main |
| Completed (PRD-004) | 10 tasks (TASK-036..045) — merged to main |
| Total Tasks | 47 completed + 12 ready + 2 testing (PRD-005) |
| PRD-004 Status | COMPLETED (v1.1.0) — Consolidated migrations, BUG-010/BUG-011 RESOLVED |
| PRD-005 Status | IN_DEVELOPMENT (v1.3.0) — 12 tasks ready, 2 in test (TASK-046/047) |
