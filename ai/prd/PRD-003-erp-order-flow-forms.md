---
id: PRD-003

title: ERP Order Flow — Transaction Forms

version: 1.0.0

status: APPROVED

priority: High

owner: planner

created: 2026-07-10

updated: 2026-07-10

approved_by: user

project: Dynamic ERP Platform

repository: erp-system

prd_branch: prd/PRD-003-erp-order-flow-forms

target_branch: main

merge_strategy: merge

tech_stack:
  - Spring Boot 3.3.4
  - Java 17
  - PostgreSQL
  - Flyway (migrations only, disabled by default — enable for this PRD)
  - React 18
  - TypeScript (strict)
  - MUI 5

related_prds:
  - PRD-001 (Dynamic Form Configuration System v1.6.0) — runtime engine dependency
  - PRD-002 (Admin Configuration Forms) — created first, provides admin forms for metadata tables

related_tasks:
  - TASK-028
  - TASK-029
  - TASK-030
  - TASK-031
  - TASK-032

related_bugs: []

dependencies:
  - PRD-001 must be TESTED/COMPLETED (runtime engine and all metadata tables exist)
  - PRD-002 may provide admin forms for managing the metadata tables used here
  - Flyway must be temporarily enabled to execute the new migrations
  - Existing DDLExecutorService from PRD-001 (TASK-003)

change_log:
  - 1.0.0 — Initial PRD: 14 tables, 17 forms, pure metadata seeding via Flyway. Renamed from PRD-002 to PRD-003 (PRD-002 reassigned to Admin Configuration Forms).

---

# Executive Summary

PRD-001 delivered the **dynamic form engine** — the ability to define tables and design forms entirely through metadata, rendered at runtime without code. But the engine is an empty factory: no actual ERP business forms exist yet.

This PRD seeds the platform with the standard ERP order-flow transaction forms that every business needs. It creates **14 database tables** (5 master data + 9 transaction) and **17 forms** using PRD-001's existing metadata infrastructure. Every form supports full CRUD through PRD-001's runtime renderer.

**Zero new UI code. Zero new API endpoints.** This PRD is pure configuration — Flyway migrations that populate the metadata tables PRD-001 already reads.

---

# Problem Statement

**What problem are we solving?**

The dynamic form engine (PRD-001) is complete and tested, but no business forms exist. End users cannot create purchase orders, sales orders, invoices, payments, shipments, or material receipts because the underlying tables and form definitions have not been created.

**Who experiences this problem?**

- End users who need to perform standard ERP transactions
- System/Tenant Administrators who need to grant role access to business forms
- Developers and testers who need real forms to validate the runtime engine end-to-end

**Why is this feature required?**

Without these forms, the platform is a form builder with nothing to build. This PRD populates the foundation of the ERP system — the transaction documents that drive procurement, sales, invoicing, payments, and inventory receiving.

---

# Business Goals

1. Seed the platform with standard ERP transaction tables and forms
2. Leverage PRD-001's runtime engine — no new code for CRUD
3. Support both Purchase and Sales contexts via form-level `where_clause` filtering
4. Use standard header-line patterns (Order → Order Lines, Invoice → Invoice Lines, etc.)
5. Master data tables (Partner, Product, UOM, Warehouse) for reference fields
6. Forms must be immediately usable after migration — full CRUD on every table

---

# Functional Requirements

## FR-001: Master Data Tables

**Description:** Create 5 master data tables via Flyway migration. Each table definition is inserted into `sys_metadata_models` and `sys_table_columns`, and the physical PostgreSQL table is created by the migration.

**Priority:** High

**Acceptance Criteria:**

#### md_business_partner (Business Partner)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| code | string (50) | ✓ | Unique identifier |
| name | string (200) | ✓ | Display name |
| partner_type | enum: customer / supplier / both | ✓ | |
| email | string (100) | | |
| phone | string (30) | | |
| address | text | | |
| tax_id | string (50) | | VAT/GST number |

#### md_product (Product)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| code | string (50) | ✓ | SKU |
| name | string (200) | ✓ | |
| description | text | | |
| product_type | enum: goods / service | ✓ | |
| uom_id | many2one → md_uom | | Default unit of measure |
| unit_price | decimal (15,2) | | |
| is_active | boolean | | Handled by system column — listed for form visibility |

#### md_uom (Unit of Measure)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| code | string (10) | ✓ | PCS, KG, LTR |
| name | string (50) | ✓ | Pieces, Kilograms |

#### md_uom_conversion (UOM Conversion)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| from_uom_id | many2one → md_uom | ✓ | |
| to_uom_id | many2one → md_uom | ✓ | |
| product_id | many2one → md_product | | Optional product-specific conversion |
| factor | decimal (15,6) | ✓ | How many `to_uom` per 1 `from_uom` |

#### md_warehouse (Warehouse)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| code | string (20) | ✓ | |
| name | string (100) | ✓ | |
| address | text | | |

**System columns auto-added to every dynamic table by convention:**
`id` (UUID PK), `tenant_id` (UUID), `created_at`, `updated_at`, `created_by` (UUID), `updated_by` (UUID), `is_active` (boolean), `deleted_at` (timestamp)

---

## FR-002: Transaction Tables

**Description:** Create 9 transaction tables via Flyway migration, following the same metadata + DDL pattern.

**Priority:** High

**Acceptance Criteria:**

### Header Tables

#### tx_order (Order)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| order_number | string (50) | ✓ | |
| order_date | date | ✓ | |
| order_type | enum: purchase / sales | ✓ | Used by where_clause to split forms |
| partner_id | many2one → md_business_partner | ✓ | |
| warehouse_id | many2one → md_warehouse | | |
| currency | string (3) | | Default: USD |
| subtotal | decimal (15,2) | | |
| tax_amount | decimal (15,2) | | |
| discount_amount | decimal (15,2) | | |
| grand_total | decimal (15,2) | | |
| status | enum: draft / confirmed / received / billed / cancelled | | |
| expected_date | date | | |
| notes | text | | |

#### tx_invoice (Invoice)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| invoice_number | string (50) | ✓ | |
| invoice_date | date | ✓ | |
| due_date | date | | |
| invoice_type | enum: purchase / sales | ✓ | |
| partner_id | many2one → md_business_partner | ✓ | |
| order_id | many2one → tx_order | | Source order |
| currency | string (3) | | |
| subtotal | decimal (15,2) | | |
| tax_amount | decimal (15,2) | | |
| discount_amount | decimal (15,2) | | |
| grand_total | decimal (15,2) | | |
| paid_amount | decimal (15,2) | | |
| due_amount | decimal (15,2) | | |
| status | enum: draft / validated / paid / partially_paid / cancelled | | |
| notes | text | | |

#### tx_payment (Payment)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| payment_number | string (50) | ✓ | |
| payment_date | date | ✓ | |
| payment_type | enum: purchase / sales | ✓ | |
| partner_id | many2one → md_business_partner | ✓ | |
| payment_method | enum: cash / check / bank_transfer / credit_card | | |
| currency | string (3) | | |
| amount | decimal (15,2) | ✓ | |
| reference | string (100) | | Check number, transaction ID |
| notes | text | | |
| status | enum: draft / posted / reconciled / cancelled | | |

#### tx_shipment (Shipment)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| shipment_number | string (50) | ✓ | |
| shipment_date | date | ✓ | |
| shipment_type | enum: purchase / sales | ✓ | |
| partner_id | many2one → md_business_partner | ✓ | |
| warehouse_id | many2one → md_warehouse | | |
| order_id | many2one → tx_order | | |
| tracking_number | string (100) | | |
| carrier | string (100) | | |
| status | enum: draft / in_transit / delivered / cancelled | | |
| notes | text | | |

#### tx_material_receipt (Material Receipt)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| receipt_number | string (50) | ✓ | |
| receipt_date | date | ✓ | |
| partner_id | many2one → md_business_partner | ✓ | |
| warehouse_id | many2one → md_warehouse | | |
| order_id | many2one → tx_order | | |
| shipment_id | many2one → tx_shipment | | |
| reference | string (100) | | Supplier delivery note |
| status | enum: draft / received / inspected / cancelled | | |
| notes | text | | |

### Line Tables

#### tx_order_line (Order Line)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| order_id | many2one → tx_order | ✓ | Parent |
| line_number | integer | ✓ | |
| product_id | many2one → md_product | ✓ | |
| description | text | | |
| quantity | decimal (15,3) | | |
| uom_id | many2one → md_uom | | |
| unit_price | decimal (15,2) | | |
| line_total | decimal (15,2) | | |
| tax_rate | decimal (5,2) | | |

#### tx_invoice_line (Invoice Line)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| invoice_id | many2one → tx_invoice | ✓ | Parent |
| line_number | integer | ✓ | |
| product_id | many2one → md_product | ✓ | |
| description | text | | |
| quantity | decimal (15,3) | | |
| uom_id | many2one → md_uom | | |
| unit_price | decimal (15,2) | | |
| line_total | decimal (15,2) | | |
| tax_rate | decimal (5,2) | | |
| order_line_id | many2one → tx_order_line | | Source line |

#### tx_shipment_line (Shipment Line)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| shipment_id | many2one → tx_shipment | ✓ | Parent |
| line_number | integer | ✓ | |
| product_id | many2one → md_product | ✓ | |
| description | text | | |
| quantity | decimal (15,3) | | |
| uom_id | many2one → md_uom | | |
| order_line_id | many2one → tx_order_line | | Source line |

#### tx_mr_line (MR Line)

| Column | Type | Required | Notes |
|--------|------|:---:|-------|
| receipt_id | many2one → tx_material_receipt | ✓ | Parent |
| line_number | integer | ✓ | |
| product_id | many2one → md_product | ✓ | |
| description | text | | |
| ordered_qty | decimal (15,3) | | |
| received_qty | decimal (15,3) | | |
| uom_id | many2one → md_uom | | |
| order_line_id | many2one → tx_order_line | | Source line |
| shipment_line_id | many2one → tx_shipment_line | | Source shipment line |

---

## FR-003: Master Data Forms

**Description:** Create form definitions for all 5 master data tables. These are simple single-screen CRUD forms with no sub-forms.

**Priority:** High

**Acceptance Criteria:**

| # | Form Code | Table | Layout |
|---|-----------|-------|--------|
| 1 | `business_partner` | md_business_partner | 2-column |
| 2 | `product` | md_product | 2-column |
| 3 | `uom` | md_uom | 1-column (simple) |
| 4 | `uom_conversion` | md_uom_conversion | 1-column |
| 5 | `warehouse` | md_warehouse | 1-column |

- Each form includes all non-system fields from its table
- Forms are stored in `sys_metadata_views` with `scope = 'global'`
- Form fields are stored in `sys_form_fields`
- Layout sections stored in `sys_form_layout_sections` and `sys_form_section_fields`
- No rules, no validations beyond required flags, no where_clause
- All forms accessible after migration via PRD-001's runtime renderer

---

## FR-004: Transaction Forms — Header Forms

**Description:** Create form definitions for all transaction header tables. Each header form includes a sub-form tab for its line items. Forms are split by document type (purchase vs sales) using `where_clause`.

**Priority:** High

**Acceptance Criteria:**

| # | Form Code | Table | Where Clause | Layout | Sub-Form Tab |
|---|-----------|-------|-------------|--------|:---:|
| 1 | `purchase_order` | tx_order | `order_type = 'purchase'` | 2-col header | Order Lines |
| 2 | `sales_order` | tx_order | `order_type = 'sales'` | 2-col header | Order Lines |
| 3 | `purchase_invoice` | tx_invoice | `invoice_type = 'purchase'` | 2-col header | Invoice Lines |
| 4 | `sales_invoice` | tx_invoice | `invoice_type = 'sales'` | 2-col header | Invoice Lines |
| 5 | `purchase_payment` | tx_payment | `payment_type = 'purchase'` | 2-col | — |
| 6 | `sales_payment` | tx_payment | `payment_type = 'sales'` | 2-col | — |
| 7 | `purchase_shipment` | tx_shipment | `shipment_type = 'purchase'` | 2-col header | Shipment Lines |
| 8 | `sales_shipment` | tx_shipment | `shipment_type = 'sales'` | 2-col header | Shipment Lines |
| 9 | `material_receipt` | tx_material_receipt | — | 2-col header | MR Lines |

**Layout Details:**

Each header form has two sections:

**Section 1 — "General Information"** (fields vary by form):
- Order: partner_id, order_date, expected_date, warehouse_id, currency
- Invoice: partner_id, invoice_date, due_date, order_id, currency
- Payment: partner_id, payment_date, payment_method, currency, reference
- Shipment: partner_id, shipment_date, warehouse_id, order_id, tracking_number, carrier
- Material Receipt: partner_id, receipt_date, warehouse_id, order_id, shipment_id, reference

**Section 2 — "Details"**:
- Order: status, subtotal, tax_amount, discount_amount, grand_total, notes
- Invoice: status, subtotal, tax_amount, discount_amount, grand_total, paid_amount, due_amount, notes
- Payment: amount, status, notes
- Shipment: status, notes
- Material Receipt: status, notes

- All forms stored in `sys_metadata_views` with `scope = 'global'`
- `order_type` / `invoice_type` / `payment_type` / `shipment_type` field is **not displayed** on the form (it's auto-set by the where_clause value on record creation — per PRD-001 FR-016)
- Sub-form configurations stored in `sys_form_sub_forms`

---

## FR-005: Transaction Forms — Line Forms

**Description:** Create form definitions for all line-item tables. These forms appear as sub-form tabs in their parent header forms and as standalone forms when the user drills down via breadcrumb.

**Priority:** High

**Acceptance Criteria:**

| # | Form Code | Table | Related To | Layout |
|---|-----------|-------|-----------|--------|
| 1 | `order_line` | tx_order_line | tx_order (order_id) | Inline grid |
| 2 | `invoice_line` | tx_invoice_line | tx_invoice (invoice_id) | Inline grid |
| 3 | `shipment_line` | tx_shipment_line | tx_shipment (shipment_id) | Inline grid |
| 4 | `mr_line` | tx_mr_line | tx_material_receipt (receipt_id) | Inline grid |

- Each line form includes: line_number, product_id, description, quantity, uom_id, unit_price, line_total, plus optional reference fields (tax_rate, order_line_id, etc.)
- The parent foreign key (e.g., `order_id`) is hidden on the line form — auto-assigned by the sub-form context
- Line forms stored as `scope = 'global'`
- Forms should be independently openable (via breadcrumb drill-down)

---

## FR-006: Sub-Form Configuration

**Description:** Configure `sys_form_sub_forms` entries that link header forms to their line forms, enabling the tabbed sub-form UI.

**Priority:** High

**Acceptance Criteria:**

| Parent Form | Relation Code | Child Form Code | Tab Label | Position |
|-------------|--------------|----------------|-----------|:---:|
| purchase_order | order_id | order_line | Order Lines | 1 |
| sales_order | order_id | order_line | Order Lines | 1 |
| purchase_invoice | invoice_id | invoice_line | Invoice Lines | 1 |
| sales_invoice | invoice_id | invoice_line | Invoice Lines | 1 |
| purchase_shipment | shipment_id | shipment_line | Shipment Lines | 1 |
| sales_shipment | shipment_id | shipment_line | Shipment Lines | 1 |
| material_receipt | receipt_id | mr_line | MR Lines | 1 |

- `relation_code` is the column on the child table that references the parent (foreign key)
- `display_as` = `tab` for all entries

---

# Non-Functional Requirements

## NFR-001: Migration Safety

- All migrations use `DROP TABLE IF EXISTS ... CASCADE` before `CREATE TABLE`
- Metadata insertions use delete-and-reinsert pattern to be idempotent
- Migrations are ordered: master data tables → transaction tables → master data forms → transaction forms → sub-form configs

## NFR-002: Flyway Integration

- Flyway is disabled by default (`spring.flyway.enabled=false`)
- This PRD's migrations must be placed in `backend/src/main/resources/db/migration/`

## NFR-003: Performance

- All tables use standard PostgreSQL types
- Metadata queries cached by PRD-001's React Query layer
- Record CRUD uses PRD-001's existing `DynamicCrudService`

---

# User Stories

## US-001: Sales Team Creates a Sales Order

> As a Sales Rep, I want to open the Sales Order form, fill in the customer and order date, add line items in the Order Lines tab, so that I can record customer orders in the system.

## US-002: Procurement Creates a Purchase Order

> As a Procurement Officer, I want to open the Purchase Order form, select a supplier, add products and quantities, so that I can send purchase orders to our vendors.

## US-003: Finance Creates a Sales Invoice

> As an Accountant, I want to create a sales invoice from a sales order, review the amounts, and issue it to the customer, so that we can bill customers for completed orders.

## US-004: Warehouse Receives Goods

> As a Warehouse Operator, I want to record a material receipt against a purchase order, checking ordered vs received quantities, so that inventory can be updated when goods arrive.

## US-005: Logistics Creates a Shipment

> As a Logistics Coordinator, I want to create a shipment for a sales order, add tracking information, and mark it as shipped, so that customers can track their deliveries.

## US-006: Finance Records a Payment

> As an Accountant, I want to record a customer payment and link it to the customer, so that accounts receivable stays current.

## US-007: Admin Manages Master Data

> As a System Admin, I want to add/edit business partners, products, UOMs, and warehouses through forms, so that the transaction forms have reference data to use.

---

# User Flow

```
User logs in → Navigation menu (populated by PRD-001 runtime)

├── Master Data
│   ├── Business Partners → List → Create/Edit/Delete
│   ├── Products → List → Create/Edit/Delete
│   ├── Units of Measure → List → Create/Edit/Delete
│   ├── UOM Conversions → List → Create/Edit/Delete
│   └── Warehouses → List → Create/Edit/Delete
│
├── Procurement
│   ├── Purchase Orders → List → Open Order
│   │       └── [Order Lines] tab → Add/Edit/Delete lines
│   ├── Purchase Invoices → List → Open Invoice
│   │       └── [Invoice Lines] tab
│   ├── Purchase Payments → List → Open Payment
│   └── Purchase Shipments → List → Open Shipment
│       └── [Shipment Lines] tab
│
├── Sales
│   ├── Sales Orders → List → Open Order
│   │       └── [Order Lines] tab
│   ├── Sales Invoices → List → Open Invoice
│   │       └── [Invoice Lines] tab
│   ├── Sales Payments → List → Open Payment
│   └── Sales Shipments → List → Open Shipment
│       └── [Shipment Lines] tab
│
└── Inventory
    └── Material Receipts → List → Open Receipt
            └── [MR Lines] tab → Verify received quantities
```

---

# Scope

## Included

- 5 master data tables with full column definitions
- 9 transaction tables (Order, Order Line, Invoice, Invoice Line, Payment, Shipment, Shipment Line, Material Receipt, MR Line)
- 5 master data forms
- 12 transaction forms (9 headers + 4 line forms)
- Sub-form tab configurations (header → line)
- All table DDL executed via Flyway migrations
- All metadata inserted via Flyway migrations
- Soft-delete via system columns (is_active, deleted_at)
- Audit timestamps (created_at, updated_at, created_by, updated_by)
- Multi-tenant isolation via `tenant_id`

## Excluded

- Business logic (status transitions, auto-calculation, GL posting)
- Workflow / approvals
- Inventory impact
- Payment matching/allocation
- Automatic numbering
- Email/PDF generation
- Reports and dashboards
- Any new Java or TypeScript code (pure metadata seeding)
- Role assignment configuration (handled separately)

---

# API Requirements

**No new API endpoints.** All CRUD is handled by PRD-001's existing runtime endpoints.

---

# Database Changes

## Migration Files (new)

All migrations placed in `backend/src/main/resources/db/migration/`.

| Migration File | Contents |
|---------------|----------|
| `V{next}__seed_master_data_tables.sql` | DROP + CREATE TABLE for 5 master data tables + metadata |
| `V{next+1}__seed_transaction_tables.sql` | DROP + CREATE TABLE for 9 transaction tables + metadata |
| `V{next+2}__seed_master_data_forms.sql` | INSERT form definitions for 5 master data forms |
| `V{next+3}__seed_transaction_header_forms.sql` | INSERT form definitions for 9 header forms |
| `V{next+4}__seed_transaction_line_forms.sql` | INSERT form definitions for 4 line forms |
| `V{next+5}__seed_sub_form_configs.sql` | INSERT sub-form configurations |

## No changes to existing tables

All existing PRD-001 metadata tables remain unchanged in structure. Only new rows are inserted.

---

# Security Requirements

- All data access through PRD-001's runtime API (JWT, tenant isolation, role-based access)
- Form-level where_clause enforced by PRD-001's runtime

---

# Edge Cases

| Edge Case | Handling |
|-----------|----------|
| Existing tables with same name | `DROP TABLE IF EXISTS ... CASCADE` before creation |
| Migration re-run | Delete-then-insert pattern for idempotency |
| Flyway disabled by default | Enable temporarily for migration, disable after |

---

# Risks

| Risk | Impact | Mitigation |
|------|--------|------------|
| DROP TABLE CASCADE on re-run | Dev only | Idempotency guards |
| Flyway temporarily enabled | Low | Documented |
| Column type mismatches | Medium | Single source in migration |

---

# Assumptions

1. PRD-001 is fully TESTED/COMPLETED — all metadata tables exist
2. Flyway migration numbering starts after last existing migration
3. `tenant_id` is injected by PRD-001's runtime from JWT
4. All forms are `scope = 'global'`
5. Role assignment handled separately

---

# Dependencies

| Dependency | Description | Status |
|-----------|-------------|--------|
| PRD-001 Runtime Engine | DynamicFormRenderer, useForm(), RuntimeFormController | TESTED |
| PRD-001 Metadata Tables | sys_metadata_models, etc. | Exist |
| PRD-001 DynamicCrudService | Runtime CRUD on dynamic tables | TESTED |
| Flyway | Must be temporarily enabled | Config change |

---

# Acceptance Criteria

1. All 5 master data tables physically created in PostgreSQL
2. All 9 transaction tables physically created in PostgreSQL
3. All table metadata inserted into sys_metadata_models and sys_table_columns
4. All 17 form definitions inserted into sys_metadata_views
5. All form fields inserted into sys_form_fields
6. Form layouts inserted into sys_form_layout_sections and sys_form_section_fields
7. Sub-form configurations inserted into sys_form_sub_forms
8. All 17 forms appear in `GET /api/runtime/forms`
9. Each form renders correctly in the browser
10. Full CRUD works on every form
11. Header-line sub-form tabs work correctly
12. Purchase/Sales where_clause filtering works
13. Soft-delete works correctly
14. Many2one fields render as dropdowns

---

# Deployment Requirements

- Temporarily set `spring.flyway.enabled=true`
- Place 6 migration files in `db/migration/`
- Start application → Flyway executes migrations
- Verify forms load
- Revert `spring.flyway.enabled=false`

---

# Testing Requirements

- Verify all 14 tables exist in PostgreSQL
- Open all 17 forms in browser
- Full CRUD cycle on each form
- Sub-form drill-down with breadcrumb
- where_clause filtering (purchase vs sales)
- Soft-delete verification

---

# Future Enhancements

1. Auto-numbering (PO-0001, INV-0001)
2. Status workflow enforcement
3. Auto-calculation of totals
4. Payment allocation to invoices
5. Inventory posting on receipt/shipment
6. Role assignment seeding
7. Currency exchange rates
8. Tax configuration

---

# Open Questions

*(none)*

---

# Change History

| Version | Reason | Date |
|---------|--------|------|
| 1.0.0 | Initial PRD — 14 tables, 17 forms, pure metadata seeding. Renamed from PRD-002 to PRD-003. | 2026-07-10 |

---

# Related Documents

- [PRD-001 — Dynamic Form Configuration System](../prd/PRD-001-dynamic-form-configuration-system.md)
- [PRD-002 — Admin Configuration Forms](../prd/PRD-002-admin-configuration-forms.md)
- [PROJECT_BOARD.md](../PROJECT_BOARD.md)
- [CHANGELOG.md](../docs/CHANGELOG.md)
