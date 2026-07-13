---
id: CHANGE-TASK-029

task_id: TASK-029

parent_prd: PRD-003

branch: feature/TASK-029

type: Feature

status: IMPLEMENTED

developer: Software Engineer

started: 2026-07-13

completed: 2026-07-13

duration: 1h

related_commits:
  - (pending commit)

related_files:
  - backend/src/main/resources/db/migration/V20__seed_transaction_tables.sql

review_required: true

test_required: true

---

# Summary

Created Flyway migration `V20__seed_transaction_tables.sql` that seeds 9 transaction tables for the ERP Order Flow module: Order, Order Line, Invoice, Invoice Line, Payment, Shipment, Shipment Line, Material Receipt, and MR Line. Each table includes physical DDL with 8 system columns, metadata registration in the engine, and foreign key indexes.

---

# Business Requirements Implemented

- [x] Flyway migration file at `V20__seed_transaction_tables.sql`
- [x] Drop existing tables in FK-safe order (line tables before headers)
- [x] All 9 tables created with correct PostgreSQL column types
- [x] All tables include 8 system columns (id, tenant_id, timestamps, soft-delete)
- [x] 9 rows inserted into `sys_metadata_models`
- [x] All column metadata inserted into `sys_table_columns` (~115 rows)
- [x] All enum columns have `enum_options` JSONB populated (order_type, invoice_type, payment_type, payment_method, shipment_type, status fields)
- [x] All many2one columns have `relation_table` set
- [x] No hard FOREIGN KEY constraints per PRD-003 spec (UUID columns store reference IDs)
- [x] Foreign key indexes on all many2one columns + tenant_id + status columns
- [x] Migration is idempotent — DROP IF EXISTS + DELETE before INSERT
- [x] `spring.flyway.enabled=true` documented in migration comments

---

# Files Added

| File | Purpose |
|------|---------|
| `backend/src/main/resources/db/migration/V20__seed_transaction_tables.sql` | Flyway migration creating 9 transaction tables + metadata registration |

---

# Database Changes

## Tables Added

| Table | Type | Business Columns |
|-------|------|-----------------|
| `tx_order` | dynamic (header) | order_number, order_date, order_type, partner_id, warehouse_id, currency, subtotal, tax_amount, discount_amount, grand_total, status, expected_date, notes |
| `tx_order_line` | dynamic (line) | order_id, line_number, product_id, description, quantity, uom_id, unit_price, line_total, tax_rate |
| `tx_invoice` | dynamic (header) | invoice_number, invoice_date, due_date, invoice_type, partner_id, order_id, currency, subtotal, tax_amount, discount_amount, grand_total, paid_amount, due_amount, status, notes |
| `tx_invoice_line` | dynamic (line) | invoice_id, line_number, product_id, description, quantity, uom_id, unit_price, line_total, tax_rate, order_line_id |
| `tx_payment` | dynamic (header) | payment_number, payment_date, payment_type, partner_id, payment_method, currency, amount, reference, notes, status |
| `tx_shipment` | dynamic (header) | shipment_number, shipment_date, shipment_type, partner_id, warehouse_id, order_id, tracking_number, carrier, status, notes |
| `tx_shipment_line` | dynamic (line) | shipment_id, line_number, product_id, description, quantity, uom_id, order_line_id |
| `tx_material_receipt` | dynamic (header) | receipt_number, receipt_date, partner_id, warehouse_id, order_id, shipment_id, reference, status, notes |
| `tx_mr_line` | dynamic (line) | receipt_id, line_number, product_id, description, ordered_qty, received_qty, uom_id, order_line_id, shipment_line_id |

## Indexes

30 indexes total — covering all foreign key columns, type discriminator columns, status columns, and tenant_id columns for multi-tenant performance.

---

# Validation

## Build

PASS — `mvn clean compile` succeeded.

## Existing Automated Tests

PASS — 36 tests run, 0 failures, 3 pre-existing H2 errors (unchanged).

---

# Manual Verification

- [x] Migration follows existing idempotency pattern
- [x] No hard FK constraints per PRD-003 spec
- [x] All enum columns store options in JSONB enum_options
- [x] All many2one columns have relation_table set
- [x] Line tables reference their header tables via relation_table
- [x] Master data references (md_product, md_business_partner, md_uom, md_warehouse) correctly set
- [x] Headers created before line tables in DDL section
- [x] DROP order drops lines before headers

---

# Known Issues

None

---

# Related Documents

- [TASK-029](../tasks/TASK-029-seed-transaction-tables.md)
- [PRD-003](../prd/PRD-003-erp-order-flow-forms.md)
