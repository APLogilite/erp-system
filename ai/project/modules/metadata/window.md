---
module: window
type: backend
layer: entity + repository + service + controller
last_updated: 2026-07-16T18:32:56+05:30
last_updated_git_sha: 2958af1b0ecd41cb6d20403374d34e41c0917a0e
paths:
  - backend/src/main/java/com/erp/modules/metadata/
  - backend/src/main/java/com/erp/core/runtime/controller/WindowDefinitionController.java
  - backend/src/main/java/com/erp/core/runtime/controller/WindowDataController.java
  - backend/src/main/java/com/erp/core/runtime/service/WindowDefinitionAssemblyService.java
  - backend/src/main/java/com/erp/core/runtime/service/WindowDataService.java
  - backend/src/main/java/com/erp/core/runtime/dto/window/
---

# Backend Metadata Window System

## Purpose
The new Window/Tab/Field metadata schema (replacing the old PRD-001 metadata tables). Provides window definitions (the top-level form concept), tabs (sub-sections within a window), fields (mapping columns to display positions), menu hierarchy, and role-based access control. This is the PRD-004 window hierarchy system.

---

## Simple Instructions *(for non-developers)*

### What is this?
This is the engine that defines the structure of every data entry screen in the ERP system. Instead of each screen being hand-coded, they are configured using "windows," "tabs," and "fields." Think of it like a blueprint system: a **Window** describes an entire data management screen (like "Sales Orders"), a **Tab** is a section within it (like "Order Lines" or "Shipping"), and **Fields** are the individual input boxes and dropdowns.

### What can you do here?
- Administrators configure windows in the **Admin > Table Designer** and **Admin > Form Designer** pages
- The **Menu** system organizes windows into the sidebar navigation
- **Window Access** controls which user roles can see which windows
- End users interact with windows through the runtime `WindowPage` at `/window/{name}`

### How to use it
1. A System Admin registers database tables via the **Table Designer**.
2. A System Admin creates **Windows** and assigns them to tables.
3. **Tabs** are created within windows to organize fields into logical groups.
4. **Fields** map database columns to positions within tabs.
5. **Menu** entries link windows to sidebar navigation items.
6. **Window Access** rules grant specific roles access to each window.
7. Users click menu items and are taken to `/window/{windowName}` to view and edit data.

### Diagram

```mermaid
graph TD
  A[Admin registers table] --> B[Admin creates Window for table]
  B --> C[Admin adds Tabs to Window]
  C --> D[Admin assigns Fields to Tabs]
  D --> E[Menu entry links to Window]
  E --> F[Window Access grants role permission]
  F --> G[User sees menu item in sidebar]
  G --> H[User clicks → /window/{name} loads]
  H --> I[WindowPage renders list + detail dialog]
```

### Common issues
| Problem | Solution |
|---------|----------|
| Window not appearing in sidebar | The menu entry may not be configured, or the user's role doesn't have Window Access. |
| Tab shows no records | The tab's parent column may not be set correctly, or there are no child records yet. |
| Field is not displayed | Check `is_displayed` flag on `sys_window_field` and the `is_display_column` flag on `sys_column`. |
| "Window not found" error | The window name in the URL may be wrong. Check that the window exists in `sys_window`. |

---

## Key Classes *(developers)*

### Entities (`backend/src/main/java/com/erp/modules/metadata/entity/`)

| Class | Table | Role |
|-------|-------|------|
| `SysTable` | `sys_table` | Physical table definitions — `name`, `label`, `tableName`, `tableType` |
| `SysColumn` | `sys_column` | Column definitions — `code`, `label`, `type`, `required`, `relationTable`, `enumOptions`, `filterWhereClause` |
| `SysWindow` | `sys_window` | Window (form) definitions — `name`, `tableId`, `description` |
| `SysTab` | `sys_tab` | Tab definitions — `windowId`, `name`, `tableId`, `seqNo`, `isSingleRow`, `whereClause`, `parentColumn` |
| `SysWindowField` | `sys_window_field` | Field-position mappings — `tabId`, `columnId`, `seqNo`, `isDisplayed`, `isReadonly`, `isMandatory`, `labelOverride`, `filterWhereClause` |
| `SysWindowAccess` | `sys_window_access` | Role-based access — `windowId`, `tenantId`, `roleId` |
| `SysMenu` | `sys_menu` | Hierarchical menu — `name`, `type` (WINDOW/GROUP), `parentId`, `windowId`, `seqNo` |

### Services (`backend/src/main/java/com/erp/modules/metadata/service/`)

| Service | Role |
|---------|------|
| `SysTableService` | CRUD for `sys_table` entries |
| `SysColumnService` | CRUD for `sys_column` entries |
| `SysWindowService` | CRUD for `sys_window` entries; `findByName()` for assembly |
| `SysTabService` | CRUD for `sys_tab` entries; `findByWindowIdOrderBySeqNoAsc()` |
| `SysWindowFieldService` | CRUD for `sys_window_field` entries; `findByTabIdOrderBySeqNoAsc()` |
| `SysWindowAccessService` | CRUD for `sys_window_access` entries |
| `SysMenuService` | CRUD for `sys_menu` entries |

### Runtime Services

| Service | Role |
|---------|------|
| `WindowDefinitionAssemblyService` | Assembles full window definition bundle from metadata entities: window → tabs → fields with resolved column info |
| `WindowDataService` | Dynamic CRUD on window tables: list records (paginated), get record with children, create/update/delete, lookup records for dropdowns, drill-down tab record fetching |

### Runtime Controllers

| Controller | Path | Role |
|------------|------|------|
| `WindowDefinitionController` | `GET /api/v1/runtime/windows/{windowName}/definition` | Returns window definition bundle (ETag + Cache-Control) |
| `WindowDefinitionController` | `GET /api/v1/runtime/windows/accessible` | Returns all windows the current user's roles can access |
| `WindowDataController` | `GET/POST/PUT/DELETE /api/v1/runtime/windows/{windowName}/records` | CRUD operations on window data |
| `WindowDataController` | `GET /api/v1/runtime/windows/{windowName}/tabs/{tabId}/records/{id}` | Drill-down: fetch record from a specific tab's table |
| `WindowDataController` | `GET /api/v1/runtime/windows/lookup/{tableName}` | Lookup records for dropdown/autocomplete fields |

---

## API Endpoints

### Window Definition

| Method | Path | Handler | Auth |
|--------|------|---------|------|
| GET | `/api/v1/runtime/windows/{windowName}/definition` | `WindowDefinitionController.getWindowDefinition()` | JWT |
| GET | `/api/v1/runtime/windows/accessible` | `WindowDefinitionController.listAccessibleWindows()` | JWT |

### Window Data CRUD

| Method | Path | Handler | Auth |
|--------|------|---------|------|
| GET | `/api/v1/runtime/windows/{windowName}/records?page=&size=` | `WindowDataController.listRecords()` | JWT |
| GET | `/api/v1/runtime/windows/{windowName}/records/{id}` | `WindowDataController.getRecord()` | JWT |
| POST | `/api/v1/runtime/windows/{windowName}/records` | `WindowDataController.createRecord()` | JWT |
| PUT | `/api/v1/runtime/windows/{windowName}/records/{id}` | `WindowDataController.updateRecord()` | JWT |
| DELETE | `/api/v1/runtime/windows/{windowName}/records/{id}` | `WindowDataController.deleteRecord()` | JWT |
| GET | `/api/v1/runtime/windows/{windowName}/tabs/{tabId}/records/{id}?childTabs=` | `WindowDataController.getTabRecord()` | JWT |
| GET | `/api/v1/runtime/windows/lookup/{tableName}` | `WindowDataController.lookupRecords()` | JWT |

### Menu

| Method | Path | Handler | Auth |
|--------|------|---------|------|
| GET | `/api/v1/runtime/menus` | `MenuController.getAccessibleMenus()` | JWT |

---

## Dependencies
- All entities extend `BaseEntity` (UUID id, soft-delete, timestamps)
- `WindowDefinitionAssemblyService` depends on all `Sys*Service` classes
- `WindowDataService` uses raw JDBC/EntityManager for dynamic table CRUD
- `RuntimeContextHolder` provides tenant context for multi-tenant isolation

---

## Related Frontend
- `routes/window/WindowPage.tsx` — renders the window list view + record dialog
- `core/runtime/api/runtimeApi.ts` — API client functions: `fetchWindowDefinition()`, `fetchWindowRecords()`, `createWindowRecord()`, etc.
- `core/runtime/components/MenuNavigation.tsx` — sidebar menu that calls `GET /runtime/windows/accessible`
- `core/runtime/hooks/useMenuItems.ts` — hook that fetches accessible windows and builds menu structure

---

## Related Module Docs
- `core-metadata-table-designer.md` — Table Designer registers tables used by the window system
- `core-metadata-runtime.md` — Old runtime system; new window system replaces the form bundle assembly
- `core-schema-ddl.md` — DDL files for `sys_window`, `sys_tab`, `sys_column`, `sys_table`, `sys_window_field`, `sys_window_access`, `sys_menu`
