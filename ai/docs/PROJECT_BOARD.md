---
document: PROJECT_BOARD
version: 1.1.0
status: ACTIVE
owner: Planner
last_updated: 2026-07-07
---

# Project Board — Dynamic Form Configuration System

**Workflow version:** 1.0.0 (PLANNING → PLANNED → READY_FOR_DEV → ...)

## Legend

| Status | Description | Owner |
|--------|-------------|-------|
| 🔵 PLANNING | Task still being defined | Planner |
| 🟤 PLANNED | Fully defined, waiting for dependencies | Planner |
| 🟢 READY_FOR_DEV | Dependencies completed, available for Developer | Developer |
| 🟡 IN_DEVELOPMENT | Developer actively working | Developer |
| 🟣 READY_FOR_TEST | Implemented, awaiting Tester | Tester |
| 🔴 BLOCKED | Work cannot continue | Current Owner |

---

## PRD Status

| PRD | Title | Version | Status |
|-----|-------|---------|--------|
| PRD-001 | Dynamic Form Configuration System | 1.6.0 | ✅ APPROVED |

---

## Phase 1: Foundation (Database & Entities)

| Task | Title | Priority | Depends On | Status |
|------|-------|----------|------------|--------|
| TASK-001 | Flyway Migrations for Metadata Storage | High | — | 🟣 READY_FOR_TEST |
| TASK-002 | JPA Entities + Repositories + DTOs | High | TASK-001 | 🟣 READY_FOR_TEST |

## Phase 2: Table Designer Backend

| Task | Title | Priority | Depends On | Status |
|------|-------|----------|------------|--------|
| TASK-003 | DDL Executor Service | High | TASK-002 | 🟣 READY_FOR_TEST |
| TASK-004 | Table Designer CRUD APIs | High | TASK-002, TASK-003 | 🟢 READY_FOR_DEV |
| TASK-005 | Schema History Service | Low | TASK-002 | 🟣 READY_FOR_TEST |

## Phase 3: Table Designer Frontend

| Task | Title | Priority | Depends On | Status |
|------|-------|----------|------------|--------|
| TASK-006 | Table Designer Admin UI | High | TASK-004 | 🟤 PLANNED |

## Phase 4: Form Designer Backend

| Task | Title | Priority | Depends On | Status |
|------|-------|----------|------------|--------|
| TASK-007 | Form Designer CRUD APIs | High | TASK-002 | 🟣 READY_FOR_TEST |
| TASK-008 | Form Rules & Validation APIs | High | TASK-007 | 🟢 READY_FOR_DEV |
| TASK-009 | Sub-Form Configuration APIs | High | TASK-007 | 🟢 READY_FOR_DEV |
| TASK-010 | Per-Tenant Role Assignment APIs | High | TASK-007 | 🟢 READY_FOR_DEV |

## Phase 5: Form Designer Frontend

| Task | Title | Priority | Depends On | Status |
|------|-------|----------|------------|--------|
| TASK-011 | Form Designer Admin UI — Core | High | TASK-007 | 🟢 READY_FOR_DEV |
| TASK-012 | Form Designer — Rules & Validation UI | High | TASK-008, TASK-011 | 🟤 PLANNED |
| TASK-013 | Form Designer — Sub-Forms & Global Forms UI | Medium | TASK-009, TASK-011 | 🟤 PLANNED |
| TASK-014 | Global Forms Role Access UI (System Admin) | Medium | TASK-010 | 🟤 PLANNED |

## Phase 6: Runtime Backend

| Task | Title | Priority | Depends On | Status |
|------|-------|----------|------------|--------|
| TASK-015 | Dynamic CRUD Service | Critical | TASK-002 | 🟣 READY_FOR_TEST |
| TASK-016 | Form Definition Bundle API | Critical | TASK-007, TASK-015 | 🟣 READY_FOR_TEST |
| TASK-017 | Record Data APIs | Critical | TASK-015, TASK-016 | 🟢 READY_FOR_DEV |
| TASK-018 | Breadcrumb & Parent Context Service | Medium | TASK-016 | 🟤 PLANNED |

## Phase 7: Runtime Frontend

| Task | Title | Priority | Depends On | Status |
|------|-------|----------|------------|--------|
| TASK-019 | useForm() Hook | Critical | TASK-016, TASK-017 | 🟤 PLANNED |
| TASK-020 | Dynamic Form Renderer | Critical | TASK-019 | 🟤 PLANNED |
| TASK-021 | Client-Side Rules Engine | High | TASK-020 | 🟤 PLANNED |
| TASK-022 | Form Toolbar | High | TASK-019 | 🟤 PLANNED |
| TASK-023 | Sub-Form Tabs & Inline Grids | High | TASK-019, TASK-020 | 🟤 PLANNED |
| TASK-024 | Breadcrumb Navigation | Medium | TASK-019 | 🟤 PLANNED |
| TASK-025 | Header Form Search Bar | Medium | TASK-019 | 🟤 PLANNED |
| TASK-026 | Role-Based Navigation Menu | High | TASK-019 | 🟤 PLANNED |
| TASK-027 | Dynamic List View | High | TASK-019, TASK-022 | 🟤 PLANNED |

---

## Summary

| Metric | Count | Tasks |
|--------|-------|-------|
| Total Tasks | 27 | TASK-001 to TASK-027 |
| 🟣 READY_FOR_TEST | 6 | TASK-001, TASK-002, TASK-003, TASK-005, TASK-007, TASK-015 |
| 🟤 PLANNED | 15 | TASK-006, TASK-012, TASK-013, TASK-014, TASK-017 to TASK-027 |
| 🟢 READY_FOR_DEV | 6 | TASK-004, TASK-008, TASK-009, TASK-010, TASK-011, TASK-016 |
| 🟡 IN_DEVELOPMENT | 0 | — |
| 🔵 PLANNING | 0 | — |
| 🔴 BLOCKED | 0 | — |

## Dependency Chain

```
TASK-001 (READY_FOR_TEST) → TASK-002 (READY_FOR_TEST)
                                            │
                        ┌───────────────────┼───────────────────┐
                        ▼                   ▼                   ▼
                  TASK-003 (PLANNED)  TASK-005 (RFT)     TASK-007 (PLANNED)
                        ▼                                       ▼
                  TASK-004 (PLANNED)                      TASK-008 (PLANNED)
                        ▼                                   etc.
                  TASK-006 (PLANNED)
```

## Activation Rules

Per WORKFLOW.md: When a task reaches COMPLETED, the system automatically checks all tasks that depend on it. If all their dependencies are COMPLETED, and the task is:
- Status = PLANNED
- Parent PRD = APPROVED
- Not BLOCKED or CANCELLED

Then automatically: PLANNED → READY_FOR_DEV (Planner involvement not required).
