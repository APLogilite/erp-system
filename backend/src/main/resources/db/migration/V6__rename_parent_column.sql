-- ============================================================
-- V6 — Rename sys_tab.parent_column → parent_link_column_id
-- Change type VARCHAR → UUID FK to sys_column.id
-- ============================================================

-- ============================================================
-- Part 1 — Add the new UUID column
-- ============================================================
ALTER TABLE sys_tab ADD COLUMN parent_link_column_id UUID;
ALTER TABLE sys_tab ADD CONSTRAINT fk_sys_tab_parent_link_column
    FOREIGN KEY (parent_link_column_id) REFERENCES sys_column(id);

-- ============================================================
-- Part 2 — Migrate existing data
-- Map the old parent_column varchar values to sys_column UUIDs
-- ============================================================
-- Sales Orders → Lines: parent_column = 'order_id' on tx_order_line table
UPDATE sys_tab t
SET parent_link_column_id = c.id
FROM sys_window w, sys_table tab, sys_column c
WHERE t.window_id = w.id
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
WHERE t.window_id = w.id
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
WHERE t.window_id = w.id
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
WHERE t.window_id = w.id
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
WHERE t.window_id = w.id
  AND w.name = 'Shipments'
  AND t.name = 'Lines'
  AND t.table_id = tab.id
  AND tab.name = 'tx_shipment_line'
  AND c.table_id = tab.id
  AND c.code = 'shipment_id';

-- Admin windows: Table Definitions → Columns
UPDATE sys_tab t
SET parent_link_column_id = c.id
FROM sys_window w, sys_table tab, sys_column c
WHERE t.window_id = w.id
  AND w.name = 'Table Definitions'
  AND t.name = 'Columns'
  AND t.table_id = tab.id
  AND tab.name = 'sys_column'
  AND c.table_id = tab.id
  AND c.code = 'table_id';

-- Admin windows: Window Definitions → Tabs
UPDATE sys_tab t
SET parent_link_column_id = c.id
FROM sys_window w, sys_table tab, sys_column c
WHERE t.window_id = w.id
  AND w.name = 'Window Definitions'
  AND t.name = 'Tabs'
  AND t.table_id = tab.id
  AND tab.name = 'sys_tab'
  AND c.table_id = tab.id
  AND c.code = 'window_id';

-- Window Definitions → Tabs → Fields (grandchild)
UPDATE sys_tab t
SET parent_link_column_id = c.id
FROM sys_window w, sys_table tab, sys_column c
WHERE t.window_id = w.id
  AND w.name = 'Window Definitions'
  AND t.name = 'Fields'
  AND t.table_id = tab.id
  AND tab.name = 'sys_window_field'
  AND c.table_id = tab.id
  AND c.code = 'tab_id';

-- ============================================================
-- Part 3 — Drop the old parent_column column
-- ============================================================
ALTER TABLE sys_tab DROP COLUMN parent_column;

-- ============================================================
-- Part 4 — Update helper function add_child_tab to accept UUID
-- ============================================================
CREATE OR REPLACE FUNCTION add_child_tab(
  p_window_name TEXT, p_tab_name TEXT, p_table_name TEXT,
  p_seq INTEGER, p_parent_link_column_id UUID, p_where_clause TEXT DEFAULT NULL
) RETURNS UUID AS $$
DECLARE
  v_window_id UUID;
  v_table_id UUID;
  v_tab_id UUID;
  v_tenant_id CONSTANT UUID := '00000000-0000-0000-0000-000000000001';
BEGIN
  SELECT id INTO v_window_id FROM sys_window WHERE name = p_window_name;
  SELECT id INTO v_table_id FROM sys_table WHERE name = p_table_name;
  INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_link_column_id, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), v_window_id, p_tab_name, v_table_id, p_seq, false, p_where_clause, p_parent_link_column_id, true, v_tenant_id, now(), now()
  WHERE NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = v_window_id AND seq_no = p_seq)
  RETURNING id INTO v_tab_id;
  RETURN v_tab_id;
END;
$$ LANGUAGE plpgsql;
