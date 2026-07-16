---
id: PRD-005

title: Backend-Frontend Separation — Move Data Logic to Backend

version: 1.2.0

status: DRAFT
# DRAFT
# REVIEW
# APPROVED
# IN_DEVELOPMENT
# TESTING
# READY_FOR_DEPLOYMENT
# COMPLETED
# REOPENED

priority: High
# Critical
# High
# Medium
# Low

owner: Product Manager

created: 2026-07-15

updated: 2026-07-15

approved_by:

project: Dynamic ERP

repository: https://github.com/APLogilite/erp-system

tech_stack:
  - Spring Boot 3.3.4
  - React 18 / TypeScript
  - Zustand / React Query

related_prds:
  - PRD-004

related_tasks: []

related_bugs: []

dependencies: []

change_log:
  - 1.0.0 — Initial Draft

---

# Executive Summary

The frontend currently contains data logic that should belong to the backend: field type mapping, child tab relationship derivation, lookup discovery, display value resolution, and model/view assembly. This creates duplication, makes the frontend harder to maintain, and violates the separation of concerns.

This PRD moves all data transformation logic to the backend. The frontend becomes a pure presentation layer that renders fields and sends user input. The backend becomes the single source of truth for **what data to show, how to format it, and where to get it**.

---

# Problem Statement

When PRD-004 (Window Hierarchy & Menu System) was built, the frontend `WindowPage.tsx` and window API client `runtimeApi.ts` were designed to consume raw metadata and assemble it on the client side. This was faster to ship but created several problems:

1. **Frontend derives tab relationships** — `WindowPage.tsx:230-242` parses `parentColumn` naming conventions (`window_id` → strip `_id` → match table name) to determine child tabs. This is schema logic the frontend should not know.

2. **Frontend maps field types** — `formToBundleMapper.ts:23-37` maps `string→TEXT`, `number→NUMBER`, `many2one→MANY_TO_ONE`. The backend already knows the field type and should return it in the format the renderer needs.

3. **Frontend discovers lookups** — `WindowPage.tsx:93-113` scans ALL fields for `relationTable`, then fires N parallel API calls. Lookup options should be embedded in each field definition by the backend.

4. **Frontend type-coerces values** — `WindowPage.tsx:211-218` calls `parseInt`/`parseFloat` on save. The backend should accept raw strings and coerce server-side.

5. **Frontend filters/sorts fields** — `DynamicListView.tsx:71-74` filters by `visible`, sorts by `position`, slices to 8. The backend should return display-ready columns.

6. **Frontend formats display values** — `formatCellValue()` in `DynamicListView.tsx` and `getRecordLabel()` in `useForm.ts` derive human-readable labels by scanning fields. The backend already resolves `_display` in `WindowDataService` but doesn't use it consistently.

7. **Frontend builds fallback layouts** — `DynamicFormRenderer.tsx:66-78` creates a default section when backend sends no sections. The backend should always send at least one section.

8. **Frontend duplicates the form-to-bundle transformation** — `formToBundleMapper.ts` exists only to convert the backend's flat `FormDefinition` into the metadata-driven renderer's `RuntimeMetadataBundle`. The backend should return the bundle directly.

---

# Business Goals

- Eliminate all data transformation logic from the frontend
- Make the frontend a pure rendering layer: receive data → render → send user input
- Make the backend the single source of truth for all business/display logic
- Reduce frontend codebase size by ~15-20% (estimate: 600+ lines removable)
- Eliminate N+1 lookup queries (frontend fires parallel calls per table)
- Enable any frontend client (React, mobile, API consumers) to render windows identically

---

# Verification: Current vs Target State

## Current state (frontend does data logic)

| Data | Where assembled |
|------|----------------|
| Field display order | Frontend filters `isDisplayed`, sorts `seqNo` |
| Child tab relationships | Frontend parses `parentColumn` naming convention |
| Lookup options for dropdowns | Frontend discovers `relationTable`, fires per-table queries |
| Field type → HTML input type | Frontend maps `string→TEXT`, `number→NUMBER` etc. |
| Type coercion on save | Frontend calls `parseInt`/`parseFloat` |
| Record display label | Frontend scans fields for first non-null value |
| Model/view/layout bundle | Frontend assembles from flat form definition |
| Ctrl+K search results | Frontend filters client-side |

## Target state (backend does data logic)

| Data | Where assembled |
|------|----------------|
| Field display order | Backend returns fields pre-sorted, pre-filtered, `isDisplayed=false` excluded |
| Child tab relationships | Backend includes `childTabIds: UUID[]` on each tab |
| Lookup options | Backend embeds `options: [{id, label}]` in each field with `relationTable` |
| HTML input type | Backend includes `htmlType: "text" | "number" | "date"` on each field |
| Type coercion | Backend accepts raw strings, coerces server-side |
| Record display label | Backend returns `_display` on every record (already partially done) |
| Form definition assembly | Backend returns `RuntimeMetadataBundle` directly |
| Ctrl+K search | Backend provides search endpoint |

---

# Functional Requirements

## FR-001: Add childTabIds to Tab Definition Response

**Priority:** High

**Description:** The `TabDefinitionResponse` DTO currently lacks `childTabIds`. The backend `WindowDefinitionAssemblyService` should compute which tabs are children of each tab (by matching `parentColumn` to table names) and include `childTabIds: UUID[]` in each tab response. The frontend's `findChildTabs()` in `WindowPage.tsx:230-242` can then be removed.

**Acceptance Criteria:**
- `TabDefinitionResponse` has new `childTabIds: UUID[]` field
- `WindowDefinitionAssemblyService` populates it by scanning all tabs and matching `parentColumn` naming conventions
- Frontend `WindowPage.tsx` no longer imports or uses `findChildTabs()`
- Frontend reads `childTabIds` from tab definition directly

**Files affected:**
- Backend: `TabDefinitionResponse.java` — add field
- Backend: `WindowDefinitionAssemblyService.java` — populate childTabIds
- Frontend: `WindowPage.tsx` — remove `findChildTabs()`, use `tab.childTabIds`
- Frontend: `runtimeApi.ts` — update `WindowTabDefinition` interface

---

## FR-002: Add htmlType and lookupOptions to Field Definition Response

**Priority:** High

**Description:** The `FieldDefinitionResponse.ColumnInfo` currently has `type` (string/integer/decimal etc.) but no `htmlType` for direct rendering. Add `htmlType: "text" | "number" | "date" | "checkbox"` populated by the backend's field type mapping logic (currently in frontend `FormFieldRenderer.tsx:34-48`). Also add `lookupOptions: [{id, label}]` for fields with `relationTable`, populated by querying the related table and resolving display columns.

**Acceptance Criteria:**
- `ColumnInfo` has new `htmlType: String` field
- Backend `WindowDefinitionAssemblyService` maps field types (string→text, integer→number, decimal→number, date→date, boolean→checkbox)
- `ColumnInfo` has new `lookupOptions: List<Map> ` field
- Backend populates `lookupOptions` by querying `SELECT id, display_column AS label FROM relation_table` when `relationTable` is set
- Frontend `FormFieldRenderer.tsx` removes `mapInputType()`, uses `field.column.htmlType` directly
- Frontend `WindowPage.tsx` removes lookup query discovery and parallel fetch calls (lines 93-113)
- Frontend `WindowPage.tsx` uses `options` from the field definition for dropdown rendering instead of separate lookup queries

**Files affected:**
- Backend: `FieldDefinitionResponse.java` — add `htmlType`, `lookupOptions`
- Backend: `WindowDefinitionAssemblyService.java` — populate both fields
- Backend: `WindowDataService.java` — can reuse FK display resolution for lookup options
- Frontend: `FormFieldRenderer.tsx` — remove `mapInputType()`, use `htmlType` and `lookupOptions`
- Frontend: `WindowPage.tsx` — remove lookup queries section (lines 93-148)
- Frontend: `runtimeApi.ts` — update `ColumnInfo` and `WindowFieldDefinition` interfaces
- Frontend: `DynamicListView.tsx` — remove `visibleFields` slice, use backend pre-sorted fields

---

## FR-003: Backend Returns Pre-Filtered, Pre-Sorted Fields

**Priority:** Medium

**Description:** Currently the frontend filters out `isDisplayed=false` fields and sorts by `seqNo` in multiple places (`WindowPage.tsx:52-54`, `DynamicListView.tsx:71-74`). The backend should exclude non-displayed fields and return them in `seqNo` order. The frontend just renders whatever fields it receives.

**Acceptance Criteria:**
- Backend already sorts fields by `seq_no` — confirmed in `WindowDefinitionAssemblyService.java:109`
- Backend excludes fields where `isDisplayed=false` from the response
- Frontend removes all `filter((f) => f.isDisplayed !== false)` and `sort((a, b) => a.seqNo - b.seqNo)` calls
- Frontend `DynamicListView.tsx` removes the `.slice(0, 8)` limit — backend controls how many fields to return for list view

**Files affected:**
- Backend: `WindowDefinitionAssemblyService.java` — filter out non-displayed fields
- Frontend: `WindowPage.tsx` — remove `getDisplayedFields()`, use fields as-is
- Frontend: `DynamicListView.tsx` — render fields as-is from response

---

## FR-004: Backend Accepts Raw Values and Coerces Server-Side

**Priority:** Medium

**Description:** `WindowPage.tsx:211-218` coerces string values to `parseInt`/`parseFloat` before sending. The backend should accept raw string values and coerce them on the server side using the column's type metadata.

**Acceptance Criteria:**
- `WindowDataController.createRecord()` and `updateRecord()` accept strings for number fields
- Backend `WindowDataService` or `DynamicCrudService` coerces values based on `sys_column.type` metadata
- Frontend `WindowPage.tsx` removes `parseInt`/`parseFloat` logic from `handleSave()`

**Files affected:**
- Backend: `WindowDataService.java` — add type coercion layer
- Frontend: `WindowPage.tsx` — simplify `handleSave()` to send raw formData

---

## FR-005: Backend Returns _display on Every Record

**Priority:** High

**Description:** The backend `WindowDataService` already resolves `_display` via the `is_display_column` flag (line 118-132). However, the frontend `useForm.ts:220-238` still has `getRecordLabel()` that scans fallback fields, and `WindowPage.tsx:298-299` checks `_display`, `name`, `code` fields as fallbacks. The backend should guarantee that `_display` is always present on returned records, and the frontend can use it without fallback logic.

**Acceptance Criteria:**
- Backend ensures every record has `_display` key (even if fallback to first non-id column or just `id`)
- Frontend `useForm.ts` removes `getRecordLabel()` — use `record._display` instead
- Frontend `WindowPage.tsx:298-299` simplifies to `rec._display`
- Frontend `DynamicListView.tsx` removes `formatCellValue()` — use `record._display` for FK fields

**Files affected:**
- Backend: `WindowDataService.java` — strengthen `_display` guarantee for all records
- Frontend: `useForm.ts` — remove `getRecordLabel()`
- Frontend: `WindowPage.tsx` — simplify display value extraction
- Frontend: `DynamicListView.tsx` — remove `formatCellValue()`

---

## FR-006: Backend Returns RuntimeMetadataBundle Directly (Delete formToBundleMapper.ts)

**Priority:** High

**Description:** `formToBundleMapper.ts` (130 lines) exists solely to convert the backend's flat `FormDefinition` response into the metadata-driven renderer's `RuntimeMetadataBundle`. The backend already has `FormDefinitionAssemblyService` and should return `RuntimeMetadataBundle` directly. This eliminates an entire file and a data transformation layer.

**Acceptance Criteria:**
- Backend form definition endpoint returns `RuntimeMetadataBundle` (model + views + actions + permissions + workflow)
- `FormDefinitionAssemblyService` assembles the bundle directly
- Frontend `formToBundleMapper.ts` is deleted
- Frontend `runtimeApi.ts` updates `FormDefinition` type to match the new response
- Frontend `DynamicFormRenderer.tsx` no longer imports from `formToBundleMapper`

**Files affected:**
- Backend: `FormDefinitionAssemblyService.java` — restructure to produce `RuntimeMetadataBundle`
- Backend: Create or update form definition response DTO
- Frontend: DELETE `formToBundleMapper.ts`
- Frontend: `runtimeApi.ts` — update types
- Frontend: `DynamicFormRenderer.tsx` — remove mapper imports
- Frontend: `useForm.ts` — remove `getRecordLabel()`

---

## FR-007: Backend Search Endpoint for Ctrl+K

**Priority:** Medium

**Description:** `FormSearchBar.tsx` fetches all accessible windows and filters client-side. The backend should provide a search endpoint that accepts a query string and returns matching windows, including parent menu path for context.

**Acceptance Criteria:**
- New endpoint: `GET /api/v1/runtime/windows/search?q={query}`
- Returns: `[{ windowId, windowName, windowLabel, tableName, tableLabel, menuPath }]`
- Frontend `FormSearchBar.tsx` sends query to backend instead of client-side filtering
- Frontend removes the manual `filter()` logic and uses the API response

**Files affected:**
- Backend: `WindowDataController.java` — add search endpoint
- Backend: `WindowDataService.java` — add search method (query sys_window + sys_menu)
- Frontend: `FormSearchBar.tsx` — replace client filter with API search
- Frontend: `runtimeApi.ts` — add `searchWindows()` function

---

## FR-008: Frontend Always Receives Sections from Backend

**Priority:** Low

**Description:** `DynamicFormRenderer.tsx:66-78` creates a fallback default section when the backend returns empty sections. The backend should always return at least one section with all fields. This eliminates the fallback logic.

**Acceptance Criteria:**
- Backend form definition endpoint guarantees `sections` is non-empty
- If no sections are configured, backend auto-generates a single section with all fields
- Frontend `DynamicFormRenderer.tsx` removes the fallback code (lines 66-78)

**Files affected:**
- Backend: Form definition assembly — ensure at least one section
- Frontend: `DynamicFormRenderer.tsx` — remove fallback section creation

---

# Non-Functional Requirements

- **Backward compatibility**: Old API clients should continue to work. Add new fields alongside existing ones (don't remove fields from existing DTOs).
- **Performance**: Lookup options (FR-002) should be fetched in batch per window, not per field.
- **Maintainability**: Backend DTOs should mirror frontend rendering needs exactly — no transformation layer.
- **Load time**: Frontend should make fewer API calls (remove N lookup queries per window open).

---

# Scope

## Included

- Window definition response changes (FR-001, FR-002, FR-003)
- Backend type coercion (FR-004)
- _display guarantee for all records (FR-005)
- RuntimeMetadataBundle direct from form endpoint (FR-006)
- Ctrl+K search endpoint (FR-007)
- Section guarantee for form definitions (FR-008)
- Frontend cleanup: remove all data transformation code
- **Remove dead backend modules**: `modules/auth/` (6 files) and `core/security/` (12 files)

## Excluded

- Mobile client — same backend serves all clients
- Real-time sync (WebSockets) — future
- Form designer UI changes — backend already feeds the designer
- Old PRD-001 form system migration — covered by PRD-004
- Module reorganization (routes/ vs modules/ alignment — separate PRD)
- Old form designer migration (`core/metadata/` PRD-001 entities) — covered by PRD-004 window system

---

# API Requirements

## FR-001: Updated Window Definition Response

```json
{
  "window": { "id": "uuid", "name": "product", "tableId": "uuid", "description": "..." },
  "tabs": [{
    "id": "uuid",
    "name": "Product",
    "seqNo": 1,
    "isSingleRow": false,
    "parentColumn": null,
    "childTabIds": ["uuid-of-child-tab"],
    "table": { "id": "uuid", "name": "md_product", "label": "Product" },
    "fields": [...]
  }]
}
```

## FR-002: Updated Field Definition

```json
{
  "id": "uuid",
  "seqNo": 10,
  "isDisplayed": true,
  "isReadonly": false,
  "isMandatory": true,
  "label": "Product Name",
  "column": {
    "code": "name",
    "label": "Product Name",
    "type": "string",
    "htmlType": "text",
    "required": true,
    "relationTable": null,
    "enumOptions": null,
    "lookupOptions": null
  }
}
```

## FR-007: Search Endpoint

```
GET /api/v1/runtime/windows/search?q=product

Response:
{
  "success": true,
  "data": [
    {
      "windowId": "uuid",
      "windowName": "product",
      "windowLabel": "Product",
      "tableName": "md_product",
      "tableLabel": "Product",
      "menuPath": "Master Data > Product"
    }
  ]
}
```

---

# Database Changes

None. All changes are in DTOs, service logic, and frontend code.

---

# Risks

| Risk | Mitigation |
|------|------------|
| Existing frontend code relies on old DTO structure | Add new fields alongside old ones (backward compatible). Frontend can migrate field-by-field. |
| Lookup options could be large (thousands of records) | Add pagination/minimum search length to lookup options. Limit to 100 results. |
| _display resolution adds DB queries | Cache display columns. Batch resolve FK lookups in a single query (already implemented in `WindowDataService`). |

---

# Tasks

## TASK-001: Add childTabIds to TabDefinitionResponse

- Owner: Software Engineer
- Scope: backend
- Files: `TabDefinitionResponse.java`, `WindowDefinitionAssemblyService.java`, `runtimeApi.ts`, `WindowPage.tsx`
- Effort: 2 hours

## TASK-002: Add htmlType and lookupOptions to FieldDefinitionResponse

- Owner: Software Engineer
- Scope: both
- Files: `FieldDefinitionResponse.java`, `WindowDefinitionAssemblyService.java`, `FormFieldRenderer.tsx`, `WindowPage.tsx`, `runtimeApi.ts`
- Effort: 4 hours

## TASK-003: Backend Pre-Filters and Pre-Sorts Fields

- Owner: Software Engineer
- Scope: backend
- Files: `WindowDefinitionAssemblyService.java`, `WindowPage.tsx`, `DynamicListView.tsx`
- Effort: 1 hour

## TASK-004: Backend Type Coercion on Save

- Owner: Software Engineer
- Scope: backend
- Files: `WindowDataService.java`, `DynamicCrudService.java`, `WindowPage.tsx`
- Effort: 2 hours

## TASK-005: Guarantee _display on Every Record

- Owner: Software Engineer
- Scope: backend
- Files: `WindowDataService.java`, `DynamicCrudService.java`, `useForm.ts`, `WindowPage.tsx`, `DynamicListView.tsx`
- Effort: 3 hours

## TASK-006: Backend Returns RuntimeMetadataBundle Directly

- Owner: Software Engineer
- Scope: both
- Files: `FormDefinitionAssemblyService.java`, DELETE `formToBundleMapper.ts`, `runtimeApi.ts`, `DynamicFormRenderer.tsx`, `useForm.ts`
- Effort: 4 hours

## TASK-007: Backend Search Endpoint for Ctrl+K

- Owner: Software Engineer
- Scope: both
- Files: `WindowDataController.java`, `WindowDataService.java`, `FormSearchBar.tsx`, `runtimeApi.ts`
- Effort: 3 hours

## TASK-008: Backend Guarantees Non-Empty Sections

- Owner: Software Engineer
- Scope: backend
- Files: Form definition assembly service, `DynamicFormRenderer.tsx`
- Effort: 1 hour

## TASK-009: Remove Dead `modules/auth/` Package

- Owner: Software Engineer
- Scope: backend
- Description: `modules/auth/controller/AuthController.java`, `service/AuthService.java`, `repository/AuthRepository.java`, `entity/AuthEntity.java`, `dto/AuthDto.java` — zero external references. The real auth system lives in `platform/identity/`. Safe to delete entirely.
- Files: DELETE `backend/src/main/java/com/erp/modules/auth/`
- Effort: 30 minutes

## TASK-010: Remove Dead `core/security/` Package

- Owner: Software Engineer
- Scope: backend
- Description: `PermissionController`, `PermissionService`, `PermissionServiceImpl`, `PermissionRegistry`, `PermissionValidator`, `PermissionMapper`, `PermissionLevel` enum, 3 DTOs, `PermissionDeniedException` — 12 files, zero external references. The real permission system lives in `platform/identity/authorization/`. Frontend calls `/identity/permissions` and `/auth/permissions`, NOT `/security/check`.
- Files: DELETE `backend/src/main/java/com/erp/core/security/`
- Effort: 30 minutes

## TASK-011: Move `customerService.ts` Out of `core/api/services/`

- Owner: Software Engineer
- Scope: frontend
- Description: `frontend/src/core/api/services/customerService.ts` is CRM-specific. Move it to a CRM module or delete if unused. The `ENDPOINTS.customers` config in `core/api/endpoints.ts` should also be moved.
- Files: `customerService.ts`, `endpoints.ts`
- Effort: 30 minutes

## TASK-012: Audit and Remove Stale Frontend API Endpoints

- Owner: Software Engineer
- Scope: frontend
- Description: `endpoints.ts` contains `customers` and `users` endpoint definitions. Verify which are still actively called from page components. Remove unused endpoint configs to prevent confusion.
- Files: `endpoints.ts`
- Effort: 30 minutes

---

# Acceptance Criteria

- Frontend `formToBundleMapper.ts` is deleted
- Frontend `WindowPage.tsx` has no `findChildTabs()`, no lookup query discovery, no type coercion, no `getDisplayedFields()` fallback
- Frontend `FormFieldRenderer.tsx` has no `mapInputType()`
- Frontend `DynamicListView.tsx` has no `formatCellValue()`, no field filtering/slicing
- Frontend `useForm.ts` has no `getRecordLabel()`
- Backend includes `htmlType`, `lookupOptions`, `childTabIds` in window definition
- Backend search endpoint for Ctrl+K exists
- All existing frontend functionality works identically (regression pass)
- 600+ lines of frontend code removed
- `modules/auth/` directory deleted (6 files)
- `core/security/` directory deleted (12 files)
- Backend compiles and all 36 existing tests pass after deletion
- `mvn clean compile` succeeds with no unresolved imports

---

# Change History

| Version | Reason |
|---------|--------|
| 1.0.0 | Initial Draft |
| 1.1.0 | Added dead code removal: `modules/auth/` and `core/security/` packages |
| 1.2.0 | Added standardization: move customerService, audit stale endpoints |
