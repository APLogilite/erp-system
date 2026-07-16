---
id: TASK-031

title: Seed Transaction Header Forms (Flyway Migration)

type: Database

status: COMPLETED

merged_to_main: 2026-07-14

priority: High

owner: QA Engineer

assigned_to: QA Engineer

assigned_branch: feature/TASK-031

locked: true

created: 2026-07-10

updated: 2026-07-13

started: 2026-07-13

completed: 2026-07-13

estimated_hours: 4

actual_hours: 1.5

parent_prd: PRD-003

prd_version: 1.0.0

prd_branch: prd/PRD-003-erp-order-flow-forms

base_branch: main

merge_target: prd/PRD-003-erp-order-flow-forms

merge_strategy: merge

parent_task:

related_tasks: []

depends_on:
  - TASK-029
  - TASK-030

blocks:
  - TASK-032

labels:
  - database
  - flyway
  - seed
  - forms
  - transaction

review_required: true

test_required: true

automation_required: false

change_summary: ai/project/changes/CHANGE-TASK-031.md

test_report:

history:
  - 2026-07-10 — Product Manager — Created task from PRD-002 v1.0.0
  - 2026-07-13 — Software Engineer — Activated to IN_DEVELOPMENT, started implementation
  - 2026-07-13 — Software Engineer — Created V22 Flyway migration with 9 transaction header forms

---

# Goal

Create a Flyway migration that defines 9 transaction header forms: Purchase Order, Sales Order, Purchase Invoice, Sales Invoice, Purchase Payment, Sales Payment, Purchase Shipment, Sales Shipment, and Material Receipt. Each form includes field configuration, 2-section layout, and where_clause for purchase/sales filtering.

---

# Description

Create file `backend/src/main/resources/db/migration/V{next}__seed_transaction_header_forms.sql`.

### Part 1 — Clean Existing (idempotency)

```sql
DELETE FROM sys_form_section_fields WHERE section_id IN (SELECT id FROM sys_form_layout_sections WHERE form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order','purchase_invoice','sales_invoice','purchase_payment','sales_payment','purchase_shipment','sales_shipment','material_receipt')));
DELETE FROM sys_form_layout_sections WHERE form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order','purchase_invoice','sales_invoice','purchase_payment','sales_payment','purchase_shipment','sales_shipment','material_receipt'));
DELETE FROM sys_form_fields WHERE form_id IN (SELECT id FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order','purchase_invoice','sales_invoice','purchase_payment','sales_payment','purchase_shipment','sales_shipment','material_receipt'));
DELETE FROM sys_metadata_views WHERE name IN ('purchase_order','sales_order','purchase_invoice','sales_invoice','purchase_payment','sales_payment','purchase_shipment','sales_shipment','material_receipt');
```

### Part 2 — Insert Form Definitions

Each form gets a row in `sys_metadata_views`. Purchase/Sales variants use `where_clause` to filter by document type. The type-discriminator field (e.g., `order_type`) is NOT included as a visible field — it's auto-set to the where_clause value on record creation.

| name | model_name | where_clause_field | where_clause_operator | where_clause_value | description |
|------|-----------|-------------------|----------------------|-------------------|-------------|
| purchase_order | tx_order | order_type | equals | purchase | Purchase orders to suppliers |
| sales_order | tx_order | order_type | equals | sales | Sales orders from customers |
| purchase_invoice | tx_invoice | invoice_type | equals | purchase | Invoices from suppliers |
| sales_invoice | tx_invoice | invoice_type | equals | sales | Invoices to customers |
| purchase_payment | tx_payment | payment_type | equals | purchase | Payments to suppliers |
| sales_payment | tx_payment | payment_type | equals | sales | Payments from customers |
| purchase_shipment | tx_shipment | shipment_type | equals | purchase | Inbound shipments from suppliers |
| sales_shipment | tx_shipment | shipment_type | equals | sales | Outbound shipments to customers |
| material_receipt | tx_material_receipt | — | — | — | Goods receipt from receiving |

All forms: `type='form'`, `scope='global'`, `tenant_id=NULL`.

### Part 3 — Insert Form Fields

Rules:
- All fields: `visible=true`, `read_only=false`
- The type discriminator (order_type, invoice_type, payment_type, shipment_type) is **excluded** — auto-set by where_clause
- `required` = ✓ as marked below
- Fields use column_code matching `sys_table_columns.code`

#### Form: purchase_order / sales_order

| Pos | column_code | label_override | required | placeholder |
|:---:|-------------|---------------|:---:|------------|
| 1 | partner_id | Supplier (purchase) / Customer (sales) | ✓ | |
| 2 | order_date | Order Date | ✓ | |
| 3 | expected_date | Expected Date | | |
| 4 | warehouse_id | Warehouse | | |
| 5 | currency | Currency | | Default: USD |
| 6 | order_number | Order Number | | Enter manually or auto |
| 7 | status | Status | | |
| 8 | subtotal | Subtotal | | |
| 9 | tax_amount | Tax Amount | | |
| 10 | discount_amount | Discount | | |
| 11 | grand_total | Grand Total | | |
| 12 | notes | Notes | | |

> **Label difference for purchase vs sales:** partner_id label = "Supplier" for purchase_order and "Customer" for sales_order.

#### Form: purchase_invoice / sales_invoice

| Pos | column_code | label_override | required | placeholder |
|:---:|-------------|---------------|:---:|------------|
| 1 | partner_id | Supplier / Customer | ✓ | |
| 2 | invoice_date | Invoice Date | ✓ | |
| 3 | due_date | Due Date | | |
| 4 | order_id | Order | | Source order |
| 5 | currency | Currency | | |
| 6 | invoice_number | Invoice Number | | |
| 7 | status | Status | | |
| 8 | subtotal | Subtotal | | |
| 9 | tax_amount | Tax Amount | | |
| 10 | discount_amount | Discount | | |
| 11 | grand_total | Grand Total | | |
| 12 | paid_amount | Paid Amount | | |
| 13 | due_amount | Due Amount | | |
| 14 | notes | Notes | | |

#### Form: purchase_payment / sales_payment

| Pos | column_code | label_override | required | placeholder |
|:---:|-------------|---------------|:---:|------------|
| 1 | partner_id | Supplier / Customer | ✓ | |
| 2 | payment_date | Payment Date | ✓ | |
| 3 | amount | Amount | ✓ | |
| 4 | payment_method | Payment Method | | |
| 5 | currency | Currency | | |
| 6 | reference | Reference | | Check/transaction number |
| 7 | payment_number | Payment Number | | |
| 8 | status | Status | | |
| 9 | notes | Notes | | |

#### Form: purchase_shipment / sales_shipment

| Pos | column_code | label_override | required | placeholder |
|:---:|-------------|---------------|:---:|------------|
| 1 | partner_id | Supplier / Customer | ✓ | |
| 2 | shipment_date | Shipment Date | ✓ | |
| 3 | warehouse_id | Warehouse | | |
| 4 | order_id | Order | | |
| 5 | tracking_number | Tracking Number | | |
| 6 | carrier | Carrier | | |
| 7 | shipment_number | Shipment Number | | |
| 8 | status | Status | | |
| 9 | notes | Notes | | |

#### Form: material_receipt

| Pos | column_code | label_override | required | placeholder |
|:---:|-------------|---------------|:---:|------------|
| 1 | partner_id | Supplier | ✓ | |
| 2 | receipt_date | Receipt Date | ✓ | |
| 3 | warehouse_id | Warehouse | | |
| 4 | order_id | Purchase Order | | |
| 5 | shipment_id | Shipment | | |
| 6 | reference | Reference | | Supplier delivery note |
| 7 | receipt_number | Receipt Number | | |
| 8 | status | Status | | |
| 9 | notes | Notes | | |

### Part 4 — Insert Layout Sections

Every header form uses the same 2-section structure:

| Section Code | Label | Cols | Pos |
|-------------|-------|:---:|:---:|
| general | General Information | 2 | 1 |
| details | Details | 2 | 2 |

Exception: **payment forms** use a single section since they have fewer fields.

For payment forms (purchase_payment, sales_payment):

| Section Code | Label | Cols | Pos |
|-------------|-------|:---:|:---:|
| general | Payment Details | 2 | 1 |

### Part 5 — Insert Section-Field Mappings

**purchase_order / sales_order:**
- Section 'general' (pos 1-5): partner_id, order_date, expected_date, warehouse_id, currency
- Section 'details' (pos 1-4): order_number, status, subtotal, tax_amount, discount_amount, grand_total, notes

**purchase_invoice / sales_invoice:**
- Section 'general' (pos 1-5): partner_id, invoice_date, due_date, order_id, currency
- Section 'details' (pos 1-7): invoice_number, status, subtotal, tax_amount, discount_amount, grand_total, paid_amount, due_amount, notes

**purchase_payment / sales_payment:**
- Section 'general' (pos 1-9): partner_id, payment_date, amount, payment_method, currency, reference, payment_number, status, notes

**purchase_shipment / sales_shipment:**
- Section 'general' (pos 1-6): partner_id, shipment_date, warehouse_id, order_id, tracking_number, carrier
- Section 'details' (pos 1-3): shipment_number, status, notes

**material_receipt:**
- Section 'general' (pos 1-6): partner_id, receipt_date, warehouse_id, order_id, shipment_id, reference
- Section 'details' (pos 1-3): receipt_number, status, notes

---

# Acceptance Criteria

- [ ] Flyway migration file exists at `V{next}__seed_transaction_header_forms.sql`
- [ ] Migration cleans existing data before inserting (idempotent)
- [ ] 9 rows inserted into `sys_metadata_views`
- [ ] ~95 field rows inserted into `sys_form_fields` (12+14+9+9+9 = ~53 per pair, plus material_receipt ~9 = ~115 total, minus type fields)
- [ ] All 9 forms have correct where_clause configured
- [ ] Type discriminator fields (order_type, invoice_type, etc.) are excluded from form fields
- [ ] Layout sections inserted correctly (2 per form, 1 for payment = 17 sections)
- [ ] Section-field mappings are complete — every field belongs to exactly one section
- [ ] purchase_order and sales_order use different partner_id labels ("Supplier" vs "Customer")
- [ ] All forms are `scope='global'`
- [ ] Migration runs successfully after TASK-028, TASK-029, TASK-030
- [ ] After migration + restart, all 9 forms are returned by `GET /api/runtime/forms`
- [ ] Each form's definition loads correctly
- [ ] Where clause filters work: purchase_order form only shows records with order_type='purchase'

---

# Technical Notes

### Label Override for Purchase vs Sales
The `partner_id` field uses `label_override` to show context-appropriate labels:
- purchase_order: `label_override = 'Supplier'`
- sales_order: `label_override = 'Customer'`

This is the ONLY difference between purchase and sales form definitions.

### Type Discriminator
The `order_type` column exists in `tx_order` and `sys_table_columns` but is NOT included in `sys_form_fields` for purchase_order or sales_order. PRD-001's runtime auto-sets it from the form's where_clause on record creation.

### Flyway Version
Use `{next}` = TASK-030's version + 1.

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__seed_transaction_header_forms.sql` (new)

---

# Developer Notes

*(maintained by Software Engineer)*

---

# Tester Notes

*(maintained by QA Engineer)*

---

# Review Notes

---

# Task History

| Date | Agent | Action |
|------|-------|--------|
| 2026-07-10 | Product Manager | Created task from PRD-002 v1.0.0 |

---

# Related Documents

- [PRD-002 — ERP Order Flow Forms](../prd/PRD-002-erp-order-flow-forms.md)
- [TASK-029 — Seed Transaction Tables](../tasks/TASK-029-seed-transaction-tables.md)
- [TASK-030 — Seed Master Data Forms](../tasks/TASK-030-seed-master-data-forms.md)
