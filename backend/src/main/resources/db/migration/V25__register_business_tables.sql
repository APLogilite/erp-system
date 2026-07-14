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
-- Part 0 — Metadata Tables (sys_*) — must be registered first
-- so admin windows (V26) can reference them.
-- ============================================================

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_table', 'Table', 'Tables', 'static', 'sys_table', 'Database table definitions', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'sys_table');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_column', 'Column', 'Columns', 'static', 'sys_column', 'Table column definitions', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'sys_column');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_window', 'Window', 'Windows', 'static', 'sys_window', 'Window definitions', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'sys_window');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_tab', 'Tab', 'Tabs', 'static', 'sys_tab', 'Window tab definitions', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'sys_tab');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_window_field', 'Window Field', 'Window Fields', 'static', 'sys_window_field', 'Window field definitions', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'sys_window_field');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_window_access', 'Window Access', 'Window Access', 'static', 'sys_window_access', 'Window access control entries', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'sys_window_access');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_menu', 'Menu', 'Menus', 'static', 'sys_menu', 'Menu tree entries', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'sys_menu');

-- ============================================================
-- Part 1 — Master Data Tables (md_*)
-- ============================================================

-- -------------------------------------------------------------------
-- 1a. md_business_partner
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'md_business_partner', 'Business Partner', 'Business Partners', 'static', 'md_business_partner', 'Customers, suppliers, and other business contacts', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_business_partner');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_business_partner'), 'code',         'Code',         'string',  true,  50,  1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_business_partner') AND code = 'code');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_business_partner'), 'name',         'Name',         'string',  true,  200, 2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_business_partner') AND code = 'name');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_business_partner'), 'partner_type', 'Partner Type', 'enum',    true,  20,  3, '["customer","supplier","both"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_business_partner') AND code = 'partner_type');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_business_partner'), 'email',        'Email',        'string',  false, 100, 4, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_business_partner') AND code = 'email');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_business_partner'), 'phone',        'Phone',        'string',  false, 30,  5, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_business_partner') AND code = 'phone');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_business_partner'), 'address',      'Address',      'text',    false,      6, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_business_partner') AND code = 'address');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_business_partner'), 'tax_id',       'Tax ID',       'string',  false, 50,  7, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_business_partner') AND code = 'tax_id');

-- -------------------------------------------------------------------
-- 1b. md_product
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'md_product', 'Product', 'Products', 'static', 'md_product', 'Goods and services catalog', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_product');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_product'), 'code',         'Code',         'string',  true,  50,  1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_product') AND code = 'code');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_product'), 'name',         'Name',         'string',  true,  200, 2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_product') AND code = 'name');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_product'), 'description',  'Description',  'text',    false,      3, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_product') AND code = 'description');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_product'), 'product_type', 'Product Type', 'enum',    true,  20,  4, '["goods","service"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_product') AND code = 'product_type');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_product'), 'uom_id',       'UOM',          'many2one', false,     5, 'md_uom', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_product') AND code = 'uom_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_product'), 'unit_price',   'Unit Price',   'decimal',  false, 15,  2,    6, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_product') AND code = 'unit_price');

-- -------------------------------------------------------------------
-- 1c. md_uom
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'md_uom', 'Unit of Measure', 'Units of Measure', 'static', 'md_uom', 'Units of measure', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_uom');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_uom'), 'code', 'Code', 'string', true, 10, 1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_uom') AND code = 'code');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_uom'), 'name', 'Name', 'string', true, 50, 2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_uom') AND code = 'name');

-- -------------------------------------------------------------------
-- 1d. md_uom_conversion
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'md_uom_conversion', 'UOM Conversion', 'UOM Conversions', 'static', 'md_uom_conversion', 'Conversion factors between units of measure', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_uom_conversion');

INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_uom_conversion'), 'from_uom_id', 'From UOM', 'many2one', true, 1, 'md_uom', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_uom_conversion') AND code = 'from_uom_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_uom_conversion'), 'to_uom_id',   'To UOM',   'many2one', true, 2, 'md_uom', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_uom_conversion') AND code = 'to_uom_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_uom_conversion'), 'product_id',  'Product',  'many2one', false, 3, 'md_product', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_uom_conversion') AND code = 'product_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_uom_conversion'), 'factor',      'Factor',   'decimal',  true,  15, 6, 4, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_uom_conversion') AND code = 'factor');

-- -------------------------------------------------------------------
-- 1e. md_warehouse
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'md_warehouse', 'Warehouse', 'Warehouses', 'static', 'md_warehouse', 'Physical storage locations', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_warehouse');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_warehouse'), 'code',    'Code',    'string', true, 20,  1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_warehouse') AND code = 'code');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_warehouse'), 'name',    'Name',    'string', true, 100, 2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_warehouse') AND code = 'name');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'md_warehouse'), 'address', 'Address', 'text',   false,    3, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'md_warehouse') AND code = 'address');

-- ============================================================
-- Part 2 — Transaction Tables (tx_*)
-- ============================================================

-- -------------------------------------------------------------------
-- 2a. tx_order
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_order', 'Order', 'Orders', 'static', 'tx_order', 'Purchase and sales order header', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_order');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order'), 'order_number',   'Order Number',    'string',  true,  50,  1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order') AND code = 'order_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order'), 'order_date',     'Order Date',      'date',    true,       2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order') AND code = 'order_date');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order'), 'order_type',     'Order Type',      'enum',    true,  20,  3, '["purchase","sales"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order') AND code = 'order_type');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order'), 'partner_id',     'Partner',         'many2one', true,  4, 'md_business_partner', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order') AND code = 'partner_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order'), 'warehouse_id',   'Warehouse',       'many2one', false, 5, 'md_warehouse', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order') AND code = 'warehouse_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order'), 'currency',       'Currency',        'string',  false, 3,  6, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order') AND code = 'currency');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order'), 'subtotal',        'Subtotal',        'decimal', false, 15, 2,  7, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order') AND code = 'subtotal');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order'), 'tax_amount',     'Tax Amount',      'decimal', false, 15, 2,  8, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order') AND code = 'tax_amount');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order'), 'discount_amount','Discount Amount', 'decimal', false, 15, 2,  9, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order') AND code = 'discount_amount');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order'), 'grand_total',    'Grand Total',     'decimal', false, 15, 2, 10, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order') AND code = 'grand_total');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order'), 'status',         'Status',          'enum',    false, 30, 11, '["draft","confirmed","received","billed","cancelled"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order') AND code = 'status');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order'), 'expected_date',  'Expected Date',   'date',    false,     12, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order') AND code = 'expected_date');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order'), 'notes',          'Notes',           'text',    false,     13, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order') AND code = 'notes');

-- -------------------------------------------------------------------
-- 2b. tx_order_line
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_order_line', 'Order Line', 'Order Lines', 'static', 'tx_order_line', 'Order line items', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_order_line');

INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order_line'), 'order_id',    'Order',       'many2one', true,  1, 'tx_order', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order_line') AND code = 'order_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order_line'), 'line_number', 'Line Number', 'integer',  true,      2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order_line') AND code = 'line_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order_line'), 'product_id',  'Product',     'many2one', true,  3, 'md_product', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order_line') AND code = 'product_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order_line'), 'description', 'Description', 'text',     false,      4, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order_line') AND code = 'description');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order_line'), 'quantity',    'Quantity',    'decimal',  false, 15, 3, 5, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order_line') AND code = 'quantity');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order_line'), 'uom_id',      'UOM',         'many2one', false, 6, 'md_uom', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order_line') AND code = 'uom_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order_line'), 'unit_price',  'Unit Price',  'decimal',  false, 15, 2, 7, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order_line') AND code = 'unit_price');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order_line'), 'line_total',  'Line Total',  'decimal',  false, 15, 2, 8, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order_line') AND code = 'line_total');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_order_line'), 'tax_rate',    'Tax Rate',    'decimal',  false,  5, 2, 9, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_order_line') AND code = 'tax_rate');

-- -------------------------------------------------------------------
-- 2c. tx_invoice
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_invoice', 'Invoice', 'Invoices', 'static', 'tx_invoice', 'Purchase and sales invoice header', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_invoice');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice'), 'invoice_number','Invoice Number',  'string',  true,  50,  1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice') AND code = 'invoice_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice'), 'invoice_date',  'Invoice Date',    'date',    true,       2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice') AND code = 'invoice_date');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice'), 'due_date',      'Due Date',        'date',    false,      3, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice') AND code = 'due_date');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice'), 'invoice_type',  'Invoice Type',    'enum',    true,  20,  4, '["purchase","sales"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice') AND code = 'invoice_type');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice'), 'partner_id',    'Partner',         'many2one', true,  5, 'md_business_partner', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice') AND code = 'partner_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice'), 'order_id',      'Order',           'many2one', false, 6, 'tx_order', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice') AND code = 'order_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice'), 'currency',      'Currency',        'string',  false, 3,  7, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice') AND code = 'currency');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice'), 'subtotal',       'Subtotal',        'decimal', false, 15, 2,  8, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice') AND code = 'subtotal');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice'), 'tax_amount',    'Tax Amount',      'decimal', false, 15, 2,  9, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice') AND code = 'tax_amount');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice'), 'discount_amount','Discount Amount','decimal', false, 15, 2, 10, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice') AND code = 'discount_amount');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice'), 'grand_total',   'Grand Total',     'decimal', false, 15, 2, 11, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice') AND code = 'grand_total');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice'), 'paid_amount',   'Paid Amount',     'decimal', false, 15, 2, 12, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice') AND code = 'paid_amount');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice'), 'due_amount',    'Due Amount',      'decimal', false, 15, 2, 13, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice') AND code = 'due_amount');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice'), 'status',        'Status',          'enum',    false, 30, 14, '["draft","validated","paid","partially_paid","cancelled"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice') AND code = 'status');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice'), 'notes',         'Notes',           'text',    false,     15, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice') AND code = 'notes');

-- -------------------------------------------------------------------
-- 2d. tx_invoice_line
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_invoice_line', 'Invoice Line', 'Invoice Lines', 'static', 'tx_invoice_line', 'Invoice line items', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_invoice_line');

INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice_line'), 'invoice_id',    'Invoice',       'many2one', true,  1, 'tx_invoice', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice_line') AND code = 'invoice_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice_line'), 'line_number',   'Line Number',   'integer',  true,      2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice_line') AND code = 'line_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice_line'), 'product_id',    'Product',       'many2one', true,  3, 'md_product', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice_line') AND code = 'product_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice_line'), 'description',   'Description',   'text',     false,      4, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice_line') AND code = 'description');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice_line'), 'quantity',      'Quantity',      'decimal',  false, 15, 3, 5, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice_line') AND code = 'quantity');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice_line'), 'uom_id',        'UOM',           'many2one', false, 6, 'md_uom', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice_line') AND code = 'uom_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice_line'), 'unit_price',    'Unit Price',    'decimal',  false, 15, 2, 7, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice_line') AND code = 'unit_price');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice_line'), 'line_total',    'Line Total',    'decimal',  false, 15, 2, 8, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice_line') AND code = 'line_total');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice_line'), 'tax_rate',      'Tax Rate',      'decimal',  false,  5, 2, 9, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice_line') AND code = 'tax_rate');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_invoice_line'), 'order_line_id', 'Order Line',    'many2one', false, 10, 'tx_order_line', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_invoice_line') AND code = 'order_line_id');

-- -------------------------------------------------------------------
-- 2e. tx_payment
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_payment', 'Payment', 'Payments', 'static', 'tx_payment', 'Payment records', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_payment');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_payment'), 'payment_number','Payment Number',  'string',  true,  50,  1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_payment') AND code = 'payment_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_payment'), 'payment_date',  'Payment Date',    'date',    true,       2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_payment') AND code = 'payment_date');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_payment'), 'payment_type',  'Payment Type',    'enum',    true,  20,  3, '["purchase","sales"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_payment') AND code = 'payment_type');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_payment'), 'partner_id',    'Partner',         'many2one', true,  4, 'md_business_partner', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_payment') AND code = 'partner_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_payment'), 'payment_method','Payment Method',  'enum',    false, 30,  5, '["cash","check","bank_transfer","credit_card"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_payment') AND code = 'payment_method');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_payment'), 'currency',      'Currency',        'string',  false, 3,  6, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_payment') AND code = 'currency');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_payment'), 'amount',        'Amount',          'decimal', true,  15,  2, 7, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_payment') AND code = 'amount');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_payment'), 'reference',     'Reference',       'string',  false, 100, 8, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_payment') AND code = 'reference');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_payment'), 'notes',         'Notes',           'text',    false,      9, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_payment') AND code = 'notes');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_payment'), 'status',        'Status',          'enum',    false, 30, 10, '["draft","posted","reconciled","cancelled"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_payment') AND code = 'status');

-- -------------------------------------------------------------------
-- 2f. tx_shipment
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_shipment', 'Shipment', 'Shipments', 'static', 'tx_shipment', 'Shipment header (inbound/outbound)', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_shipment');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment'), 'shipment_number','Shipment Number','string',  true,  50,  1, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment') AND code = 'shipment_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment'), 'shipment_date', 'Shipment Date',   'date',    true,       2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment') AND code = 'shipment_date');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment'), 'shipment_type', 'Shipment Type',   'enum',    true,  20,  3, '["inbound","outbound"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment') AND code = 'shipment_type');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment'), 'partner_id',    'Partner',         'many2one', true,  4, 'md_business_partner', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment') AND code = 'partner_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment'), 'warehouse_id',  'Warehouse',       'many2one', false, 5, 'md_warehouse', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment') AND code = 'warehouse_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment'), 'order_id',      'Order',           'many2one', false, 6, 'tx_order', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment') AND code = 'order_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment'), 'tracking_number','Tracking Number','string',  false, 100, 7, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment') AND code = 'tracking_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment'), 'carrier',       'Carrier',         'string',  false, 100, 8, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment') AND code = 'carrier');
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment'), 'status',        'Status',          'enum',    false, 30,  9, '["draft","in_transit","delivered","cancelled"]', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment') AND code = 'status');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment'), 'notes',         'Notes',           'text',    false,     10, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment') AND code = 'notes');

-- -------------------------------------------------------------------
-- 2g. tx_shipment_line
-- -------------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_shipment_line', 'Shipment Line', 'Shipment Lines', 'static', 'tx_shipment_line', 'Shipment line items', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_shipment_line');

INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment_line'), 'shipment_id',   'Shipment',      'many2one', true,  1, 'tx_shipment', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment_line') AND code = 'shipment_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment_line'), 'line_number',   'Line Number',   'integer',  true,      2, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment_line') AND code = 'line_number');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment_line'), 'product_id',    'Product',       'many2one', true,  3, 'md_product', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment_line') AND code = 'product_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment_line'), 'description',   'Description',   'text',     false,      4, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment_line') AND code = 'description');
INSERT INTO sys_column (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment_line'), 'quantity',      'Quantity',      'decimal',  false, 15, 3, 5, true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment_line') AND code = 'quantity');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment_line'), 'uom_id',        'UOM',           'many2one', false, 6, 'md_uom', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment_line') AND code = 'uom_id');
INSERT INTO sys_column (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = 'tx_shipment_line'), 'order_line_id', 'Order Line',    'many2one', false, 7, 'tx_order_line', true, now(), now() WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'tx_shipment_line') AND code = 'order_line_id');
