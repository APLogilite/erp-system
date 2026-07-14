-- ============================================================
-- PRD-004 / TASK-044 — Seed ERP Windows with Tabs/Fields
--
-- Creates Windows/Tabs/Fields for all standard ERP transactions
-- and master data, replacing what PRD-003 seeded on the old schema.
--
-- DEPENDS ON: V25 (business tables registered), V26 (admin windows)
-- ============================================================

-- ============================================================
-- Helper: insert a field if not exists
-- ============================================================
CREATE OR REPLACE FUNCTION ensure_field(
  p_window_name TEXT, p_tab_seq_no INTEGER, p_column_code TEXT,
  p_seq_no INTEGER, p_is_same_line BOOLEAN, p_is_displayed BOOLEAN,
  p_is_readonly BOOLEAN, p_is_mandatory BOOLEAN
) RETURNS void AS $$
DECLARE
  v_tab_id UUID;
  v_column_id UUID;
  v_table_name TEXT;
BEGIN
  SELECT st.id, st2.name INTO v_tab_id, v_table_name
  FROM sys_tab st
  JOIN sys_window sw ON st.window_id = sw.id
  JOIN sys_table st2 ON st.table_id = st2.id
  WHERE sw.name = p_window_name AND st.seq_no = p_tab_seq_no;

  SELECT c.id INTO v_column_id
  FROM sys_column c
  JOIN sys_table t ON c.table_id = t.id
  WHERE t.name = v_table_name AND c.code = p_column_code;

  IF v_tab_id IS NOT NULL AND v_column_id IS NOT NULL THEN
    INSERT INTO sys_window_field (id, tab_id, column_id, seq_no, is_same_line, num_lines, column_width, is_displayed, is_readonly, is_mandatory, is_active, created_at, updated_at)
    SELECT gen_random_uuid(), v_tab_id, v_column_id, p_seq_no, p_is_same_line, 1, 12, p_is_displayed, p_is_readonly, p_is_mandatory, true, now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_window_field WHERE tab_id = v_tab_id AND column_id = v_column_id);
  END IF;
END;
$$ LANGUAGE plpgsql;

-- Helper: create window + main tab in one step
CREATE OR REPLACE FUNCTION create_window(
  p_name TEXT, p_table_name TEXT, p_description TEXT,
  p_tab_name TEXT, p_tab_seq INTEGER
) RETURNS UUID AS $$
DECLARE
  v_window_id UUID;
  v_table_id UUID;
BEGIN
  SELECT id INTO v_table_id FROM sys_table WHERE name = p_table_name;

  INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
  SELECT gen_random_uuid(), p_name, v_table_id, p_description, true, now(), now()
  WHERE NOT EXISTS (SELECT 1 FROM sys_window WHERE name = p_name)
  RETURNING id INTO v_window_id;

  IF v_window_id IS NOT NULL THEN
    INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, is_active, created_at, updated_at)
    SELECT gen_random_uuid(), v_window_id, p_tab_name, v_table_id, p_tab_seq, false, true, now(), now();
  END IF;

  RETURN v_window_id;
END;
$$ LANGUAGE plpgsql;

-- Helper: add child tab to existing window
CREATE OR REPLACE FUNCTION add_child_tab(
  p_window_name TEXT, p_tab_name TEXT, p_table_name TEXT,
  p_seq INTEGER, p_parent_column TEXT, p_where_clause TEXT DEFAULT NULL
) RETURNS UUID AS $$
DECLARE
  v_window_id UUID;
  v_table_id UUID;
  v_tab_id UUID;
BEGIN
  SELECT id INTO v_window_id FROM sys_window WHERE name = p_window_name;
  SELECT id INTO v_table_id FROM sys_table WHERE name = p_table_name;

  INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
  SELECT gen_random_uuid(), v_window_id, p_tab_name, v_table_id, p_seq, false, p_where_clause, p_parent_column, true, now(), now()
  WHERE NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = v_window_id AND seq_no = p_seq)
  RETURNING id INTO v_tab_id;

  RETURN v_tab_id;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- Part 1 — Master Data Windows
-- ============================================================

-- 1a. Business Partners
SELECT create_window('Business Partners', 'md_business_partner',
  'Manage customers, suppliers, and other business contacts', 'Partners', 10);
SELECT ensure_field('Business Partners', 10, 'code',         10, false, true, false, true);
SELECT ensure_field('Business Partners', 10, 'name',         20, false, true, false, true);
SELECT ensure_field('Business Partners', 10, 'partner_type', 30, false, true, false, true);
SELECT ensure_field('Business Partners', 10, 'email',        40, false, true, false, false);
SELECT ensure_field('Business Partners', 10, 'phone',        50, true,  true, false, false);
SELECT ensure_field('Business Partners', 10, 'address',      60, false, true, false, false);
SELECT ensure_field('Business Partners', 10, 'tax_id',       70, false, true, false, false);

-- 1b. Products
SELECT create_window('Products', 'md_product',
  'Manage product catalog', 'Products', 10);
SELECT ensure_field('Products', 10, 'code',         10, false, true, false, true);
SELECT ensure_field('Products', 10, 'name',         20, false, true, false, true);
SELECT ensure_field('Products', 10, 'description',  30, false, true, false, false);
SELECT ensure_field('Products', 10, 'product_type', 40, false, true, false, true);
SELECT ensure_field('Products', 10, 'uom_id',       50, false, true, false, false);
SELECT ensure_field('Products', 10, 'unit_price',   60, false, true, false, false);

-- 1c. UOM
SELECT create_window('UOM', 'md_uom',
  'Manage units of measure', 'UOM', 10);
SELECT ensure_field('UOM', 10, 'code', 10, false, true, false, true);
SELECT ensure_field('UOM', 10, 'name', 20, false, true, false, true);

-- 1d. Warehouses
SELECT create_window('Warehouses', 'md_warehouse',
  'Manage warehouse locations', 'Warehouses', 10);
SELECT ensure_field('Warehouses', 10, 'code',    10, false, true, false, true);
SELECT ensure_field('Warehouses', 10, 'name',    20, false, true, false, true);
SELECT ensure_field('Warehouses', 10, 'address', 30, false, true, false, false);

-- ============================================================
-- Part 2 — Transaction Windows
-- ============================================================

-- 2a. Sales Orders (where order_type = 'sales')
SELECT create_window('Sales Orders', 'tx_order',
  'Manage customer sales orders', 'Header', 10);
UPDATE sys_tab SET where_clause = 'order_type = ''sales''' WHERE window_id = (SELECT id FROM sys_window WHERE name = 'Sales Orders') AND seq_no = 10;

SELECT ensure_field('Sales Orders', 10, 'order_number',   10, false, true, false, true);
SELECT ensure_field('Sales Orders', 10, 'order_date',     20, true,  true, false, true);
SELECT ensure_field('Sales Orders', 10, 'partner_id',     30, false, true, false, true);
SELECT ensure_field('Sales Orders', 10, 'warehouse_id',   40, false, true, false, false);
SELECT ensure_field('Sales Orders', 10, 'currency',       50, true,  true, false, false);
SELECT ensure_field('Sales Orders', 10, 'subtotal',       60, false, true, true,  false);
SELECT ensure_field('Sales Orders', 10, 'tax_amount',     70, true,  true, true,  false);
SELECT ensure_field('Sales Orders', 10, 'grand_total',    80, false, true, true,  false);
SELECT ensure_field('Sales Orders', 10, 'status',         90, false, true, false, false);
SELECT ensure_field('Sales Orders', 10, 'notes',          100, false, true, false, false);

-- Sales Orders → Lines child tab
SELECT add_child_tab('Sales Orders', 'Lines', 'tx_order_line', 20, 'order_id');
SELECT ensure_field('Sales Orders', 20, 'line_number', 10, false, true, false, true);
SELECT ensure_field('Sales Orders', 20, 'product_id',  20, false, true, false, true);
SELECT ensure_field('Sales Orders', 20, 'description', 30, false, true, false, false);
SELECT ensure_field('Sales Orders', 20, 'quantity',    40, true,  true, false, false);
SELECT ensure_field('Sales Orders', 20, 'uom_id',      50, true,  true, false, false);
SELECT ensure_field('Sales Orders', 20, 'unit_price',  60, true,  true, false, false);
SELECT ensure_field('Sales Orders', 20, 'line_total',  70, false, true, true,  false);

-- Sales Orders → Shipments child tab
SELECT add_child_tab('Sales Orders', 'Shipments', 'tx_shipment', 30, 'order_id',
  'shipment_type = ''outbound''');
SELECT ensure_field('Sales Orders', 30, 'shipment_number', 10, false, true, false, true);
SELECT ensure_field('Sales Orders', 30, 'shipment_date',   20, true,  true, false, true);
SELECT ensure_field('Sales Orders', 30, 'partner_id',      30, false, true, false, true);
SELECT ensure_field('Sales Orders', 30, 'status',          40, false, true, false, false);

-- 2b. Purchase Orders (where order_type = 'purchase')
SELECT create_window('Purchase Orders', 'tx_order',
  'Manage vendor purchase orders', 'Header', 10);
UPDATE sys_tab SET where_clause = 'order_type = ''purchase''' WHERE window_id = (SELECT id FROM sys_window WHERE name = 'Purchase Orders') AND seq_no = 10;

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

-- Purchase Orders → Lines child tab
SELECT add_child_tab('Purchase Orders', 'Lines', 'tx_order_line', 20, 'order_id');
SELECT ensure_field('Purchase Orders', 20, 'line_number', 10, false, true, false, true);
SELECT ensure_field('Purchase Orders', 20, 'product_id',  20, false, true, false, true);
SELECT ensure_field('Purchase Orders', 20, 'description', 30, false, true, false, false);
SELECT ensure_field('Purchase Orders', 20, 'quantity',    40, true,  true, false, false);
SELECT ensure_field('Purchase Orders', 20, 'uom_id',      50, true,  true, false, false);
SELECT ensure_field('Purchase Orders', 20, 'unit_price',  60, true,  true, false, false);
SELECT ensure_field('Purchase Orders', 20, 'line_total',  70, false, true, true,  false);

-- 2c. Sales Invoices (where invoice_type = 'sales')
SELECT create_window('Sales Invoices', 'tx_invoice',
  'Manage customer sales invoices', 'Header', 10);
UPDATE sys_tab SET where_clause = 'invoice_type = ''sales''' WHERE window_id = (SELECT id FROM sys_window WHERE name = 'Sales Invoices') AND seq_no = 10;

SELECT ensure_field('Sales Invoices', 10, 'invoice_number',  10, false, true, false, true);
SELECT ensure_field('Sales Invoices', 10, 'invoice_date',    20, true,  true, false, true);
SELECT ensure_field('Sales Invoices', 10, 'due_date',        30, true,  true, false, false);
SELECT ensure_field('Sales Invoices', 10, 'partner_id',      40, false, true, false, true);
SELECT ensure_field('Sales Invoices', 10, 'order_id',        50, false, true, false, false);
SELECT ensure_field('Sales Invoices', 10, 'currency',        60, true,  true, false, false);
SELECT ensure_field('Sales Invoices', 10, 'subtotal',        70, false, true, true,  false);
SELECT ensure_field('Sales Invoices', 10, 'grand_total',     80, false, true, true,  false);
SELECT ensure_field('Sales Invoices', 10, 'paid_amount',     90, false, true, true,  false);
SELECT ensure_field('Sales Invoices', 10, 'due_amount',      100, false, true, true,  false);
SELECT ensure_field('Sales Invoices', 10, 'status',          110, false, true, false, false);
SELECT ensure_field('Sales Invoices', 10, 'notes',           120, false, true, false, false);

-- Sales Invoices → Lines child tab
SELECT add_child_tab('Sales Invoices', 'Lines', 'tx_invoice_line', 20, 'invoice_id');
SELECT ensure_field('Sales Invoices', 20, 'line_number', 10, false, true, false, true);
SELECT ensure_field('Sales Invoices', 20, 'product_id',  20, false, true, false, true);
SELECT ensure_field('Sales Invoices', 20, 'description', 30, false, true, false, false);
SELECT ensure_field('Sales Invoices', 20, 'quantity',    40, true,  true, false, false);
SELECT ensure_field('Sales Invoices', 20, 'uom_id',      50, true,  true, false, false);
SELECT ensure_field('Sales Invoices', 20, 'unit_price',  60, true,  true, false, false);
SELECT ensure_field('Sales Invoices', 20, 'line_total',  70, false, true, true,  false);

-- 2d. Purchase Invoices (where invoice_type = 'purchase')
SELECT create_window('Purchase Invoices', 'tx_invoice',
  'Manage vendor purchase invoices', 'Header', 10);
UPDATE sys_tab SET where_clause = 'invoice_type = ''purchase''' WHERE window_id = (SELECT id FROM sys_window WHERE name = 'Purchase Invoices') AND seq_no = 10;

SELECT ensure_field('Purchase Invoices', 10, 'invoice_number',  10, false, true, false, true);
SELECT ensure_field('Purchase Invoices', 10, 'invoice_date',    20, true,  true, false, true);
SELECT ensure_field('Purchase Invoices', 10, 'due_date',        30, true,  true, false, false);
SELECT ensure_field('Purchase Invoices', 10, 'partner_id',      40, false, true, false, true);
SELECT ensure_field('Purchase Invoices', 10, 'order_id',        50, false, true, false, false);
SELECT ensure_field('Purchase Invoices', 10, 'currency',        60, true,  true, false, false);
SELECT ensure_field('Purchase Invoices', 10, 'subtotal',        70, false, true, true,  false);
SELECT ensure_field('Purchase Invoices', 10, 'grand_total',     80, false, true, true,  false);
SELECT ensure_field('Purchase Invoices', 10, 'paid_amount',     90, false, true, true,  false);
SELECT ensure_field('Purchase Invoices', 10, 'due_amount',      100, false, true, true,  false);
SELECT ensure_field('Purchase Invoices', 10, 'status',          110, false, true, false, false);

-- Purchase Invoices → Lines child tab
SELECT add_child_tab('Purchase Invoices', 'Lines', 'tx_invoice_line', 20, 'invoice_id');
SELECT ensure_field('Purchase Invoices', 20, 'line_number', 10, false, true, false, true);
SELECT ensure_field('Purchase Invoices', 20, 'product_id',  20, false, true, false, true);
SELECT ensure_field('Purchase Invoices', 20, 'description', 30, false, true, false, false);
SELECT ensure_field('Purchase Invoices', 20, 'quantity',    40, true,  true, false, false);
SELECT ensure_field('Purchase Invoices', 20, 'unit_price',  60, true,  true, false, false);
SELECT ensure_field('Purchase Invoices', 20, 'line_total',  70, false, true, true,  false);

-- 2e. Payments
SELECT create_window('Payments', 'tx_payment',
  'Manage payment records', 'Payments', 10);
SELECT ensure_field('Payments', 10, 'payment_number', 10, false, true, false, true);
SELECT ensure_field('Payments', 10, 'payment_date',   20, true,  true, false, true);
SELECT ensure_field('Payments', 10, 'payment_type',   30, false, true, false, true);
SELECT ensure_field('Payments', 10, 'partner_id',     40, false, true, false, true);
SELECT ensure_field('Payments', 10, 'payment_method', 50, true,  true, false, false);
SELECT ensure_field('Payments', 10, 'currency',       60, true,  true, false, false);
SELECT ensure_field('Payments', 10, 'amount',         70, false, true, false, true);
SELECT ensure_field('Payments', 10, 'reference',      80, false, true, false, false);
SELECT ensure_field('Payments', 10, 'status',         90, false, true, false, false);
SELECT ensure_field('Payments', 10, 'notes',          100, false, true, false, false);

-- 2f. Shipments
SELECT create_window('Shipments', 'tx_shipment',
  'Manage shipments and receipts', 'Shipments', 10);
SELECT ensure_field('Shipments', 10, 'shipment_number', 10, false, true, false, true);
SELECT ensure_field('Shipments', 10, 'shipment_date',   20, true,  true, false, true);
SELECT ensure_field('Shipments', 10, 'shipment_type',   30, false, true, false, true);
SELECT ensure_field('Shipments', 10, 'partner_id',      40, false, true, false, true);
SELECT ensure_field('Shipments', 10, 'warehouse_id',    50, false, true, false, false);
SELECT ensure_field('Shipments', 10, 'order_id',        60, false, true, false, false);
SELECT ensure_field('Shipments', 10, 'tracking_number', 70, false, true, false, false);
SELECT ensure_field('Shipments', 10, 'carrier',         80, true,  true, false, false);
SELECT ensure_field('Shipments', 10, 'status',          90, false, true, false, false);
SELECT ensure_field('Shipments', 10, 'notes',           100, false, true, false, false);

-- Shipments → Lines child tab
SELECT add_child_tab('Shipments', 'Lines', 'tx_shipment_line', 20, 'shipment_id');
SELECT ensure_field('Shipments', 20, 'line_number', 10, false, true, false, true);
SELECT ensure_field('Shipments', 20, 'product_id',  20, false, true, false, true);
SELECT ensure_field('Shipments', 20, 'description', 30, false, true, false, false);
SELECT ensure_field('Shipments', 20, 'quantity',    40, true,  true, false, false);
SELECT ensure_field('Shipments', 20, 'uom_id',      50, true,  true, false, false);

-- ============================================================
-- Cleanup helper functions
-- ============================================================
DROP FUNCTION IF EXISTS ensure_field(TEXT, INTEGER, TEXT, INTEGER, BOOLEAN, BOOLEAN, BOOLEAN, BOOLEAN);
DROP FUNCTION IF EXISTS create_window(TEXT, TEXT, TEXT, TEXT, INTEGER);
DROP FUNCTION IF EXISTS add_child_tab(TEXT, TEXT, TEXT, INTEGER, TEXT, TEXT);
