---
id: PRD-001

title: Dynamic Form Configuration System

version: 1.6.0

status: APPROVED

priority: High

owner: planner

created: 2026-07-07

updated: 2026-07-07

approved_by: user

project: Dynamic ERP Platform

repository: erp-system

tech_stack:
  - Spring Boot 3.3.4
  - Java 17
  - PostgreSQL (JSONB)
  - React 18
  - TypeScript (strict)
  - MUI 5
  - Zustand
  - React Query
  - Vite

related_prds: []

related_tasks:
  - TASK-001
  - TASK-002
  - TASK-003
  - TASK-004
  - TASK-005
  - TASK-006
  - TASK-007
  - TASK-008
  - TASK-009
  - TASK-010
  - TASK-011
  - TASK-012
  - TASK-013
  - TASK-014
  - TASK-015
  - TASK-016
  - TASK-017
  - TASK-018
  - TASK-019
  - TASK-020
  - TASK-021
  - TASK-022
  - TASK-023
  - TASK-024
  - TASK-025
  - TASK-026
  - TASK-027

related_bugs: []

dependencies:
  - Architecture blueprint (Phase 0 frozen)
  - Existing MetadataModel entity
  - Existing MetadataView entity
  - Existing Registry system (field, layout, view)
  - Existing BaseEntity framework
  - Existing Dynamic CRUD service (to be built per architecture)

change_log:
  - 1.0.0 — Initial Draft
  - 1.1.0 — Added global forms, per-tenant role assignment, tenant isolation
  - 1.2.0 — Added header form search (Ctrl+K), consolidated single-request bundle API
  - 1.3.0 — Added multi-level sub-forms, breadcrumb navigation, inline sub-form grids
  - 1.4.0 — Normalized all table/column/form storage into relational tables (no JSONB for mutable data). Two-request loading pattern (definition cached + data fresh). Frontend useForm() hook abstraction.
  - 1.5.0 — Added form toolbar with Create, Save, Save & New, Discard, Refresh, Delete, Previous/Next. Keyboard shortcuts (Ctrl+S, Alt+arrows, F5, Escape).
  - 1.6.0 — Added role-based row-level data access. Row filters per role (tenant isolation + role-based conditions). Dynamic variables resolved from JWT. 404 on filtered records.

---

# Executive Summary

The Dynamic ERP Platform requires the ability for administrators to define database tables and generate fully functional forms from those tables — all without writing code. This PRD describes a three-part system:

1. **Table Designer** — Enables System Admins to define new database tables, their columns, types, and relationships at runtime.
2. **Form Designer** — Enables both System Admins and Tenant Admins to create multiple form layouts for any existing table, complete with field-level rules (visibility, read-only, required, validation), data filtering (where clauses), and role-based access control.
3. **Runtime Form Renderer** — Enables all users to view, create, edit, and delete records through the dynamically configured forms.

A single table can support multiple form variants (e.g., an `Order` table can have both a `Sales Order` form and a `Purchase Order` form), each with its own field layout, rules, data scope, and role assignments.

**Two types of forms exist:**

- **Global Forms** — Created by System Admin. Available to all tenants. Each tenant independently configures which of their roles can access the form. Data is fully tenant-isolated (each tenant sees only their own records).
- **Tenant Forms** — Created by Tenant Admin. Only visible and usable within their tenant. Role access is configured within that tenant's roles.

All configuration is performed through an admin UI and stored as metadata — no code changes required.

---

# Problem Statement

Traditional ERP systems require hardcoded forms, screens, and database schemas for every business entity. This approach has several drawbacks:

- **Rigidity** — Adding a new table or form requires backend development, database migrations, frontend changes, and deployment.
- **Duplication** — The same underlying table (e.g., `Order`) often needs multiple form presentations (Sales Order vs Purchase Order), but current patterns force redundant code.
- **Slow iteration** — Business requirement changes to form layouts, field rules, or validations require developer intervention and full release cycles.
- **Multi-tenant complexity** — Different tenants may need different form layouts for the same core business entity, but there is no mechanism to support this.

**Who experiences this problem:**
- System Administrators who need to extend the ERP data model for new business requirements
- Tenant Administrators who need to tailor forms for their organization's workflows
- End Users who experience delays when requesting form changes
- Developers who are bottlenecked by constant form configuration requests

**Why this feature is required:**
The Dynamic ERP Platform's architecture is fundamentally metadata-driven. Without a dynamic form configuration system, every ERP module (Product, Order, Inventory, etc.) would need to be hardcoded, defeating the purpose of the runtime execution engine. This feature is the core enabler of the platform's "configure, don't code" philosophy.

---

# Business Goals

1. **Eliminate code changes for form/table configuration** — System and Tenant Admins can create tables and design forms entirely through the admin UI.
2. **Support multiple form variants per table** — A single underlying business entity can support multiple operational contexts (e.g., Sales Order vs Purchase Order).
3. **Enable field-level dynamic behavior** — Fields can be shown/hidden, made read-only/editable, required/optional, and validated based on configurable rules evaluated at runtime.
4. **Enforce data isolation per form** — Each form variant can define its own where clause to scope which records are visible (e.g., Sales Order shows only `order_type = 'sales'`).
5. **Global forms reusable across tenants** — System Admin creates a form once; all tenants can use it with their own role assignments.
6. **Per-tenant role-based access** — Each tenant independently decides which of their roles can access a form, without affecting other tenants.
7. **Tenant data isolation for shared forms** — Even when using the same global form, each tenant's data is fully isolated.
8. **Deliver a no-code configuration experience** — A visual admin interface for designing forms without writing JSON or code.
9. **Build on the existing metadata architecture** — Leverage existing `MetadataModel`, `MetadataView`, and Registry infrastructure rather than creating parallel systems.

---

# Functional Requirements

## System A: Table Designer (System Admin)

### FR-001: Create Table Definition

**Description:** System Admin can create a new database table definition through the admin UI. The admin enters a table code (snake_case), singular label, plural label, an optional description, and defines the table's columns.

**Priority:** High

**Acceptance Criteria:**
- Admin can navigate to the "Table Designer" section in the admin panel
- Admin clicks "Create Table" button
- Admin enters: table code (e.g., `tx_expense_report`), singular label (e.g., "Expense Report"), plural label (e.g., "Expense Reports"), description (optional)
- System validates that the table code is unique and follows snake_case convention
- On save, the table definition is stored in `sys_metadata_models`
- On save, the system creates the physical PostgreSQL table with the defined columns
- Admin is redirected to the table detail view where columns can be managed

### FR-002: Manage Table Columns

**Description:** System Admin can add, edit, reorder, and remove columns on a table definition. Each column has a code, label, data type, and optional constraints.

**Priority:** High

**Acceptance Criteria:**
- Admin can view all columns of a table definition in a list/grid
- Supported column types: `string` (with max length), `text` (long text), `integer`, `decimal` (with precision/scale), `boolean`, `date`, `datetime`, `many2one` (relation to another table), `enum` (with predefined options)
- Admin can add a new column with: code (snake_case), label, type, required (yes/no), default value, max length (for string), precision/scale (for decimal), related table (for many2one), enum options list (for enum)
- Admin can edit any existing column's metadata
- Admin can delete a column (with confirmation dialog warning about data loss)
- Admin can reorder columns via drag-and-drop
- On save, the column definition is persisted in the table's definition JSONB
- On save, the physical PostgreSQL table is altered (ALTER TABLE) to reflect column changes
- A migration log is maintained for tracking schema changes

### FR-003: View Available Tables

**Description:** System Admin can view all defined tables in a paginated list with search, filter, and sort capabilities.

**Priority:** Medium

**Acceptance Criteria:**
- Paginated table listing with: code, label, column count, created date, last modified date
- Search by table code or label
- Filter by status (active/inactive)
- Sort by any column
- Each row has "Edit", "View Forms" (shows forms using this table), and "Deactivate" actions

### FR-004: Deactivate/Restore Table

**Description:** System Admin can deactivate (soft-delete) a table definition, which prevents new forms from referencing it without deleting existing data.

**Priority:** Medium

**Acceptance Criteria:**
- Deactivating a table sets `is_active = false` on the metadata record
- Existing forms referencing the table continue to work
- The table cannot be selected when creating new forms
- Admin can restore a deactivated table
- Confirmation dialog required for deactivation, warning about impact on existing forms

### FR-005: View Table Schema History

**Description:** System Admin can view the schema change history for a table, including what columns were added, modified, or removed and when.

**Priority:** Low

**Acceptance Criteria:**
- Accessible from the table detail view
- Chronological list of schema changes with timestamp, admin user, and change description
- Stored in the `sys_metadata_version` table (referenced in architecture)

---

## System B: Form Designer (System Admin & Tenant Admin)

The Form Designer has two modes depending on who is creating the form:

- **System Admin** creates **Global Forms** — shared across all tenants, with per-tenant role assignment
- **Tenant Admin** creates **Tenant Forms** — scoped to their tenant only, with role assignment within their tenant roles

### FR-006: Create Form Definition (System Admin & Tenant Admin)

**Description:** Both System Admin and Tenant Admin can create a new form definition linked to an existing table. The form is given a scope (global or tenant), code, label, optional description, and a where clause for data filtering.

**Priority:** High

**Acceptance Criteria (System Admin — Global Form):**
- System Admin can navigate to the "Form Designer" section in the admin panel
- Admin clicks "Create Form" button
- Admin selects a scope: **"Global Form"** (available to all tenants) or **"Tenant Form"** (scoped to a specific tenant)
- Admin selects an existing, active table from a dropdown (all tables available)
- Admin enters: form code (e.g., `sales_order`), form label (e.g., "Sales Order"), description (optional)
- Admin can optionally enter a where clause (e.g., `order_type = 'sales'`) to filter which records this form shows
- System validates the form code is unique globally and follows snake_case
- System validates the where clause using the expression evaluator
- On save, the form definition is stored as a `MetadataView` with `type = 'form'` and `scope = 'global'`
- The form is now available to all tenants, but each tenant must explicitly assign roles for their users to access it

**Acceptance Criteria (Tenant Admin — Tenant Form):**
- Tenant Admin can navigate to the "Form Designer" section in their tenant admin panel
- Admin clicks "Create Form" button
- Form scope is automatically **"Tenant Form"** (only visible within this tenant)
- Admin selects an existing, active table from a dropdown (only tables available to the tenant are shown, including global tables)
- Admin enters: form code (e.g., `expense_report_simple`), form label (e.g., "Simple Expense Report"), description (optional)
- Admin can optionally enter a where clause (e.g., `order_type = 'expense'`) to filter which records this form shows
- System validates the form code is unique within the tenant
- System validates the where clause using the expression evaluator
- On save, the form definition is stored as a `MetadataView` with `type = 'form'` and `scope = 'tenant'`
- The form is only visible within the admin's tenant

### FR-007: Configure Form Fields

**Description:** Tenant Admin can select which fields from the table appear on the form, reorder them, and configure per-field labels, default values, and placeholder text.

**Priority:** High

**Acceptance Criteria:**
- Admin sees a list of all available columns from the selected table
- Admin can toggle fields on/off for the form (by default, all fields are included)
- Admin can drag-and-drop to reorder fields
- Each field can have its label overridden (if not overridden, the column label is used)
- Each field can have a default value configured
- Each field can have placeholder text configured
- Configuration is stored in the form's definition JSONB

### FR-008: Configure Field Rules

**Description:** Tenant Admin can configure display rules, read-only rules, required rules, and visibility rules for each field. These rules conditionally change field behavior based on other field values.

**Priority:** High

**Acceptance Criteria:**
- For each field in the form, admin can add one or more rules
- Each rule has a **condition** (when to trigger) and an **action** (what to do)
- **Condition format:** `{source_field} {operator} {value}` — e.g., `customer_tier equals Gold`
- **Supported operators:** `equals`, `not_equals`, `greater_than`, `less_than`, `greater_than_or_equal`, `less_than_or_equal`, `contains`, `is_empty`, `is_not_empty`, `in` (list)
- **Supported actions:**
  - `show` / `hide` — visibility toggle
  - `read_only` / `editable` — editability toggle
  - `required` / `optional` — required toggle
- **Multiple rules can be combined** with AND/OR logic
- A **preview mode** allows the admin to test rules by entering sample values and seeing how the form responds
- Rules are stored in the field configuration within the form definition JSONB
- Rules are evaluated client-side in real-time as the user fills out the form

### FR-009: Configure Field Validation

**Description:** Tenant Admin can configure server-side and client-side validation rules for form fields, including both simple constraints and cross-field validation.

**Priority:** High

**Acceptance Criteria:**
- Admin can configure per-field validations:
  - **Required** (always required, independent of rules)
  - **Min length / Max length** (for string/text fields)
  - **Min value / Max value** (for numeric fields)
  - **Pattern / regex** (for string fields)
  - **Custom expression** (e.g., `end_date > start_date`)
- Cross-field validations use the same expression format as rules
- Validation messages are configurable per field
- Validation runs on the frontend (for instant feedback) and is re-evaluated on the backend (for security)
- Invalid submissions are rejected with clear error messages

### FR-010: Configure Form Layout

**Description:** Tenant Admin can organize fields into sections, columns, and tabs to create a structured form layout.

**Priority:** Medium

**Acceptance Criteria:**
- Admin can group fields into named **sections** (e.g., "General Information", "Financial Details")
- Within each section, fields can be arranged in **columns** (1, 2, or 3 columns)
- Sections can be organized into **tabs**
- Sections can be marked as **collapsible** (expandable/collapsible)
- Admin can drag-and-drop fields between sections
- Layout structure is stored in the form definition JSONB under a `layout` key
- The runtime renderer renders the form according to this layout structure

### FR-011: Configure Form Role Access (Per-Tenant)

**Description:** Each tenant independently configures which of their roles can access a form. For global forms, this is configured by Tenant Admin. For tenant forms, this is configured by the same Tenant Admin.

**Priority:** High

**Acceptance Criteria (Global Forms — Tenant Admin manages role access for their tenant):**
- Tenant Admin navigates to the global form's access configuration page
- Admin sees a "Role Access" section; only roles belonging to their tenant are shown
- Admin can select one or more roles from a multi-select dropdown of their tenant's roles
- These assignments affect ONLY their tenant — other tenants' role assignments remain independent
- If no roles are assigned by the tenant, the form is not accessible to any user in that tenant
- Role assignments are stored in the `sys_form_tenant_role` join table (form_id + tenant_id + role_id)
- A tenant can modify their own role assignments at any time without affecting other tenants

**Acceptance Criteria (Tenant Forms — Tenant Admin manages role access):**
- Tenant Admin creates a form scoped to their tenant
- Role access works the same way, but scope is limited to the form's owning tenant
- Same UI and storage mechanism as global form role assignments

**Acceptance Criteria (End User Experience):**
- When a user logs in, the system loads all forms where:
  - The form is global AND the user's tenant has assigned at least one of the user's roles to that form, OR
  - The form is a tenant form AND the user's tenant owns the form AND the user has an assigned role for it
- Navigation menus only show accessible forms
- Direct URL access to an unauthorized form returns a 403 error

### FR-014: Configure Sub-Forms (Multi-Level Nested Forms)

**Description:** Both System Admin and Tenant Admin can configure sub-forms for a form definition based on one2many relationships on the underlying table. A sub-form appears as a tab inside the parent form and allows drill-down navigation with breadcrumbs. Sub-forms can themselves have sub-forms, enabling multi-level nesting (e.g., Order → Order Line → Tax Entry).

**Priority:** High

**Acceptance Criteria:**
- The Form Designer includes a **"Sub-Forms" tab** in addition to Fields, Rules, Layout, Validation, and Access
- The Sub-Forms tab displays all one2many relationships available on the table (e.g., `order_lines` if Order has a one2many to Line)
- For each relationship, the admin can:
  - Toggle it on/off (include it as a sub-form tab)
  - Select which form definition to use for rendering the sub-form (e.g., the "Order Line" form)
  - Configure the tab label (e.g., "Order Lines", "Items")
  - Set the display order of tabs
- The admin cannot select a sub-form that would create a circular reference (a form referencing itself)
- The admin can preview the sub-form tab layout in the Form Designer preview
- A sub-form on the Order form references a form like "Order Line", which itself can have sub-forms (e.g., "Tax Entry")
- Sub-form configuration is stored in the form definition JSONB under a `sub_forms` key
- The breadcrumb path context is automatically inferred from the sub-form chain (Order > Order Line > Tax Entry)

### FR-011b: Browse & Enable Global Forms (Tenant Admin)

**Description:** Tenant Admin can view all global forms available in the system and configure which roles in their tenant can access them.

**Priority:** High

**Acceptance Criteria:**
- Tenant Admin sees a "Global Forms" section in their admin panel
- All global forms (created by System Admin) are listed with their table, description, and where clause
- For each global form, Tenant Admin can see the current role assignments for their tenant
- Tenant Admin can click "Configure Access" on any global form to assign/remove role access for their tenant
- Tenant Admin cannot modify the form's fields, layout, rules, or where clause (those are controlled by System Admin)
- Global forms that have no role access configured are marked as "Not accessible by any user" for that tenant

### FR-012: Clone Form

**Description:** Both System Admin and Tenant Admin can clone an existing form definition to use as a starting point for a new form.

**Priority:** Low

**Acceptance Criteria:**
- Admin clicks "Clone" on an existing form
- Admin enters a new form code and label
- All field configurations, rules, validations, and layout are copied
- Role assignments are NOT copied (must be re-assigned)
- Where clause is copied (but can be modified)
- The cloned form is saved as a new independent form definition
- System Admin cloning a global form creates another global form
- Tenant Admin cloning a tenant form creates another tenant form (within their tenant)
- Tenant Admin cloning a global form creates a tenant form (the clone belongs to their tenant)

### FR-013: Preview Form

**Description:** Both System Admin and Tenant Admin can preview the form as it would appear to end users, including testing rules and validation with sample data.

**Priority:** Medium

**Acceptance Criteria:**
- "Preview" button in the form designer opens a new tab or modal
- The form is rendered exactly as it would appear to end users
- Admin can enter test data and see rules/vallidations trigger in real-time
- Preview uses the same Runtime Form Renderer component that end users see
- Preview respects the form's where clause and field rules

---

## System C: Runtime Form Renderer

### FR-014: Dynamic Form Rendering (Two-Request Pattern)

**Description:** When an end user navigates to a form, the frontend makes two internal requests. The **first request loads the form definition** (structure, fields, layout, rules — changes rarely, cached aggressively). The **second request loads the record data** (changes frequently, always fetched fresh). The frontend has an internal abstraction layer (hook/service) that orchestrates these two requests transparently.

**Priority:** Critical

**Acceptance Criteria:**
- When a user navigates to a form (e.g., "Sales Orders"), the frontend internally makes:
  1. `GET /api/runtime/forms/{formCode}/definition` — returns the **form definition bundle** (form metadata + field config + rules + validations + layout + sub-forms + model columns). This response is cached in the frontend (React Query / Zustand) and only re-fetched when the form definition changes.
  2. `GET /api/runtime/forms/{formCode}/records?page=1` — returns the **paginated records** (data only, no form structure).
- The frontend has an internal hook (e.g., `useForm(formCode)`) that orchestrates these two requests:
  - Returns `{ formDefinition, records, isLoading, error }` to the component
  - Caches `formDefinition` across navigations (form structure rarely changes)
  - Always fetches fresh `records` on navigation
- When a user opens a specific record for editing, the frontend makes:
  1. `GET /api/runtime/forms/{formCode}/definition` — from cache (no network request if already cached)
  2. `GET /api/runtime/forms/{formCode}/records/{id}` — returns record data + sub-form child records for populating tabs
- When a user navigates from one record to another in the same form, only request #2 is made (definition is already cached)
- The form renders fully within 2 seconds. If the definition is cached, rendering is instant + data loading.
- The frontend abstraction layer handles loading states (skeleton for data, but definition renders immediately from cache)
- Form definition cache is invalidated when the Tenant Admin saves changes to the form

### FR-015: List View (Data Grid)

**Description:** Each form has a corresponding list view showing records matching the form's where clause, with sortable/filterable columns.

**Priority:** High

**Acceptance Criteria:**
- List view displays records matching the form's where clause
- Columns shown correspond to the fields configured in the form (first N visible fields)
- Users can sort by any column
- Users can search/filter records
- Pagination is supported (default 20 per page)
- Clicking a row opens the record in the form (edit mode)
- "Create New" button opens an empty form (create mode)

### FR-016: Create Record

**Description:** User can create a new record through the dynamic form. The system validates the data against the configured rules and persists it to the underlying table.

**Priority:** Critical

**Acceptance Criteria:**
- Empty form is presented based on the form definition
- Field defaults are pre-populated
- Real-time validation occurs as the user fills fields (client-side)
- Client-side rules (visibility, read-only, required) are evaluated as fields change
- On submit, data is sent to the backend CRUD API
- Backend re-evaluates all validations and rules before persisting
- On success, user is redirected to the record view
- On validation failure, errors are shown inline on the relevant fields
- The where clause field (e.g., `order_type`) is automatically set to the form's configured value on create

### FR-017: Edit Record

**Description:** User can open an existing record and edit its fields. The form loads with the record's current values and applies all rules dynamically.

**Priority:** Critical

**Acceptance Criteria:**
- Record data is loaded from the backend and populated in the form
- All rules (visibility, read-only, required) are evaluated based on current data
- User can modify fields and submit
- Backend validates and persists the changes
- Audit log entry is created for the update

### FR-018: Delete Record

**Description:** User can delete a record from the list view or form view, with confirmation.

**Priority:** Medium

**Acceptance Criteria:**
- "Delete" button is available (subject to role permissions)
- Confirmation dialog is shown before deletion
- On confirm, the record is soft-deleted (is_active = false)
- Record is removed from the list view
- Audit log entry is created for the deletion

### FR-019: Role-Based Form Access

**Description:** Users only see and can access forms that are assigned to their roles.

**Priority:** High

**Acceptance Criteria:**
- On login, the system loads forms assigned to the user's roles
- Navigation menus only show accessible forms
- Direct URL access to a form the user is not authorized for returns a 403 error
- Role assignments are enforced on both frontend (navigation) and backend (API)

### FR-020: Global Form Search in Header

**Description:** A search bar in the application header allows users to quickly find and navigate to any form by typing its name. Search results are filtered to only show forms the user has permission to access.

**Priority:** High

**Acceptance Criteria:**
- A search bar is prominently displayed in the application header (top navigation bar)
- The search bar supports keyboard shortcut (e.g., `Ctrl+K` or `Cmd+K`) for quick access
- As the user types, search results appear in a dropdown below the search bar
- Search matches against form label and form code (partial match, case-insensitive)
- Search results only include forms that:
  - The form is accessible to the user's tenant AND
  - The user's role has been granted access by their Tenant Admin
- Each search result displays: form label, form code, and the table label it is based on
- Clicking a search result navigates the user directly to that form's list view
- Pressing `Escape` closes the search dropdown
- Clicking outside the search dropdown closes it
- The search bar supports typing a full form code (e.g., `sales_order`) and navigating directly via `Enter`
- The search is performed client-side against the cached list of accessible forms (no additional API call needed)
- If the cached form list is not yet loaded, the search triggers a single request to load it

### FR-021: Multi-Level Sub-Form Navigation with Breadcrumbs

**Description:** When a form has configured sub-forms (based on one2many relationships), the runtime renderer displays them as tabs within the form. Users can drill down through multiple levels of nested sub-forms, with breadcrumbs showing their current position in the hierarchy.

**Priority:** High

**Acceptance Criteria:**
- When viewing a parent record (e.g., Order), the form displays **tabs** for each configured sub-form (e.g., "Order Lines")
- Each sub-form tab shows an inline editable grid of child records (e.g., line items for this order)
- Users can add, edit, or remove child records directly in the grid
- Clicking a child record in the grid navigates to that record's **full form view** (e.g., Order Line Form)
- The Order Line Form itself shows its own sub-form tabs (e.g., "Tax Entries")
- A **breadcrumb trail** at the top of the page shows the navigation hierarchy:
  - Example: `Orders > #1024 > Order Lines > #3 > Tax Entries`
  - Each breadcrumb segment is clickable for quick navigation back
- The breadcrumb is built from the sub-form chain defined in the form configuration
- **Only one level below is shown at a time** — while viewing Order, you see Line tabs. When you drill into a Line, you see Tax tabs. The parent context remains in the breadcrumb.
- The consolidated bundle API for a sub-form record includes:
  - The sub-form definition + its model definition + the record data
  - The parent context (parent form code + parent record ID + parent label)
  - The sub-forms' definitions (for rendering tabs)
  - The child records for each sub-form (for populating grids)
- Creating a new record in a sub-form auto-assigns the parent foreign key (e.g., new Order Line gets the current Order's ID)
- Navigating back via breadcrumb does not lose unsaved changes (warn user if there are pending changes)
- The sub-form tab UI handles empty states (no child records yet) and loading states gracefully

### FR-022: Form Toolbar with Record Actions

**Description:** Every record view (both parent and child) has a persistent toolbar at the top with actions for managing the current record and navigating between records. The toolbar adapts based on context (list view vs record view, create vs edit mode).

**Priority:** High

**Acceptance Criteria:**
- A **toolbar** is displayed at the top of every record view (below the breadcrumb, above the form fields)
- The toolbar contains the following action buttons:
  - **Create New** (`+`) — Opens an empty form to create a new record. Visible in both list view and record view.
  - **Save** — Saves the current record. Enabled only when there are unsaved changes. Shows a loading state during save.
  - **Save & New** — Saves the current record and immediately opens an empty form for the next record. Available in create mode.
  - **Discard / Revert** — Reverts all unsaved changes back to the last saved state. Shows a confirmation dialog if there are unsaved changes. Disabled when there are no unsaved changes.
  - **Refresh** — Reloads the current record data from the server. Shows a confirmation dialog if there are unsaved changes (discard changes and refresh?).
  - **Delete** — Deletes the current record. Shows a confirmation dialog. Soft-deletes on confirm.
  - **Previous / Next** — Navigates to the previous or next record in the current list/page context. Disabled at the boundaries (first/last record). Keyboard shortcuts: `Alt+Left` / `Alt+Right`.
- In **list view**, the toolbar shows: Create New, Refresh, and pagination controls
- In **record view (edit mode)**, the toolbar shows: Create New, Save, Discard, Refresh, Delete, Previous, Next
- In **record view (create mode)**, the toolbar shows: Save, Save & New, Discard. Previous/Next are hidden.
- The toolbar shows the **record count context** (e.g., "Record 3 of 15") when Previous/Next are visible
- **Keyboard shortcuts** are supported:
  - `Ctrl+S` — Save
  - `Ctrl+Shift+S` — Save & New
  - `Escape` — Discard changes (with confirmation if dirty)
  - `F5` — Refresh
  - `Alt+Left` — Previous record
  - `Alt+Right` — Next record
- The toolbar actions are **subject to role permissions** — if the user's role does not have delete permission, the Delete button is hidden
- The toolbar is **sticky/fixed** at the top so it remains visible when scrolling through long forms
- Toolbar uses the same consolidated two-request pattern: definition is cached, data requests are fresh

### FR-023: Role-Based Row-Level Data Access

**Description:** All data loaded in any form must be filtered based on the user's context — their tenant, their role, and optionally their user ID or other attributes. A user should only ever see records they are authorized to access.

**Priority:** Critical

**Acceptance Criteria:**
- **Tenant isolation is always enforced:** Every query on a dynamic table includes `WHERE tenant_id = :currentTenant`. A user in Tenant A can never see records belonging to Tenant B, even if they manually craft API requests.
- **Form-level tenant isolation:** Global forms do not share data between tenants. Two tenants using the same global form see completely separate data sets.
- **Role-based row filters (NEW):** In the Form Designer, admins can configure additional data filters per role. These filters are appended to every data query for users with that role.
- **Row filter format:** Same condition format as field rules:
  - `{ "field": "created_by", "operator": "equals", "value": "{current_user_id}" }` — user sees only their own records
  - `{ "field": "region", "operator": "equals", "value": "{current_user_region}" }` — regional filtering
  - `{ "field": "status", "operator": "not_equals", "value": "internal" }` — hide sensitive records from certain roles
  - Dynamic variables: `{current_user_id}`, `{current_user_role}`, `{current_tenant_id}`, and any custom user attribute exposed in the JWT
- **Multiple filters are combined with AND** — all conditions must be met
- **Filter inheritance:** When accessing a sub-form record, the parent's row filters plus the child's row filters are both applied
- **Backend enforcement is mandatory:** Row filters are applied on the backend server-side. The frontend never receives data the user shouldn't see.
- **Filter violation attempt:** If a user tries to access a record ID that exists but is filtered out by their role, the system returns 404 (not 403 — to avoid leaking the record's existence).
- **Default filter:** If no row filter is configured for a user's role, they see all records within their tenant (subject to the tenant isolation).
- **Admin exception:** System Admin can optionally bypass row filters (see all records) when explicitly toggling into an "audit mode."

**Examples of role-based filtering:**

| Role | Row Filter | Effect |
|------|-----------|--------|
| Sales Rep | `created_by = {current_user_id}` | Sees only orders they created |
| Sales Manager | *(no filter)* | Sees all orders in the tenant |
| Regional Manager | `region = {current_user_region}` | Sees only orders in their region |
| Support Agent | `status != internal` | Sees all orders except those marked internal |
| Accountant | `grand_total > 1000` | Sees only orders above $1000 |

---

# Non-Functional Requirements

## NFR-001: Performance

- Consolidated form bundle loading (form + model + records): < 1.5 seconds for first page of 20 records (with caching)
- Form rendering: < 2 seconds for forms with up to 50 fields
- Rule evaluation: < 100ms per rule evaluation cycle
- Create/Update record API: < 1 second
- Header form search response (client-side from cache): < 100ms
- DDL execution (create table): < 3s
- DDL execution (add column): < 1s

## NFR-002: Security

- All CRUD operations require valid JWT authentication
- Field-level rules (read-only) are enforced on the backend — frontend rules are UX-only
- Where clauses are validated to prevent SQL injection or data leakage
- Role-based access is enforced on both frontend (UI) and backend (API)
- Tenant isolation is enforced on every query — `WHERE tenant_id = :currentTenant` is always appended
- **Role-based row filters are enforced server-side** — never send data to the client that fails the row filter conditions
- Row filters are applied to ALL data queries (list, get, child records, exports, etc.)
- If a record is filtered out by row filters, return 404 (not 403) to avoid leaking record existence
- Tenant Admin can only create forms for tables/roles within their tenant
- Table Designer is only accessible to System Admin role
- Dynamic variables in row filters (e.g., `{current_user_id}`) are resolved server-side from JWT claims — client cannot manipulate them

## NFR-003: Scalability

- The system should support up to 500 table definitions per instance
- The system should support up to 1000 form definitions per table
- The system should support up to 100 fields per form without performance degradation
- List views should handle up to 1 million records with pagination

## NFR-004: Maintainability

- All form and table configurations are pure metadata — no generated code to maintain
- Schema changes are logged and versioned for audit and rollback
- Field rules use a consistent expression format that is human-readable in the JSONB storage

## NFR-005: Reliability

- Creating a table and its columns must be transactional — if DDL fails, the metadata is rolled back
- Form configuration changes do not affect other forms using the same table
- Deleting a column warns about all forms that use that column

---

# User Stories

## US-001: System Admin Creating a Table

> As a System Admin,  
> I want to create a new database table with custom columns through a UI,  
> So that new business entities can be tracked without developer involvement.

## US-002: System Admin Managing Columns

> As a System Admin,  
> I want to add, edit, and remove columns on a table,  
> So that the data model evolves with business requirements.

## US-003: Tenant Admin Designing a Sales Order Form

> As a Tenant Admin,  
> I want to create a Sales Order form from the Order table with a where clause `order_type = 'sales'`,  
> So that my sales team only sees sales orders and not purchase orders.

## US-004: Tenant Admin Creating Field Rules

> As a Tenant Admin,  
> I want the `discount_percent` field to only be visible when `customer_tier` equals "Gold",  
> So that discounts are only offered to premium customers.

## US-005: Tenant Admin Assigning Role Access

> As a Tenant Admin,  
> I want to assign the "Sales Manager" and "Sales Rep" roles to the Sales Order form,  
> So that only sales personnel can access it.

## US-006: End User Creating a Record

> As a Sales Rep,  
> I want to open the Sales Order form and create a new order,  
> So that I can record customer purchases in the system.

## US-007: End User Seeing Rules in Action

> As a Sales Rep,  
> I want the discount field to automatically appear when I select a Gold-tier customer,  
> So that I know when discounts are applicable.

## US-008: Tenant Admin Designing Form Layout

> As a Tenant Admin,  
> I want to organize 30+ fields into sections and columns,  
> So that the form is easy to navigate for end users.

## US-009: System Admin Creating a Global Form

> As a System Admin,  
> I want to create a "Comment" form from the Comment table as a global form,  
> So that all tenants can use it without each one building their own.

## US-010: Tenant Admin Configuring Access to a Global Form

> As a Tenant Admin for Tenant A,  
> I want to assign the "Sales" role to the global Comment form,  
> So that only my sales team can access it — without affecting Tenant B who assigns it to "Desk Team".

## US-011: End User Seeing Only Their Tenant's Data in a Global Form

> As a Sales Rep in Tenant A,  
> I want to use the global Comment form and see only comments created by my tenant,  
> So that my data is isolated from Tenant B even though we use the same form.

## US-012: Tenant Admin Independent Role Configuration

> As a Tenant Admin,  
> I want to assign different roles to the same global form than what another tenant assigned,  
> So that each tenant's access configuration is independent and does not interfere.

## US-013: End User Searching for a Form

> As a Sales Rep,  
> I want to press `Ctrl+K` and search for "Sales Order" in the header search bar,  
> So that I can quickly navigate to the Sales Order form without browsing menus.

## US-014: End User Navigating Directly by Form Code

> As a power user,  
> I want to type `sales_order` in the search bar and press Enter,  
> So that I can quickly jump to a form I know by its code.

## US-015: End User Loading a Form with a Single Request

> As an end user,  
> I want the form to load everything it needs (fields, layout, records) in a single request,  
> So that the form renders fast without multiple loading spinners.

## US-016: Tenant Admin Configuring Sub-Forms

> As a Tenant Admin designing the Order form,  
> I want to add the Order Lines form as a sub-form tab,  
> So that users can see and manage line items directly within the Order view.

## US-017: End User Managing Child Records via Sub-Form Tabs

> As a Sales Rep viewing an Order,  
> I want to see an "Order Lines" tab with an editable grid of line items,  
> So that I can add, edit, or remove items without leaving the Order form.

## US-018: End User Drilling Down Through Multi-Level Sub-Forms

> As a Sales Rep viewing an Order Line,  
> I want to see a "Tax Entries" tab showing the tax breakdown for that line,  
> So that I can review and manage taxes at the line-item level.

## US-019: End User Navigating with Breadcrumbs

> As a Sales Rep,  
> I want to see a breadcrumb trail showing "Orders > #1024 > Order Lines > Line #3",  
> So that I know exactly where I am and can click any breadcrumb to jump back.

## US-020: End User Using Form Toolbar Actions

> As a Sales Rep editing an Order,  
> I want a toolbar with Save (Ctrl+S), Discard (Escape), Previous/Next (Alt+arrows), and Refresh (F5),  
> So that I can efficiently manage records without hunting for buttons in menus.

## US-021: End User Navigating Between Records

> As a Sales Rep,  
> I want to click "Next" or press Alt+Right to move to the next order,  
> So that I can quickly review multiple records in sequence.

---

# User Flow

## Flow 1: Table Designer (System Admin)

```
Admin Panel → Table Designer
     │
     ├── View All Tables (paginated list)
     │       │
     │       ├── Click "Create Table"
     │       │       │
     │       │       └── Fill: code, label, plural label, description → Save
     │       │               │
     │       │               └── Redirect to Table Detail (column management)
     │       │
     │       └── Click Existing Table
     │               │
     │               └── Table Detail View
     │                       │
     │                       ├── Add Column → Fill: code, label, type, constraints → Save
     │                       ├── Edit Column → Modify metadata → Save
     │                       ├── Delete Column → Confirm → Remove
     │                       ├── Reorder Columns (drag & drop)
     │                       ├── View Schema History
     │                       ├── Deactivate Table → Confirm
     │                       └── View Associated Forms
     │
     └── Search/Filter/Sort tables
```

## Flow 2a: Form Designer — System Admin (Creating Global Forms)

```
Admin Panel → Form Designer
     │
     ├── View All Forms (global + all tenant forms visible to super admin)
     │       │
     │       ├── Click "Create Form"
     │       │       │
     │       │       └── Select Scope: "Global Form" OR "Tenant Form"
     │       │               │
     │       │               └── Select Table → Enter: code, label, where clause → Save
     │       │                       │
     │       │                       └── Form Designer Interface
     │       │                               │
     │       │                               ├── Fields Tab: toggle fields, reorder, set labels/defaults
     │       │                               ├── Rules Tab: configure conditions & actions per field
     │       │                               ├── Layout Tab: organize sections, columns, tabs
     │       │                               ├── Validation Tab: set constraints & custom validations
     │       │                               ├── Access Tab: NOT available (role assignment is per-tenant)
     │       │                               ├── Preview: test the form with sample data
     │       │                               └── Save
     │       │
     │       ├── Click Existing Global Form → Edit Form
     │       ├── Clone Form → Enter new code/label → Clone
     │       └── Delete Form → Confirm
     │
     └── Search/Filter/Sort forms
```

## Flow 2b: Form Designer — Tenant Admin (Creating Tenant Forms & Configuring Global Forms)

```
Admin Panel → Form Designer
     │
     ├── Tab: "My Forms" (tenant-specific forms created by this admin)
     │       │
     │       ├── Click "Create Form"
     │       │       │
     │       │       └── Select Table → Enter: code, label, where clause → Save
     │       │               │
     │       │               └── Form Designer Interface (same as Flow 2a, but scope = tenant)
     │       │                       │
     │       │                       ├── Fields Tab
     │       │                       ├── Rules Tab
     │       │                       ├── Layout Tab
     │       │                       ├── Validation Tab
     │       │                       ├── Access Tab: assign roles from this tenant
     │       │                       ├── Preview
     │       │                       └── Save
     │       │
     │       ├── Click Existing Form → Edit Form
     │       ├── Clone Form → Enter new code/label → Clone
     │       └── Delete Form → Confirm
     │
     ├── Tab: "Global Forms" (system-created, available to all tenants)
     │       │
     │       ├── Browse available global forms
     │       ├── For each form: "Configure Access"
     │       │       │
     │       │       └── Select roles from this tenant that can access this form
     │       │
     │       └── Cannot edit fields/layout/rules (read-only for Tenant Admin)
     │
     └── Search/Filter/Sort forms
```

## Flow 3: Runtime Form Usage (End User)

```
User Login
     │
     ├── Application Header
     │       │
     │       ├── Global Form Search Bar (Ctrl+K / Cmd+K)
     │       │       │
     │       │       ├── Type form name → Dropdown shows matching forms (filtered by role access)
     │       │       ├── Click result → Navigate to form list view
     │       │       └── Type form code + Enter → Direct navigation
     │       │
     │       └── Navigation Menu (shows forms based on user's roles)
     │
      ├── Select Form (via menu or search)
      │       │
      │       ├── FRONTEND HOOK: useForm(formCode)
      │       │       │
      │       │       ├── (1) GET .../definition → cached 5 min → { form + model }
      │       │       ├── (2) GET .../records?page=1 → fresh → { records }
      │       │       └── Merge → { formDefinition, records, isLoading }
      │       │
      │       └── List View (no breadcrumb)
      │               │
      │               ├── Records sorted/filtered/paginated
      │               ├── Click "Create New" → Empty from cache → Submit → Save → Redirect
      │               │
      │               └── Click Existing Record
      │                       │
      │                       ├── (1) definition → from cache (no network)
      │                       └── (2) GET .../records/{id} → fresh
      │                               │
      │                               └── { record + sub_form_records + parent + breadcrumb }
      │                                       │
      │                                       └── Record View
      │                                               │
      │                                               ├── Breadcrumb: Orders > #1024
      │                                               ├── Form fields in layout
      │                                               ├── Sub-Form Tabs: "Order Lines"
      │                                               │       └── Inline grid (child records)
      │                                               │               └── Click row → Drill down
      │                                               │                       └── (2) GET .../order_line/records/{lineId}
      │                                               │                               └── { record + sub_form_records + parent + breadcrumb }
      │                                               │                                       └── Child View
      │                                               │                                               ├── Breadcrumb: ... > #1024 > Line #3
      │                                               │                                               ├── Fields
      │                                               │                                               └── Sub-Form Tabs: "Tax Entries"
      │                                               │
      │                                               └── Save / Delete
      │
      └── Navigating between records in same form: only (2) re-fetched. (1) cached.
```

---

# Scope

## Included

- **Table Designer** — Full CRUD for table definitions and columns via admin UI
- **Form Designer** — Full CRUD for form definitions (global + tenant-scoped) with field configuration, rules, layout, validation, and role access via admin UI
- **Runtime Form Renderer** — Dynamic list view and form view for end users, with client-side rule evaluation and CRUD operations
- **Expression-based field rules** — Simple condition/action rules for visibility, read-only, required (with expression format for future extensibility)
- **Where clause filtering** — Data scoping per form variant
- **Role-based form access** — Forms are gated by role assignments
- **Dynamic table creation** — Physical PostgreSQL tables created from metadata
- **Column alteration** — Add/modify columns with ALTER TABLE DDL
- **Global header form search** — `Ctrl+K` / `Cmd+K` search bar for navigating to forms
- **Two-request loading pattern** — Form definition (cached) and record data (fresh) loaded via separate internal endpoints, abstracted by a frontend hook
- **Multi-level sub-form configuration** — Form Designer includes Sub-Forms tab for configuring one2many child relationships
- **Breadcrumb navigation** — Clickable breadcrumb trail showing hierarchy (Order > Order Line > Tax Entry)
- **Inline sub-form grids** — Child records shown as editable grids within parent form tabs
- **Drill-down navigation** — Clicking a child record opens its full form with its own sub-forms
- **Form toolbar** — Create, Save, Discard, Refresh, Delete, Previous/Next record actions with keyboard shortcuts
- **Role-based row-level data access** — Every query filters by tenant + role-specific row filters. Users only see data they're authorized to access.

## Excluded

- **Advanced expression engine (JSON Logic)** — Delayed for future enhancement; MVP uses simple condition/action format
- **Drag-and-drop form designer canvas** — MVP uses structured configuration UI (field selector, rule builder); full drag-and-drop placement of fields on a visual canvas is future
- **Form import/export** — Exporting/importing form definitions as JSON files between tenants/instances
- **Form versioning and rollback** — Ability to save form version history and rollback to a previous version
- **Conditional sections/tabs** — Hiding/showing entire sections or tabs based on rules (future enhancement)
- **Computed/formula fields** — Fields whose values are derived from expressions (future enhancement)
- **Multi-language / i18n support** for form labels
- **Audit log viewer** in the admin UI
- **Bulk operations** on list views
- **Advanced grid configuration** (column freezing, grouping, summaries)

---

# UI / UX Requirements

## Admin Panel — Table Designer

- **Layout:** Left sidebar with "Table Designer" navigation link
- **Table List:** MUI DataGrid with search, sort, and filter
- **Create/Edit Table:** Dialog or dedicated page with a form
- **Column Management:** Inline editable table/grid with drag-and-drop reorder
- **Column Form:** Dialog with fields for code, label, type dropdown, type-specific constraints
- **Schema History:** Timeline/feed view with change entries

## Admin Panel — Form Designer

- **Layout:** Two-panel design — left panel has form configuration tabs, right panel has live preview
- **Tabs:** Fields, Rules, Layout, Validation, Sub-Forms, Row Access, Access
- **Fields Tab:** Sortable list of all table columns with toggle switches and inline label editing
- **Rules Tab:** Per-field rule configuration with condition builder (field dropdown + operator dropdown + value input) and action selection
- **Layout Tab:** Section/column/tab configuration with drag-and-drop field assignment
- **Validation Tab:** Per-field constraint configuration
- **Sub-Forms Tab:** Lists all one2many relationships on the table. Admin can toggle each on/off, select which form to use for rendering, set tab labels, and reorder tabs. Shows the sub-form chain preview (e.g., Order → Order Line → Tax Entry)
- **Row Access Tab:** Configure which records each role can see using the same condition builder as field rules. Each row filter has: role selector, condition field, operator, value (with dynamic variables like `{current_user_id}`, `{current_user_role}`, `{current_user_region}`). Example: `created_by = {current_user_id}` shows only self-created records.
- **Access Tab:** Role multi-select dropdown (which roles can access this form at all)
- **Preview:** Live preview of the form rendered in a mockup container, including sub-form tabs

## End User — Runtime Forms

- **Global Header Search Bar:** Prominently placed in the top navigation bar. Supports `Ctrl+K` / `Cmd+K` keyboard shortcut. Search-as-you-type dropdown shows matching forms (by label or code) filtered by the user's role access. Click a result or press Enter to navigate.
- **Navigation Menu:** Left sidebar or top bar showing all accessible forms, organized logically
- **List View:** MUI DataGrid or table with sortable columns, search bar, pagination, and "Create New" button. Toolbar shows: Create New, Refresh, pagination controls.
- **Record View Toolbar:** Sticky toolbar below breadcrumb with actions:
  - **Create New** (`+`) — Opens empty form
  - **Save** (`Ctrl+S`) — Save changes (enabled only when dirty)
  - **Save & New** (`Ctrl+Shift+S`) — Save and open next (create mode only)
  - **Discard** (`Escape`) — Revert to last saved state (with confirmation)
  - **Refresh** (`F5`) — Reload from server (with confirmation if dirty)
  - **Delete** — Soft-delete with confirmation
  - **Previous / Next** (`Alt+Left` / `Alt+Right`) — Navigate records. Shows "Record 3 of 15" context.
- **Record View (Parent Level):** Breadcrumb > Toolbar > Form fields in configured layout > Sub-form tabs
- **Sub-Form Tabs:** Horizontal tabs below the record fields. Each tab shows an inline editable grid of child records. Users can add/edit/remove child records inline or click a row to drill down.
- **Child Record View:** Full form view for a child record with breadcrumb, toolbar, fields, and its own sub-form tabs
- **Breadcrumb Navigation:** Each breadcrumb segment is clickable. Navigating back warns about unsaved changes.
- **Form Fields:** MUI components (TextField, Select, DatePicker, Checkbox, etc.) based on field type
- **Rules:** Fields hide/show, enable/disable, required/optional dynamically as user enters data
- **Validation:** Inline error messages below fields, submit button disabled until valid
- **Two-Request Loading:** Form definition cached (5 min), record data always fresh — abstracted by `useForm()` hook

## Theme & Responsiveness

- Follow existing MUI 5 theme and typography
- Desktop-first with reasonable mobile adaptation (single column layout on mobile)

---

# API Requirements

## Endpoints Overview

All endpoints are prefixed with `/api`.

### Metadata / Configuration APIs

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/metadata/tables` | List all table definitions | System Admin |
| POST | `/api/metadata/tables` | Create a new table definition | System Admin |
| GET | `/api/metadata/tables/{id}` | Get table definition with columns | System Admin |
| PUT | `/api/metadata/tables/{id}` | Update table definition metadata | System Admin |
| DELETE | `/api/metadata/tables/{id}` | Soft-delete table definition | System Admin |
| POST | `/api/metadata/tables/{id}/columns` | Add a column | System Admin |
| PUT | `/api/metadata/tables/{id}/columns/{colId}` | Update column definition | System Admin |
| DELETE | `/api/metadata/tables/{id}/columns/{colId}` | Remove column | System Admin |
| PUT | `/api/metadata/tables/{id}/columns/reorder` | Reorder columns | System Admin |
| GET | `/api/metadata/tables/{id}/history` | Get schema change history | System Admin |

### Form Definition APIs

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/metadata/forms` | List form definitions (filtered by scope/tenant) | System Admin, Tenant Admin |
| POST | `/api/metadata/forms` | Create form definition (global or tenant) | System Admin, Tenant Admin |
| GET | `/api/metadata/forms/{id}` | Get form definition with all config | System Admin, Tenant Admin |
| PUT | `/api/metadata/forms/{id}` | Update form definition | System Admin, Tenant Admin |
| DELETE | `/api/metadata/forms/{id}` | Delete form definition | System Admin, Tenant Admin |
| POST | `/api/metadata/forms/{id}/clone` | Clone form definition | System Admin, Tenant Admin |
| GET | `/api/metadata/forms/{id}/available-tables` | Get tables available for form creation | System Admin, Tenant Admin |

### Per-Tenant Form Role Access APIs

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/metadata/forms/{id}/tenant-roles` | Get role assignments for current tenant | Tenant Admin |
| PUT | `/api/metadata/forms/{id}/tenant-roles` | Set role assignments for current tenant | Tenant Admin |
| GET | `/api/metadata/forms/{id}/global-tenant-roles` | List all tenants' role assignments (for System Admin visibility) | System Admin |

### Runtime APIs (Two-Request Pattern)

The runtime uses a **two-request pattern**. The frontend internally orchestrates:
1. **Form Definition Request** — returns the form structure (fields, layout, rules, sub-forms) + model definition. This response is **cached aggressively** on the frontend since form structure rarely changes.
2. **Data Request** — returns records or a single record. This is **always fetched fresh**.

The frontend has an internal abstraction layer (e.g., a `useForm(formCode)` hook) that handles this orchestration transparently. The component never sees the two requests — it receives `{ formDefinition, records, isLoading, error }`.

| Method | Endpoint | Description | Cached? | Auth |
|--------|----------|-------------|---------|------|
| GET | `/api/runtime/forms` | List all forms accessible to current user (for menu + header search) | Yes (session) | Authenticated |
| GET | `/api/runtime/forms/{formCode}/definition` | **Form definition bundle:** form config + fields + rules + layout + sub-forms + model columns | Yes (aggressive) | Authenticated |
| GET | `/api/runtime/forms/{formCode}/records` | Paginated records list (data only) | No (always fresh) | Authenticated |
| GET | `/api/runtime/forms/{formCode}/records/{id}` | Single record + sub-form child records for tab grids | No (always fresh) | Authenticated |
| POST | `/api/runtime/forms/{formCode}/records` | Create record | N/A | Authenticated |
| PUT | `/api/runtime/forms/{formCode}/records/{id}` | Update record | N/A | Authenticated |
| DELETE | `/api/runtime/forms/{formCode}/records/{id}` | Soft-delete record | N/A | Authenticated |

#### Form Definition Response (`GET /api/runtime/forms/{formCode}/definition`)

This response is assembled by the backend from the normalized tables and cached on the frontend. It contains everything needed to render the form structure — but NO record data.

```json
{
  "success": true,
  "data": {
    "form": {
      "code": "sales_order",
      "label": "Sales Orders",
      "description": "...",
      "where_clause": { "field": "order_type", "operator": "equals", "value": "sales" },
      "fields": [
        {
          "column_code": "customer_id",
          "label": "Customer",
          "type": "many2one",
          "relation_table": "md_business_partner",
          "visible": true,
          "read_only": false,
          "required": true,
          "position": 1,
          "default_value": null,
          "placeholder": "Search customer...",
          "rules": [
            {
              "condition_field": "customer_tier",
              "condition_operator": "equals",
              "condition_value": "Gold",
              "action": "show"
            }
          ],
          "validations": [
            { "type": "required", "value": null, "message": "Customer is required" }
          ]
        }
      ],
      "layout": {
        "sections": [
          {
            "code": "details",
            "label": "Order Details",
            "collapsible": false,
            "columns": 2,
            "fields": ["customer_id", "order_date"]
          }
        ]
      },
      "sub_forms": [
        {
          "relation_code": "order_id",
          "child_form_code": "order_line_form",
          "label": "Order Lines",
          "display_as": "tab",
          "position": 1
        }
      ]
    },
    "model": {
      "code": "order",
      "label": "Order",
      "table_name": "tx_orders",
      "columns": [
        { "code": "customer_id", "type": "many2one", "label": "Customer", "relation_table": "md_business_partner" },
        { "code": "order_date", "type": "date", "label": "Order Date" },
        { "code": "status", "type": "enum", "label": "Status", "enum_options": ["draft", "confirmed", "shipped"] },
        { "code": "grand_total", "type": "decimal", "label": "Grand Total", "precision": 15, "scale": 2 }
      ]
    }
  },
  "message": "Form definition loaded successfully."
}
```

#### Data Responses (Always Fresh)

**`GET /api/runtime/forms/{formCode}/records`** (List View):
```json
{
  "success": true,
  "data": {
    "records": {
      "items": [
        { "id": "uuid-1", "customer_id": "uuid-c1", "order_date": "2026-07-01", "status": "draft", "grand_total": 1500.00 },
        { "id": "uuid-2", "customer_id": "uuid-c2", "order_date": "2026-07-02", "status": "confirmed", "grand_total": 2500.00 }
      ],
      "page": 1,
      "size": 20,
      "total": 2
    }
  },
  "message": "Records loaded successfully."
}
```

**`GET /api/runtime/forms/{formCode}/records/{id}`** (Record View — parent with sub-form child data):
```json
{
  "success": true,
  "data": {
    "record": {
      "id": "uuid-1",
      "customer_id": "uuid-c1",
      "order_date": "2026-07-01",
      "status": "draft",
      "grand_total": 1500.00
    },
    "sub_form_records": {
      "order_line": {
        "items": [
          { "id": "line-1", "product_id": "prod-1", "quantity": 10, "unit_price": 100.00, "line_total": 1000.00 },
          { "id": "line-2", "product_id": "prod-2", "quantity": 5, "unit_price": 100.00, "line_total": 500.00 }
        ],
        "total": 2
      }
    }
  },
  "message": "Record loaded successfully."
}
```

**`GET /api/runtime/forms/{formCode}/records/{id}`** (Child Record View — with parent context + breadcrumb):
```json
{
  "success": true,
  "data": {
    "record": {
      "id": "line-1",
      "order_id": "uuid-1",
      "product_id": "prod-1",
      "quantity": 10,
      "unit_price": 100.00,
      "line_total": 1000.00
    },
    "parent": {
      "form_code": "order",
      "record_id": "uuid-1",
      "label": "#1024"
    },
    "breadcrumb": [
      { "form_code": "order", "record_id": null, "label": "Orders" },
      { "form_code": "order", "record_id": "uuid-1", "label": "#1024" },
      { "form_code": "order_line", "record_id": null, "label": "Order Lines" },
      { "form_code": "order_line", "record_id": "line-1", "label": "Line #3" }
    ],
    "sub_form_records": {
      "tax_entry": {
        "items": [
          { "id": "tax-1", "tax_type": "GST", "tax_rate": 18.00, "tax_amount": 180.00 }
        ],
        "total": 1
      }
    }
  },
  "message": "Record loaded successfully."
}
```

#### Frontend Internal Abstraction

The frontend does NOT call these endpoints directly from components. Instead, a hook abstracts the two-request pattern:

```typescript
// Internal hook — components use this, not raw API calls
function useForm(formCode: string, recordId?: string) {
  // 1. Fetch form definition (cached aggressively)
  const definitionQuery = useQuery({
    queryKey: ['form-definition', formCode],
    queryFn: () => api.get(`/api/runtime/forms/${formCode}/definition`),
    staleTime: 5 * 60 * 1000, // 5 min cache — form structure rarely changes
  });

  // 2. Fetch data (always fresh)
  const dataQuery = useQuery({
    queryKey: ['form-data', formCode, recordId, page],
    queryFn: () => recordId
      ? api.get(`/api/runtime/forms/${formCode}/records/${recordId}`)
      : api.get(`/api/runtime/forms/${formCode}/records?page=${page}`),
    staleTime: 0, // always fresh
  });

  return {
    formDefinition: definitionQuery.data,
    records: dataQuery.data?.records,
    record: dataQuery.data?.record,
    subFormRecords: dataQuery.data?.sub_form_records,
    breadcrumb: dataQuery.data?.breadcrumb,
    parent: dataQuery.data?.parent,
    isLoading: definitionQuery.isLoading || dataQuery.isLoading,
    error: definitionQuery.error || dataQuery.error,
  };
}
```

### Expression Validation API

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/runtime/expressions/validate` | Validate a rule expression | Authenticated |
| POST | `/api/runtime/expressions/evaluate` | Test-evaluate an expression with sample data | Authenticated |

### Standard Response Formats

All endpoints follow the existing `ApiResponse<T>` envelope:

**Success:**
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation completed successfully."
}
```

**Error:**
```json
{
  "success": false,
  "errorCode": "VALIDATION_ERROR",
  "message": "One or more fields failed validation.",
  "details": [
    { "field": "table_code", "issue": "Table code must be unique." }
  ]
}
```

**Paginated:**
```json
{
  "success": true,
  "data": {
    "items": [ ... ],
    "page": 1,
    "size": 20,
    "total": 142
  },
  "message": "Records loaded."
}
```

---

# Database Changes

All metadata is stored in **normalized relational tables**, not JSONB blobs. This avoids the problem of rewriting a large JSON object on every small update. The backend assembles the JSON response for the frontend from these normalized tables at request time.

## Entity Relationship Diagram

```
sys_metadata_models (tables)
    │
    ├──< sys_table_columns (columns of a table)
    │
    ├──< sys_metadata_views (form definitions)
    │       │
    │       ├──< sys_form_fields (fields on a form)
    │       │       │
    │       │       ├──< sys_form_field_rules (rules per field)
    │       │       └──< sys_form_field_validations (validations per field)
    │       │
    │       ├──< sys_form_layout_sections (layout sections)
    │       │       │
    │       │       └──< sys_form_section_fields (maps fields to sections)
    │       │
    │       ├──< sys_form_role_filters (role-based row-level data filters)
    │       ├──< sys_form_sub_forms (sub-form tab references)
    │       │
    │       └──< sys_form_tenant_role (per-tenant role assignments)
    │
    └── creates → Dynamic PostgreSQL tables (at runtime)
```

## Table: `sys_metadata_models` (Table Registry)

The table definition itself — no columns stored here, just the table header.

| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | |
| `name` | VARCHAR(100) | Unique code, e.g., `tx_expense_report` |
| `label` | VARCHAR(100) | "Expense Report" |
| `plural_label` | VARCHAR(100) | "Expense Reports" |
| `table_type` | VARCHAR(20) | `'dynamic'` (user-created) or `'static'` (pre-existing) |
| `table_name` | VARCHAR(100) | Physical PostgreSQL table name, e.g., `tx_expense_report` |
| `description` | TEXT | Optional description |
| `is_active` | BOOLEAN | Soft-delete |
| + BaseEntity | | created_at, updated_at, created_by, updated_by, deleted_at |

## Table: `sys_table_columns` (Normalized — NEW)

One row per column. No JSONB — each property is a proper column.

| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | |
| `table_id` | UUID (FK → sys_metadata_models) | Which table |
| `code` | VARCHAR(100) | `customer_id` |
| `label` | VARCHAR(100) | "Customer" |
| `type` | VARCHAR(50) | `string`, `text`, `integer`, `decimal`, `boolean`, `date`, `datetime`, `many2one`, `enum` |
| `required` | BOOLEAN | Is this column required? |
| `default_value` | TEXT | Default value (stored as string, cast by type) |
| `max_length` | INTEGER | For string type |
| `precision` | INTEGER | For decimal type |
| `scale` | INTEGER | For decimal type |
| `relation_table` | VARCHAR(100) | For many2one — referenced table name |
| `enum_options` | JSONB | For enum type — `["Option A", "Option B"]` |
| `position` | INTEGER | Display/order position |
| `is_active` | BOOLEAN | Soft-delete |
| + BaseEntity | | |
| **Unique:** | `(table_id, code)` | No duplicate column codes in a table |

## Table: `sys_metadata_views` (Form Registry)

The form definition header. Fields, layout, rules, and sub-forms are in their own tables.

| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | |
| `name` | VARCHAR(100) | Unique form code, e.g., `sales_order` |
| `model_name` | VARCHAR(100) | FK → sys_metadata_models.name |
| `type` | VARCHAR(50) | `'form'` |
| `scope` | VARCHAR(20) | `'global'` or `'tenant'` |
| `tenant_id` | UUID (nullable) | Set for tenant forms, NULL for global |
| `description` | TEXT | Optional |
| `where_clause_field` | VARCHAR(100) | Field for automatic filtering (optional) |
| `where_clause_operator` | VARCHAR(50) | Operator for automatic filtering |
| `where_clause_value` | VARCHAR(255) | Value for automatic filtering |
| `is_active` | BOOLEAN | Soft-delete |
| + BaseEntity | | |

## Table: `sys_form_fields` (Normalized — NEW)

One row per field on a form. Each field references a column on the associated table.

| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | |
| `form_id` | UUID (FK → sys_metadata_views) | Which form |
| `column_code` | VARCHAR(100) | References `sys_table_columns.code` |
| `label_override` | VARCHAR(200) | Override the column's label (nullable — use column label if null) |
| `visible` | BOOLEAN | Show this field? |
| `read_only` | BOOLEAN | Read-only? |
| `required` | BOOLEAN | Always required? (before rules) |
| `position` | INTEGER | Display order |
| `default_value` | TEXT | Default when creating new record |
| `placeholder` | VARCHAR(255) | Placeholder text |
| `is_active` | BOOLEAN | Soft-delete |
| + BaseEntity | | |
| **Unique:** | `(form_id, column_code)` | No duplicate column references in a form |

## Table: `sys_form_field_rules` (Normalized — NEW)

One row per rule condition-action pair.

| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | |
| `field_id` | UUID (FK → sys_form_fields) | Which field this rule applies to |
| `condition_field` | VARCHAR(100) | The source field to check (e.g., `customer_tier`) |
| `condition_operator` | VARCHAR(50) | `equals`, `not_equals`, `greater_than`, `less_than`, `contains`, `is_empty`, `is_not_empty`, `in` |
| `condition_value` | VARCHAR(255) | The value to compare against (e.g., `Gold`) |
| `action` | VARCHAR(50) | `show`, `hide`, `read_only`, `editable`, `required`, `optional` |
| `logic_group` | INTEGER | For AND/OR grouping (same group = AND, different groups = OR) |
| `position` | INTEGER | Order within the logic group |
| + BaseEntity | | |

## Table: `sys_form_field_validations` (Normalized — NEW)

One row per validation constraint.

| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | |
| `field_id` | UUID (FK → sys_form_fields) | Which field |
| `type` | VARCHAR(50) | `required`, `min_length`, `max_length`, `min`, `max`, `pattern`, `custom_expression` |
| `value` | VARCHAR(255) | Constraint value (e.g., `0` for min, `100` for max, `^[A-Z].*` for pattern) |
| `message` | VARCHAR(500) | Error message shown on validation failure |
| `position` | INTEGER | Evaluation order |
| + BaseEntity | | |

## Table: `sys_form_layout_sections` (Normalized — NEW)

One row per section in the form layout.

| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | |
| `form_id` | UUID (FK → sys_metadata_views) | Which form |
| `code` | VARCHAR(100) | Section identifier |
| `label` | VARCHAR(200) | Display label |
| `collapsible` | BOOLEAN | Can this section be collapsed? |
| `columns` | INTEGER | 1, 2, or 3 columns |
| `position` | INTEGER | Display order |
| + BaseEntity | | |

## Table: `sys_form_section_fields` (Normalized — NEW)

Maps fields to sections. A field can belong to at most one section.

| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | |
| `section_id` | UUID (FK → sys_form_layout_sections) | |
| `field_id` | UUID (FK → sys_form_fields) | |
| `position` | INTEGER | Order within the section |
| **Unique:** | `(section_id, field_id)` | |
| **Unique:** | `(field_id)` | A field belongs to only one section |

## Table: `sys_form_role_filters` (Normalized — NEW)

One row per role-based row-level data filter on a form. These filters are appended to every data query to ensure users only see records they're authorized to access.

| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | |
| `form_id` | UUID (FK → sys_metadata_views) | Which form |
| `role_id` | UUID | Which role this filter applies to |
| `condition_field` | VARCHAR(100) | Field to filter on (supports dynamic variables: `{current_user_id}`, `{current_user_role}`, `{current_tenant_id}`, etc.) |
| `condition_operator` | VARCHAR(50) | `equals`, `not_equals`, `greater_than`, `less_than`, `contains`, `in` |
| `condition_value` | VARCHAR(255) | Static value or dynamic variable reference |
| `position` | INTEGER | Evaluation order |
| + BaseEntity | | |
| **Index:** | `(form_id, role_id)` | For efficient query lookup |

Dynamic variables are resolved server-side from the JWT token at query time. Examples:
- `created_by = {current_user_id}` → User sees only their own records
- `region = {current_user_region}` → User sees only records in their region
- `status != internal` → User sees records except those marked internal

## Table: `sys_form_sub_forms` (Normalized — NEW)

One row per sub-form tab reference on a parent form.

| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | |
| `parent_form_id` | UUID (FK → sys_metadata_views) | The parent form (e.g., `sales_order`) |
| `relation_code` | VARCHAR(100) | The column code on the child table that references the parent (e.g., `order_id` on `order_line` table) |
| `child_form_code` | VARCHAR(100) | References `sys_metadata_views.name` — which form to render the child with |
| `label` | VARCHAR(200) | Tab label (e.g., "Order Lines") |
| `display_as` | VARCHAR(50) | `tab` or `inline_grid` |
| `position` | INTEGER | Tab order |
| + BaseEntity | | |

## Table: `sys_form_tenant_role` (Normalized — NEW)

Per-tenant role assignments for forms.

```sql
CREATE TABLE sys_form_tenant_role (
    id UUID PRIMARY KEY,
    form_id UUID NOT NULL REFERENCES sys_metadata_views(id),
    tenant_id UUID NOT NULL,
    role_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE (form_id, tenant_id, role_id)
);

CREATE INDEX idx_ftr_form ON sys_form_tenant_role(form_id);
CREATE INDEX idx_ftr_tenant ON sys_form_tenant_role(tenant_id);
CREATE INDEX idx_ftr_role ON sys_form_tenant_role(role_id);
```

## Dynamic Table Creation

When a System Admin creates a table definition, the backends does:

1. **Save metadata:** INSERT into `sys_metadata_models` + INSERT each column into `sys_table_columns`
2. **Execute DDL:** Create physical PostgreSQL table with the defined columns
3. **Log change:** Record in `sys_metadata_version`

Every dynamic table includes these system columns:
- `id UUID PRIMARY KEY`
- `tenant_id UUID NOT NULL` (tenant isolation)
- `created_at TIMESTAMP`, `updated_at TIMESTAMP`
- `created_by UUID`, `updated_by UUID`
- `is_active BOOLEAN`, `deleted_at TIMESTAMP`

**PostgreSQL Type Mapping:**

| Metadata Type | PostgreSQL Type |
|---------------|-----------------|
| string | VARCHAR(n) |
| text | TEXT |
| integer | INTEGER |
| decimal | NUMERIC(p, s) |
| boolean | BOOLEAN |
| date | DATE |
| datetime | TIMESTAMP |
| many2one | UUID (with FK) |
| enum | VARCHAR(100) |

## Migration Requirements

- Flyway migration to create `sys_table_columns` table (migrate data from JSONB if exists)
- Flyway migration to add `table_type`, `table_name`, `description` columns to `sys_metadata_models`
- Flyway migration to add `scope`, `tenant_id` columns to `sys_metadata_views`
- Flyway migration to add `where_clause_field`, `where_clause_operator`, `where_clause_value` columns to `sys_metadata_views` (replace JSONB where_clause)
- Flyway migration to create all form-related tables (`sys_form_fields`, `sys_form_field_rules`, `sys_form_field_validations`, `sys_form_layout_sections`, `sys_form_section_fields`, `sys_form_sub_forms`, `sys_form_tenant_role`)
- All DDL operations for dynamic tables are executed via Spring JDBC `JdbcTemplate`

---

# Security Requirements

## Authentication
- All admin and runtime endpoints require valid JWT authentication
- JWT contains tenant context and user roles

## Authorization

| Role | Can Access |
|------|------------|
| System Admin | Table Designer (all endpoints), Form Designer (global + any tenant), all tenants |
| Tenant Admin | Form Designer (their tenant only — create tenant forms, configure role access for global forms), runtime forms |
| End User | Runtime forms accessible to their tenant AND assigned to their roles |

## Authorization Enforcement
- **Table Designer APIs:** Guarded by `@PreAuthorize("hasRole('SYSTEM_ADMIN')")`
- **Form Designer APIs (System Admin):** Can create/modify/delete global forms and any tenant forms
- **Form Designer APIs (Tenant Admin):** Can only create/modify/delete forms scoped to their tenant; can read global forms and configure their tenant's role assignments on them
- **Runtime API — Form listing:** The `GET /api/runtime/forms` endpoint returns only forms where:
  1. The form scope is `'global'` AND `sys_form_tenant_role` has an entry for (form_id, user's tenant_id, user's role_id), OR
  2. The form scope is `'tenant'` AND form's tenant_id = user's tenant_id AND `sys_form_tenant_role` has an entry for (form_id, tenant_id, user's role_id)
- **Runtime API — Data access:** All CRUD operations on dynamic tables enforce tenant isolation by appending `tenant_id = :currentTenant` to all queries
- **Field-level read-only enforcement:** Backend strips/ignores updates to read-only fields
- **Where clause enforcement:** Backend appends the where clause to all queries to prevent data leakage

## Input Validation
- Table codes: validated against `^[a-z][a-z0-9_]*$` pattern
- Column codes: validated against `^[a-z][a-z0-9_]*$` pattern
- Where clause expressions: validated to prevent injection
- All user input: sanitized against XSS

## Audit
- All table schema changes logged in `sys_metadata_version`
- All form configuration changes tracked via metadata versioning
- All CRUD operations on dynamic records logged in audit log

---

# Performance Requirements

| Operation | Target |
|-----------|--------|
| Metadata loading (cached) | < 500ms |
| Form rendering (50 fields) | < 2s |
| List page load (20 records) | < 2s |
| Create/Update record | < 1s |
| Rule evaluation (client-side) | < 100ms |
| DDL execution (create table) | < 3s |
| DDL execution (add column) | < 1s |

## Caching Strategy
- Table and form definitions cached in backend (Caffeine L1)
- Form definitions cached in frontend (Zustand + React Query)
- Metadata changes invalidate relevant caches

---

# Logging Requirements

## Application Logs
- All table DDL operations logged (who created/modified what table)
- All form configuration changes logged
- All CRUD operations on dynamic records logged (especially create and delete)

## Audit Logs
- Schema changes stored in `sys_metadata_version`
- Record CRUD stored in `sys_audit_log` (for dynamic tables)

---

# Edge Cases

| Edge Case | Handling |
|-----------|----------|
| **Duplicate table code** | Rejected at validation; unique constraint on `name` column |
| **Duplicate form code** | Global forms: unique globally. Tenant forms: unique within the tenant |
| **Form with no roles assigned** | Form is inaccessible; warn admin when saving |
| **Delete column with existing data** | Warn admin; column value becomes NULL in existing rows; use ALTER TABLE DROP COLUMN |
| **Delete column used by forms** | Warn which forms use the column; allow deletion (form will show field as missing) |
| **Add column to table with existing data** | New column added with NULL or default value |
| **Where clause syntax error** | Validate on save; reject with clear error message |
| **Concurrent form editing** | Use optimistic locking via `updated_at` timestamp |
| **Form with no fields selected** | Warn admin that form has no fields; allow save but render empty form |
| **Tenant Admin selects a table not in their tenant scope** | Only show tables accessible to the tenant |
| **Very large form (100+ fields)** | Lazy-render fields not in viewport; paginated sections |
| **Slow network / offline** | Standard error handling; show loading states; no offline mode in MVP |
| **Tenant Admin assigns roles to a global form, then System Admin deletes the form** | When a global form is deleted, all `sys_form_tenant_role` entries referencing it are cascade-deleted; warn System Admin about number of tenant configurations affected |
| **Two tenants assign different roles to the same global form** | Fully supported; each tenant's role assignments are independent via `sys_form_tenant_role` join table with tenant_id |
| **Tenant Admin assigns a role to a global form, but that role is later deleted** | Orphaned role reference in `sys_form_tenant_role` should be cleaned up on role deletion (cascade) |
| **New tenant is created after global forms exist** | The new tenant starts with no role assignments for any global forms; Tenant Admin must explicitly configure access for their desired global forms |
| **Tenant Admin sees a global form's field config as read-only** | Tenant Admin cannot modify global form fields/layout/rules; only System Admin can. UI should show lock/read-only indicators |
| **User tries to access a record filtered out by row filter** | Return 404 (not 403) to avoid leaking record existence |
| **User has multiple roles with different row filters** | Apply the most permissive filter (union) — if one role grants full access and another restricts, the user sees all data |
| **User attribute used in row filter changes mid-session** | Row filters are resolved per-request from JWT, so changes take effect on next request |
| **Row filter variable resolves to null** | Treat as no filter for that condition (skip it) |
| **System Admin in audit mode** | Bypass row filters but still enforce tenant isolation |
| **Sub-form records with different row filters** | Child records are filtered by the child form's role filters independently of the parent |

---

# Risks

| Risk | Impact | Likelihood | Mitigation |
|------|--------|------------|------------|
| DDL operations on dynamic tables could fail mid-migration | High | Medium | Use transactional DDL where possible; rollback metadata on failure |
| Performance degradation with many field rules evaluated on every keystroke | Medium | Medium | Debounce rule evaluation; evaluate only changed-field dependencies |
| Data leakage through malformed where clauses | Critical | Low | Server-side validation of where clause; never inject raw user input into SQL |
| Tenant Admin accidentally deletes or modifies a form in use | Medium | Medium | Confirmation dialogs; soft-delete with restore option |
| Dynamic table DDL could conflict with existing table names | High | Low | Prefix or namespace dynamic table names; validate uniqueness across all tables |
| Expression evaluation errors (invalid expressions in rules) | Medium | Medium | Validate rules on save; provide test/preview mode |
| System Admin changes a global form's fields, breaking Tenant Admin's role configuration expectations | Medium | Low | Notify tenants of form changes; version global forms (future enhancement) |
| Cross-tenant data leak in shared global forms | Critical | Low | Enforce `tenant_id` filtering on every query; never allow cross-tenant data access | 

---

# Assumptions

1. The existing `MetadataModel` and `MetadataView` entities are the foundation and will be extended rather than replaced.
2. The Dynamic CRUD service (outlined in the architecture blueprint) will be built to handle CRUD operations on dynamic tables.
3. Tenant context is available via the JWT token and propagated through the request lifecycle.
4. Role definitions and user-role assignments already exist in the system.
5. The existing `BaseEntity` pattern (UUID, timestamps, soft-delete) is applied to all dynamic tables.
6. PostgreSQL is the only supported database — no database abstraction layer is needed for DDL operations.
7. The existing `sys_metadata_version` table will be used for tracking schema changes.
8. The system uses the standard `ApiResponse<T>` envelope for all new endpoints.
9. **Tenant isolation is enforced at the database level** — all dynamic tables include a `tenant_id` column, and every query filters by the current user's tenant.
10. **Global forms are read-only to Tenant Admins** — they can only configure role access, not modify the form's structure or rules.
11. **Role IDs are scoped per tenant** — two tenants can have roles with the same name but different IDs, and the system correctly resolves them via `sys_form_tenant_role`.

---

# Dependencies

| Dependency | Description | Status |
|-----------|-------------|--------|
| Phase 0 Architecture Blueprint | Defines metadata-driven approach, naming, conventions | Frozen |
| Existing MetadataModel entity | Foundation for table definitions | Exists |
| Existing MetadataView entity | Foundation for form definitions | Exists |
| BaseEntity framework | Provides UUID, timestamps, soft-delete | Exists |
| JWT Authentication | Auth for all endpoints | Exists |
| Role/Permission system | Role definitions and user-role assignments | Exists (partial) |
| Dynamic CRUD Service | Runtime CRUD for dynamic tables (needs implementation) | Not started |
| Dynamic Table DDL Executor | Service to create/alter PostgreSQL tables from metadata | New |
| Frontend Registry system | For dynamic component resolution | Exists (partial) |
| React Query + Zustand | Caching and state management | Exists |

---

# Acceptance Criteria

1. System Admin can create a table with columns, and the table is physically created in PostgreSQL.
2. System Admin can modify columns (add, edit, delete, reorder) with corresponding database DDL.
3. System Admin can create a global form available to all tenants.
4. Tenant Admin can create a tenant-specific form from a table with a where clause and role assignments.
5. Tenant Admin can browse available global forms and configure which tenant roles can access them.
6. Tenant Admin can configure field rules (visibility, read-only, required) that evaluate correctly.
7. Tenant Admin can configure field validations that are enforced on create/update.
8. Tenant Admin can organize fields into sections, columns, and tabs.
9. Tenant Admin can preview the form before publishing.
10. Tenant Admin can clone an existing form.
11. Two different tenants can assign different roles to the same global form without affecting each other.
12. End users see only forms accessible to their tenant AND assigned to their roles in navigation.
13. End users using the same global form in different tenants see only their own tenant's data (full tenant isolation).
14. End users can create, view, edit, and delete records through dynamically rendered forms.
15. Field rules trigger correctly as users interact with the form (client-side).
16. Backend enforces read-only fields, validations, and where clause filtering.
17. Audit logs are created for table schema changes and record CRUD operations.
18. The system performs within the defined performance targets.
19. Users can search for forms by name/code via the global header search bar (Ctrl+K/Cmd+K), with results filtered by their role access.
20. Navigating to a form triggers a single consolidated API request that returns form definition + model definition + records in one response.
21. Opening a form makes two internal requests: form definition (cached) + record data (fresh), abstracted by a frontend hook.
22. The form definition endpoint response is cached on the frontend and reused across navigations to the same form.
23. Navigating between records in the same form only re-fetches data, not the definition.
24. Tenant Admin can configure sub-forms (one2many child relationships) in the Form Designer, selecting which form to use for each child.
25. End users see sub-form tabs on parent records with inline editable grids of child records.
26. Clicking a child record opens its full form view with breadcrumb navigation showing the hierarchy.
27. Breadcrumbs show the full path (e.g., Orders > #1024 > Order Lines > Line #3) and each segment is clickable.
28. All metadata is stored in normalized relational tables — individual columns, fields, rules, and validations each have their own table. No JSONB blobs for mutable data.
29. Every record view has a sticky toolbar with: Create New, Save, Discard, Refresh, Delete, Previous/Next.
30. Keyboard shortcuts work: Ctrl+S (Save), Ctrl+Shift+S (Save & New), Escape (Discard), F5 (Refresh), Alt+Left/Right (Previous/Next).
31. Previous/Next navigates between records in the current list context, with keyboard support.
32. The toolbar shows "Record X of Y" context when Previous/Next are visible.
33. Tenant isolation is enforced on every data query — users from different tenants never see each other's data.
34. Role-based row filters are applied to all data queries — a user only sees records matching their role's configured filters.
35. Dynamic variables in row filters (`{current_user_id}`, `{current_user_role}`, etc.) resolve correctly from the JWT.
36. Attempting to access a filtered-out record returns 404 (not 403).
37. System Admin can bypass row filters in audit mode.

---

# Deployment Requirements

## Feature Flags
- No feature flags needed — this is a foundational feature of the platform

## Configuration
- No new environment variables required
- Existing `VITE_API_URL` and database connection settings are sufficient

## Migration Steps
1. Run Flyway migration to add `table_type` and `table_name` columns to `sys_metadata_models`
2. Run Flyway migration to add any needed columns to `sys_metadata_views`
3. Deploy backend with new services (Table Designer, Form Designer, DDL Executor, Dynamic CRUD)
4. Deploy frontend with new admin UI screens and updated runtime renderer

## Rollback Plan
- Revert backend and frontend deployments
- Dynamic tables and data are preserved but inaccessible from UI until re-deploy
- Metadata changes in `sys_metadata_models` and `sys_metadata_views` are backward-compatible

---

# Testing Requirements

## Manual Testing
- System Admin: Create table, add columns, verify physical table exists in PostgreSQL
- System Admin: Add/edit/delete columns, verify ALTER TABLE works
- System Admin: Deactivate/restore table
- System Admin: Create a global form, verify it appears for Tenant Admins in all tenants
- Tenant Admin: Create tenant-specific form with where clause, verify list view shows only matching records
- Tenant Admin: Browse global forms, configure role access for their tenant
- Tenant Admin: Configure field rules, test with preview
- Tenant Admin: Configure validation, test with invalid data
- Tenant Admin: Assign roles, verify access control
- Tenant Admin: Cannot modify a global form's fields or layout (read-only)
- End User: Navigate forms — only see forms assigned to their roles by their Tenant Admin
- End User: Create/edit/delete records in both global and tenant forms
- End User: Verify rule evaluation on field value changes
- End User: Verify validation errors appear inline
- **Tenant isolation test:** Tenant A user creates a record in a global form; Tenant B user sees no data in the same form
- **Per-tenant role test:** Tenant A assigns "Sales" role to global form; Tenant B assigns "Support" role — verify users see correct forms per tenant

## Automated Testing
- **Backend Unit Tests:** Service layer for table creation, DDL execution, form configuration CRUD
- **Backend Integration Tests:** Verify DDL execution against test PostgreSQL (Testcontainers)
- **Backend Integration Tests:** CRUD operations on dynamic tables
- **Backend API Tests:** All new endpoints with valid/invalid payloads
- **Frontend Unit Tests:** Rule evaluation logic, form configuration state management
- **Frontend Component Tests:** Form renderer with various configurations

## Performance Tests
- Form metadata loading with 500+ forms cached
- Dynamic table CRUD with 1M+ records
- Rule evaluation with 50+ rules on a form

## Security Tests
- Role escalation attempts (user without role tries to access form)
- Where clause injection attempts
- Cross-tenant data access attempts (Tenant A user tries to access Tenant B's data in a global form)
- Cross-tenant role assignment interference (Tenant A tries to modify Tenant B's role assignments)
- Read-only field update attempts (backend enforcement)
- Tenant Admin attempts to modify a global form's configuration
- Newly created tenant attempts to access global forms before role assignment

---

# Future Enhancements

1. **Advanced Expression Engine** — Full JSON Logic support for complex rules (nested conditions, math operations, dates)
2. **Drag-and-Drop Form Canvas** — Visual designer for precise field placement on a canvas
3. **Form Versioning & Rollback** — Full version history for forms with one-click rollback
4. **Conditional Sections/Tabs** — Show/hide entire form sections based on conditions
5. **Computed / Formula Fields** — Fields whose values are calculated from expressions
6. **Multi-Language Support** — Field labels and validation messages in multiple languages
7. **Form Import/Export** — JSON export/import for moving forms between instances
8. **Audit Log Viewer UI** — Admin UI for browsing audit logs per table/record
9. **Dashboard Widgets from Dynamic Tables** — Create charts and KPIs based on dynamic table data
10. **Bulk Operations** — Bulk create, update, delete on list views
11. **Form Templates** — Pre-built form templates for common ERP patterns (order, invoice, etc.)

---

# Open Questions

1. **DDL-as-a-Service:** Should the backend execute DDL synchronously or asynchronously? Synchronous is simpler but could cause timeouts for large tables. (RESOLVED: Synchronous with timeout monitoring for MVP)

2. **Table Naming Convention:** Should dynamic tables use a prefix like `dyn_` to distinguish them from static system tables? (RESOLVED: Use prefixes per architecture convention — `md_` for master data, `tx_` for transactions)

3. **Enum Columns:** Should enum values be stored as simple strings, or should they reference a separate `sys_enum` lookup table? (RESOLVED: Simple string storage for MVP; enum lookup table for future)

4. **Where Clause Security:** How should where clauses be validated against SQL injection? (RESOLVED: The where clause is NOT raw SQL — it uses the structured expression format `{field, operator, value}` which is safely compiled to parameterized queries on the backend)

---

# Change History

| Version | Reason | Date |
|---------|--------|------|
| 1.0.0 | Initial Draft | 2026-07-07 |
| 1.1.0 | Added global forms, per-tenant role assignment, tenant isolation | 2026-07-07 |
| 1.2.0 | Added header form search (Ctrl+K), consolidated single-request bundle API | 2026-07-07 |
| 1.3.0 | Added multi-level sub-forms, breadcrumb navigation, inline sub-form grids | 2026-07-07 |
| 1.4.0 | Normalized storage (no JSONB for mutable data). Two-request loading pattern. Frontend useForm() hook. | 2026-07-07 |
| 1.5.0 | Added form toolbar with Create, Save, Save & New, Discard, Refresh, Delete, Previous/Next. Keyboard shortcuts. | 2026-07-07 |
| 1.6.0 | Added role-based row-level data access. Row filters per role with dynamic variables. 404 on filtered records. | 2026-07-07 |

---

# Related Documents

- [Architecture Blueprint (Phase 0)](/docs/architecture-blueprint-P0.md)
- [Workspace Agent Prompt (Phase 0)](/docs/workspace-agent-prompt-P0.md)
- [PRD Template](/ai/docs/PRD_TEMPLATE.md)
- [Task Rules](/ai/docs/TASK_RULES.md)
- [Workflow](/ai/docs/WORKFLOW.md)
