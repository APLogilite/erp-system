---
id: CHANGE-TASK-050

task_id: TASK-050

parent_prd: PRD-005

branch: feature/TASK-050

type: Feature

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-17

completed: 2026-07-17

duration: 2 hours (estimated)

related_commits:
  - feat(TASK-050): guarantee _display on every record, frontend removes fallback logic

related_files:
  - backend/src/main/java/com/erp/core/runtime/service/WindowDataService.java
  - frontend/src/routes/window/WindowPage.tsx
  - frontend/src/core/runtime/components/DynamicListView.tsx
  - frontend/src/core/runtime/hooks/useForm.ts
  - frontend/src/core/runtime/hooks/index.ts

review_required: true

test_required: true

---

# Summary

Strengthened the `_display` resolution in `WindowDataService` to guarantee every record has a `_display` key. The backend now uses a fallback chain: configured display column → first non-id column value → record UUID. Frontend was cleaned up: `WindowPage.tsx` simplified `getDisplayVal()` to use `_display` directly, `DynamicListView.tsx` removed `formatCellValue()`, and `useForm.ts` removed `getRecordLabel()`.

---

# Scope Verification

- [x] Frontend
- [x] Backend
- [ ] Database
- [ ] Configuration

---

# Business Requirements Implemented

- FR-005: Backend Returns _display on Every Record
  - Added `guaranteeDisplayOnRecords()` method with fallback chain
  - Called from `resolveDisplayNames()` after FK resolution
  - Frontend `WindowPage.tsx` simplified to use `rec._display` directly
  - Frontend `DynamicListView.tsx` removed `formatCellValue()`
  - Frontend `useForm.ts` removed `getRecordLabel()`
  - Barrel export in `hooks/index.ts` updated

---

# Files Modified

| File | Summary |
|------|---------|
| `backend/.../WindowDataService.java` | Added `guaranteeDisplayOnRecords()` with fallback chain; called during FK resolution and standalone |
| `frontend/.../WindowPage.tsx` | Simplified `getDisplayVal()` to `(rec?._display as string) ?? ''` |
| `frontend/.../DynamicListView.tsx` | Replaced `formatCellValue()` with simple `String(rec[f.columnCode] ?? '')` |
| `frontend/.../useForm.ts` | Removed `getRecordLabel()` function |
| `frontend/.../hooks/index.ts` | Removed `getRecordLabel` from exports |

---

# Validation

## Build

PASS — Backend `mvn clean compile` succeeds; Frontend `tsc --noEmit` succeeds

---

## Existing Automated Tests

PASS — All 36 backend tests pass
