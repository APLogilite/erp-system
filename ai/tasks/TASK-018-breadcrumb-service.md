---
id: TASK-018

title: Implement Breadcrumb & Parent Context Service (Backend)

type: Feature

status: TESTED

priority: Medium

owner: developer

assigned_to: QA Engineer

assigned_branch: feature/TASK-018

locked: true

created: 2026-07-07

updated: 2026-07-09

started: 2026-07-08

completed: 2026-07-08

estimated_hours: 4

actual_hours:

parent_prd: PRD-001

prd_version: 1.6.0
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-016
  - TASK-017

depends_on:
  - TASK-016

blocks: []

labels: [backend, service, breadcrumb]

review_required: true

test_required: true

automation_required: true

change_summary: ai/changes/CHANGE-TASK-018.md

test_report: ai/tests/TEST-TASK-018.md

history:
  - created
  - 2026-07-08 — Planning audit: status → READY_FOR_TEST. Implementation found on disk (BreadcrumbService.java). No change summary found — Developer should create one.
  - 2026-07-08 — Documentation audit: created CHANGE-TASK-018.md (change_summary restored)

---

# Goal

Implement a service that builds the breadcrumb trail and parent context for sub-form record navigation.

---

# Description

Create `BreadcrumbService` that, given a form code and record ID, walks up the sub-form chain to build the full breadcrumb path.

## Methods

### `buildBreadcrumb(String currentFormCode, UUID currentRecordId, UUID tenantId)`
1. Load current form definition
2. Check if the current form is a sub-form of any other form (query `sys_form_sub_forms` WHERE child_form_code = ?)
3. If parent found:
   - Load parent form definition
   - Query the child table to find the parent record ID (using the relation column)
   - Add parent to breadcrumb entry
   - Recurse up
4. Build the full breadcrumb array from root to current

Returns: `List<BreadcrumbEntry>` where each entry has:
```
{ formCode, recordId (nullable), label }
```

### `getParentContext(String currentFormCode, UUID currentRecordId, UUID tenantId)`
Returns the immediate parent context:
```
{ formCode, recordId, label }
```

### `getRecordLabel(String tableName, UUID recordId, UUID tenantId)`
Returns a human-readable label for a record (e.g., "#1024" or the record's name field if it has one)

---

# Acceptance Criteria

- [ ] Breadcrumb is correctly built from the sub-form chain
- [ ] Breadcrumb shows the full path from root form to current record
- [ ] Parent context is correctly identified
- [ ] Works for arbitrary nesting depth (Order → Line → Tax → ...)
- [ ] Handles root-level forms (no parent) — returns empty breadcrumb
- [ ] Record labels are human-readable

---

# Technical Notes

- The parent lookup queries the child table to find the parent foreign key: `SELECT {relation_code} FROM {child_table} WHERE id = ?`
- Record labels can use a `name` or `code` column if it exists, otherwise fall back to a shortened UUID or "#" prefix with a sequence
- Cache breadcrumb results briefly (short TTL) since they don't change frequently

---

# Files Expected

- `backend/src/main/java/com/erp/core/runtime/service/BreadcrumbService.java`
- `backend/src/main/java/com/erp/core/runtime/dto/BreadcrumbEntry.java`
- `backend/src/main/java/com/erp/core/runtime/dto/ParentContext.java`
