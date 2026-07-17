---
id: CHANGE-TASK-051

task_id: TASK-051

parent_prd: PRD-005

branch: feature/TASK-051

type: Feature

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-17

completed: 2026-07-17

duration: 3 hours (estimated)

related_commits:
  - feat(TASK-051): backend returns RuntimeMetadataBundle directly, delete formToBundleMapper.ts

related_files:
  - backend/.../service/FormDefinitionAssemblyService.java
  - backend/.../controller/RuntimeFormController.java
  - frontend/src/core/runtime/api/runtimeApi.ts
  - frontend/src/routes/runtime/RuntimePage.tsx
  - DELETED frontend/src/core/runtime/api/formToBundleMapper.ts

review_required: true

test_required: true

---

# Summary

Added a new backend endpoint `GET /api/v1/runtime/forms/{formCode}/bundle` that returns a `RuntimeMetadataBundle` directly, eliminating the need for the frontend `formToBundleMapper.ts` transformation layer. The new `FormDefinitionAssemblyService.assembleBundle()` method builds the bundle structure (model + views + layout sections with field references) using the same underlying data as the existing `assembleDefinition()`. The frontend `RuntimePage.tsx` was updated to use the new endpoint, and `formToBundleMapper.ts` was deleted (130 lines removed).

---

# Scope Verification

- [x] Frontend
- [x] Backend
- [ ] Database
- [ ] Configuration

---

# Business Requirements Implemented

- FR-006: Backend Returns RuntimeMetadataBundle Directly
  - Backend `FormDefinitionAssemblyService.assembleBundle()` assembles RuntimeMetadataBundle
  - New endpoint: `GET /api/v1/runtime/forms/{formCode}/bundle`
  - Frontend `formToBundleMapper.ts` deleted (130 lines removed)
  - Frontend `runtimeApi.ts` — added `fetchFormBundle()` function
  - Frontend `RuntimePage.tsx` — uses `fetchFormBundle` directly, no mapper import

---

# Files Added

None.

---

# Files Modified

| File | Summary |
|------|---------|
| `backend/.../FormDefinitionAssemblyService.java` | Added `assembleBundle()` method (builds Map-based RuntimeMetadataBundle) and `mapApiTypeToSchemaType()` helper |
| `backend/.../RuntimeFormController.java` | Added `GET /{formCode}/bundle` endpoint |
| `frontend/.../runtimeApi.ts` | Added `fetchFormBundle()` function |
| `frontend/.../RuntimePage.tsx` | Uses `fetchFormBundle` directly, removed mapper import |

---

# Files Removed

| File | Reason |
|------|--------|
| `frontend/src/core/runtime/api/formToBundleMapper.ts` | Replaced by backend `assembleBundle()` |

---

# Validation

## Build

PASS — Backend `mvn clean compile` succeeds; Frontend `tsc --noEmit` succeeds

---

## Existing Automated Tests

PASS — All 36 backend tests pass
