---
id: TASK-009

title: Implement Sub-Form Configuration APIs (Backend)

type: API

status: COMPLETED

priority: High

owner: developer

assigned_to: QA Engineer

assigned_branch: feature/TASK-009

locked: true

created: 2026-07-07

updated: 2026-07-09

started: 2026-07-08

completed: 2026-07-08

estimated_hours: 4

actual_hours: 1.5

parent_prd: PRD-001

prd_version: 1.6.0
prd_branch: prd/PRD-001-dynamic-form-configuration
base_branch: prd/PRD-001-dynamic-form-configuration
merge_target: prd/PRD-001-dynamic-form-configuration
merge_strategy: merge

parent_task:

related_tasks:
  - TASK-007

depends_on:
  - TASK-007

blocks:
  - TASK-013

labels: [backend, api, sub-forms]

review_required: true

test_required: true

automation_required: true

change_summary: ai/changes/CHANGE-TASK-009.md

test_report: ai/tests/TEST-TASK-009.md

history:
  - created
  - 2026-07-08 — Planning audit: demoted READY_FOR_DEV → PLANNED
  - 2026-07-08 — Re-evaluated: restored to READY_FOR_DEV
  - 2026-07-08 — Implementation: added available-relations and reorder endpoints, circular reference check, @PreAuthorize
  - 2026-07-09 — QA verified; all files present; compilation PASS; status: TESTED

---

# Goal

Implement APIs for configuring sub-form tabs on parent forms (FR-014), including providing the list of available one2many relationships from the table definition.

---

# Description

## APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/metadata/forms/{formId}/sub-forms` | List sub-form configurations |
| POST | `/api/metadata/forms/{formId}/sub-forms` | Add sub-form reference |
| PUT | `/api/metadata/forms/{formId}/sub-forms/{subFormId}` | Update sub-form config |
| DELETE | `/api/metadata/forms/{formId}/sub-forms/{subFormId}` | Remove sub-form |
| PUT | `/api/metadata/forms/{formId}/sub-forms/reorder` | Reorder sub-form tabs |
| GET | `/api/metadata/forms/{formId}/available-relations` | Get available one2many relations on the form's table |

## Service Logic

### Available Relations
When designing a form for table A, the endpoint returns all tables that have a `many2one` column pointing to table A. This tells the admin what child relationships are available for sub-forms.

Example: Table `order_line` has column `order_id` (many2one → `order`). So the Order form's available relations include `order_id` referencing `order_line`.

Each available relation includes:
- `relation_code` — the column code on the child table (e.g., `order_id`)
- `child_table_code` — the child table code (e.g., `order_line`)
- `child_table_label` — the child table label
- `existing_form_codes` — forms already created for the child table that could be used

### Add Sub-Form
1. Validate: child form exists, child form's model matches the relation's table
2. Check for circular references (parent cannot reference itself)
3. Save to `sys_form_sub_forms`

---

# Acceptance Criteria

- [ ] Available relations endpoint returns valid one2many candidates
- [ ] Admin can add/remove/reorder sub-form tabs
- [ ] Circular reference is detected and rejected
- [ ] Sub-form references are stored in `sys_form_sub_forms`

---

# Technical Notes

- The available relations query scans `sys_table_columns` for many2one columns referencing the parent table
- Circular reference check: walk the sub-form chain to ensure the new child doesn't eventually lead back to the parent

---

# Files Expected

- `backend/src/main/java/com/erp/core/metadata/controller/FormSubFormController.java`
- `backend/src/main/java/com/erp/core/metadata/service/FormSubFormService.java`
- `backend/src/main/java/com/erp/core/metadata/dto/SubFormConfigRequest.java`
- `backend/src/main/java/com/erp/core/metadata/dto/AvailableRelationResponse.java`
