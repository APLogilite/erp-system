-- ============================================================
-- V5 — ERP Windows: Master Data + Transactions + Menu + Access
-- Each window is a self-contained block (window + tabs + fields)
-- ============================================================

-- ============================================================
-- Part 1 — Register Business Tables in sys_table
-- ============================================================
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'md_business_partner', 'Business Partner', 'Business Partners', 'static', 'md_business_partner', 'Customers, vendors and contacts', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_business_partner');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'md_product', 'Product', 'Products', 'static', 'md_product', 'Product catalog', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_product');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'md_uom', 'UOM', 'UOMs', 'static', 'md_uom', 'Units of measure', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_uom');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'md_warehouse', 'Warehouse', 'Warehouses', 'static', 'md_warehouse', 'Warehouse locations', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_warehouse');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_order', 'Order', 'Orders', 'static', 'tx_order', 'Purchase and sales order header', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_order');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_order_line', 'Order Line', 'Order Lines', 'static', 'tx_order_line', 'Order line items', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_order_line');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_invoice', 'Invoice', 'Invoices', 'static', 'tx_invoice', 'Invoice header', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_invoice');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_invoice_line', 'Invoice Line', 'Invoice Lines', 'static', 'tx_invoice_line', 'Invoice line items', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_invoice_line');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_payment', 'Payment', 'Payments', 'static', 'tx_payment', 'Payment records', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_payment');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_shipment', 'Shipment', 'Shipments', 'static', 'tx_shipment', 'Shipment and receipt header', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_shipment');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'tx_shipment_line', 'Shipment Line', 'Shipment Lines', 'static', 'tx_shipment_line', 'Shipment line items', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'tx_shipment_line');

-- ============================================================
-- Part 2 — Register Business Columns
-- ============================================================
CREATE OR REPLACE FUNCTION ensure_column(
  p_table_name TEXT, p_code TEXT, p_label TEXT,
  p_type TEXT, p_required BOOLEAN, p_max_length INTEGER, p_position INTEGER,
  p_relation_table TEXT DEFAULT NULL
) RETURNS void AS $$
BEGIN
  INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = p_table_name), p_code, p_label, p_type, p_required, p_max_length, p_position, p_relation_table, true, '00000000-0000-0000-0000-000000000001', now(), now()
  WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = p_table_name) AND code = p_code);
END;
$$ LANGUAGE plpgsql;

-- md_business_partner
SELECT ensure_column('md_business_partner', 'code', 'Code', 'string', true, 50, 1);
SELECT ensure_column('md_business_partner', 'name', 'Name', 'string', true, 200, 2);
SELECT ensure_column('md_business_partner', 'partner_type', 'Type', 'string', true, 20, 3);
SELECT ensure_column('md_business_partner', 'email', 'Email', 'string', false, 100, 4);
SELECT ensure_column('md_business_partner', 'phone', 'Phone', 'string', false, 30, 5);
SELECT ensure_column('md_business_partner', 'tax_id', 'Tax ID', 'string', false, 50, 6);

-- md_product
SELECT ensure_column('md_product', 'code', 'Code', 'string', true, 50, 1);
SELECT ensure_column('md_product', 'name', 'Name', 'string', true, 200, 2);
SELECT ensure_column('md_product', 'product_type', 'Type', 'string', true, 20, 3);
SELECT ensure_column('md_product', 'uom_id', 'UOM', 'many2one', false, null, 4, 'md_uom');
SELECT ensure_column('md_product', 'unit_price', 'Unit Price', 'decimal', false, null, 5);
SELECT ensure_column('md_product', 'description', 'Description', 'text', false, null, 6);

-- md_uom
SELECT ensure_column('md_uom', 'code', 'Code', 'string', true, 10, 1);
SELECT ensure_column('md_uom', 'name', 'Name', 'string', true, 50, 2);

-- md_warehouse
SELECT ensure_column('md_warehouse', 'code', 'Code', 'string', true, 20, 1);
SELECT ensure_column('md_warehouse', 'name', 'Name', 'string', true, 100, 2);

-- tx_order
SELECT ensure_column('tx_order', 'order_number', 'Order Number', 'string', true, 50, 1);
SELECT ensure_column('tx_order', 'order_date', 'Order Date', 'date', true, null, 2);
SELECT ensure_column('tx_order', 'order_type', 'Order Type', 'enum', true, 20, 3);
SELECT ensure_column('tx_order', 'partner_id', 'Partner', 'many2one', true, null, 4, 'md_business_partner');
SELECT ensure_column('tx_order', 'warehouse_id', 'Warehouse', 'many2one', false, null, 5, 'md_warehouse');
SELECT ensure_column('tx_order', 'currency', 'Currency', 'string', false, 3, 6);
SELECT ensure_column('tx_order', 'subtotal', 'Subtotal', 'decimal', false, null, 7);
SELECT ensure_column('tx_order', 'tax_amount', 'Tax', 'decimal', false, null, 8);
SELECT ensure_column('tx_order', 'grand_total', 'Total', 'decimal', false, null, 9);
SELECT ensure_column('tx_order', 'status', 'Status', 'string', false, 30, 10);
SELECT ensure_column('tx_order', 'notes', 'Notes', 'text', false, null, 11);

-- tx_order_line
SELECT ensure_column('tx_order_line', 'line_number', 'Line', 'integer', true, null, 1);
SELECT ensure_column('tx_order_line', 'product_id', 'Product', 'many2one', true, null, 2, 'md_product');
SELECT ensure_column('tx_order_line', 'description', 'Description', 'text', false, null, 3);
SELECT ensure_column('tx_order_line', 'quantity', 'Quantity', 'decimal', false, null, 4);
SELECT ensure_column('tx_order_line', 'uom_id', 'UOM', 'many2one', false, null, 5, 'md_uom');
SELECT ensure_column('tx_order_line', 'unit_price', 'Unit Price', 'decimal', false, null, 6);
SELECT ensure_column('tx_order_line', 'line_total', 'Line Total', 'decimal', false, null, 7);

-- tx_invoice
SELECT ensure_column('tx_invoice', 'invoice_number', 'Invoice Number', 'string', true, 50, 1);
SELECT ensure_column('tx_invoice', 'invoice_date', 'Invoice Date', 'date', true, null, 2);
SELECT ensure_column('tx_invoice', 'due_date', 'Due Date', 'date', false, null, 3);
SELECT ensure_column('tx_invoice', 'invoice_type', 'Invoice Type', 'enum', true, 20, 4);
SELECT ensure_column('tx_invoice', 'partner_id', 'Partner', 'many2one', true, null, 5, 'md_business_partner');
SELECT ensure_column('tx_invoice', 'currency', 'Currency', 'string', false, 3, 6);
SELECT ensure_column('tx_invoice', 'grand_total', 'Total', 'decimal', false, null, 7);
SELECT ensure_column('tx_invoice', 'status', 'Status', 'string', false, 30, 8);

-- tx_invoice_line
SELECT ensure_column('tx_invoice_line', 'line_number', 'Line', 'integer', true, null, 1);
SELECT ensure_column('tx_invoice_line', 'product_id', 'Product', 'many2one', true, null, 2, 'md_product');
SELECT ensure_column('tx_invoice_line', 'description', 'Description', 'text', false, null, 3);
SELECT ensure_column('tx_invoice_line', 'quantity', 'Quantity', 'decimal', false, null, 4);
SELECT ensure_column('tx_invoice_line', 'unit_price', 'Unit Price', 'decimal', false, null, 5);
SELECT ensure_column('tx_invoice_line', 'line_total', 'Line Total', 'decimal', false, null, 6);

-- tx_payment
SELECT ensure_column('tx_payment', 'payment_number', 'Payment Number', 'string', true, 50, 1);
SELECT ensure_column('tx_payment', 'payment_date', 'Payment Date', 'date', true, null, 2);
SELECT ensure_column('tx_payment', 'payment_type', 'Payment Type', 'string', true, 20, 3);
SELECT ensure_column('tx_payment', 'partner_id', 'Partner', 'many2one', true, null, 4, 'md_business_partner');
SELECT ensure_column('tx_payment', 'payment_method', 'Method', 'string', false, 30, 5);
SELECT ensure_column('tx_payment', 'amount', 'Amount', 'decimal', true, null, 6);
SELECT ensure_column('tx_payment', 'reference', 'Reference', 'string', false, 100, 7);
SELECT ensure_column('tx_payment', 'status', 'Status', 'string', false, 30, 8);

-- tx_shipment
SELECT ensure_column('tx_shipment', 'shipment_number', 'Shipment Number', 'string', true, 50, 1);
SELECT ensure_column('tx_shipment', 'shipment_date', 'Shipment Date', 'date', true, null, 2);
SELECT ensure_column('tx_shipment', 'shipment_type', 'Type', 'string', true, 20, 3);
SELECT ensure_column('tx_shipment', 'partner_id', 'Partner', 'many2one', true, null, 4, 'md_business_partner');
SELECT ensure_column('tx_shipment', 'warehouse_id', 'Warehouse', 'many2one', false, null, 5, 'md_warehouse');
SELECT ensure_column('tx_shipment', 'order_id', 'Order', 'many2one', false, null, 6, 'tx_order');
SELECT ensure_column('tx_shipment', 'status', 'Status', 'string', false, 30, 7);

-- tx_shipment_line
SELECT ensure_column('tx_shipment_line', 'line_number', 'Line', 'integer', true, null, 1);
SELECT ensure_column('tx_shipment_line', 'product_id', 'Product', 'many2one', true, null, 2, 'md_product');
SELECT ensure_column('tx_shipment_line', 'description', 'Description', 'text', false, null, 3);
SELECT ensure_column('tx_shipment_line', 'quantity', 'Quantity', 'decimal', false, null, 4);

DROP FUNCTION IF EXISTS ensure_column(TEXT, TEXT, TEXT, TEXT, BOOLEAN, INTEGER, INTEGER, TEXT);

-- ============================================================
-- Part 3 — Master Data Windows
-- ============================================================

-- 1. Business Partners
SELECT create_window('Business Partners', 'md_business_partner', 'Manage customer and vendor records', 'Business Partners', 10);
SELECT ensure_field('Business Partners', 10, 'code', 10, false, true, false, true);
SELECT ensure_field('Business Partners', 10, 'name', 20, true, true, false, true);
SELECT ensure_field('Business Partners', 10, 'partner_type', 30, false, true, false, true);
SELECT ensure_field('Business Partners', 10, 'email', 40, false, true, false, false);
SELECT ensure_field('Business Partners', 10, 'phone', 50, true, true, false, false);
SELECT ensure_field('Business Partners', 10, 'tax_id', 60, false, true, false, false);

-- 2. Products
SELECT create_window('Products', 'md_product', 'Manage product catalog', 'Products', 10);
SELECT ensure_field('Products', 10, 'code', 10, false, true, false, true);
SELECT ensure_field('Products', 10, 'name', 20, true, true, false, true);
SELECT ensure_field('Products', 10, 'product_type', 30, false, true, false, true);
SELECT ensure_field('Products', 10, 'uom_id', 40, false, true, false, false);
SELECT ensure_field('Products', 10, 'unit_price', 50, false, true, false, false);
SELECT ensure_field('Products', 10, 'description', 60, false, true, false, false);

-- 3. UOM
SELECT create_window('UOM', 'md_uom', 'Manage units of measure', 'UOM', 10);
SELECT ensure_field('UOM', 10, 'code', 10, false, true, false, true);
SELECT ensure_field('UOM', 10, 'name', 20, true, true, false, true);

-- 4. Warehouses
SELECT create_window('Warehouses', 'md_warehouse', 'Manage warehouse locations', 'Warehouses', 10);
SELECT ensure_field('Warehouses', 10, 'code', 10, false, true, false, true);
SELECT ensure_field('Warehouses', 10, 'name', 20, true, true, false, true);

-- ============================================================
-- Part 4 — Transaction Windows
-- ============================================================

-- 5. Sales Orders (order_type = 'sales')
SELECT create_window('Sales Orders', 'tx_order', 'Manage customer sales orders', 'Header', 10);
UPDATE sys_tab SET where_clause = 'order_type = ''sales''' WHERE name = 'Header' AND window_id = (SELECT id FROM sys_window WHERE name = 'Sales Orders') AND seq_no = 10;
SELECT ensure_field('Sales Orders', 10, 'order_number',  10, false, true, false, true);
SELECT ensure_field('Sales Orders', 10, 'order_date',    20, true,  true, false, true);
SELECT ensure_field('Sales Orders', 10, 'partner_id',    30, false, true, false, true);
SELECT ensure_field('Sales Orders', 10, 'warehouse_id',  40, false, true, false, false);
SELECT ensure_field('Sales Orders', 10, 'currency',      50, true,  true, false, false);
SELECT ensure_field('Sales Orders', 10, 'subtotal',      60, false, true, true,  false);
SELECT ensure_field('Sales Orders', 10, 'tax_amount',    70, true,  true, true,  false);
SELECT ensure_field('Sales Orders', 10, 'grand_total',   80, false, true, true,  false);
SELECT ensure_field('Sales Orders', 10, 'status',        90, false, true, false, false);
SELECT ensure_field('Sales Orders', 10, 'notes',         100, false, true, false, false);
-- Lines child tab
SELECT add_child_tab('Sales Orders', 'Lines', 'tx_order_line', 20, 'order_id');
SELECT ensure_field('Sales Orders', 20, 'line_number', 10, false, true, false, true);
SELECT ensure_field('Sales Orders', 20, 'product_id',  20, false, true, false, true);
SELECT ensure_field('Sales Orders', 20, 'description', 30, false, true, false, false);
SELECT ensure_field('Sales Orders', 20, 'quantity',    40, true,  true, false, false);
SELECT ensure_field('Sales Orders', 20, 'uom_id',      50, true,  true, false, false);
SELECT ensure_field('Sales Orders', 20, 'unit_price',  60, true,  true, false, false);
SELECT ensure_field('Sales Orders', 20, 'line_total',  70, false, true, true,  false);

-- 6. Purchase Orders (order_type = 'purchase')
SELECT create_window('Purchase Orders', 'tx_order', 'Manage vendor purchase orders', 'Header', 10);
UPDATE sys_tab SET where_clause = 'order_type = ''purchase''' WHERE name = 'Header' AND window_id = (SELECT id FROM sys_window WHERE name = 'Purchase Orders') AND seq_no = 10;
SELECT ensure_field('Purchase Orders', 10, 'order_number',   10, false, true, false, true);
SELECT ensure_field('Purchase Orders', 10, 'order_date',     20, true,  true, false, true);
SELECT ensure_field('Purchase Orders', 10, 'partner_id',     30, false, true, false, true);
SELECT ensure_field('Purchase Orders', 10, 'warehouse_id',   40, false, true, false, false);
SELECT ensure_field('Purchase Orders', 10, 'currency',       50, true,  true, false, false);
SELECT ensure_field('Purchase Orders', 10, 'subtotal',       60, false, true, true,  false);
SELECT ensure_field('Purchase Orders', 10, 'tax_amount',     70, true,  true, true,  false);
SELECT ensure_field('Purchase Orders', 10, 'grand_total',    80, false, true, true,  false);
SELECT ensure_field('Purchase Orders', 10, 'status',         90, false, true, false, false);
SELECT ensure_field('Purchase Orders', 10, 'notes',          100, false, true, false, false);
-- Lines child tab
SELECT add_child_tab('Purchase Orders', 'Lines', 'tx_order_line', 20, 'order_id');
SELECT ensure_field('Purchase Orders', 20, 'line_number', 10, false, true, false, true);
SELECT ensure_field('Purchase Orders', 20, 'product_id',  20, false, true, false, true);
SELECT ensure_field('Purchase Orders', 20, 'description', 30, false, true, false, false);
SELECT ensure_field('Purchase Orders', 20, 'quantity',    40, true,  true, false, false);
SELECT ensure_field('Purchase Orders', 20, 'uom_id',      50, true,  true, false, false);
SELECT ensure_field('Purchase Orders', 20, 'unit_price',  60, true,  true, false, false);
SELECT ensure_field('Purchase Orders', 20, 'line_total',  70, false, true, true,  false);

-- 7. Sales Invoices (invoice_type = 'sales')
SELECT create_window('Sales Invoices', 'tx_invoice', 'Manage customer invoices', 'Sales Invoices', 10);
UPDATE sys_tab SET where_clause = 'invoice_type = ''sales''' WHERE name = 'Sales Invoices' AND window_id = (SELECT id FROM sys_window WHERE name = 'Sales Invoices') AND seq_no = 10;
SELECT ensure_field('Sales Invoices', 10, 'invoice_number', 10, false, true, false, true);
SELECT ensure_field('Sales Invoices', 10, 'invoice_date',   20, true,  true, false, true);
SELECT ensure_field('Sales Invoices', 10, 'due_date',       30, true,  true, false, false);
SELECT ensure_field('Sales Invoices', 10, 'partner_id',     40, false, true, false, true);
SELECT ensure_field('Sales Invoices', 10, 'grand_total',    50, false, true, true,  false);
SELECT ensure_field('Sales Invoices', 10, 'status',         60, false, true, false, false);
-- Lines child tab
SELECT add_child_tab('Sales Invoices', 'Lines', 'tx_invoice_line', 20, 'invoice_id');
SELECT ensure_field('Sales Invoices', 20, 'line_number', 10, false, true, false, true);
SELECT ensure_field('Sales Invoices', 20, 'product_id',  20, false, true, false, true);
SELECT ensure_field('Sales Invoices', 20, 'description', 30, false, true, false, false);
SELECT ensure_field('Sales Invoices', 20, 'quantity',    40, true,  true, false, false);
SELECT ensure_field('Sales Invoices', 20, 'unit_price',  50, true,  true, false, false);
SELECT ensure_field('Sales Invoices', 20, 'line_total',  60, false, true, true,  false);

-- 8. Purchase Invoices (invoice_type = 'purchase')
SELECT create_window('Purchase Invoices', 'tx_invoice', 'Manage vendor invoices', 'Purchase Invoices', 10);
UPDATE sys_tab SET where_clause = 'invoice_type = ''purchase''' WHERE name = 'Purchase Invoices' AND window_id = (SELECT id FROM sys_window WHERE name = 'Purchase Invoices') AND seq_no = 10;
SELECT ensure_field('Purchase Invoices', 10, 'invoice_number', 10, false, true, false, true);
SELECT ensure_field('Purchase Invoices', 10, 'invoice_date',   20, true,  true, false, true);
SELECT ensure_field('Purchase Invoices', 10, 'due_date',       30, true,  true, false, false);
SELECT ensure_field('Purchase Invoices', 10, 'partner_id',     40, false, true, false, true);
SELECT ensure_field('Purchase Invoices', 10, 'grand_total',    50, false, true, true,  false);
SELECT ensure_field('Purchase Invoices', 10, 'status',         60, false, true, false, false);
-- Lines child tab
SELECT add_child_tab('Purchase Invoices', 'Lines', 'tx_invoice_line', 20, 'invoice_id');
SELECT ensure_field('Purchase Invoices', 20, 'line_number', 10, false, true, false, true);
SELECT ensure_field('Purchase Invoices', 20, 'product_id',  20, false, true, false, true);
SELECT ensure_field('Purchase Invoices', 20, 'description', 30, false, true, false, false);
SELECT ensure_field('Purchase Invoices', 20, 'quantity',    40, true,  true, false, false);
SELECT ensure_field('Purchase Invoices', 20, 'unit_price',  50, true,  true, false, false);
SELECT ensure_field('Purchase Invoices', 20, 'line_total',  60, false, true, true,  false);

-- 9. Payments
SELECT create_window('Payments', 'tx_payment', 'Manage payment records', 'Payments', 10);
SELECT ensure_field('Payments', 10, 'payment_number', 10, false, true, false, true);
SELECT ensure_field('Payments', 10, 'payment_date',   20, true,  true, false, true);
SELECT ensure_field('Payments', 10, 'payment_type',   30, false, true, false, true);
SELECT ensure_field('Payments', 10, 'partner_id',     40, false, true, false, true);
SELECT ensure_field('Payments', 10, 'payment_method', 50, true,  true, false, false);
SELECT ensure_field('Payments', 10, 'currency',       60, true,  true, false, false);
SELECT ensure_field('Payments', 10, 'amount',         70, false, true, false, true);
SELECT ensure_field('Payments', 10, 'reference',      80, false, true, false, false);
SELECT ensure_field('Payments', 10, 'status',         90, false, true, false, false);

-- 10. Shipments (standalone — NOT a child of Sales Orders)
SELECT create_window('Shipments', 'tx_shipment', 'Manage shipments and receipts', 'Shipments', 10);
SELECT ensure_field('Shipments', 10, 'shipment_number', 10, false, true, false, true);
SELECT ensure_field('Shipments', 10, 'shipment_date',   20, true,  true, false, true);
SELECT ensure_field('Shipments', 10, 'shipment_type',   30, false, true, false, true);
SELECT ensure_field('Shipments', 10, 'partner_id',      40, false, true, false, true);
SELECT ensure_field('Shipments', 10, 'warehouse_id',    50, false, true, false, false);
SELECT ensure_field('Shipments', 10, 'order_id',        60, false, true, false, false);
SELECT ensure_field('Shipments', 10, 'status',          70, false, true, false, false);
-- Lines child tab
SELECT add_child_tab('Shipments', 'Lines', 'tx_shipment_line', 20, 'shipment_id');
SELECT ensure_field('Shipments', 20, 'line_number', 10, false, true, false, true);
SELECT ensure_field('Shipments', 20, 'product_id',  20, false, true, false, true);
SELECT ensure_field('Shipments', 20, 'description', 30, false, true, false, false);
SELECT ensure_field('Shipments', 20, 'quantity',    40, true,  true, false, false);

-- ============================================================
-- Part 5 — Menu Tree
-- ============================================================

-- Menu root: Administration
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Administration', 'group', NULL, NULL, 10, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Administration' AND type = 'group' AND parent_id IS NULL);
-- Administration → Table Definitions
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Table Definitions', 'window', (SELECT id FROM sys_menu WHERE name = 'Administration' AND type = 'group' AND parent_id IS NULL), (SELECT id FROM sys_window WHERE name = 'Table Definitions'), 10, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Table Definitions' AND type = 'window');
-- Administration → Window Definitions
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Window Definitions', 'window', (SELECT id FROM sys_menu WHERE name = 'Administration' AND type = 'group' AND parent_id IS NULL), (SELECT id FROM sys_window WHERE name = 'Window Definitions'), 20, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Window Definitions' AND type = 'window');
-- Administration → Menu Configuration
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Menu Configuration', 'window', (SELECT id FROM sys_menu WHERE name = 'Administration' AND type = 'group' AND parent_id IS NULL), (SELECT id FROM sys_window WHERE name = 'Menu Configuration'), 30, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Menu Configuration' AND type = 'window');

-- Menu root: Master Data
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Master Data', 'group', NULL, NULL, 20, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Master Data' AND type = 'group' AND parent_id IS NULL);
-- Master Data → Business Partners
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Business Partners', 'window', (SELECT id FROM sys_menu WHERE name = 'Master Data' AND type = 'group'), (SELECT id FROM sys_window WHERE name = 'Business Partners'), 10, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Business Partners' AND type = 'window');
-- Master Data → Products
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Products', 'window', (SELECT id FROM sys_menu WHERE name = 'Master Data' AND type = 'group'), (SELECT id FROM sys_window WHERE name = 'Products'), 20, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Products' AND type = 'window');
-- Master Data → UOM
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'UOM', 'window', (SELECT id FROM sys_menu WHERE name = 'Master Data' AND type = 'group'), (SELECT id FROM sys_window WHERE name = 'UOM'), 30, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'UOM' AND type = 'window');
-- Master Data → Warehouses
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Warehouses', 'window', (SELECT id FROM sys_menu WHERE name = 'Master Data' AND type = 'group'), (SELECT id FROM sys_window WHERE name = 'Warehouses'), 40, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Warehouses' AND type = 'window');

-- Menu root: Transactions
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Transactions', 'group', NULL, NULL, 30, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Transactions' AND type = 'group' AND parent_id IS NULL);
-- Transactions → Sales
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Sales', 'group', (SELECT id FROM sys_menu WHERE name = 'Transactions' AND type = 'group'), NULL, 10, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Sales' AND parent_id = (SELECT id FROM sys_menu WHERE name = 'Transactions' AND type = 'group'));
-- Sales → Sales Orders
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Sales Orders', 'window', (SELECT id FROM sys_menu WHERE name = 'Sales' AND parent_id = (SELECT id FROM sys_menu WHERE name = 'Transactions' AND type = 'group')), (SELECT id FROM sys_window WHERE name = 'Sales Orders'), 10, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Sales Orders' AND type = 'window');
-- Sales → Sales Invoices
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Sales Invoices', 'window', (SELECT id FROM sys_menu WHERE name = 'Sales' AND parent_id = (SELECT id FROM sys_menu WHERE name = 'Transactions' AND type = 'group')), (SELECT id FROM sys_window WHERE name = 'Sales Invoices'), 20, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Sales Invoices' AND type = 'window');
-- Sales → Payments
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Payments', 'window', (SELECT id FROM sys_menu WHERE name = 'Sales' AND parent_id = (SELECT id FROM sys_menu WHERE name = 'Transactions' AND type = 'group')), (SELECT id FROM sys_window WHERE name = 'Payments'), 30, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Payments' AND type = 'window');
-- Transactions → Purchasing
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Purchasing', 'group', (SELECT id FROM sys_menu WHERE name = 'Transactions' AND type = 'group'), NULL, 20, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Purchasing' AND parent_id = (SELECT id FROM sys_menu WHERE name = 'Transactions' AND type = 'group'));
-- Purchasing → Purchase Orders
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Purchase Orders', 'window', (SELECT id FROM sys_menu WHERE name = 'Purchasing' AND parent_id = (SELECT id FROM sys_menu WHERE name = 'Transactions' AND type = 'group')), (SELECT id FROM sys_window WHERE name = 'Purchase Orders'), 10, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Purchase Orders' AND type = 'window');
-- Purchasing → Purchase Invoices
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Purchase Invoices', 'window', (SELECT id FROM sys_menu WHERE name = 'Purchasing' AND parent_id = (SELECT id FROM sys_menu WHERE name = 'Transactions' AND type = 'group')), (SELECT id FROM sys_window WHERE name = 'Purchase Invoices'), 20, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Purchase Invoices' AND type = 'window');
-- Transactions → Shipments
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Shipments', 'window', (SELECT id FROM sys_menu WHERE name = 'Transactions' AND type = 'group'), (SELECT id FROM sys_window WHERE name = 'Shipments'), 30, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Shipments' AND type = 'window');

-- ============================================================
-- Part 6 — Window Access for sys_admin
-- ============================================================
INSERT INTO sys_window_access (id, window_id, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), w.id, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_window w
WHERE NOT EXISTS (SELECT 1 FROM sys_window_access WHERE window_id = w.id);
