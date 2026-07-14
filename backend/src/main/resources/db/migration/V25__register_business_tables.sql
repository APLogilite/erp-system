-- ============================================================
-- PRD-004 / TASK-042 — Register Business Tables in New Schema
--
-- Registers all existing master data (md_*) and transaction
-- (tx_*) tables in the new sys_table and sys_column tables.
-- This replaces the old PRD-003 registration pattern.
--
-- NOTE: Physical tables were created by V19 and V20 migrations.
-- This migration only registers them in the new metadata tables
-- created by V24, without modifying the actual table structures.
--
-- DEPENDS ON: V24__drop_old_metadata_create_new_schema.sql
-- ============================================================

-- ============================================================
-- Part 1 — Master Data Tables (md_*)
-- ============================================================

-- -------------------------------------------------------------------
-- 1a. md_business_partner
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'md_business_partner', 'Business Partner', 'Business Partners', 'static', 'md_business_partner', 'Customers, suppliers, and other business contacts', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_business_partner');

WITH table_id AS (SELECT id FROM sys_table WHERE name = 'md_business_partner')
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'code',         'Code',         'string',  true,  50,  1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'code');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'name',         'Name',         'string',  true,  200, 2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'name');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'partner_type', 'Partner Type', 'enum',    true,  20,  3, '["customer","supplier","both"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'partner_type');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'email',        'Email',        'string',  false, 100, 4, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'email');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'phone',        'Phone',        'string',  false, 30,  5, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'phone');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'address',      'Address',      'text',    false,      6, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'address');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'tax_id',       'Tax ID',       'string',  false, 50,  7, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'tax_id');

-- -------------------------------------------------------------------
-- 1b. md_product
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'md_product', 'Product', 'Products', 'static', 'md_product', 'Goods and services catalog', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_product');

WITH table_id AS (SELECT id FROM sys_table WHERE name = 'md_product')
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'code',         'Code',         'string',  true,  50,  1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'code');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'name',         'Name',         'string',  true,  200, 2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'name');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'description',  'Description',  'text',    false,      3, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'description');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'product_type', 'Product Type', 'enum',    true,  20,  4, '["goods","service"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'product_type');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'uom_id',       'UOM',          'many2one', false,     5, 'md_uom', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'uom_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'unit_price',   'Unit Price',   'decimal',  false, 15,  2,    6, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'unit_price');

-- -------------------------------------------------------------------
-- 1c. md_uom
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'md_uom', 'Unit of Measure', 'Units of Measure', 'static', 'md_uom', 'Units of measure', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_uom');

WITH table_id AS (SELECT id FROM sys_table WHERE name = 'md_uom')
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'code', 'Code', 'string', true, 10, 1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'code');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'name', 'Name', 'string', true, 50, 2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'name');

-- -------------------------------------------------------------------
-- 1d. md_uom_conversion
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'md_uom_conversion', 'UOM Conversion', 'UOM Conversions', 'static', 'md_uom_conversion', 'Conversion factors between units of measure', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_uom_conversion');

WITH table_id AS (SELECT id FROM sys_table WHERE name = 'md_uom_conversion')
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'from_uom_id', 'From UOM', 'many2one', true, 1, 'md_uom', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'from_uom_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'to_uom_id',   'To UOM',   'many2one', true, 2, 'md_uom', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'to_uom_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'product_id',  'Product',  'many2one', false, 3, 'md_product', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'product_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'factor',      'Factor',   'decimal',  true,  15, 6, 4, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'factor');

-- -------------------------------------------------------------------
-- 1e. md_warehouse
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'md_warehouse', 'Warehouse', 'Warehouses', 'static', 'md_warehouse', 'Physical storage locations', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_warehouse');

WITH table_id AS (SELECT id FROM sys_table WHERE name = 'md_warehouse')
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'code',    'Code',    'string', true, 20,  1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'code');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'name',    'Name',    'string', true, 100, 2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'name');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'address', 'Address', 'text',   false,    3, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'address');

-- ============================================================
-- Part 2 — Transaction Tables (tx_*)
-- ============================================================

-- -------------------------------------------------------------------
-- 2a. tx_order
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_order', 'Order', 'Orders', 'static', 'tx_order', 'Purchase and sales order header', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_order');

WITH table_id AS (SELECT id FROM sys_table WHERE name = 'tx_order')
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'order_number',   'Order Number',    'string',  true,  50,  1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'order_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'order_date',     'Order Date',      'date',    true,       2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'order_date');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'order_type',     'Order Type',      'enum',    true,  20,  3, '["purchase","sales"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'order_type');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'partner_id',     'Partner',         'many2one', true,  4, 'md_business_partner', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'partner_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'warehouse_id',   'Warehouse',       'many2one', false, 5, 'md_warehouse', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'warehouse_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'currency',       'Currency',        'string',  false, 3,  6, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'currency');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'subtotal',        'Subtotal',        'decimal', false, 15, 2,  7, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'subtotal');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'tax_amount',     'Tax Amount',      'decimal', false, 15, 2,  8, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'tax_amount');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'discount_amount','Discount Amount', 'decimal', false, 15, 2,  9, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'discount_amount');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'grand_total',    'Grand Total',     'decimal', false, 15, 2, 10, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'grand_total');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'status',         'Status',          'enum',    false, 30, 11, '["draft","confirmed","received","billed","cancelled"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'status');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'expected_date',  'Expected Date',   'date',    false,     12, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'expected_date');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'notes',          'Notes',           'text',    false,     13, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'notes');

-- -------------------------------------------------------------------
-- 2b. tx_order_line
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_order_line', 'Order Line', 'Order Lines', 'static', 'tx_order_line', 'Order line items', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_order_line');

WITH table_id AS (SELECT id FROM sys_table WHERE name = 'tx_order_line')
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'order_id',    'Order',       'many2one', true,  1, 'tx_order', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'order_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'line_number', 'Line Number', 'integer',  true,      2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'line_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'product_id',  'Product',     'many2one', true,  3, 'md_product', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'product_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'description', 'Description', 'text',     false,      4, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'description');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'quantity',    'Quantity',    'decimal',  false, 15, 3, 5, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'quantity');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'uom_id',      'UOM',         'many2one', false, 6, 'md_uom', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'uom_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'unit_price',  'Unit Price',  'decimal',  false, 15, 2, 7, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'unit_price');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'line_total',  'Line Total',  'decimal',  false, 15, 2, 8, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'line_total');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'tax_rate',    'Tax Rate',    'decimal',  false,  5, 2, 9, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'tax_rate');

-- -------------------------------------------------------------------
-- 2c. tx_invoice
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_invoice', 'Invoice', 'Invoices', 'static', 'tx_invoice', 'Purchase and sales invoice header', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_invoice');

WITH table_id AS (SELECT id FROM sys_table WHERE name = 'tx_invoice')
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'invoice_number','Invoice Number',  'string',  true,  50,  1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'invoice_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'invoice_date',  'Invoice Date',    'date',    true,       2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'invoice_date');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'due_date',      'Due Date',        'date',    false,      3, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'due_date');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'invoice_type',  'Invoice Type',    'enum',    true,  20,  4, '["purchase","sales"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'invoice_type');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'partner_id',    'Partner',         'many2one', true,  5, 'md_business_partner', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'partner_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'order_id',      'Order',           'many2one', false, 6, 'tx_order', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'order_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'currency',      'Currency',        'string',  false, 3,  7, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'currency');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'subtotal',       'Subtotal',        'decimal', false, 15, 2,  8, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'subtotal');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'tax_amount',    'Tax Amount',      'decimal', false, 15, 2,  9, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'tax_amount');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'discount_amount','Discount Amount','decimal', false, 15, 2, 10, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'discount_amount');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'grand_total',   'Grand Total',     'decimal', false, 15, 2, 11, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'grand_total');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'paid_amount',   'Paid Amount',     'decimal', false, 15, 2, 12, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'paid_amount');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'due_amount',    'Due Amount',      'decimal', false, 15, 2, 13, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'due_amount');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'status',        'Status',          'enum',    false, 30, 14, '["draft","validated","paid","partially_paid","cancelled"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'status');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'notes',         'Notes',           'text',    false,     15, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'notes');

-- -------------------------------------------------------------------
-- 2d. tx_invoice_line
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_invoice_line', 'Invoice Line', 'Invoice Lines', 'static', 'tx_invoice_line', 'Invoice line items', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_invoice_line');

WITH table_id AS (SELECT id FROM sys_table WHERE name = 'tx_invoice_line')
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'invoice_id',    'Invoice',       'many2one', true,  1, 'tx_invoice', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'invoice_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'line_number',   'Line Number',   'integer',  true,      2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'line_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'product_id',    'Product',       'many2one', true,  3, 'md_product', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'product_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'description',   'Description',   'text',     false,      4, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'description');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'quantity',      'Quantity',      'decimal',  false, 15, 3, 5, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'quantity');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'uom_id',        'UOM',           'many2one', false, 6, 'md_uom', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'uom_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'unit_price',    'Unit Price',    'decimal',  false, 15, 2, 7, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'unit_price');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'line_total',    'Line Total',    'decimal',  false, 15, 2, 8, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'line_total');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'tax_rate',      'Tax Rate',      'decimal',  false,  5, 2, 9, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'tax_rate');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'order_line_id', 'Order Line',    'many2one', false, 10, 'tx_order_line', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'order_line_id');

-- -------------------------------------------------------------------
-- 2e. tx_payment
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_payment', 'Payment', 'Payments', 'static', 'tx_payment', 'Payment records', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_payment');

WITH table_id AS (SELECT id FROM sys_table WHERE name = 'tx_payment')
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'payment_number','Payment Number',  'string',  true,  50,  1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'payment_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'payment_date',  'Payment Date',    'date',    true,       2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'payment_date');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'payment_type',  'Payment Type',    'enum',    true,  20,  3, '["purchase","sales"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'payment_type');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'partner_id',    'Partner',         'many2one', true,  4, 'md_business_partner', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'partner_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'payment_method','Payment Method',  'enum',    false, 30,  5, '["cash","check","bank_transfer","credit_card"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'payment_method');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'currency',      'Currency',        'string',  false, 3,  6, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'currency');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'amount',        'Amount',          'decimal', true,  15,  2, 7, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'amount');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'reference',     'Reference',       'string',  false, 100, 8, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'reference');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'notes',         'Notes',           'text',    false,      9, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'notes');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'status',        'Status',          'enum',    false, 30, 10, '["draft","posted","reconciled","cancelled"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'status');

-- -------------------------------------------------------------------
-- 2f. tx_shipment
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_shipment', 'Shipment', 'Shipments', 'static', 'tx_shipment', 'Shipment header (inbound/outbound)', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_shipment');

WITH table_id AS (SELECT id FROM sys_table WHERE name = 'tx_shipment')
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'shipment_number','Shipment Number','string',  true,  50,  1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'shipment_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'shipment_date', 'Shipment Date',   'date',    true,       2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'shipment_date');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'shipment_type', 'Shipment Type',   'enum',    true,  20,  3, '["inbound","outbound"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'shipment_type');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'partner_id',    'Partner',         'many2one', true,  4, 'md_business_partner', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'partner_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'warehouse_id',  'Warehouse',       'many2one', false, 5, 'md_warehouse', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'warehouse_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'order_id',      'Order',           'many2one', false, 6, 'tx_order', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'order_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'tracking_number','Tracking Number','string',  false, 100, 7, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'tracking_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'carrier',       'Carrier',         'string',  false, 100, 8, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'carrier');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'status',        'Status',          'enum',    false, 30,  9, '["draft","in_transit","delivered","cancelled"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'status');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'notes',         'Notes',           'text',    false,     10, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'notes');

-- -------------------------------------------------------------------
-- 2g. tx_shipment_line
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_shipment_line', 'Shipment Line', 'Shipment Lines', 'static', 'tx_shipment_line', 'Shipment line items', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_shipment_line');

WITH table_id AS (SELECT id FROM sys_table WHERE name = 'tx_shipment_line')
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'shipment_id',   'Shipment',      'many2one', true,  1, 'tx_shipment', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'shipment_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'line_number',   'Line Number',   'integer',  true,      2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'line_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'product_id',    'Product',       'many2one', true,  3, 'md_product', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'product_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'description',   'Description',   'text',     false,      4, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'description');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'quantity',      'Quantity',      'decimal',  false, 15, 3, 5, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'quantity');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'uom_id',        'UOM',           'many2one', false, 6, 'md_uom', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'uom_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM table_id), 'order_line_id', 'Order Line',    'many2one', false, 7, 'tx_order_line', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM table_id) AND code = 'order_line_id');
