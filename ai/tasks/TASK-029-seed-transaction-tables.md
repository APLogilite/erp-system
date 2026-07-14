---
id: TASK-029

title: Seed Transaction Tables (Flyway Migration)

type: Database

status: TESTED

priority: High

owner: QA Engineer

assigned_to: QA Engineer

assigned_branch: feature/TASK-029

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
  - TASK-028

blocks:
  - TASK-031

labels:
  - database
  - flyway
  - seed
  - transaction

review_required: true

test_required: true

automation_required: false

change_summary: ai/changes/CHANGE-TASK-029.md

test_report:

history:
  - 2026-07-10 — Product Manager — Created task from PRD-002 v1.0.0
  - 2026-07-13 — Software Engineer — Activated to IN_DEVELOPMENT, started implementation
  - 2026-07-13 — Software Engineer — Created V20 Flyway migration with 9 transaction tables + metadata registration

---

# Goal

Create a Flyway migration that seeds the 9 transaction tables: Order, Order Line, Invoice, Invoice Line, Payment, Shipment, Shipment Line, Material Receipt, MR Line. Includes both physical table DDL and metadata inserts.

---

# Description

Create file `backend/src/main/resources/db/migration/V{next}__seed_transaction_tables.sql`.

### Part 1 — Drop Existing Tables (in FK-safe order)

```sql
DROP TABLE IF EXISTS tx_mr_line CASCADE;
DROP TABLE IF EXISTS tx_shipment_line CASCADE;
DROP TABLE IF EXISTS tx_invoice_line CASCADE;
DROP TABLE IF EXISTS tx_order_line CASCADE;
DROP TABLE IF EXISTS tx_material_receipt CASCADE;
DROP TABLE IF EXISTS tx_shipment CASCADE;
DROP TABLE IF EXISTS tx_payment CASCADE;
DROP TABLE IF EXISTS tx_invoice CASCADE;
DROP TABLE IF EXISTS tx_order CASCADE;
```

### Part 2 — Create Header Tables

All tables include system columns: `id UUID PRIMARY KEY DEFAULT gen_random_uuid()`, `tenant_id UUID NOT NULL`, `created_at TIMESTAMP NOT NULL DEFAULT now()`, `updated_at TIMESTAMP NOT NULL DEFAULT now()`, `created_by UUID`, `updated_by UUID`, `is_active BOOLEAN NOT NULL DEFAULT true`, `deleted_at TIMESTAMP`.

#### tx_order

| Column | PostgreSQL Type | Constraint |
|--------|----------------|------------|
| order_number | VARCHAR(50) | NOT NULL |
| order_date | DATE | NOT NULL |
| order_type | VARCHAR(20) | NOT NULL |
| partner_id | UUID | NOT NULL |
| warehouse_id | UUID | |
| currency | VARCHAR(3) | DEFAULT 'USD' |
| subtotal | NUMERIC(15,2) | DEFAULT 0 |
| tax_amount | NUMERIC(15,2) | DEFAULT 0 |
| discount_amount | NUMERIC(15,2) | DEFAULT 0 |
| grand_total | NUMERIC(15,2) | DEFAULT 0 |
| status | VARCHAR(30) | DEFAULT 'draft' |
| expected_date | DATE | |
| notes | TEXT | |

#### tx_invoice

| Column | PostgreSQL Type | Constraint |
|--------|----------------|------------|
| invoice_number | VARCHAR(50) | NOT NULL |
| invoice_date | DATE | NOT NULL |
| due_date | DATE | |
| invoice_type | VARCHAR(20) | NOT NULL |
| partner_id | UUID | NOT NULL |
| order_id | UUID | |
| currency | VARCHAR(3) | DEFAULT 'USD' |
| subtotal | NUMERIC(15,2) | DEFAULT 0 |
| tax_amount | NUMERIC(15,2) | DEFAULT 0 |
| discount_amount | NUMERIC(15,2) | DEFAULT 0 |
| grand_total | NUMERIC(15,2) | DEFAULT 0 |
| paid_amount | NUMERIC(15,2) | DEFAULT 0 |
| due_amount | NUMERIC(15,2) | DEFAULT 0 |
| status | VARCHAR(30) | DEFAULT 'draft' |
| notes | TEXT | |

#### tx_payment

| Column | PostgreSQL Type | Constraint |
|--------|----------------|------------|
| payment_number | VARCHAR(50) | NOT NULL |
| payment_date | DATE | NOT NULL |
| payment_type | VARCHAR(20) | NOT NULL |
| partner_id | UUID | NOT NULL |
| payment_method | VARCHAR(30) | |
| currency | VARCHAR(3) | DEFAULT 'USD' |
| amount | NUMERIC(15,2) | NOT NULL |
| reference | VARCHAR(100) | |
| notes | TEXT | |
| status | VARCHAR(30) | DEFAULT 'draft' |

#### tx_shipment

| Column | PostgreSQL Type | Constraint |
|--------|----------------|------------|
| shipment_number | VARCHAR(50) | NOT NULL |
| shipment_date | DATE | NOT NULL |
| shipment_type | VARCHAR(20) | NOT NULL |
| partner_id | UUID | NOT NULL |
| warehouse_id | UUID | |
| order_id | UUID | |
| tracking_number | VARCHAR(100) | |
| carrier | VARCHAR(100) | |
| status | VARCHAR(30) | DEFAULT 'draft' |
| notes | TEXT | |

#### tx_material_receipt

| Column | PostgreSQL Type | Constraint |
|--------|----------------|------------|
| receipt_number | VARCHAR(50) | NOT NULL |
| receipt_date | DATE | NOT NULL |
| partner_id | UUID | NOT NULL |
| warehouse_id | UUID | |
| order_id | UUID | |
| shipment_id | UUID | |
| reference | VARCHAR(100) | |
| status | VARCHAR(30) | DEFAULT 'draft' |
| notes | TEXT | |

### Part 3 — Create Line Tables

#### tx_order_line

| Column | PostgreSQL Type | Constraint |
|--------|----------------|------------|
| order_id | UUID | NOT NULL |
| line_number | INTEGER | NOT NULL |
| product_id | UUID | NOT NULL |
| description | TEXT | |
| quantity | NUMERIC(15,3) | |
| uom_id | UUID | |
| unit_price | NUMERIC(15,2) | |
| line_total | NUMERIC(15,2) | |
| tax_rate | NUMERIC(5,2) | |

#### tx_invoice_line

| Column | PostgreSQL Type | Constraint |
|--------|----------------|------------|
| invoice_id | UUID | NOT NULL |
| line_number | INTEGER | NOT NULL |
| product_id | UUID | NOT NULL |
| description | TEXT | |
| quantity | NUMERIC(15,3) | |
| uom_id | UUID | |
| unit_price | NUMERIC(15,2) | |
| line_total | NUMERIC(15,2) | |
| tax_rate | NUMERIC(5,2) | |
| order_line_id | UUID | |

#### tx_shipment_line

| Column | PostgreSQL Type | Constraint |
|--------|----------------|------------|
| shipment_id | UUID | NOT NULL |
| line_number | INTEGER | NOT NULL |
| product_id | UUID | NOT NULL |
| description | TEXT | |
| quantity | NUMERIC(15,3) | |
| uom_id | UUID | |
| order_line_id | UUID | |

#### tx_mr_line

| Column | PostgreSQL Type | Constraint |
|--------|----------------|------------|
| receipt_id | UUID | NOT NULL |
| line_number | INTEGER | NOT NULL |
| product_id | UUID | NOT NULL |
| description | TEXT | |
| ordered_qty | NUMERIC(15,3) | |
| received_qty | NUMERIC(15,3) | |
| uom_id | UUID | |
| order_line_id | UUID | |
| shipment_line_id | UUID | |

### Part 4 — Foreign Key Indexes

```sql
-- Order
CREATE INDEX IF NOT EXISTS idx_order_partner ON tx_order(partner_id);
CREATE INDEX IF NOT EXISTS idx_order_warehouse ON tx_order(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_order_type ON tx_order(order_type);

-- Order Line
CREATE INDEX IF NOT EXISTS idx_ol_order ON tx_order_line(order_id);
CREATE INDEX IF NOT EXISTS idx_ol_product ON tx_order_line(product_id);

-- Invoice
CREATE INDEX IF NOT EXISTS idx_invoice_partner ON tx_invoice(partner_id);
CREATE INDEX IF NOT EXISTS idx_invoice_order ON tx_invoice(order_id);
CREATE INDEX IF NOT EXISTS idx_invoice_type ON tx_invoice(invoice_type);

-- Invoice Line
CREATE INDEX IF NOT EXISTS idx_il_invoice ON tx_invoice_line(invoice_id);
CREATE INDEX IF NOT EXISTS idx_il_product ON tx_invoice_line(product_id);
CREATE INDEX IF NOT EXISTS idx_il_ol ON tx_invoice_line(order_line_id);

-- Payment
CREATE INDEX IF NOT EXISTS idx_payment_partner ON tx_payment(partner_id);
CREATE INDEX IF NOT EXISTS idx_payment_type ON tx_payment(payment_type);

-- Shipment
CREATE INDEX IF NOT EXISTS idx_ship_partner ON tx_shipment(partner_id);
CREATE INDEX IF NOT EXISTS idx_ship_warehouse ON tx_shipment(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_ship_order ON tx_shipment(order_id);
CREATE INDEX IF NOT EXISTS idx_ship_type ON tx_shipment(shipment_type);

-- Shipment Line
CREATE INDEX IF NOT EXISTS idx_sl_shipment ON tx_shipment_line(shipment_id);
CREATE INDEX IF NOT EXISTS idx_sl_product ON tx_shipment_line(product_id);

-- Material Receipt
CREATE INDEX IF NOT EXISTS idx_mr_partner ON tx_material_receipt(partner_id);
CREATE INDEX IF NOT EXISTS idx_mr_warehouse ON tx_material_receipt(warehouse_id);
CREATE INDEX IF NOT EXISTS idx_mr_order ON tx_material_receipt(order_id);
CREATE INDEX IF NOT EXISTS idx_mr_shipment ON tx_material_receipt(shipment_id);

-- MR Line
CREATE INDEX IF NOT EXISTS idx_mrl_receipt ON tx_mr_line(receipt_id);
CREATE INDEX IF NOT EXISTS idx_mrl_product ON tx_mr_line(product_id);
```

### Part 5 — Insert Metadata

**For each header/line table, insert into `sys_metadata_models`:**
```sql
DELETE FROM sys_table_columns WHERE table_id IN (SELECT id FROM sys_metadata_models WHERE name = 'tx_order');
DELETE FROM sys_metadata_models WHERE name = 'tx_order';

INSERT INTO sys_metadata_models (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
VALUES (gen_random_uuid(), 'tx_order', 'Order', 'Orders', 'dynamic', 'tx_order', 'Purchase and sales orders', true, now(), now());
```

Repeat for all 9 tables with their labels:
- `tx_order` → 'Order' / 'Orders'
- `tx_order_line` → 'Order Line' / 'Order Lines'
- `tx_invoice` → 'Invoice' / 'Invoices'
- `tx_invoice_line` → 'Invoice Line' / 'Invoice Lines'
- `tx_payment` → 'Payment' / 'Payments'
- `tx_shipment` → 'Shipment' / 'Shipments'
- `tx_shipment_line` → 'Shipment Line' / 'Shipment Lines'
- `tx_material_receipt` → 'Material Receipt' / 'Material Receipts'
- `tx_mr_line` → 'MR Line' / 'MR Lines'

**For every column, insert into `sys_table_columns`** using the `SELECT id FROM sys_metadata_models WHERE name = '...'` pattern to resolve table_id.

Complete column metadata — consult PRD-002 FR-002 for the full column inventory per table.

### Enum Column Values

Record as JSONB in `enum_options`:

- `order_type`: `'["purchase","sales"]'`
- `invoice_type`: `'["purchase","sales"]'`
- `payment_type`: `'["purchase","sales"]'`
- `shipment_type`: `'["purchase","sales"]'`
- `payment_method`: `'["cash","check","bank_transfer","credit_card"]'`
- Status fields (order): `'["draft","confirmed","received","billed","cancelled"]'`
- Status fields (invoice): `'["draft","validated","paid","partially_paid","cancelled"]'`
- Status fields (payment): `'["draft","posted","reconciled","cancelled"]'`
- Status fields (shipment): `'["draft","in_transit","delivered","cancelled"]'`
- Status fields (mr): `'["draft","received","inspected","cancelled"]'`

### many2one relation_table values

Set the `relation_table` column in `sys_table_columns` for all foreign key fields. The value is the referenced table's `name` in `sys_metadata_models`:
- `partner_id` → `md_business_partner`
- `warehouse_id` → `md_warehouse`
- `product_id` → `md_product`
- `uom_id` → `md_uom`
- `order_id` → `tx_order`
- `invoice_id` → `tx_invoice`
- `shipment_id` → `tx_shipment`
- `receipt_id` → `tx_material_receipt`
- `order_line_id` → `tx_order_line`
- `shipment_line_id` → `tx_shipment_line`

---

# Acceptance Criteria

- [ ] Flyway migration file exists at `V{next}__seed_transaction_tables.sql`
- [ ] Migration drops existing tables before creating (idempotent)
- [ ] All 9 tables are created with correct column types
- [ ] All tables include 8 system columns
- [ ] 9 rows inserted into `sys_metadata_models`
- [ ] All column metadata inserted into `sys_table_columns` (~90 rows total)
- [ ] All enum column types have `enum_options` JSONB populated
- [ ] All many2one columns have `relation_table` set
- [ ] Foreign key indexes exist on all many2one and type columns
- [ ] Migration runs successfully
- [ ] Migration is idempotent
- [ ] All 9 tables are queryable after migration

---

# Technical Notes

### Dependencies
This migration must run AFTER TASK-028 (master data tables) because line tables reference `md_product`, `md_uom`, `md_business_partner`, `md_warehouse` via foreign key columns stored as UUID.

### Flyway Version
Use `{next}` = whatever was used for TASK-028 + 1.

### No Hard Foreign Key Constraints
Since these are dynamic tables managed by the metadata engine, do NOT add hard PostgreSQL `REFERENCES` constraints. PRD-001's runtime handles referential integrity through the metadata system. The UUID columns simply store the referenced record's ID.

---

# Files Expected

- `backend/src/main/resources/db/migration/V{next}__seed_transaction_tables.sql` (new)

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
- [TASK-028 — Seed Master Data Tables](../tasks/TASK-028-seed-master-data-tables.md)
