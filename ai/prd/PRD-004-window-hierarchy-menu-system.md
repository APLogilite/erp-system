---
id: PRD-004

title: Window Hierarchy & Menu System

version: 1.0.0

status: READY_FOR_DEPLOYMENT

priority: High

owner: planner

created: 2026-07-13

updated: 2026-07-14

approved_by: user

project: Dynamic ERP Platform

repository: erp-system

prd_branch: prd/PRD-004-window-hierarchy-menu

target_branch: main

tech_stack:
  - Spring Boot 3.3.4
  - Java 17
  - PostgreSQL
  - React 18
  - TypeScript (strict)
  - MUI 5
  - Zustand
  - React Query

related_prds:
  - PRD-001 (Dynamic Form Configuration System v1.6.0) — replaced by this PRD's new schema
  - PRD-002 (Admin Configuration Forms) — absorbed into this PRD with new Window/Tab/Field layout
  - PRD-003 (ERP Order Flow — Transaction Forms) — absorbed into this PRD with new Window/Tab/Field layout

related_tasks: []

related_bugs:
  - ENH-003 (RuntimePage not integrated with API — hardcoded sample bundles)
  - BUG-003 (Navigation menu issues — raw labels, incomplete integration)

dependencies:
  - Spring Boot 3.3.4 / Java 17 / PostgreSQL
  - React 18 / TypeScript / MUI 5 / Zustand / React Query
  - Flyway for schema creation and seed data

change_log:
  - 1.0.0 — Initial Draft

---

# Executive Summary

PRD-001 built a dynamic form engine, but the current interface exposes raw table codes to users instead of friendly names, lacks a hierarchical menu, and the RuntimePage uses hardcoded sample bundles. PRD-002 and PRD-003 added admin forms and seed data on top of this engine — but the underlying table structure was confusing.

PRD-004 replaces the entire metadata schema with an iDempiere-inspired three-layer design:
1. **Database Schema** (`sys_table` / `sys_column`) — what tables and columns exist
2. **Window Design** (`sys_window` / `sys_tab` / `sys_window_field` / `sys_window_access`) — what users see and interact with
3. **Menu** (`sys_menu`) — how users navigate between windows

This PRD also absorbs everything PRD-002 and PRD-003 did: admin forms for metadata management and seeded ERP windows (Sales Order, Purchase Order, Invoice, etc.) — all rebuilt from scratch on the new schema. The RuntimePage is fixed, routes change to `/window/{name}`, and the flat "DYNAMIC FORMS" list is replaced by a collapsible hierarchical menu.

---

# Business Goals

1. **Replace flat form list with hierarchical menu** — Collapsible menu groups → menu items → windows, organized by business function.
2. **Create clean iDempiere-inspired metadata schema** — Three clear layers: Database (table/column), Design (window/tab/field/access), Navigation (menu).
3. **Fix RuntimePage to render actual dynamic forms** — Replace hardcoded sample bundles with real API-driven form rendering.
4. **Enable System and Tenant Admins to configure everything** — Tables, columns, windows, tabs, fields, access, and menus all through admin UI.
5. **Seed standard ERP windows** — Sales Order, Purchase Order, Invoice, Product, Partner, etc., with proper Window/Tab/Field structure.

---

# Problem Statement

**What problem are we solving?**

1. **No hierarchical menu exists** — all forms are shown in a flat list under "DYNAMIC FORMS" without logical grouping (Sales vs Purchasing vs Administration).
2. **RuntimePage is broken** — ENH-003 confirmed it uses hardcoded sample bundles instead of calling the runtime API, so users cannot actually use any dynamic forms.
3. **Metadata table names are confusing** — `sys_metadata_views`, `sys_form_fields`, `sys_form_sub_forms` don't convey their business purpose. The existing schema mixes database concerns (what tables exist) with design concerns (what windows/tabs/fields look like) without clear separation.
4. **PRD-002 and PRD-003 data needs rebuilding anyway** — Since the schema is changing, the admin forms and seed data from those PRDs must be re-created on the new schema.

**Who experiences this problem?**

- End users who cannot find or navigate between business forms
- System/Tenant Admins who need to configure windows, tabs, fields, and menus
- Developers maintaining the metadata layer

**Why is this feature required?**

Without a proper menu, the system has no navigable interface. Without fixing the RuntimePage, forms don't work at all. The schema restructuring is a one-time clean-up that aligns the platform with established ERP patterns (iDempiere/ADempiere).

---

# Functional Requirements

## Layer 1: Database Schema Renames

### FR-001: Rename sys_metadata_models → sys_table

**Description:** Rename the table definition table to `sys_table` to clearly indicate it stores table definitions.

**Priority:** High

**Acceptance Criteria:**
- Table is renamed from `sys_metadata_models` to `sys_table`
- All existing data is preserved
- All FK references are updated
- `sys_table.id` UUID (PK)
- `sys_table.name` VARCHAR(100) — unique code, e.g. `tx_expense_report`
- `sys_table.label` VARCHAR(100) — "Expense Report"
- `sys_table.plural_label` VARCHAR(100) — "Expense Reports"
- `sys_table.table_type` VARCHAR(20) — `dynamic` or `static`
- `sys_table.table_name` VARCHAR(100) — physical PostgreSQL table name
- `sys_table.description` TEXT — optional
- `sys_table.is_active` BOOLEAN
- + BaseEntity fields

---

### FR-002: Rename sys_table_columns → sys_column

**Description:** Rename the column definition table to `sys_column` with a FK to `sys_table`.

**Priority:** High

**Acceptance Criteria:**
- Table renamed from `sys_table_columns` to `sys_column`
- `sys_column.table_id` UUID (FK → sys_table.id)
- `sys_column.code` VARCHAR(100) — e.g. `customer_id`
- `sys_column.label` VARCHAR(100) — "Customer"
- `sys_column.type` VARCHAR(50) — `string`, `text`, `integer`, `decimal`, `boolean`, `date`, `datetime`, `many2one`, `enum`
- `sys_column.required` BOOLEAN
- `sys_column.default_value` TEXT
- `sys_column.max_length` INTEGER (for string)
- `sys_column.precision` INTEGER, `scale` INTEGER (for decimal)
- `sys_column.relation_table` VARCHAR(100) (for many2one)
- `sys_column.enum_options` JSONB (for enum)
- `sys_column.position` INTEGER
- `sys_column.is_active` BOOLEAN
- + BaseEntity
- All column data migrated from old table

---

## Layer 2: Window / Tab / Field

### FR-003: Create sys_window table (renamed from sys_metadata_views)

**Description:** Rename `sys_metadata_views` to `sys_window`. A window is the top-level form concept — it represents what users see when they open a menu item like "Sales Orders".

**Priority:** High

**Acceptance Criteria:**
- Table renamed from `sys_metadata_views` to `sys_window`
- `sys_window.id` UUID (PK)
- `sys_window.name` VARCHAR(100) — unique code, e.g. `sales_order`
- `sys_window.table_id` UUID (FK → sys_table.id) — primary table for this window
- `sys_window.description` TEXT — optional
- `sys_window.is_active` BOOLEAN
- + BaseEntity

---

### FR-004: Create sys_tab table (renamed from sys_form_sub_forms)

**Description:** A tab represents a sub-section within a window, like "Header" and "Lines" tabs in a Sales Order window. Each tab can point to a different table.

**Priority:** High

**Acceptance Criteria:**
- New table `sys_tab` replacing `sys_form_sub_forms`
- `sys_tab.id` UUID (PK)
- `sys_tab.window_id` UUID (FK → sys_window.id) — which window this tab belongs to
- `sys_tab.name` VARCHAR(100) — e.g. "Header" or "Lines"
- `sys_tab.table_id` UUID (FK → sys_table.id) — which table this tab reads/writes
- `sys_tab.seq_no` INTEGER — tab order
- `sys_tab.is_single_row` BOOLEAN — if true, opens detail view directly (no list view)
- `sys_tab.where_clause` TEXT — row filter expression (e.g. `order_id = @id@`)
- `sys_tab.parent_column` VARCHAR(100) — for child tabs, the column linking to parent (e.g. `order_id`)
- `sys_tab.is_active` BOOLEAN
- + BaseEntity

**Tab types:**
- **Main tab** (`parent_column = null`) — the primary tab of a window, shows the main record
- **Child tab** (`parent_column` set) — linked sub-tab, shows child records filtered by parent link
  - Example: Sales Order window → Tab "Lines" has `parent_column = order_id`, `where_clause = order_id = @id@`
  - The `@id@` variable resolves to the current parent record's UUID at runtime

---

### FR-005: Create sys_window_field table (renamed from sys_form_fields)

**Description:** A field maps a `sys_column` to a position within a `sys_tab`. Display and read-only logic is stored inline on the field.

**Priority:** High

**Acceptance Criteria:**
- Table renamed from `sys_form_fields` to `sys_window_field`
- `sys_window_field.id` UUID (PK)
- `sys_window_field.tab_id` UUID (FK → sys_tab.id)
- `sys_window_field.column_id` UUID (FK → sys_column.id) — maps to a table column
- `sys_window_field.seq_no` INTEGER — display order within the tab
- `sys_window_field.is_same_line` BOOLEAN — if true, renders next to previous field on same row
- `sys_window_field.num_lines` INTEGER — field height (1 = single line, 2+ = multi-line)
- `sys_window_field.column_width` INTEGER — display width (e.g. 1-12 columns)
- `sys_window_field.is_displayed` BOOLEAN — is this field visible on the form?
- `sys_window_field.is_readonly` BOOLEAN — is this field read-only?
- `sys_window_field.is_mandatory` BOOLEAN — is this field required?
- `sys_window_field.display_logic` TEXT — expression for conditional display (e.g. `@PaymentRule@=Credit`)
- `sys_window_field.readonly_logic` TEXT — expression for conditional read-only
- `sys_window_field.default_value` TEXT — default value expression
- `sys_window_field.label_override` VARCHAR(200) — override column's label for this field instance
- `sys_window_field.is_active` BOOLEAN
- + BaseEntity

**Notes:**
- No separate `sys_rule` or `sys_validation` tables — all logic is inline on the field (iDempiere approach)
- No `sys_form_layout_sections` or `sys_section_field` — fields are ordered by `seq_no` within the tab

---

### FR-006: Create sys_window_access table (renamed from sys_form_tenant_role)

**Description:** Controls which roles can access which windows. A user sees only windows assigned to their role.

**Priority:** High

**Acceptance Criteria:**
- Table renamed from `sys_form_tenant_role` to `sys_window_access`
- `sys_window_access.id` UUID (PK)
- `sys_window_access.window_id` UUID (FK → sys_window.id)
- `sys_window_access.tenant_id` UUID (nullable — null means global)
- `sys_window_access.role_id` UUID
- `sys_window_access.is_active` BOOLEAN
- Unique constraint on `(window_id, tenant_id, role_id)`
- + BaseEntity

---

### FR-007: Drop sys_form_layout_sections and sys_form_section_fields

**Description:** Remove these tables. Field ordering is handled by `seq_no` on `sys_window_field` — no layout grouping. This simplifies the system and matches iDempiere's approach.

**Priority:** Medium

**Acceptance Criteria:**
- Both tables are dropped after migrating field positions to `sys_window_field.seq_no`
- No data loss for field ordering

---

### FR-008: Drop sys_form_role_filters (row filters)

**Description:** Row-level data filtering moves to `sys_tab.where_clause`. No separate filter table.

**Priority:** Medium

**Acceptance Criteria:**
- `sys_form_role_filters` table dropped
- Existing where clause logic is migrated to `sys_tab.where_clause`
- Dynamic variables (`{current_user_id}`, `{current_user_role}`) still supported

---

## Layer 3: Menu System

### FR-009: Create sys_menu table

**Description:** A hierarchical menu table that organizes windows into collapsible groups. Users navigate through menus instead of seeing a flat list of forms.

**Priority:** Critical

**Acceptance Criteria:**

| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID (PK) | |
| `name` | VARCHAR(100) | Display label (e.g. "Sales Orders") |
| `type` | VARCHAR(20) | `group` (collapsible section) or `window` (opens a window) |
| `parent_id` | UUID (FK → sys_menu.id, nullable) | Parent group for nesting |
| `window_id` | UUID (FK → sys_window.id, nullable) | Only for `type='window'` |
| `seq_no` | INTEGER | Order within parent |
| `icon` | VARCHAR(100) | Optional icon name |
| `is_active` | BOOLEAN | |
| + BaseEntity | | |

**Menu structure:**
- A `group` entry is a collapsible section with a label and optional icon
- A `window` entry points to a `sys_window` and navigates to it on click
- A `group` can contain both `group` and `window` children (nested groups are allowed)
- Root-level entries have `parent_id = null`
- Ordering within a parent is by `seq_no`

**Access control:**
- The menu itself inherits access from `sys_window_access`
- If a user's role has no access to a window, the corresponding menu item (and its parent groups, if they have no visible children) is hidden
- Empty groups (no visible window items) are automatically hidden

**Admin UI:**
- System Admin and Tenant Admin can create, edit, reorder, and delete menu entries
- Menu is stored as data and loaded dynamically on each session

---

## Fixes for Existing Issues

### FR-010: Fix RuntimePage to use dynamic forms

**Description:** The existing RuntimePage uses hardcoded sample bundles instead of calling the runtime API. This must be fixed so that opening a window from the menu renders the actual dynamic form from the metadata.

**Priority:** Critical

**Acceptance Criteria:**
- Opening a menu item navigates to the window's list view
- List view fetches records from `GET /api/runtime/windows/{windowName}/records` (or similar)
- List view shows fields in the tab's configured sequence
- Clicking a record opens the detail view with all tabs
- Sub-tabs render child records filtered by the parent column
- Field display/readonly logic is evaluated client-side
- All existing form definitions from PRD-002 and PRD-003 render correctly

---

### FR-011: Show user-friendly labels throughout

**Description:** Everywhere the user sees a window/tab/field name, show the user-friendly label, never raw internal codes.

**Priority:** High

**Acceptance Criteria:**
- Menu items show `sys_menu.name`
- Window title shows `sys_window.name`
- Tab headers show `sys_tab.name`
- Field labels show `label_override` (if set) or `sys_column.label`
- List view column headers use window field labels
- Ctrl+K search shows window names, not table codes
- No raw internal table codes (`sys_`, `admin_` prefixes) displayed to any user

---

### FR-012: Change frontend routes to /window/{windowName}

**Description:** The existing frontend uses `/runtime/{formCode}` for routing. Change this to `/window/{windowName}` to reflect the new Window concept.

**Priority:** High

**Acceptance Criteria:**
- Route pattern changes from `/runtime/sales_order` to `/window/sales_order`
- All existing navigation, breadcrumbs, and Ctrl+K search use the new route
- Old `/runtime/*` routes redirect to `/window/*` (if needed for backward compatibility)
- URL changes do not break existing functionality

---

### FR-013: Change runtime API endpoints from formCode to windowName

**Description:** The backend runtime APIs use `formCode` in endpoint paths. Change to `windowName` to match the new terminology.

**Priority:** High

**Acceptance Criteria:**
- `GET /api/runtime/forms/{formCode}/definition` → `GET /api/runtime/windows/{windowName}/definition`
- `GET /api/runtime/forms/{formCode}/records` → `GET /api/runtime/windows/{windowName}/records`
- All other runtime endpoints updated similarly
- The `formCode` parameter in existing backend services is replaced with `windowName`
- Backend returns data using new table structure (`sys_window`, `sys_tab`, `sys_window_field`, `sys_column`)

---

### FR-014: Admin Forms for Metadata Tables (replaces PRD-002)

**Description:** Create Windows/Tabs/Fields for all system metadata tables so admins can manage configuration through the same runtime interface. This replaces what PRD-002 did but on the new schema.

**Priority:** High

**Acceptance Criteria:**

The following admin windows must be created:

| Window | Table | Description |
|--------|-------|-------------|
| `Table Definitions` | `sys_table` | Manage table definitions |
| `Table Columns` | `sys_column` | Manage columns (sub-tab of Table) |
| `Window Definitions` | `sys_window` | Manage windows |
| `Window Tabs` | `sys_tab` | Manage tabs (sub-tab of Window) |
| `Window Fields` | `sys_window_field` | Manage fields (sub-tab of Tab) |
| `Window Access` | `sys_window_access` | Manage role access (sub-tab of Window) |
| `Menu Configuration` | `sys_menu` | Manage menu tree |

**Structure for each admin window:**
- Window has a main tab pointing to the admin table
- Where applicable, child tabs show related records (e.g. Table → Columns sub-tab, Window → Tabs → Fields sub-tab chain)
- Fields use `is_same_line`, `seq_no`, and `is_displayed` for clean layout
- All admins can access through an "Administration" menu group

---

### FR-015: ERP Seed Windows/Tabs/Fields (replaces PRD-003)

**Description:** Seed the platform with standard ERP windows — Sales Orders, Purchase Orders, Invoices, Products, Partners, etc. — using the new Window/Tab/Field structure. This replaces what PRD-003 seeded.

**Priority:** High

**Acceptance Criteria:**

**Master Data windows:**

| Window | Table | Description |
|--------|-------|-------------|
| Business Partners | `md_business_partner` | Customers, vendors |
| Products | `md_product` | Product catalog |
| UOM | `md_uom` | Units of measure |
| Warehouses | `md_warehouse` | Warehouse locations |

**Transaction windows:**

| Window | Table | Child tabs | Description |
|--------|-------|------------|-------------|
| Sales Orders | `tx_orders` (where type=sales) | Lines, Shipments | Customer orders |
| Purchase Orders | `tx_orders` (where type=purchase) | Lines, Receipts | Vendor orders |
| Invoices | `tx_invoice` | Lines, Payments | AR/AP invoices |
| Payments | `tx_payment` | Allocations | Payment records |
| Shipments / Material Receipts | `tx_shipment` (single table, `movement_type` = inbound/outbound) | Lines | Both customer shipments and vendor receipts use the same table, differentiated by movement_type — like iDempiere's M_InOut |

Each window should have:
- A main tab (header fields)
- Child tabs for line items (where applicable)
- Fields displayed with proper `seq_no`, `is_same_line`, `is_displayed` settings
- Windows visible only to appropriate roles via `sys_window_access`

---

# Database Changes Summary

## Migration Strategy

Since the platform is still in initial development with no production data, this PRD **drops the old metadata schema entirely and creates the new one from scratch**. No migration scripts needed for existing data — the old tables are replaced.

The following old tables are completely removed:
- `sys_metadata_models`, `sys_table_columns`
- `sys_metadata_views`, `sys_form_sub_forms`, `sys_form_fields`, `sys_form_tenant_role`
- `sys_form_field_rules`, `sys_form_field_validations`
- `sys_form_layout_sections`, `sys_form_section_fields`
- `sys_form_role_filters`

## Complete table inventory

### Metadata tables (sys_ prefix — new, replaces old schema)

| Table | Purpose |
|-------|---------|
| `sys_table` | Table definitions |
| `sys_column` | Column definitions |
| `sys_window` | Window definitions |
| `sys_tab` | Tab definitions |
| `sys_window_field` | Field definitions |
| `sys_window_access` | Role-based window access |
| `sys_menu` | Hierarchical menu entries (NEW) |

### Master data tables (md_ prefix — existing from PRD-003, unchanged)

| Table | Purpose |
|-------|---------|
| `md_business_partner` | Customers, vendors |
| `md_product` | Product catalog |
| `md_uom` | Units of measure |
| `md_uom_conversion` | UOM conversion factors |
| `md_warehouse` | Warehouse locations |

### Transaction tables (tx_ prefix — existing from PRD-003, with shipment consolidation)

| Table | Purpose |
|-------|---------|
| `tx_orders` | Order header (sales + purchase, split by type) |
| `tx_order_lines` | Order lines |
| `tx_invoice` | Invoice header |
| `tx_invoice_lines` | Invoice lines |
| `tx_payment` | Payment records |
| `tx_shipment` | Shipments + Material Receipts (single table, `movement_type` = inbound/outbound — replaces separate tx_shipment + tx_material_receipt) |
| `tx_shipment_line` | Shipment/receipt lines (replaces separate tx_shipment_line + tx_mr_line) |

**Total: 19 tables** (7 sys_ + 5 md_ + 7 tx_)

---

# Entity Relationship Diagram

```
═══ LAYER 1: DATABASE SCHEMA ═══

sys_table  (what tables exist)
    └──→ sys_column  (what columns each table has)
            FK: table_id → sys_table.id


═══ LAYER 2: WINDOW DESIGN ═══

sys_window  (what users see — a "Sales Order" window)
    │  FK: table_id → sys_table.id (primary table)
    │
    ├──→ sys_tab  (tabs within the window — "Header", "Lines")
    │       │  FK: window_id → sys_window.id
    │       │  FK: table_id → sys_table.id (tab's own table)
    │       │
    │       └──→ sys_window_field  (fields on a tab)
    │               FK: tab_id → sys_tab.id
    │               FK: column_id → sys_column.id
    │
    └──→ sys_window_access  (who can see the window)
            FK: window_id → sys_window.id


═══ LAYER 3: MENU ═══

sys_menu  (what users navigate)
    │  type: 'group' (collapsible section, has children)
    │        'window' (leaf item, opens a window)
    │  FK: parent_id → sys_menu.id (for nesting groups)
    │  FK: window_id → sys_window.id (for 'window' type)
    │
    └── tree structure → rendered as collapsible menu
```

---

# Scope

## Included

- Drop old metadata schema (all old tables removed)
- Create new metadata schema: `sys_table`, `sys_column`, `sys_window`, `sys_tab`, `sys_window_field`, `sys_window_access`, `sys_menu`
- Fix RuntimePage to render actual dynamic forms (replace hardcoded bundles)
- Change frontend routes from `/runtime/{formCode}` to `/window/{windowName}`
- Fix label display throughout (no raw table codes in UI)
- Admin windows for all metadata tables (replaces PRD-002)
- Seed ERP windows: Sales Order, Purchase Order, Invoice, Payment, Shipment, Material Receipt, Business Partner, Product, UOM, Warehouse (replaces PRD-003)
- Replace flat "DYNAMIC FORMS" with hierarchical menu (`sys_menu`)
- Frontend: Menu component that loads from API, filters by role access, renders collapsible groups
- Admin UI for menu configuration (System Admin + Tenant Admin)
- Ctrl+K search bar searches window names
- Window access inherits to menu visibility (empty groups auto-hidden)

## Excluded

- Drag-and-drop menu reordering in admin UI (future)
- Menu item icons beyond basic icon picker
- Multi-language menu labels (future i18n)
- Print/Report menu items (future)
- Process/Workflow menu items (future)

---

# User Flow

## Menu Setup (Admin)

```
Admin Panel → Menu Designer
     │
     ├── View all menu entries (tree view)
     │
     ├── Create Group
     │       └── Enter: name, icon, seq_no → Save
     │
     ├── Create Menu Item
     │       └── Select: parent group, window, seq_no → Save
     │
     ├── Reorder (change seq_no)
     └── Delete (with confirmation)
```

## Runtime Usage (End User)

```
User Login
     │
     ├── Menu loads from sys_menu, filtered by sys_window_access
     │       │
     │       ├── ▶ Master Data  (collapsible group)
     │       │       ├── Business Partners (opens partner window)
     │       │       ├── Products (opens product window)
     │       │       └── Warehouses (opens warehouse window)
     │       │
     │       ├── ▶ Sales  (collapsible group)
     │       │       ├── Sales Orders (opens sales_order window)
     │       │       ├── Invoices (opens invoice window)
     │       │       └── Customers (opens customer window)
     │       │
     │       └── ▶ Purchasing
     │               └── Purchase Orders
     │
     ├── Click menu item → Window opens
     │       │
     │       ├── List view (main tab records) with search/pagination
     │       ├── Click record → Detail view
     │       │       │
     │       │       ├── Tab: "Header" (main tab fields)
     │       │       ├── Tab: "Lines" (child tab with inline grid)
     │       │       └── Tab: "Shipping" (another child tab)
     │       │
     │       └── Toolbar: Create, Save, Delete, Previous/Next
     │
     └── Ctrl+K search → Find window by name → Navigate
```

---

# Resolved Questions

1. ✅ Shipments and Material Receipts use a single `tx_shipment` table with `movement_type` field (inbound/outbound) — like iDempiere's `M_InOut`.
2. ✅ Route pattern: `/window/{windowName}` — simple, flat window name (e.g. `/window/sales_order`).
3. ✅ API pattern: Window layout (definition) sent as one JSON bundle and cached. Separate requests for record data only (tenant-filtered + context). Two-request pattern preserved.

---

# Risks

- **Scope creep** — This PRD is large: new schema + admin windows + seed data + menu + RuntimePage fix. Must be decomposed into clear implementation tasks to avoid getting stuck.
- **Frontend routing rewrite** — Changing from `/runtime/{formCode}` to `/window/{windowName}` affects routing, navigation, and search. Must coordinate frontend changes carefully.
- **Backend API changes** — All runtime endpoints need to change from `formCode` to `windowName` pattern. Existing PRD-001 backend services must be refactored.
- **ERP table dependency** — If the ERP business tables (`tx_orders`, `md_product`, etc.) from PRD-003 need changes, that adds more scope.

---

# Dependencies

- Spring Boot 3.3.4 / Java 17 / PostgreSQL
- React 18 / TypeScript / MUI 5 / Zustand / React Query
- Flyway for schema creation and seed data
- Existing PRD-003 business tables (tx_*, md_*) — kept as-is unless changes required

---

# Change History

| Version | Reason |
|---------|--------|
| 1.0.0 | Initial Draft |
