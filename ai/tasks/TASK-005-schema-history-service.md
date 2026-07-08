---
id: TASK-005

title: Implement Schema History Service

type: Feature

status: READY_FOR_TEST

priority: Low

owner: planner

assigned_to:

assigned_branch:

locked: false

created: 2026-07-07

updated: 2026-07-07

started: 2026-07-07

completed: 2026-07-07

estimated_hours: 3

actual_hours: 0.5

parent_prd: PRD-001

prd_version: 1.6.0
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-004

depends_on:
  - TASK-002

blocks: []

labels: [backend, service, audit]

review_required: true

test_required: true

automation_required: false

change_summary: ai/changes/CHANGE-TASK-005.md

test_report:

history:
  - created
  - implemented 2026-07-07 — Developer completed Schema History Service
  - 2026-07-08 — Planning audit: prd_version corrected 1.5.0 → 1.6.0 (metadata synchronization)

---

# Goal

Create a service that records all table schema changes (create table, add/edit/delete column) in `sys_metadata_version` for audit and rollback purposes.

---

# Description

Create `SchemaHistoryService` that logs schema changes and provides history retrieval.

## Methods

### `logChange(UUID tableId, String changeDescription, Map<String, Object> definitionSnapshot)`
Records a new entry in `sys_metadata_version` with:
- Auto-incrementing version number per table
- Change description (e.g., "Added column 'unit_price' (decimal, 15,2)")
- Snapshot of the full table definition JSON at this point
- Current user ID from SecurityContext

### `getHistory(UUID tableId)`
Returns a chronological list of changes for a table

### `getLatestVersion(UUID tableId)`
Returns the most recent version number for a table

---

# Acceptance Criteria

- [ ] Every schema change (create table, add/delete column, modify column, reorder columns) creates a history entry
- [ ] Each entry captures the full table definition snapshot at that point
- [ ] History is retrievable by table ID in chronological order
- [ ] History entries include timestamp and acting user

---

# Technical Notes

- Extend existing `MetadataVersion` entity or create a dedicated service
- Call from `TableDesignerService` after every successful DDL operation
- The definition snapshot should include all columns (not just the changed one)

---

# Files Expected

- `backend/src/main/java/com/erp/core/metadata/service/SchemaHistoryService.java`
