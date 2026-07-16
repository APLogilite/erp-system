-- ============================================================
-- V4 — Admin Windows: Metadata Tables Registration + Admin UI
-- ============================================================

-- ============================================================
-- Helper functions (reusable across migrations)
-- ============================================================

CREATE OR REPLACE FUNCTION create_window(
  p_name TEXT, p_table_name TEXT, p_description TEXT,
  p_tab_name TEXT, p_tab_seq INTEGER
) RETURNS UUID AS $$
DECLARE
  v_window_id UUID;
  v_table_id UUID;
  v_tenant_id CONSTANT UUID := '00000000-0000-0000-0000-000000000001';
BEGIN
  SELECT id INTO v_table_id FROM sys_table WHERE name = p_table_name;
  INSERT INTO sys_window (id, name, table_id, description, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), p_name, v_table_id, p_description, true, v_tenant_id, now(), now()
  WHERE NOT EXISTS (SELECT 1 FROM sys_window WHERE name = p_name)
  RETURNING id INTO v_window_id;
  IF v_window_id IS NOT NULL THEN
    INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_window_id, p_tab_name, v_table_id, p_tab_seq, false, true, v_tenant_id, now(), now();
  END IF;
  RETURN v_window_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION add_child_tab(
  p_window_name TEXT, p_tab_name TEXT, p_table_name TEXT,
  p_seq INTEGER, p_parent_column TEXT, p_where_clause TEXT DEFAULT NULL
) RETURNS UUID AS $$
DECLARE
  v_window_id UUID;
  v_table_id UUID;
  v_tab_id UUID;
  v_tenant_id CONSTANT UUID := '00000000-0000-0000-0000-000000000001';
BEGIN
  SELECT id INTO v_window_id FROM sys_window WHERE name = p_window_name;
  SELECT id INTO v_table_id FROM sys_table WHERE name = p_table_name;
  INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), v_window_id, p_tab_name, v_table_id, p_seq, false, p_where_clause, p_parent_column, true, v_tenant_id, now(), now()
  WHERE NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = v_window_id AND seq_no = p_seq)
  RETURNING id INTO v_tab_id;
  RETURN v_tab_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION ensure_field(
  p_window_name TEXT, p_tab_seq_no INTEGER, p_column_code TEXT,
  p_seq_no INTEGER, p_is_same_line BOOLEAN, p_is_displayed BOOLEAN,
  p_is_readonly BOOLEAN, p_is_mandatory BOOLEAN,
  p_filter_where TEXT DEFAULT NULL
) RETURNS void AS $$
DECLARE
  v_tab_id UUID;
  v_column_id UUID;
  v_column_label TEXT;
  v_table_name TEXT;
  v_tenant_id CONSTANT UUID := '00000000-0000-0000-0000-000000000001';
BEGIN
  SELECT st.id, st2.name INTO v_tab_id, v_table_name
  FROM sys_tab st
  JOIN sys_window sw ON st.window_id = sw.id
  JOIN sys_table st2 ON st.table_id = st2.id
  WHERE sw.name = p_window_name AND st.seq_no = p_tab_seq_no;
  SELECT c.id, c.label INTO v_column_id, v_column_label
  FROM sys_column c
  JOIN sys_table t ON c.table_id = t.id
  WHERE t.name = v_table_name AND c.code = p_column_code;
  IF v_tab_id IS NOT NULL AND v_column_id IS NOT NULL THEN
    INSERT INTO sys_window_field (id, tab_id, column_id, seq_no, is_same_line, num_lines, column_width, is_displayed, is_readonly, is_mandatory, label_override, filter_where_clause, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_tab_id, v_column_id, p_seq_no, p_is_same_line, 1, 12, p_is_displayed, p_is_readonly, p_is_mandatory, v_column_label, p_filter_where, true, v_tenant_id, now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_window_field WHERE tab_id = v_tab_id AND column_id = v_column_id);
  END IF;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- Part 1 — Register Metadata Tables (sys_*) in sys_table
-- ============================================================
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_table', 'Table', 'Tables', 'static', 'sys_table', 'Database table definitions', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'sys_table');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_column', 'Column', 'Columns', 'static', 'sys_column', 'Table column definitions', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'sys_column');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_window', 'Window', 'Windows', 'static', 'sys_window', 'Window definitions', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'sys_window');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_tab', 'Tab', 'Tabs', 'static', 'sys_tab', 'Window tab definitions', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'sys_tab');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_window_field', 'Window Field', 'Window Fields', 'static', 'sys_window_field', 'Window field definitions', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'sys_window_field');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_window_access', 'Window Access', 'Window Access', 'static', 'sys_window_access', 'Window access control entries', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'sys_window_access');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_menu', 'Menu', 'Menus', 'static', 'sys_menu', 'Menu tree entries', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'sys_menu');

-- ============================================================
-- Part 2 — Register Columns for Metadata Tables
-- ============================================================

-- Helper: register a column if not exists
CREATE OR REPLACE FUNCTION ensure_column(
  p_table_name TEXT, p_code TEXT, p_label TEXT,
  p_type TEXT, p_required BOOLEAN, p_max_length INTEGER, p_position INTEGER,
  p_relation_table TEXT DEFAULT NULL, p_is_display BOOLEAN DEFAULT false,
  p_filter_where TEXT DEFAULT NULL
) RETURNS void AS $$
BEGIN
  INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, filter_where_clause, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), (SELECT id FROM sys_table WHERE name = p_table_name), p_code, p_label, p_type, p_required, p_max_length, p_position, p_relation_table, p_is_display, p_filter_where, true, '00000000-0000-0000-0000-000000000001', now(), now()
  WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = p_table_name) AND code = p_code);
END;
$$ LANGUAGE plpgsql;

-- sys_table columns
SELECT ensure_column('sys_table', 'name', 'Name', 'string', true, 100, 1, NULL, true);
SELECT ensure_column('sys_table', 'label', 'Label', 'string', true, 100, 2);
SELECT ensure_column('sys_table', 'plural_label', 'Plural Label', 'string', false, 100, 3);
SELECT ensure_column('sys_table', 'table_type', 'Table Type', 'string', true, 20, 4);
SELECT ensure_column('sys_table', 'table_name', 'DB Table Name', 'string', true, 100, 5);
SELECT ensure_column('sys_table', 'description', 'Description', 'text', false, null, 6);
SELECT ensure_column('sys_table', 'is_active', 'Is Active', 'boolean', false, null, 7);

-- sys_column columns
SELECT ensure_column('sys_column', 'table_id', 'Table', 'many2one', true, null, 0, 'sys_table');
SELECT ensure_column('sys_column', 'code', 'DB Column Name', 'string', true, 100, 1);
SELECT ensure_column('sys_column', 'label', 'Label', 'string', true, 100, 2, NULL, true);
SELECT ensure_column('sys_column', 'type', 'Type', 'string', true, 50, 3);
SELECT ensure_column('sys_column', 'required', 'Required', 'boolean', false, null, 4);
SELECT ensure_column('sys_column', 'max_length', 'Max Length', 'integer', false, null, 5);
SELECT ensure_column('sys_column', 'relation_table', 'Relation Table', 'string', false, 100, 6);
SELECT ensure_column('sys_column', 'position', 'Position', 'integer', false, null, 7);
SELECT ensure_column('sys_column', 'is_active', 'Is Active', 'boolean', false, null, 8);

-- sys_window columns
SELECT ensure_column('sys_window', 'name', 'Name', 'string', true, 100, 1, NULL, true);
SELECT ensure_column('sys_window', 'description', 'Description', 'text', false, null, 2);
SELECT ensure_column('sys_window', 'is_active', 'Is Active', 'boolean', false, null, 3);

-- sys_tab columns
SELECT ensure_column('sys_tab', 'table_id', 'Table', 'many2one', true, null, 0, 'sys_table');
SELECT ensure_column('sys_tab', 'name', 'Name', 'string', true, 100, 1, NULL, true);
SELECT ensure_column('sys_tab', 'seq_no', 'Seq No', 'integer', true, null, 2);
SELECT ensure_column('sys_tab', 'is_single_row', 'Is Single Row', 'boolean', false, null, 3);
SELECT ensure_column('sys_tab', 'where_clause', 'Where Clause', 'text', false, null, 4);
SELECT ensure_column('sys_tab', 'parent_column', 'Parent Column', 'string', false, 100, 5);
SELECT ensure_column('sys_tab', 'is_active', 'Is Active', 'boolean', false, null, 6);

-- sys_window_field columns
SELECT ensure_column('sys_window_field', 'column_id', 'Column', 'many2one', true, null, 0, 'sys_column');
SELECT ensure_column('sys_window_field', 'label_override', 'Label', 'string', false, 200, 1, NULL, true);
SELECT ensure_column('sys_window_field', 'seq_no', 'Seq No', 'integer', true, null, 2);
-- label_override already registered above as display column
SELECT ensure_column('sys_window_field', 'is_same_line', 'Is Same Line', 'boolean', false, null, 4);
SELECT ensure_column('sys_window_field', 'num_lines', 'Num Lines', 'integer', false, null, 5);
SELECT ensure_column('sys_window_field', 'column_width', 'Column Width', 'integer', false, null, 6);
SELECT ensure_column('sys_window_field', 'is_displayed', 'Is Displayed', 'boolean', false, null, 7);
SELECT ensure_column('sys_window_field', 'is_readonly', 'Is Readonly', 'boolean', false, null, 8);
SELECT ensure_column('sys_window_field', 'is_mandatory', 'Is Mandatory', 'boolean', false, null, 9);
SELECT ensure_column('sys_window_field', 'display_logic', 'Display Logic', 'text', false, null, 10);
SELECT ensure_column('sys_window_field', 'readonly_logic', 'Readonly Logic', 'text', false, null, 11);
SELECT ensure_column('sys_window_field', 'default_value', 'Default Value', 'text', false, null, 12);
SELECT ensure_column('sys_window_field', 'is_active', 'Is Active', 'boolean', false, null, 13);
SELECT ensure_column('sys_window_field', 'filter_where_clause', 'Filter Where', 'text', false, null, 14);

-- sys_window_access columns
SELECT ensure_column('sys_window_access', 'is_active', 'Is Active', 'boolean', false, null, 1);

-- sys_menu columns
SELECT ensure_column('sys_menu', 'window_id', 'Window', 'many2one', false, null, 0, 'sys_window');
SELECT ensure_column('sys_menu', 'name', 'Name', 'string', true, 100, 1, NULL, true);
SELECT ensure_column('sys_menu', 'type', 'Type', 'string', true, 20, 2);
SELECT ensure_column('sys_menu', 'parent_id', 'Parent', 'many2one', false, null, 3, 'sys_menu');
SELECT ensure_column('sys_menu', 'seq_no', 'Seq No', 'integer', true, null, 4);
SELECT ensure_column('sys_menu', 'icon', 'Icon', 'string', false, 100, 5);
SELECT ensure_column('sys_menu', 'is_active', 'Is Active', 'boolean', false, null, 6);

DROP FUNCTION IF EXISTS ensure_column(TEXT, TEXT, TEXT, TEXT, BOOLEAN, INTEGER, INTEGER, TEXT, BOOLEAN, TEXT);

-- ============================================================
-- Part 3 — Admin Windows
-- ============================================================

-- Window 1: Table Definitions (sys_table) with Columns child tab
SELECT create_window('Table Definitions', 'sys_table', 'Manage table and column definitions', 'Tables', 10);
SELECT add_child_tab('Table Definitions', 'Columns', 'sys_column', 20, 'table_id');
SELECT ensure_field('Table Definitions', 10, 'name', 10, false, true, false, false);
SELECT ensure_field('Table Definitions', 10, 'label', 20, true, true, false, false);
SELECT ensure_field('Table Definitions', 10, 'plural_label', 30, true, true, false, false);
SELECT ensure_field('Table Definitions', 10, 'table_type', 40, false, true, true, false);
SELECT ensure_field('Table Definitions', 10, 'table_name', 50, true, true, false, false);
SELECT ensure_field('Table Definitions', 10, 'description', 60, false, true, false, false);
SELECT ensure_field('Table Definitions', 10, 'is_active', 70, false, true, false, false);
SELECT ensure_field('Table Definitions', 20, 'table_id', 5, false, true, false, true);
SELECT ensure_field('Table Definitions', 20, 'code', 10, false, true, false, true);
SELECT ensure_field('Table Definitions', 20, 'label', 20, true, true, false, true);
SELECT ensure_field('Table Definitions', 20, 'type', 30, false, true, false, true);
SELECT ensure_field('Table Definitions', 20, 'required', 40, true, true, false, false);
SELECT ensure_field('Table Definitions', 20, 'max_length', 50, true, true, false, false);
SELECT ensure_field('Table Definitions', 20, 'relation_table', 60, false, true, false, false);
SELECT ensure_field('Table Definitions', 20, 'position', 70, false, true, false, false);
SELECT ensure_field('Table Definitions', 20, 'is_active', 80, false, true, false, false);

-- Window 2: Window Definitions (sys_window) with Tabs → Fields → Access hierarchy
SELECT create_window('Window Definitions', 'sys_window', 'Manage windows, tabs, fields and access', 'Windows', 10);
SELECT add_child_tab('Window Definitions', 'Tabs', 'sys_tab', 20, 'window_id');
SELECT add_child_tab('Window Definitions', 'Access', 'sys_window_access', 30, 'window_id');
-- Fields is a grandchild of Tabs (child of Tabs tab)
SELECT add_child_tab('Window Definitions', 'Fields', 'sys_window_field', 15, 'tab_id');
SELECT ensure_field('Window Definitions', 10, 'name', 10, false, true, false, true);
SELECT ensure_field('Window Definitions', 10, 'description', 20, false, true, false, false);
SELECT ensure_field('Window Definitions', 10, 'is_active', 30, false, true, false, false);
SELECT ensure_field('Window Definitions', 20, 'table_id', 5, false, true, false, true);
SELECT ensure_field('Window Definitions', 20, 'name', 10, false, true, false, true);
SELECT ensure_field('Window Definitions', 20, 'seq_no', 20, false, true, false, true);
SELECT ensure_field('Window Definitions', 20, 'where_clause', 30, false, true, false, false);
SELECT ensure_field('Window Definitions', 20, 'parent_column', 40, false, true, false, false);
SELECT ensure_field('Window Definitions', 20, 'is_single_row', 50, true, true, false, false);
SELECT ensure_field('Window Definitions', 20, 'is_active', 60, false, true, false, false);
SELECT ensure_field('Window Definitions', 15, 'column_id', 5, false, true, false, true, 'table_id = @Tabs.table_id@');
SELECT ensure_field('Window Definitions', 15, 'label_override', 10, false, true, false, false);
SELECT ensure_field('Window Definitions', 15, 'seq_no', 20, false, true, false, true);
SELECT ensure_field('Window Definitions', 15, 'filter_where_clause', 25, false, true, false, false);
SELECT ensure_field('Window Definitions', 15, 'is_displayed', 30, true, true, false, false);
SELECT ensure_field('Window Definitions', 15, 'is_readonly', 40, true, true, false, false);
SELECT ensure_field('Window Definitions', 15, 'is_mandatory', 50, true, true, false, false);
SELECT ensure_field('Window Definitions', 15, 'is_active', 60, false, true, false, false);
SELECT ensure_field('Window Definitions', 30, 'is_active', 10, false, true, false, false);

-- Window 3: Menu Configuration
SELECT create_window('Menu Configuration', 'sys_menu', 'Manage menu tree entries', 'Menu', 10);
SELECT ensure_field('Menu Configuration', 10, 'window_id', 5, false, true, false, false);
SELECT ensure_field('Menu Configuration', 10, 'name', 10, false, true, false, true);
SELECT ensure_field('Menu Configuration', 10, 'type', 20, false, true, false, true);
SELECT ensure_field('Menu Configuration', 10, 'parent_id', 30, false, true, false, false);
SELECT ensure_field('Menu Configuration', 10, 'seq_no', 40, false, true, false, true);
SELECT ensure_field('Menu Configuration', 10, 'icon', 50, false, true, false, false);
SELECT ensure_field('Menu Configuration', 10, 'is_active', 60, false, true, false, false);
