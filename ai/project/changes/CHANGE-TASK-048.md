---
id: CHANGE-TASK-048

task_id: TASK-048

parent_prd: PRD-005

branch: feature/TASK-048

type: Feature

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-17

completed: 2026-07-17

duration: 1 hour (estimated)

related_commits:
  - feat(TASK-048): backend pre-filters non-displayed fields, frontend removes client-side filtering

related_files:
  - backend/src/main/java/com/erp/core/runtime/service/WindowDefinitionAssemblyService.java
  - frontend/src/routes/window/WindowPage.tsx
  - frontend/src/core/runtime/components/DynamicListView.tsx

review_required: true

test_required: true

---

# Summary

Moved field filtering logic from frontend to backend. The backend `WindowDefinitionAssemblyService.assembleTab()` now skips fields where `isDisplayed` is `false`. The frontend `WindowPage.tsx` no longer uses `getDisplayedFields()` helper — it reads fields directly from the API response. The old `DynamicListView.tsx` also had its client-side filter/sort/slice removed. Fields are already sorted by `seq_no` from the backend query (`findByTabIdOrderBySeqNoAsc`), so frontend sorting is redundant.

---

# Scope Verification

- [x] Frontend
- [x] Backend
- [ ] Database
- [ ] Configuration

---

# Business Requirements Implemented

- FR-003: Backend Returns Pre-Filtered, Pre-Sorted Fields
  - Backend excludes fields where `isDisplayed=false` from the response
  - Frontend `WindowPage.tsx` removed `getDisplayedFields()` — uses `currentLevelTab.fields` directly
  - Frontend `DynamicListView.tsx` removed the `.filter(f => f.visible).sort(...).slice(0, 8)` chain
  - Backend compiles and existing tests pass

---

# Files Modified

| File | Summary |
|------|---------|
| `backend/.../WindowDefinitionAssemblyService.java` | Added `Boolean.FALSE.equals(field.getIsDisplayed())` check to skip non-displayed fields |
| `frontend/.../WindowPage.tsx` | Removed `getDisplayedFields()` function; replaced both usages with `.fields` directly |
| `frontend/.../DynamicListView.tsx` | Removed `.filter(f => f.visible).sort(...).slice(0, 8)` — use `formDefinition.fields` directly |

---

# Validation

## Build

PASS — Backend `mvn clean compile` succeeds; Frontend `tsc --noEmit` succeeds

---

## Existing Automated Tests

PASS — All 36 backend tests pass

---

# Breaking Changes

None. Backend already sorts by seq_no, and frontend was doing the same sort redundantly. Non-displayed fields were already hidden by the frontend filter — this just moves that logic server-side.
