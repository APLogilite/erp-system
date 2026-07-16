---
id: TASK-053

title: Backend Guarantees Non-Empty Sections

type: Feature

scope: backend

status: PLANNING

priority: Low

owner: developer

assigned_to:

assigned_branch:

locked: false

created: 2026-07-16

updated: 2026-07-16

started:

completed:

estimated_hours: 1

actual_hours:

parent_prd: PRD-005

prd_version: 1.3.0

prd_branch: prd/PRD-005

base_branch:

merge_target:

merge_strategy:

parent_task:

related_tasks: []

depends_on: []

blocks: []

labels:
  - backend
  - prd-005

review_required: true

test_required: true

automation_required: false

change_summary:

test_report:

test_script:

history:
  - created

---

# Goal

Backend always returns at least one section in form definitions, removing frontend fallback section creation logic.

---

# Description

`DynamicFormRenderer.tsx:66-78` creates a fallback default section when the backend returns empty sections. The backend should always return at least one section with all fields. This eliminates the fallback logic from the frontend.

---

# Acceptance Criteria

- [ ] Backend form definition endpoint guarantees `sections` is non-empty
- [ ] If no sections exist in the database, backend auto-generates a single section containing all fields
- [ ] Frontend `DynamicFormRenderer.tsx` removes the fallback code (lines 66-78)
- [ ] Backend compiles and existing tests pass

---

# Technical Notes

- In `FormDefinitionAssemblyService`, after loading sections, check if empty
- If empty, create a single default section: code="default", label=formName, columns=2, fieldIds=allFields
- This mirrors the existing frontend fallback logic exactly

---

# Files Expected

- `backend/src/main/java/com/erp/core/runtime/service/FormDefinitionAssemblyService.java` — guarantee at least one section
- `frontend/src/core/runtime/components/DynamicFormRenderer.tsx` — remove fallback section creation
