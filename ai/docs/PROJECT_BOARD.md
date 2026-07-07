---
document: PROJECT_BOARD
version: 2.0.0
status: ACTIVE
owner: Planner
last_updated: 2026-07-07
---

# Project Board — Dynamic Form Configuration System

**Git Workflow:** PRD-based branching (main → prd/PRD-XXX → feature/TASK-XXX)

## Legend

| Status | Description | Owner |
|--------|-------------|-------|
| 🔵 PLANNING | Task still being defined | Planner |
| 🟤 PLANNED | Fully defined, waiting for dependencies | Planner |
| 🟢 READY_FOR_DEV | Dependencies satisfied, available for Developer | Developer |
| 🟡 IN_DEVELOPMENT | Developer actively working | Developer |
| 🟣 READY_FOR_TEST | Implemented, awaiting Tester | Tester |
| 🔴 BLOCKED | Work cannot continue | Current Owner |

---

## PRD Status

| PRD | Title | Version | Status | Branch |
|-----|-------|---------|--------|--------|
| PRD-001 | Dynamic Form Configuration System | 1.6.0 | APPROVED | `prd/PRD-001-dynamic-form-configuration` |

---

## Phase 1: Foundation (Database & Entities)

| Task | Title | Priority | Depends On | Status | PRD Branch |
|------|-------|----------|------------|--------|------------|
| TASK-001 | Flyway Migrations for Metadata Storage | High | — | READY_FOR_TEST | prd/PRD-001 |
| TASK-002 | JPA Entities + Repositories + DTOs | High | TASK-001 | READY_FOR_TEST | prd/PRD-001 |

## Phase 2: Table Designer Backend

| Task | Title | Priority | Depends On | Status | PRD Branch |
|------|-------|----------|------------|--------|------------|
| TASK-003 | DDL Executor Service | High | TASK-002 | READY_FOR_TEST | prd/PRD-001 |
| TASK-004 | Table Designer CRUD APIs | High | TASK-002, TASK-003 | PLANNED | prd/PRD-001 |
| TASK-005 | Schema History Service | Low | TASK-002 | READY_FOR_TEST | prd/PRD-001 |

## Phase 3: Table Designer Frontend

| Task | Title | Priority | Depends On | Status | PRD Branch |
|------|-------|----------|------------|--------|------------|
| TASK-006 | Table Designer Admin UI | High | TASK-004 | PLANNED | prd/PRD-001 |

## Phase 4: Form Designer Backend

| Task | Title | Priority | Depends On | Status | PRD Branch |
|------|-------|----------|------------|--------|------------|
| TASK-007 | Form Designer CRUD APIs | High | TASK-002 | READY_FOR_TEST | prd/PRD-001 |
| TASK-008 | Form Rules & Validation APIs | High | TASK-007 | PLANNED | prd/PRD-001 |
| TASK-009 | Sub-Form Configuration APIs | High | TASK-007 | PLANNED | prd/PRD-001 |
| TASK-010 | Per-Tenant Role Assignment APIs | High | TASK-007 | PLANNED | prd/PRD-001 |

## Phase 5: Form Designer Frontend

| Task | Title | Priority | Depends On | Status | PRD Branch |
|------|-------|----------|------------|--------|------------|
| TASK-011 | Form Designer Admin UI — Core | High | TASK-007 | PLANNED | prd/PRD-001 |
| TASK-012 | Form Designer — Rules & Validation UI | High | TASK-008, TASK-011 | PLANNED | prd/PRD-001 |
| TASK-013 | Form Designer — Sub-Forms & Global Forms UI | Medium | TASK-009, TASK-011 | PLANNED | prd/PRD-001 |
| TASK-014 | Global Forms Role Access UI (System Admin) | Medium | TASK-010 | PLANNED | prd/PRD-001 |

## Phase 6: Runtime Backend

| Task | Title | Priority | Depends On | Status | PRD Branch |
|------|-------|----------|------------|--------|------------|
| TASK-015 | Dynamic CRUD Service | Critical | TASK-002 | READY_FOR_TEST | prd/PRD-001 |
| TASK-016 | Form Definition Bundle API | Critical | TASK-007, TASK-015 | PLANNED | prd/PRD-001 |
| TASK-017 | Record Data APIs | Critical | TASK-015, TASK-016 | PLANNED | prd/PRD-001 |
| TASK-018 | Breadcrumb & Parent Context Service | Medium | TASK-016 | PLANNED | prd/PRD-001 |

## Phase 7: Runtime Frontend

| Task | Title | Priority | Depends On | Status | PRD Branch |
|------|-------|----------|------------|--------|------------|
| TASK-019 | useForm() Hook | Critical | TASK-016, TASK-017 | PLANNED | prd/PRD-001 |
| TASK-020 | Dynamic Form Renderer | Critical | TASK-019 | PLANNED | prd/PRD-001 |
| TASK-021 | Client-Side Rules Engine | High | TASK-020 | PLANNED | prd/PRD-001 |
| TASK-022 | Form Toolbar | High | TASK-019 | PLANNED | prd/PRD-001 |
| TASK-023 | Sub-Form Tabs & Inline Grids | High | TASK-019, TASK-020 | PLANNED | prd/PRD-001 |
| TASK-024 | Breadcrumb Navigation | Medium | TASK-019 | PLANNED | prd/PRD-001 |
| TASK-025 | Header Form Search Bar | Medium | TASK-019 | PLANNED | prd/PRD-001 |
| TASK-026 | Role-Based Navigation Menu | High | TASK-019 | PLANNED | prd/PRD-001 |
| TASK-027 | Dynamic List View | High | TASK-019, TASK-022 | PLANNED | prd/PRD-001 |

---

## Summary

| Metric | Count | Tasks |
|--------|-------|-------|
| Total Tasks | 27 | TASK-001 to TASK-027 |
| 🟣 READY_FOR_TEST | 6 | TASK-001, TASK-002, TASK-003, TASK-005, TASK-007, TASK-015 |
| 🟤 PLANNED | 21 | TASK-004, TASK-006, TASK-008 to TASK-014, TASK-016 to TASK-027 |
| 🟢 READY_FOR_DEV | 0 | — |
| 🟡 IN_DEVELOPMENT | 0 | — |
| 🔵 PLANNING | 0 | — |
| 🔴 BLOCKED | 0 | — |

---

## Git Workflow Reference

All tasks belong to PRD-001.

| Field | Value |
|-------|-------|
| PRD Branch | `prd/PRD-001-dynamic-form-configuration` |
| Base Branch for Tasks | `prd/PRD-001-dynamic-form-configuration` |
| Merge Target for Tasks | `prd/PRD-001-dynamic-form-configuration` |
| Merge Target for PRD | `main` |
| Merge Strategy | merge |

---

## Branch Workflow

```
main
  │
  └── prd/PRD-001-dynamic-form-configuration
        │
        ├── feature/TASK-XXX
        │     └── merge → prd/PRD-001-dynamic-form-configuration
        │
        ├── feature/TASK-YYY
        │     └── merge → prd/PRD-001-dynamic-form-configuration
        │
        └── ... (QA validates PRD branch)
              └── merge → main
```

---

## Activation Rules

Per TASK_ACTIVATION_RULES.md: a task may move from PLANNED to READY_FOR_DEV when:
- Parent PRD is APPROVED
- All dependency tasks are READY_FOR_TEST (or beyond)
- No blocking bug exists
- Task is not cancelled or locked

---

## Notes

- All code from the previous implementation cycle has been merged to `main`.
- The `prd/PRD-001-dynamic-form-configuration` branch is the integration branch for all TASK-001 to TASK-027 development.
- Historical feature branches (`feature/TASK-*`) are superseded by this workflow and should not be referenced.
