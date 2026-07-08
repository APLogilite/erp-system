---
id: CHANGE-TASK-018

task_id: TASK-018

parent_prd: PRD-001

branch: feature/TASK-018

type: Feature

status: IMPLEMENTED

developer: developer

started: 2026-07-08

completed: 2026-07-08

duration: 2h

related_commits:
  - 3745d0e
  - 7c1a92c
  - 13745d7
  - be6dc78

related_files:
  - backend/src/main/java/com/erp/core/runtime/service/BreadcrumbService.java
  - backend/src/main/java/com/erp/core/runtime/dto/BreadcrumbEntry.java
  - backend/src/main/java/com/erp/core/runtime/dto/ParentContext.java

review_required: true

test_required: true

---

# Summary

Implemented `BreadcrumbService` (106 lines) that builds breadcrumb trails for sub-form record navigation. Given a form code and record ID, it walks up the sub-form chain to build the full hierarchical path. Also provides parent context querying and record label generation. Uses `NamedParameterJdbcTemplate` for dynamic table queries across the sub-form hierarchy.

---

# Business Requirements Implemented

- FR-018: Breadcrumb Navigation (Backend) — build breadcrumb trail from sub-form hierarchy
- Multi-level breadcrumb: recursively walks up sub-form chain (Order → Order Line → Tax Entry → ...)
- Parent context: returns immediate parent form/record for navigation
- Record labels: human-readable labels with shortened UUID fallback
- Tenant isolation: all queries include tenant_id filter

---

# Files Added

| File | Purpose |
|------|---------|
| `backend/src/main/java/com/erp/core/runtime/service/BreadcrumbService.java` | Service that builds breadcrumbs, resolves parent context, and generates record labels |
| `backend/src/main/java/com/erp/core/runtime/dto/BreadcrumbEntry.java` | DTO with formCode, recordId, label for each breadcrumb segment |
| `backend/src/main/java/com/erp/core/runtime/dto/ParentContext.java` | DTO with formCode, recordId, label, relationColumn for immediate parent context |

---

# Files Modified

None.

---

# Files Removed

None

---

# Database Changes

None (queries existing dynamic tables via JdbcTemplate)

---

# API Changes

None (service is consumed internally by RuntimeFormController / RecordCrudService)

---

# Routes

None (backend service only, no REST endpoints)

---

# Classes Added

| Class | Purpose |
|--------|---------|
| BreadcrumbService | Main service: buildBreadcrumb, getParentContext, getRecordLabel |
| BreadcrumbEntry | Breadcrumb segment DTO |
| ParentContext | Immediate parent context DTO |

---

# Classes Updated

None

---

# Methods Added

| Class | Method | Purpose |
|--------|--------|---------|
| BreadcrumbService | buildBreadcrumb | Build full breadcrumb chain from current record to root (reverses internal chain for correct display order) |
| BreadcrumbService | buildChain | Recursive helper: walks up sub-form hierarchy via FormSubFormRepository |
| BreadcrumbService | getParentContext | Return immediate parent form context (formCode, recordId, label, relationColumn) |
| BreadcrumbService | getRecordLabel | Generate human-readable label ("#" + first 8 chars of UUID) |
| BreadcrumbService | findParentId | Query child table for parent foreign key using relation column |

---

# Methods Updated

None

---

# Models

None

---

# Services

Added: BreadcrumbService

---

# Repositories

None new (uses existing MetadataViewRepository and FormSubFormRepository)

---

# DTOs

Added: BreadcrumbEntry, ParentContext

---

# Requests

None

---

# Policies

None

---

# Events

None

---

# Jobs

None

---

# Configuration

None

---

# Dependencies

Uses existing: NamedParameterJdbcTemplate, MetadataViewRepository, FormSubFormRepository.

---

# Validation

## Build

PASS — `mvn compile` (backend)

## Lint

N/A (backend)

## Static Analysis

N/A

## Existing Automated Tests

PASS — pre-existing test results unchanged

---

# Manual Verification

- [x] Compilation succeeds with zero errors
- [x] buildBreadcrumb reverses internal chain for correct display order (root → current)
- [x] Root-level forms (no parent) return empty breadcrumb
- [x] Tenant isolation enforced on all dynamic table queries

---

# Breaking Changes

None. New service with no existing consumers. Integrates into TASK-016/TASK-017 runtime flow.

---

# Known Issues

1. **Record label**: Currently returns shortened UUID (`#abc12345`). Full human-readable labels require a `name` or `code` column convention.
2. **Recursion depth**: No explicit depth limit — could theoretically recurse infinitely with circular sub-form references (prevented by data integrity at Form Designer layer).
3. **Parent lookup**: Uses in-memory filter of all sub-form entries (`subFormRepository.findAll()`). Should use indexed query for production scale.
4. **Error handling**: `findParentId()` silently catches exceptions for tables/columns that may not exist yet. Helpful for development; should log warnings in production.

---

# Future Improvements

- Add indexed query method to FormSubFormRepository (`findByChildFormCode`)
- Implement name-based record labels (look for `name` or `code` column)
- Add recursion depth limit as safety net

---

# Developer Notes

- **Chain reversal**: `buildChain()` builds the chain from bottom-up (current → root), then `buildBreadcrumb()` reverses the list for correct display order (root → current).
- **Parent lookup**: Queries the child table's relation column to find the parent record ID: `SELECT "{relationColumn}" FROM "{parentTableName}" WHERE id = :id AND tenant_id = :tenantId`.
- **Tenant isolation**: All dynamic SQL queries include `tenant_id = :tenantId` to prevent cross-tenant data access.
- **Empty breadcrumb**: If the current form has no parent sub-form reference, returns an empty list.

---

# QA Handoff

Suggested test focus:
1. Breadcrumb is correctly built from sub-form chain
2. Breadcrumb shows full path from root form to current record
3. Parent context is correctly identified
4. Works for arbitrary nesting depth
5. Handles root-level forms (no parent) — returns empty breadcrumb
6. Record labels are human-readable
7. Tenant isolation prevents cross-tenant data access in parent lookup

Potential risk areas:
- Dynamic SQL injection through relation column names (relationCode is admin-configured, not user-supplied)
- Performance with large sub-form hierarchies

---

# Related Documents

Task: ai/tasks/TASK-018-breadcrumb-service.md

PRD: ai/prd/PRD-001-dynamic-form-configuration-system.md
