---
id: TASK-051

title: Backend Returns RuntimeMetadataBundle Directly

type: Feature

scope: both

status: COMPLETED

priority: High

owner: developer

assigned_to:

assigned_branch: feature/TASK-051

locked: false

created: 2026-07-16

updated: 2026-07-17

started: 2026-07-17

completed: 2026-07-17

estimated_hours: 4

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
  - both
  - prd-005

review_required: true

test_required: true

automation_required: false

change_summary: ai/project/changes/CHANGE-TASK-051.md

test_report: ai/project/tests/TEST-TASK-051.md

test_script:

history:
  - created
  - 2026-07-17: activated to READY_FOR_DEV (SE)
  - 2026-07-17: locked and started IN_DEVELOPMENT (SE)
  - 2026-07-17: merged to PRD branch, READY_FOR_TEST (SE)

---

# Goal

Backend form definition endpoint returns `RuntimeMetadataBundle` directly, eliminating the frontend `formToBundleMapper.ts` transformation layer.

---

# Description

`formToBundleMapper.ts` (130 lines) exists solely to convert the backend's flat `FormDefinition` response into the metadata-driven renderer's `RuntimeMetadataBundle`. The backend's `FormDefinitionAssemblyService` should return the bundle directly in the format the frontend renderer needs.

The mapper currently:
1. Maps field types (`string→TEXT`, `number→NUMBER`, etc.)
2. Builds a `ModelDefinition` from form fields
3. Builds layout sections with proper types
4. Assembles the final `RuntimeMetadataBundle`

All of this should happen server-side. After this change, `formToBundleMapper.ts` is deleted.

---

# Acceptance Criteria

- [ ] Backend `FormDefinitionAssemblyService` assembles `RuntimeMetadataBundle` directly
- [ ] Backend form definition endpoint returns `RuntimeMetadataBundle` format
- [ ] Frontend `RuntimePage.tsx` receives the bundle directly from API response
- [ ] `formToBundleMapper.ts` is deleted
- [ ] `runtimeApi.ts` `fetchFormDefinition()` returns the new format
- [ ] Frontend `DynamicFormRenderer.tsx` works with the new format
- [ ] Backend compiles and existing tests pass

---

# Technical Notes

- The `RuntimeMetadataBundle` shape is defined in `frontend/src/core/metadata/schema/RuntimeMetadataBundle.ts`:
  ```
  { model: ModelDefinition, views: ViewDefinition[], actions: ActionDefinition[],
    permissions: string[], workflow?: WorkflowDefinition }
  ```
- `FormDefinitionAssemblyService` currently returns a flat `FormDefinition` — restructure to build the bundle
- The field type mapping in `formToBundleMapper.ts:23-37` should move to the backend
- Keep the old flat `FormDefinition` endpoint working for backward compatibility (or route through the new one)

---

# Files Expected

- `backend/src/main/java/com/erp/core/runtime/service/FormDefinitionAssemblyService.java` — restructure to produce `RuntimeMetadataBundle`
- `backend/src/main/java/com/erp/core/runtime/controller/RuntimeFormController.java` — update response type
- `frontend/src/core/runtime/api/formToBundleMapper.ts` — DELETE
- `frontend/src/routes/runtime/RuntimePage.tsx` — remove mapper import, read bundle directly
- `frontend/src/core/runtime/api/runtimeApi.ts` — update `FormDefinition` type
