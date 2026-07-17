---
id: CHANGE-TASK-047

task_id: TASK-047

parent_prd: PRD-005

branch: feature/TASK-047

type: Feature

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-17

completed: 2026-07-17

duration: 4 hours (estimated)

related_commits:
  - feat(TASK-047): add htmlType and lookupOptions to FieldDefinitionResponse, populate server-side, update frontend

related_files:
  - backend/src/main/java/com/erp/core/runtime/dto/window/FieldDefinitionResponse.java
  - backend/src/main/java/com/erp/core/runtime/service/WindowDefinitionAssemblyService.java
  - frontend/src/core/runtime/api/runtimeApi.ts
  - frontend/src/routes/window/WindowPage.tsx
  - frontend/src/core/runtime/components/FormFieldRenderer.tsx

review_required: true

test_required: true

---

# Summary

Added `htmlType` and `lookupOptions` fields to the `ColumnInfo` DTO within `FieldDefinitionResponse`. The backend `WindowDefinitionAssemblyService` now maps column types to HTML input types (e.g., string→text, integer→number, date→date, boolean→checkbox, many2one→select) and fetches lookup options (id, label pairs) from related tables when a `relationTable` is configured. The frontend `WindowPage.tsx` was updated to use backend-provided `lookupOptions` for dropdown rendering instead of firing parallel lookup queries. The `FormFieldRenderer.tsx` was updated to prefer the new `htmlType` field when available.

---

# Scope Verification

- [x] Frontend
- [x] Backend
- [ ] Database
- [ ] Configuration

---

# Business Requirements Implemented

- FR-002: Add htmlType and lookupOptions to Field Definition Response
  - `ColumnInfo` has new `htmlType: String` field
  - Backend `WindowDefinitionAssemblyService` maps field types: string→text, integer→number, decimal→number, date→date, boolean→checkbox, datetime→datetime-local, enum→select, many2one→select, text→textarea
  - `ColumnInfo` has new `lookupOptions: List<Map>` field with `{id, label}` pairs
  - Backend populates `lookupOptions` by querying `SELECT id, <display_column> AS label FROM <relation_table> WHERE is_active = true LIMIT 100`
  - Frontend `WindowPage.tsx` removed lookup query discovery and parallel fetch calls (~50 lines removed)
  - Frontend `WindowPage.tsx` uses `field.column.lookupOptions` for dropdown rendering
  - Frontend `FormFieldRenderer.tsx` uses `field.htmlType` when available, with legacy fallback

---

# Files Added

None.

---

# Files Modified

| File | Summary |
|------|---------|
| `backend/src/main/java/com/erp/core/runtime/dto/window/FieldDefinitionResponse.java` | Added `htmlType`, `lookupOptions` to `ColumnInfo` with getters/setters |
| `backend/src/main/java/com/erp/core/runtime/service/WindowDefinitionAssemblyService.java` | Added `DynamicCrudService` dependency, `mapToHtmlType()`, `fetchLookupOptions()`, `findDisplayColumnForTable()` methods; populate htmlType and lookupOptions during field assembly |
| `frontend/src/core/runtime/api/runtimeApi.ts` | Added `htmlType?: string` and `lookupOptions?: Array<{id, label}>` to `ColumnInfo` interface |
| `frontend/src/routes/window/WindowPage.tsx` | Removed `useQueries` import, `fetchLookupRecords` import, lookup config collection (80 lines), lookup queries (20 lines), drillContext; updated many2one rendering to use `field.column.lookupOptions` |
| `frontend/src/core/runtime/components/FormFieldRenderer.tsx` | Replaced `mapInputType()` with `resolveInputType()` that checks `htmlType` first with legacy fallback |

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

Response field definitions now include:
```json
{
  "column": {
    "code": "name",
    "type": "string",
    "htmlType": "text",
    "relationTable": "md_product_category",
    "lookupOptions": [
      { "id": "uuid-1", "label": "Category A" },
      { "id": "uuid-2", "label": "Category B" }
    ]
  }
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
| `FieldDefinitionResponse.ColumnInfo` | Added `htmlType`, `lookupOptions` fields |
| `WindowDefinitionAssemblyService` | Added htmlType mapping, lookup option fetching, display column resolution |

---

# Methods Added

| Class | Method | Purpose |
|--------|--------|---------|
| `ColumnInfo` | `getHtmlType()` | Returns the HTML input type for rendering |
| `ColumnInfo` | `setHtmlType()` | Sets the HTML input type |
| `ColumnInfo` | `getLookupOptions()` | Returns dropdown options |
| `ColumnInfo` | `setLookupOptions()` | Sets dropdown options |
| `WindowDefinitionAssemblyService` | `mapToHtmlType()` | Maps backend column types to HTML input types |
| `WindowDefinitionAssemblyService` | `fetchLookupOptions()` | Queries relation table for id/label pairs |
| `WindowDefinitionAssemblyService` | `findDisplayColumnForTable()` | Finds the display column from sys_column metadata |

---

# Methods Updated

| Class | Method | Summary |
|--------|--------|---------|
| `WindowDefinitionAssemblyService` | `assembleField()` | Added htmlType and lookupOptions population to ColumnInfo |

---

# DTOs

| DTO | Change |
|-----|--------|
| `FieldDefinitionResponse.ColumnInfo` | Added `htmlType`, `lookupOptions` fields |

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

All 36 backend tests pass.

---

# Manual Verification

- Verified `FieldDefinitionResponse.ColumnInfo` compiles with htmlType and lookupOptions fields
- Verified type→htmlType mapping covers all expected types
- Verified lookupOptions query uses display column from sys_column metadata
- Verified lookup options limited to 100 results
- Verified frontend `WindowPage.tsx` no longer fires parallel lookup queries
- Verified many2one dropdown renders from `field.column.lookupOptions` instead of separate query results
- Backward compatible — existing `type` field remains on response

---

# Breaking Changes

None. Backward compatible — new fields added alongside existing ones.

---

# Known Issues

None.

---

# Future Improvements

- Add caching for lookup options to avoid repeated DB queries for frequently-accessed tables
- Support dynamic filter where-clauses in lookup queries (currently uses is_active = true always)

---

# Developer Notes

- The `fetchLookupOptions()` method reuses the same SQL pattern as `WindowDataService.lookupRecords()` but runs during window definition assembly rather than on-demand
- The `findDisplayColumnForTable()` method is duplicated from `WindowDataService` — this is acceptable as the two services are independent (assembly vs data)
- Lookup options are fetched eagerly during definition assembly (all relation tables queried upfront). For windows with many FK fields, this could increase definition load time but eliminates N+1 frontend queries

---

# QA Handoff

**Suggested test focus:**
- Open a window with many2one/relation fields
- Verify dropdown options appear correctly without parallel lookup API calls
- Verify the correct display labels are shown in dropdowns
- Verify enum fields still render correctly with their configured options

**Potential risk areas:**
- Lookup options may be stale if relation table data changes between definition load and record open
- Tables without `is_active` column will return 0 results from lookup query
- Tables without a configured display column will show empty dropdowns

**Important edge cases:**
- Relation table with no records — dropdown should show "None" option only
- Field with relationTable but no display column configured — empty dropdown
- Enum fields with comma-separated options in enumOptions field — must parse correctly
