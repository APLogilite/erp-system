-- ============================================================
-- PRD-004 / TASK-043 — Seed Admin Windows for Metadata Management
--
-- Creates admin Windows/Tabs/Fields for all 7 metadata tables
-- so administrators can manage configuration through the UI.
--
-- DEPENDS ON: V24 (schema), V25 (business tables registered)
-- ============================================================

-- ============================================================
-- Helper function to get a column ID by table name and column code
-- ============================================================
CREATE OR REPLACE FUNCTION col_id(tbl_name TEXT, col_code TEXT) RETURNS UUID AS $$
  SELECT c.id FROM sys_column c JOIN sys_table t ON c.table_id = t.id WHERE t.name = tbl_name AND c.code = col_code;
$$ LANGUAGE SQL IMMUTABLE;

-- ============================================================
-- Part 1 — Admin Window Definitions (sys_window)
-- ============================================================

-- 1a. Table Definitions
INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'admin_table_definitions', t.id, 'Manage table definitions', true, now(), now()
FROM sys_table t WHERE t.name = 'sys_table'
AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'admin_table_definitions');

-- 1b. Table Columns
INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'admin_table_columns', t.id, 'Manage table columns', true, now(), now()
FROM sys_table t WHERE t.name = 'sys_column'
AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'admin_table_columns');

-- 1c. Window Definitions
INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'admin_window_definitions', t.id, 'Manage window definitions', true, now(), now()
FROM sys_table t WHERE t.name = 'sys_window'
AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'admin_window_definitions');

-- 1d. Window Tabs
INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'admin_window_tabs', t.id, 'Manage window tabs', true, now(), now()
FROM sys_table t WHERE t.name = 'sys_tab'
AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'admin_window_tabs');

-- 1e. Window Fields
INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'admin_window_fields', t.id, 'Manage window fields', true, now(), now()
FROM sys_table t WHERE t.name = 'sys_window_field'
AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'admin_window_fields');

-- 1f. Window Access
INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'admin_window_access', t.id, 'Manage window access control', true, now(), now()
FROM sys_table t WHERE t.name = 'sys_window_access'
AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'admin_window_access');

-- 1g. Menu Configuration
INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'admin_menu_configuration', t.id, 'Manage menu tree', true, now(), now()
FROM sys_table t WHERE t.name = 'sys_menu'
AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'admin_menu_configuration');

-- ============================================================
-- Part 2 — Tab Definitions (sys_tab)
-- ============================================================

-- Table Definitions: Main tab (seq 10) + Columns child tab (seq 20)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Tables', t.id, 10, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_table_definitions' AND t.name = 'sys_table'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 10);

INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Columns', t.id, 20, false, NULL, 'table_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_table_definitions' AND t.name = 'sys_column'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 20);

-- Table Columns: Main tab only (seq 10)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Columns', t.id, 10, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_table_columns' AND t.name = 'sys_column'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 10);

-- Window Definitions: Main tab (seq 10) + Tabs child tab (seq 20) + Access child tab (seq 30)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Windows', t.id, 10, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_window_definitions' AND t.name = 'sys_window'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 10);

INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Tabs', t.id, 20, false, NULL, 'window_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_window_definitions' AND t.name = 'sys_tab'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 20);

INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Access', t.id, 30, false, NULL, 'window_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_window_definitions' AND t.name = 'sys_window_access'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 30);

-- Window Tabs: Main tab (seq 10) + Fields child tab (seq 20)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Tabs', t.id, 10, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_window_tabs' AND t.name = 'sys_tab'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 10);

INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Fields', t.id, 20, false, NULL, 'tab_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_window_tabs' AND t.name = 'sys_window_field'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 20);

-- Window Fields: Main tab only (seq 10)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Fields', t.id, 10, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_window_fields' AND t.name = 'sys_window_field'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 10);

-- Window Access: Main tab only (seq 10)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Access', t.id, 10, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_window_access' AND t.name = 'sys_window_access'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 10);

-- Menu Configuration: Main tab only (seq 10)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Menu', t.id, 10, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_menu_configuration' AND t.name = 'sys_menu'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 10);

-- ============================================================
-- Part 3 — Field Definitions (sys_window_field)
-- Each tab gets appropriate fields with proper seq_no, is_same_line, etc.
-- ============================================================

-- Helper: insert a field if not exists
CREATE OR REPLACE FUNCTION ensure_field(
  p_window_name TEXT, p_tab_seq_no INTEGER, p_column_code TEXT,
  p_seq_no INTEGER, p_is_same_line BOOLEAN, p_is_displayed BOOLEAN,
  p_is_readonly BOOLEAN, p_is_mandatory BOOLEAN, p_label_override TEXT DEFAULT NULL
) RETURNS void AS $$
DECLARE
  v_tab_id UUID;
  v_column_id UUID;
  v_table_name TEXT;
BEGIN
  -- Get tab id
  SELECT st.id, st2.name INTO v_tab_id, v_table_name
  FROM sys_tab st
  JOIN sys_window sw ON st.window_id = sw.id
  JOIN sys_table st2 ON st.table_id = st2.id
  WHERE sw.name = p_window_name AND st.seq_no = p_tab_seq_no;

  -- Get column id
  SELECT c.id INTO v_column_id
  FROM sys_column c
  JOIN sys_table t ON c.table_id = t.id
  WHERE t.name = v_table_name AND c.code = p_column_code;

  IF v_tab_id IS NOT NULL AND v_column_id IS NOT NULL THEN
    INSERT INTO sys_window_field (id, tab_id, column_id, seq_no, is_same_line, num_lines, column_width, is_displayed, is_readonly, is_mandatory, label_override, is_active, created_at, updated_at)
    SELECT gen_random_uuid(), v_tab_id, v_column_id, p_seq_no, p_is_same_line, 1, 12, p_is_displayed, p_is_readonly, p_is_mandatory, p_label_override, true, now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_window_field WHERE tab_id = v_tab_id AND column_id = v_column_id);
  END IF;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- 3a. Table Definitions → Main Tab (sys_table fields)
-- ============================================================
SELECT ensure_field('admin_table_definitions', 10, 'name',         10, false, true, false, true);
SELECT ensure_field('admin_table_definitions', 10, 'label',        20, false, true, false, true);
SELECT ensure_field('admin_table_definitions', 10, 'plural_label', 30, false, true, false, false);
SELECT ensure_field('admin_table_definitions', 10, 'table_type',   40, false, true, false, true);
SELECT ensure_field('admin_table_definitions', 10, 'table_name',   50, false, true, false, true);
SELECT ensure_field('admin_table_definitions', 10, 'description',  60, false, true, false, false);

-- ============================================================
-- 3b. Table Definitions → Columns Child Tab (sys_column fields)
-- ============================================================
SELECT ensure_field('admin_table_definitions', 20, 'code',          10, false, true, false, true);
SELECT ensure_field('admin_table_definitions', 20, 'label',         20, false, true, false, true);
SELECT ensure_field('admin_table_definitions', 20, 'type',          30, false, true, false, true);
SELECT ensure_field('admin_table_definitions', 20, 'required',      40, true,  true, false, false);
SELECT ensure_field('admin_table_definitions', 20, 'max_length',    50, false, true, false, false);
SELECT ensure_field('admin_table_definitions', 20, 'precision',     60, true,  true, false, false);
SELECT ensure_field('admin_table_definitions', 20, 'scale',         70, true,  true, false, false);
SELECT ensure_field('admin_table_definitions', 20, 'relation_table',80, false, true, false, false);
SELECT ensure_field('admin_table_definitions', 20, 'enum_options',  90, false, true, false, false);
SELECT ensure_field('admin_table_definitions', 20, 'position',      100, false, true, false, false);

-- ============================================================
-- 3c. Table Columns → Main Tab (sys_column fields)
-- ============================================================
SELECT ensure_field('admin_table_columns', 10, 'code',          10, false, true, false, true);
SELECT ensure_field('admin_table_columns', 10, 'label',         20, false, true, false, true);
SELECT ensure_field('admin_table_columns', 10, 'type',          30, false, true, false, true);
SELECT ensure_field('admin_table_columns', 10, 'required',      40, true,  true, false, false);
SELECT ensure_field('admin_table_columns', 10, 'max_length',    50, false, true, false, false);
SELECT ensure_field('admin_table_columns', 10, 'precision',     60, true,  true, false, false);
SELECT ensure_field('admin_table_columns', 10, 'scale',         70, true,  true, false, false);
SELECT ensure_field('admin_table_columns', 10, 'relation_table',80, false, true, false, false);
SELECT ensure_field('admin_table_columns', 10, 'enum_options',  90, false, true, false, false);
SELECT ensure_field('admin_table_columns', 10, 'position',      100, false, true, false, false);

-- ============================================================
-- 3d. Window Definitions → Main Tab (sys_window fields)
-- ============================================================
SELECT ensure_field('admin_window_definitions', 10, 'name',        10, false, true, false, true);
SELECT ensure_field('admin_window_definitions', 10, 'description', 20, false, true, false, false);

-- ============================================================
-- 3e. Window Definitions → Tabs Child Tab (sys_tab fields)
-- ============================================================
SELECT ensure_field('admin_window_definitions', 20, 'name',          10, false, true, false, true);
SELECT ensure_field('admin_window_definitions', 20, 'seq_no',        20, true,  true, false, true);
SELECT ensure_field('admin_window_definitions', 20, 'is_single_row', 30, true,  true, false, false);
SELECT ensure_field('admin_window_definitions', 20, 'where_clause',  40, false, true, false, false);
SELECT ensure_field('admin_window_definitions', 20, 'parent_column', 50, false, true, false, false);

-- ============================================================
-- 3f. Window Definitions → Access Child Tab (sys_window_access fields)
-- ============================================================
SELECT ensure_field('admin_window_definitions', 30, 'role_id',   10, false, true, false, true);
SELECT ensure_field('admin_window_definitions', 30, 'tenant_id', 20, false, true, false, false);

-- ============================================================
-- 3g. Window Tabs → Main Tab (sys_tab fields)
-- ============================================================
SELECT ensure_field('admin_window_tabs', 10, 'name',          10, false, true, false, true);
SELECT ensure_field('admin_window_tabs', 10, 'seq_no',        20, true,  true, false, true);
SELECT ensure_field('admin_window_tabs', 10, 'is_single_row', 30, true,  true, false, false);
SELECT ensure_field('admin_window_tabs', 10, 'where_clause',  40, false, true, false, false);
SELECT ensure_field('admin_window_tabs', 10, 'parent_column', 50, false, true, false, false);

-- ============================================================
-- 3h. Window Tabs → Fields Child Tab (sys_window_field fields)
-- ============================================================
SELECT ensure_field('admin_window_tabs', 20, 'seq_no',        10, false, true, false, true);
SELECT ensure_field('admin_window_tabs', 20, 'is_same_line',  20, true,  true, false, false);
SELECT ensure_field('admin_window_tabs', 20, 'num_lines',     30, true,  true, false, false);
SELECT ensure_field('admin_window_tabs', 20, 'column_width',  40, true,  true, false, false);
SELECT ensure_field('admin_window_tabs', 20, 'is_displayed',  50, true,  true, false, false);
SELECT ensure_field('admin_window_tabs', 20, 'is_readonly',   60, true,  true, false, false);
SELECT ensure_field('admin_window_tabs', 20, 'is_mandatory',  70, true,  true, false, false);
SELECT ensure_field('admin_window_tabs', 20, 'display_logic', 80, false, true, false, false);
SELECT ensure_field('admin_window_tabs', 20, 'readonly_logic',90, false, true, false, false);
SELECT ensure_field('admin_window_tabs', 20, 'label_override',100, false, true, false, false);

-- ============================================================
-- 3i. Window Fields → Main Tab (sys_window_field fields)
-- ============================================================
SELECT ensure_field('admin_window_fields', 10, 'seq_no',        10, false, true, false, true);
SELECT ensure_field('admin_window_fields', 10, 'is_same_line',  20, true,  true, false, false);
SELECT ensure_field('admin_window_fields', 10, 'num_lines',     30, true,  true, false, false);
SELECT ensure_field('admin_window_fields', 10, 'column_width',  40, true,  true, false, false);
SELECT ensure_field('admin_window_fields', 10, 'is_displayed',  50, true,  true, false, false);
SELECT ensure_field('admin_window_fields', 10, 'is_readonly',   60, true,  true, false, false);
SELECT ensure_field('admin_window_fields', 10, 'is_mandatory',  70, true,  true, false, false);
SELECT ensure_field('admin_window_fields', 10, 'display_logic', 80, false, true, false, false);
SELECT ensure_field('admin_window_fields', 10, 'readonly_logic',90, false, true, false, false);
SELECT ensure_field('admin_window_fields', 10, 'label_override',100, false, true, false, false);

-- ============================================================
-- 3j. Window Access → Main Tab (sys_window_access fields)
-- ============================================================
SELECT ensure_field('admin_window_access', 10, 'role_id',   10, false, true, false, true);
SELECT ensure_field('admin_window_access', 10, 'tenant_id', 20, false, true, false, false);

-- ============================================================
-- 3k. Menu Configuration → Main Tab (sys_menu fields)
-- ============================================================
SELECT ensure_field('admin_menu_configuration', 10, 'name',      10, false, true, false, true);
SELECT ensure_field('admin_menu_configuration', 10, 'type',      20, false, true, false, true);
SELECT ensure_field('admin_menu_configuration', 10, 'seq_no',    30, true,  true, false, true);
SELECT ensure_field('admin_menu_configuration', 10, 'icon',      40, false, true, false, false);

-- Clean up helper functions
DROP FUNCTION IF EXISTS ensure_field(TEXT, INTEGER, TEXT, INTEGER, BOOLEAN, BOOLEAN, BOOLEAN, BOOLEAN, TEXT);
DROP FUNCTION IF EXISTS col_id(TEXT, TEXT);
