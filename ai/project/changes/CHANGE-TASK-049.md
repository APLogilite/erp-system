---
id: CHANGE-TASK-049

task_id: TASK-049

parent_prd: PRD-005

branch: feature/TASK-049

type: Feature

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-17

completed: 2026-07-17

duration: 1.5 hours (estimated)

related_commits:
  - feat(TASK-049): backend type coercion on save, frontend removes parseInt/parseFloat

related_files:
  - backend/src/main/java/com/erp/core/runtime/service/WindowDataService.java
  - frontend/src/routes/window/WindowPage.tsx

review_required: true

test_required: true

---

# Summary

Added server-side type coercion in `WindowDataService` so the backend accepts raw string values for all fields and converts them to the correct Java types before persisting. The frontend `WindowPage.tsx` `handleSave()` was simplified to send raw form data without `parseInt`/`parseFloat` preprocessing. Coercion handles integer, decimal/numeric, boolean, date, and datetime types. Empty strings are converted to null for all non-string fields.

---

# Scope Verification

- [x] Frontend
- [x] Backend
- [ ] Database
- [ ] Configuration

---

# Business Requirements Implemented

- FR-004: Backend Accepts Raw Values and Coerces Server-Side
  - `WindowDataService.createRecord()` coerces field types before persisting
  - `WindowDataService.updateRecord()` coerces field types before updating
  - Supports: integer, decimal/numeric, boolean, date, datetime
  - Frontend `WindowPage.tsx` removes `parseInt`/`parseFloat` logic from `handleSave()`

---

# Files Modified

| File | Summary |
|------|---------|
| `backend/.../WindowDataService.java` | Added `coerceFieldTypes()` method; calls it in both `createRecord()` and `updateRecord()` |
| `frontend/.../WindowPage.tsx` | Simplified `handleSave()` to send raw `formData` without type coercion loop |

---

# Validation

## Build

PASS — Backend `mvn clean compile` succeeds; Frontend `tsc --noEmit` succeeds

---

## Existing Automated Tests

PASS — All 36 backend tests pass

---

# Breaking Changes

None. Backward compatible — the backend previously expected typed values; it now also accepts strings.
