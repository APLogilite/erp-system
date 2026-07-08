---
document: CHANGELOG
version: 1.0.0
status: ACTIVE
owner: Planner
last_updated: 2026-07-08
---

# Changelog

## 2026-07-08 (Re-evaluation)

### Correction
- The 2026-07-07 Workflow Sync CHANGELOG entry incorrectly stated "All dependency checks now require COMPLETED (not READY_FOR_TEST)". The actual WORKFLOW.md (lines 259, 281, 285) states **"READY_FOR_TEST/COMPLETED"** — either status satisfies dependencies. The CHANGELOG discrepancy has been corrected.

### Task Restorations (PLANNED → READY_FOR_DEV)
Under the correct WORKFLOW rule (READY_FOR_TEST satisfies dependencies), the following 7 tasks were restored to READY_FOR_DEV:

- **TASK-004** — Deps TASK-002 (READY_FOR_TEST), TASK-003 (READY_FOR_TEST) ✓
- **TASK-008** — Dep TASK-007 (READY_FOR_TEST) ✓
- **TASK-009** — Dep TASK-007 (READY_FOR_TEST) ✓
- **TASK-010** — Dep TASK-007 (READY_FOR_TEST) ✓
- **TASK-011** — Dep TASK-007 (READY_FOR_TEST) ✓
- **TASK-016** — Deps TASK-007 (READY_FOR_TEST), TASK-015 (READY_FOR_TEST) ✓
- **ENH-001** — Dep TASK-007 (READY_FOR_TEST) ✓

### State After Re-evaluation
- **7 tasks READY_FOR_DEV** — Available for Software Engineer immediately
- **6 tasks READY_FOR_TEST** — Awaiting QA Engineer
- **14 tasks PLANNED** — Awaiting downstream dependency completion
- **0 tasks COMPLETED** — QA has not yet begun

---

## 2026-07-08 (Planning Audit — Framework Migration)

### Summary
Full planning audit executed to migrate all planning artifacts to the current planning framework. PROJECT_BOARD.md was missing and recreated. Task dependencies and metadata were reviewed for consistency.

### NOTE: The demotions performed in this audit were subsequently reversed in the 2026-07-08 Re-evaluation above. See that entry for the correct, final state.

### PROJECT_BOARD
- **Created** `ai/PROJECT_BOARD.md` with full PRD, task, enhancement, and statistics tracking

### Task Status Corrections (READY_FOR_DEV → PLANNED)
The following 7 tasks were demoted from READY_FOR_DEV to PLANNED because their dependencies are at READY_FOR_TEST (not COMPLETED). The current WORKFLOW requires dependencies to reach COMPLETED before dependent tasks can activate.

- **TASK-004** → PLANNED (depends on TASK-002, TASK-003 — both READY_FOR_TEST)
- **TASK-008** → PLANNED (depends on TASK-007 — READY_FOR_TEST)
- **TASK-009** → PLANNED (depends on TASK-007 — READY_FOR_TEST)
- **TASK-010** → PLANNED (depends on TASK-007 — READY_FOR_TEST)
- **TASK-011** → PLANNED (depends on TASK-007 — READY_FOR_TEST)
- **TASK-016** → PLANNED (depends on TASK-007, TASK-015 — both READY_FOR_TEST)
- **ENH-001** → PLANNED (depends on TASK-007 — READY_FOR_TEST)

### Metadata Corrections
- **TASK-001** — prd_version: 1.5.0 → 1.6.0 (implementation scope already covered 1.6.0 requirements per change summary)
- **TASK-002** — prd_version: 1.5.0 → 1.6.0 (same)
- **TASK-005** — prd_version: 1.5.0 → 1.6.0 (same)

### State After Migration
- **0 tasks READY_FOR_DEV** — No dependencies are COMPLETED yet; development blocked until QA completes
- **6 tasks READY_FOR_TEST** — Awaiting QA Engineer (TASK-001, TASK-002, TASK-003, TASK-005, TASK-007, TASK-015)
- **21 tasks PLANNED** — All fully defined, awaiting dependency activation
- **1 enhancement PLANNED** — ENH-001 awaiting TASK-007 COMPLETED

### Known Issue (Flagged, Not Fixed)
- **PRD-001** FR-014 identifier is used twice (Sub-Forms section and Dynamic Form Rendering section). Documentation inconsistency — does not affect implementation. Should be resolved in next PRD version update.

### Verified
- All 27 tasks reference `parent_prd: PRD-001` ✅
- All tasks have acceptance criteria ✅
- All tasks have dependencies defined ✅
- All task Git workflow fields consistent ✅
- No circular dependencies ✅
- No BLOCKED or CANCELLED tasks ✅
- No failure reports ✅
- No Bug tasks (implementation not yet QA-tested) ✅

---

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

## 2026-07-07 (Workflow Sync)

### WORKFLOW.md Updated
- New task status `PLANNED` added between `PLANNING` and `READY_FOR_DEV`
- Automatic task activation: when a task reaches COMPLETED, dependent PLANNED tasks auto-advance to READY_FOR_DEV
- PROJECT_BOARD.md is now the single source of truth for execution
- All dependency checks now require READY_FOR_TEST or COMPLETED (either satisfies dependencies)
- Clearer ownership matrix per status

### Synchronization Actions
- **PROJECT_BOARD.md** rebuilt (v1.1.0) with correct PLANNED status
- **24 tasks** moved from PLANNING → PLANNED (all fully defined, awaiting dependencies)
- **3 tasks** remain at READY_FOR_TEST (TASK-001, TASK-002, TASK-005 — implemented by Developer)
- **prd_version** updated from 1.5.0 → 1.6.0 on all 24 PLANNED tasks (PRD was updated)
- **0 tasks** at READY_FOR_DEV (no dependencies are COMPLETED yet)
- **0 tasks** at PLANNING (no tasks are incomplete in their definition)

## 2026-07-07 (Sync)

### Updated
- **TASK-001, TASK-002** → READY_FOR_TEST (Developer completed implementation)
- **TASK-003, TASK-005, TASK-007, TASK-015** → READY_FOR_DEV (dependencies satisfied)
- Created **PROJECT_BOARD.md** with full task tracking

## 2026-07-07 (Planning Audit)

### Updated
- **TASK_TEMPLATE.md** — Added `PLANNED`, `TESTING`, `TESTED` statuses; added git workflow fields (`prd_branch`, `base_branch`, `merge_target`, `merge_strategy`)
- **PROJECT_MEMORY.md** — Filled with project decisions, architecture notes, known limitations
- **PROJECT_BOARD.md** — Added ENH-001 to Phase 4 and Summary

### Corrected
- **TASK-003** — Removed duplicate `PLANNED` status line; added `started`, `completed`, `actual_hours`
- **TASK-005** — Added `actual_hours: 0.5`
- **TASK-007** — Removed duplicate `PLANNED` status line; added `actual_hours: 0.5`
- **TASK-015** — Added `actual_hours: 0.5`

### Created
- **ENH-001** — Enhancement for Form Designer Tenant Authorization (recreated from lost filesystem state)

### Verified (No Changes Needed)
- **TASK-001, TASK-002** — Frontmatter complete, matches template, change summaries exist ✅
- **TASK-004, TASK-006, TASK-008-014, TASK-016-027** — All at `PLANNED` with correct `prd_version: 1.6.0` ✅
- All tasks reference `parent_prd: PRD-001` ✅
- All tasks have acceptance criteria ✅
- All tasks have dependencies defined ✅
- No BLOCKED or CANCELLED tasks ✅
- No failure reports exist ✅
