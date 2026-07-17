---
id: TASK-049

title: Backend Type Coercion on Save

type: Feature

scope: backend

status: COMPLETED

priority: Medium

owner: developer

assigned_to:

assigned_branch: feature/TASK-049

locked: false

created: 2026-07-16

updated: 2026-07-17

started: 2026-07-17

completed: 2026-07-17

estimated_hours: 2

actual_hours:

parent_prd: PRD-005

prd_version: 1.3.0

prd_branch: prd/PRD-005

base_branch:

merge_target:

merge_strategy:

parent_task:

related_tasks:
  - TASK-047

depends_on:
  - TASK-047

blocks: []

labels:
  - backend
  - prd-005

review_required: true

test_required: true

automation_required: false

change_summary: ai/project/changes/CHANGE-TASK-049.md

test_report: ai/project/tests/TEST-TASK-049.md

test_script:

history:
  - created
  - 2026-07-17: activated to READY_FOR_DEV (SE, dep TASK-047 satisfied)
  - 2026-07-17: locked and started IN_DEVELOPMENT (SE)
  - 2026-07-17: merged to PRD branch, READY_FOR_TEST (SE)

---

# Goal

Backend accepts raw string values for all fields and coerces them to the correct type server-side, removing type coercion logic from the frontend.

---

# Description

`WindowPage.tsx:211-218` currently coerces string values to `parseInt`/`parseFloat` before sending data to the backend. The backend should accept raw string values and coerce them on the server side using the column's type metadata from `sys_column` or `sys_window_field`.

Currently the frontend `handleSave()` does:
```javascript
if (field.column.type === 'integer') parsed[field.column.code] = parseInt(val, 10);
if (field.column.type === 'decimal') parsed[field.column.code] = parseFloat(val);
```

This should be removed. The backend's `WindowDataService` or `DynamicCrudService` should handle type conversion.

---

# Acceptance Criteria

- [ ] `WindowDataController.createRecord()` and `updateRecord()` accept raw string/number values
- [ ] `WindowDataService` or `DynamicCrudService` coerces values based on column type before persisting
- [ ] Frontend `WindowPage.tsx` removes `parseInt`/`parseFloat` logic from `handleSave()`
- [ ] All field types work correctly: integer, decimal, boolean, date, datetime, string, text
- [ ] Backend compiles and existing tests pass

---

# Technical Notes

- The `WindowDefinitionAssemblyService` already resolves field metadata — pass the field type info to the save handler
- For `DynamicCrudService`, add a type coercion step before building the SQL UPDATE/INSERT:
  - integer → `Integer.parseInt()`
  - decimal/numeric → `BigDecimal`
  - boolean → parse "true"/"false" or 1/0
  - date → `LocalDate.parse()`
  - datetime → `LocalDateTime.parse()`
- Empty strings should be treated as `null` for non-string fields

---

# Files Expected

- `backend/src/main/java/com/erp/core/runtime/service/WindowDataService.java` — add type coercion layer
- `backend/src/main/java/com/erp/core/runtime/service/DynamicCrudService.java` — add type coercion in save methods
- `frontend/src/routes/window/WindowPage.tsx` — remove parseInt/parseFloat from handleSave()
