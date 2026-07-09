---
document: PROJECT_MEMORY
version: 1.1.0
status: ACTIVE
owner: Planner
last_updated: 2026-07-07
---

# Project Overview

| Field | Value |
|-------|-------|
| Project Name | Dynamic ERP Platform |
| Business Goal | Metadata-driven ERP runtime platform |
| Current Phase | PRD-001 — Dynamic Form Configuration System (TESTING) |
| Current Version | 1.6.0 |

---

# Technology Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.3.4 / Java 17 / Maven |
| Frontend | React 18 / TypeScript (strict) / Vite / MUI 5 |
| State | Zustand + React Query |
| Database | PostgreSQL (JSONB for static metadata, relational for mutable data) |
| Grid | AG Grid Enterprise |
| Validation | Zod (frontend), Jakarta (backend) |
| Auth | JWT + Spring Security |

---

# Architecture

| Aspect | Decision |
|--------|----------|
| Pattern | Metadata-driven runtime execution engine |
| Storage | Normalized relational tables for mutable metadata, JSONB for static config |
| API Pattern | Two-request loading (definition cached + data fresh) with frontend hook abstraction |
| Multi-tenancy | Shared table + tenant_id column on every dynamic table |
| Row Security | Tenant isolation + role-based row filters (server-enforced) |
| Form Hierarchy | Multi-level sub-forms with breadcrumb navigation |

---

# Project Decisions

| Decision | Rationale |
|----------|-----------|
| Normalized metadata tables (not JSONB) | Avoid rewriting large JSONB on every field label change; support concurrent edits |
| Two-request API pattern (not consolidated bundle) | Form definition changes rarely (cached 5 min); data always fresh; separation of concerns |
| Row-level data access in per-role filters | Different roles within same tenant need different data visibility |
| Global forms with per-tenant role access | System Admin creates forms once; each tenant independently configures access |
| Soft-delete on all dynamic tables | Matches existing BaseEntity pattern; audit trail preserved |
| Frontend useForm() hook | Abstracts two-request pattern from components; manages cache + freshness |

---

# Development Rules

- All tasks must follow the lifecycle: PLANNING → PLANNED → READY_FOR_DEV → IN_DEVELOPMENT → READY_FOR_TEST → TESTING → TESTED → COMPLETED
- PROJECT_BOARD.md is the single source of truth for execution
- Branching: main → prd/PRD-XXX → feature/TASK-XXX (or bugfix/ or enhancement/)
- Completed work is never modified; use Enhancement or Bug tasks for changes
- All PRD updates after implementation starts require Enhancement Tasks

---

# Known Limitations

- Form Designer API (TASK-007) lacks tenant authorization — tracked as ENH-001
- 3 pre-existing test failures in DatabaseConnectionTest (H2 vs PostgreSQL incompatibility)

---

# Common Terms

| Term | Definition |
|------|------------|
| Global Form | Created by System Admin, available to all tenants |
| Tenant Form | Created by Tenant Admin, scoped to their tenant |
| Row Filter | Role-based condition that limits which records a user can see |
| Sub-Form | One2many child relationship rendered as a tab within parent form |
| Form Bundle | Assembled JSON response containing form definition + model definition |
