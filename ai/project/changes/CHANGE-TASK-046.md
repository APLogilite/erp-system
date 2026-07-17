---
id: CHANGE-TASK-046

task_id: TASK-046

parent_prd: PRD-005

branch: feature/TASK-046

type: Feature

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-17

completed: 2026-07-17

duration: 2 hours (estimated)

related_commits:
  - feat(TASK-046): add childTabIds to TabDefinitionResponse, populate server-side, update frontend

related_files:
  - backend/src/main/java/com/erp/core/runtime/dto/window/TabDefinitionResponse.java
  - backend/src/main/java/com/erp/core/runtime/service/WindowDefinitionAssemblyService.java
  - frontend/src/core/runtime/api/runtimeApi.ts
  - frontend/src/routes/window/WindowPage.tsx

review_required: true

test_required: true

---

# Summary

Added `childTabIds: UUID[]` field to `TabDefinitionResponse` DTO and implemented server-side computation in `WindowDefinitionAssemblyService`. The backend now determines parent-child tab relationships by matching `parentColumn` naming conventions against tab table names, following the same logic previously implemented client-side in `WindowPage.tsx:findChildTabs()`. The frontend `WindowPage.tsx` was updated to use `tab.childTabIds` from the API response, removing the client-side `findChildTabs()` function and its fallback logic.

---

# Scope Verification

- [x] Frontend
- [x] Backend
- [ ] Database
- [ ] Configuration

---

# Business Requirements Implemented

- FR-001: Add childTabIds to Tab Definition Response
  - `TabDefinitionResponse` has new `childTabIds: UUID[]` field (empty list if no children)
  - `WindowDefinitionAssemblyService.assembleDefinition()` populates it by scanning all tabs and matching `parentColumn` naming conventions
  - Frontend `WindowPage.tsx` no longer imports or uses `findChildTabs()`
  - Frontend reads `tab.childTabIds` from tab definition directly

---

# Files Added

None.

---

# Files Modified

| File | Summary |
|------|---------|
| `backend/src/main/java/com/erp/core/runtime/dto/window/TabDefinitionResponse.java` | Added `childTabIds: List<UUID>` field with getter/setter, added `ArrayList` import |
| `backend/src/main/java/com/erp/core/runtime/service/WindowDefinitionAssemblyService.java` | Added childTabIds computation in `assembleDefinition()` after all tabs are assembled; added `HashMap`/`Map` imports |
| `frontend/src/core/runtime/api/runtimeApi.ts` | Added `childTabIds: string[]` to `WindowTabDefinition` interface |
| `frontend/src/routes/window/WindowPage.tsx` | Replaced `findChildTabs()` function and its two usages with `currentLevelTab.childTabIds ?? []` |

---

# Files Removed

None.

---

# Script Updates

No schema changes — no DDL files to update.

- [ ] `ai/project/schema/` updated (if schema changed)
- [ ] Verification scripts updated (if applicable)

---

# Database Changes

None.

---

# API Changes

## Updated Endpoints

`GET /api/v1/runtime/windows/{windowName}/definition`

Response now includes `childTabIds: string[]` on each tab definition object.

## Response Changes

Tab definition response now includes:
```json
{
  "id": "uuid",
  "name": "Product",
  "parentColumn": null,
  "childTabIds": ["uuid-of-child-tab"],
  "table": { "id": "uuid", "name": "md_product", "label": "Product" },
  "fields": [...]
}
```

---

# Routes

No route changes.

---

# Classes Added

None.

---

# Classes Updated

| Class | Summary |
|--------|---------|
| `TabDefinitionResponse` | Added `childTabIds: List<UUID>` field |
| `WindowDefinitionAssemblyService` | Added childTabIds computation logic in `assembleDefinition()` |

---

# Methods Added

None.

---

# Methods Updated

| Class | Method | Summary |
|--------|--------|---------|
| `TabDefinitionResponse` | `getChildTabIds()` | New getter for childTabIds |
| `TabDefinitionResponse` | `setChildTabIds()` | New setter for childTabIds |
| `WindowDefinitionAssemblyService` | `assembleDefinition()` | Added post-processing loop to compute childTabIds for each tab |

---

# Models

None.

---

# Services

None.

---

# Repositories

None.

---

# DTOs

| DTO | Change |
|-----|--------|
| `TabDefinitionResponse` | Added `childTabIds` field |

---

# Requests

None.

---

# Policies

None.

---

# Events

None.

---

# Jobs

None.

---

# Configuration

None.

---

# Dependencies

None.

---

# Validation

## Build

PASS

Backend `mvn clean compile` succeeds. Frontend `tsc --noEmit` succeeds.

---

## Lint

PASS

Pre-existing lint warnings remain (unrelated to this change).

---

## Static Analysis

PASS

---

## Existing Automated Tests

PASS

All 36 backend tests pass (3 DatabaseConnectionTest + 13 PasswordServiceTest + 6 PermissionCacheTest + 9 PermissionEvaluatorTest + 5 JwtProviderTest).

---

# Manual Verification

- Verified `TabDefinitionResponse.java` compiles with the new field
- Verified childTabIds computation logic matches the original frontend `findChildTabs()` algorithm
- Verified frontend typecheck passes with updated `WindowTabDefinition` interface
- Verified `WindowPage.tsx` no longer references `findChildTabs()`
- Backward compatible — `parentColumn` field remains on the response

---

# Breaking Changes

None. The `parentColumn` field is kept on the response for backward compatibility. `childTabIds` is an additional field.

---

# Known Issues

None.

---

# Future Improvements

Consider whether the `parentColumn` field can be removed in a future cleanup once all consumers have migrated to `childTabIds`.

---

# Developer Notes

The childTabIds computation follows the exact same algorithm as the removed frontend `findChildTabs()`:
1. For each tab, check if other tabs have `parentColumn` ending with `_id`
2. Strip `_id` from `parentColumn` (e.g., `window_id` → `window`)
3. Check if the candidate parent's table name ends with `_` + the stripped stub (e.g., `sys_window` ends with `_window`)

This ensures identical behavior before and after the change.

---

# QA Handoff

**Suggested test focus:**
- Open a window with parent-child tab relationships (e.g., Product window with pricing tabs)
- Verify child tabs appear correctly in accordion panels
- Verify drill-down navigation works (clicking a child record opens the correct sub-form)
- Verify new record creation in child tabs sets the correct parent FK

**Potential risk areas:**
- If a tab has no table info set, childTabIds will be empty
- The naming convention matching relies on `parentColumn` ending with `_id` — edge case tables with non-standard naming may not match

**Important edge cases:**
- Window with a single tab (no children) — childTabIds should be empty
- Window where tabs have no parentColumn set — no child relationships
- Window with deeply nested tabs (grandchildren) — all relationships should be captured
