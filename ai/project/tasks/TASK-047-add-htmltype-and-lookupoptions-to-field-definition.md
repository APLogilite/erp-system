---
id: TASK-047

title: Add htmlType and lookupOptions to FieldDefinitionResponse

type: Feature

scope: both

status: READY_FOR_DEV

priority: High

owner: developer

assigned_to:

assigned_branch:

locked: false

created: 2026-07-16

updated: 2026-07-17

started:

completed:

estimated_hours: 4

actual_hours:

parent_prd: PRD-005

prd_version: 1.3.0

prd_branch: prd/PRD-005

base_branch:

merge_target:

merge_strategy:

parent_task:

related_tasks:
  - TASK-046
  - TASK-048

depends_on: []

blocks: []

labels:
  - both
  - dto
  - prd-005

review_required: true

test_required: true

automation_required: false

change_summary:

test_report:

test_script:

history:
  - created
  - 2026-07-17: activated to READY_FOR_DEV (SE)

---

# Goal

Add `htmlType` and `lookupOptions` to the field definition response so the frontend can render fields directly without mapping types or firing parallel lookup queries.

---

# Description

The `FieldDefinitionResponse.ColumnInfo` currently has `type` (string/integer/decimal/etc.) but no `htmlType` for direct rendering. Add `htmlType: "text" | "number" | "date" | "checkbox"` populated by the backend's field type mapping logic (currently in frontend `FormFieldRenderer.tsx:34-48`). Also add `lookupOptions: [{id, label}]` for fields with `relationTable`, populated by querying the related table and resolving display columns.

Currently the frontend:
1. Maps backend types to HTML input types via `mapInputType()` (FormFieldRenderer.tsx:34-48)
2. Discovers lookup tables by scanning all fields for `relationTable` (WindowPage.tsx:93-113)
3. Fires N parallel API calls to fetch lookup data (WindowPage.tsx:101-108)

All of this should be in the backend.

---

# Acceptance Criteria

- [ ] `ColumnInfo` has new `htmlType: String` field
- [ ] Backend `WindowDefinitionAssemblyService` maps field types (string→text, integer→number, decimal→number, date→date, boolean→checkbox, datetime→datetime-local, enum→select, many2one→select)
- [ ] `ColumnInfo` has new `lookupOptions: List<Map>` field (list of `{id, label}` objects)
- [ ] Backend populates `lookupOptions` by querying `SELECT id, display_column AS label FROM relation_table` when `relationTable` is set (limit to max 100 results)
- [ ] Frontend `FormFieldRenderer.tsx` removes `mapInputType()`, uses `field.column.htmlType` directly
- [ ] Frontend `WindowPage.tsx` removes lookup query discovery and parallel fetch calls (lines 93-148)
- [ ] Frontend `WindowPage.tsx` uses `options` from field definition for dropdown rendering
- [ ] Backend compiles and existing tests pass

---

# Technical Notes

- Reuse the `WindowDataService.resolveFkDisplayNames()` logic for populating `lookupOptions` — it already has the SQL pattern for resolving display columns
- For `lookupOptions`, query: `SELECT id, "<display_column>" AS label FROM "<relation_table>" ORDER BY <display_column> LIMIT 100`
- The field type → htmlType mapping should mirror the existing pattern in `FormFieldRenderer.tsx`:
  - integer/number → "number"
  - decimal → "number" (with step="0.01")
  - date → "date"
  - datetime → "datetime-local"
  - boolean → "checkbox"
  - enum → "select" (with options from enumOptions)
  - many2one → "select" (with options from lookupOptions)
  - text → "textarea"
  - default → "text"

---

# Files Expected

- `backend/src/main/java/com/erp/core/runtime/dto/window/FieldDefinitionResponse.java` — add `htmlType`, `lookupOptions` to `ColumnInfo`
- `backend/src/main/java/com/erp/core/runtime/service/WindowDefinitionAssemblyService.java` — populate both fields during field assembly
- `backend/src/main/java/com/erp/core/runtime/service/WindowDataService.java` — reuse FK display resolution for lookup options
- `frontend/src/routes/window/WindowPage.tsx` — remove lookup queries, use field options
- `frontend/src/core/runtime/api/runtimeApi.ts` — update `ColumnInfo` and `WindowFieldDefinition` interfaces
- `frontend/src/core/runtime/components/FormFieldRenderer.tsx` — remove `mapInputType()`, use `htmlType`
