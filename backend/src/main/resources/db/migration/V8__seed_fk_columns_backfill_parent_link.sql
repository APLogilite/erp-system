-- ============================================================
-- V8 — Seed missing FK columns in sys_column + backfill
--      sys_tab.parent_link_column_id (BUG-013 follow-up)
--
-- Root cause: the V4/V5 seeds never registered the parent-FK
-- columns in sys_column metadata (they are not form fields, so
-- they were omitted). Without a sys_column row, the V7 backfill
-- and V5 add_child_tab() subqueries matched zero rows, leaving
-- sys_tab.parent_link_column_id NULL for every child tab except
-- Table Definitions → Columns. Result: childTabIds empty at
-- runtime and child tabs never render.
--
-- This migration:
--   Part 1 — registers the 6 missing FK columns in sys_column
--            (metadata only; form fields come from sys_window_field,
--            so these do not appear on any form).
--   Part 2 — backfills sys_tab.parent_link_column_id.
--
-- IDEMPOTENT: safe on fresh DBs (runs after V5/V7) and on
-- existing DBs that already applied V5–V7.
-- ============================================================

-- ============================================================
-- Part 1 — Seed missing FK columns in sys_column
-- ============================================================

-- tx_order_line.order_id → tx_order  (Sales Orders / Purchase Orders → Lines)
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'order_id', 'Order', 'many2one', true, null, 0, 'tx_order', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t
WHERE t.name = 'tx_order_line'
  AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'order_id');

-- tx_invoice_line.invoice_id → tx_invoice  (Sales Invoices / Purchase Invoices → Lines)
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'invoice_id', 'Invoice', 'many2one', true, null, 0, 'tx_invoice', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t
WHERE t.name = 'tx_invoice_line'
  AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'invoice_id');

-- tx_shipment_line.shipment_id → tx_shipment  (Shipments → Lines)
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'shipment_id', 'Shipment', 'many2one', true, null, 0, 'tx_shipment', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t
WHERE t.name = 'tx_shipment_line'
  AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'shipment_id');

-- sys_tab.window_id → sys_window  (Window Definitions → Tabs)
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'window_id', 'Window', 'many2one', true, null, 0, 'sys_window', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t
WHERE t.name = 'sys_tab'
  AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'window_id');

-- sys_window_access.window_id → sys_window  (Window Definitions → Access)
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'window_id', 'Window', 'many2one', true, null, 0, 'sys_window', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t
WHERE t.name = 'sys_window_access'
  AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'window_id');

-- sys_window_field.tab_id → sys_tab  (Window Definitions → Fields)
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'tab_id', 'Tab', 'many2one', true, null, 0, 'sys_tab', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t
WHERE t.name = 'sys_window_field'
  AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'tab_id');

-- ============================================================
-- Part 2 — Backfill sys_tab.parent_link_column_id
-- (same mappings as V7; now the sys_column rows exist)
-- ============================================================

-- Sales Orders → Lines
UPDATE sys_tab t
SET parent_link_column_id = c.id
FROM sys_window w, sys_table tab, sys_column c
WHERE t.parent_link_column_id IS NULL
  AND t.window_id = w.id
  AND w.name = 'Sales Orders'
  AND t.name = 'Lines'
  AND t.table_id = tab.id
  AND tab.name = 'tx_order_line'
  AND c.table_id = tab.id
  AND c.code = 'order_id';

-- Purchase Orders → Lines
UPDATE sys_tab t
SET parent_link_column_id = c.id
FROM sys_window w, sys_table tab, sys_column c
WHERE t.parent_link_column_id IS NULL
  AND t.window_id = w.id
  AND w.name = 'Purchase Orders'
  AND t.name = 'Lines'
  AND t.table_id = tab.id
  AND tab.name = 'tx_order_line'
  AND c.table_id = tab.id
  AND c.code = 'order_id';

-- Sales Invoices → Lines
UPDATE sys_tab t
SET parent_link_column_id = c.id
FROM sys_window w, sys_table tab, sys_column c
WHERE t.parent_link_column_id IS NULL
  AND t.window_id = w.id
  AND w.name = 'Sales Invoices'
  AND t.name = 'Lines'
  AND t.table_id = tab.id
  AND tab.name = 'tx_invoice_line'
  AND c.table_id = tab.id
  AND c.code = 'invoice_id';

-- Purchase Invoices → Lines
UPDATE sys_tab t
SET parent_link_column_id = c.id
FROM sys_window w, sys_table tab, sys_column c
WHERE t.parent_link_column_id IS NULL
  AND t.window_id = w.id
  AND w.name = 'Purchase Invoices'
  AND t.name = 'Lines'
  AND t.table_id = tab.id
  AND tab.name = 'tx_invoice_line'
  AND c.table_id = tab.id
  AND c.code = 'invoice_id';

-- Shipments → Lines
UPDATE sys_tab t
SET parent_link_column_id = c.id
FROM sys_window w, sys_table tab, sys_column c
WHERE t.parent_link_column_id IS NULL
  AND t.window_id = w.id
  AND w.name = 'Shipments'
  AND t.name = 'Lines'
  AND t.table_id = tab.id
  AND tab.name = 'tx_shipment_line'
  AND c.table_id = tab.id
  AND c.code = 'shipment_id';

-- Table Definitions → Columns (self-sufficiency; V7 already covers it)
UPDATE sys_tab t
SET parent_link_column_id = c.id
FROM sys_window w, sys_table tab, sys_column c
WHERE t.parent_link_column_id IS NULL
  AND t.window_id = w.id
  AND w.name = 'Table Definitions'
  AND t.name = 'Columns'
  AND t.table_id = tab.id
  AND tab.name = 'sys_column'
  AND c.table_id = tab.id
  AND c.code = 'table_id';

-- Window Definitions → Tabs
UPDATE sys_tab t
SET parent_link_column_id = c.id
FROM sys_window w, sys_table tab, sys_column c
WHERE t.parent_link_column_id IS NULL
  AND t.window_id = w.id
  AND w.name = 'Window Definitions'
  AND t.name = 'Tabs'
  AND t.table_id = tab.id
  AND tab.name = 'sys_tab'
  AND c.table_id = tab.id
  AND c.code = 'window_id';

-- Window Definitions → Access
UPDATE sys_tab t
SET parent_link_column_id = c.id
FROM sys_window w, sys_table tab, sys_column c
WHERE t.parent_link_column_id IS NULL
  AND t.window_id = w.id
  AND w.name = 'Window Definitions'
  AND t.name = 'Access'
  AND t.table_id = tab.id
  AND tab.name = 'sys_window_access'
  AND c.table_id = tab.id
  AND c.code = 'window_id';

-- Window Definitions → Fields (grandchild of Tabs)
UPDATE sys_tab t
SET parent_link_column_id = c.id
FROM sys_window w, sys_table tab, sys_column c
WHERE t.parent_link_column_id IS NULL
  AND t.window_id = w.id
  AND w.name = 'Window Definitions'
  AND t.name = 'Fields'
  AND t.table_id = tab.id
  AND tab.name = 'sys_window_field'
  AND c.table_id = tab.id
  AND c.code = 'tab_id';
