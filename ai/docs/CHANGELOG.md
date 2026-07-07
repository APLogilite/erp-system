---
document: CHANGELOG
version: 1.0.0
status: ACTIVE
owner: Planner
last_updated: 2026-07-07
---

# Changelog

## 2026-07-07

### Added

- **PRD-001** — Dynamic Form Configuration System (v1.1.0, REVIEW)
  - Table Designer (System Admin)
  - Form Designer (System Admin + Tenant Admin)
  - Runtime Form Renderer (All Users)
  - Global forms + per-tenant role assignment
  - Tenant data isolation for shared forms
  - Header form search (Ctrl+K/Cmd+K)
  - Consolidated single-request bundle API (form + model + records in one call)
  - Multi-level sub-forms with breadcrumb navigation
  - Normalized storage (all metadata in relational tables, no JSONB for mutable data)
  - Two-request loading pattern (definition cached + data fresh)
  - Frontend useForm() hook abstraction
  - Form toolbar (Create, Save, Discard, Refresh, Delete, Previous/Next)
  - Keyboard shortcuts (Ctrl+S, Alt+arrows, F5, Escape)
  - Record-to-record navigation with "Record X of Y" context
  - Role-based row-level data access (tenant isolation + per-role row filters)
- **PRD-001** (v1.6.0 APPROVED)
- **Generated 27 implementation tasks** (TASK-001 to TASK-027)
  - Phase 1: Database migrations + JPA entities (TASK-001, TASK-002)
  - Phase 2: Table Designer backend + frontend (TASK-003 to TASK-006)
  - Phase 3: Form Designer backend (TASK-007 to TASK-010)
  - Phase 4: Form Designer frontend (TASK-011 to TASK-014)
  - Phase 5: Runtime backend (TASK-015 to TASK-018)
  - Phase 6: Runtime frontend (TASK-019 to TASK-027)
