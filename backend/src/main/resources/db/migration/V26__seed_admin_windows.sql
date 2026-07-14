-- ============================================================
-- PRD-004 / TASK-043 — Seed Admin Windows for Metadata Management
--
-- Creates 3 admin windows for system configuration:
--   1. sys_table — Tables + child Columns
--   2. sys_window — Windows + child Tabs + child Fields + Access
--   3. sys_menu — Menu configuration
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
-- Part 1 — Admin Window Definitions (3 windows)
-- ============================================================

-- Window 1: Table & Column
INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_table', t.id, 'Manage table and column definitions', true, now(), now()
FROM sys_table t WHERE t.name = 'sys_table'
AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'sys_table');

-- Window 2: Window, Tab, Field & Access
INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_window', t.id, 'Manage windows, tabs, fields and access control', true, now(), now()
FROM sys_table t WHERE t.name = 'sys_window'
AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'sys_window');

-- Window 3: Menu
INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_menu', t.id, 'Manage menu tree configuration', true, now(), now()
FROM sys_table t WHERE t.name = 'sys_menu'
AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'sys_menu');

-- ============================================================
-- Part 2 — Tab Definitions (sys_tab)
-- ============================================================

-- Window 1: Tables (parent, seq 10) + Columns (child, seq 20 via table_id)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Tables', t.id, 10, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'sys_table' AND t.name = 'sys_table'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 10);

INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Columns', t.id, 20, false, NULL, 'table_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'sys_table' AND t.name = 'sys_column'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 20);

-- Window 2: Windows (parent, seq 10) + Tabs (child, seq 20 via window_id) + Fields (child, seq 30 via tab_id) + Access (seq 40)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Windows', t.id, 10, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'sys_window' AND t.name = 'sys_window'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 10);

INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Tabs', t.id, 20, false, NULL, 'window_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'sys_window' AND t.name = 'sys_tab'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 20);

INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Fields', t.id, 30, false, NULL, 'tab_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'sys_window' AND t.name = 'sys_window_field'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 30);

INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Access', t.id, 40, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'sys_window' AND t.name = 'sys_window_access'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 40);

-- Window 3: Menu (seq 10)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Menu', t.id, 10, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'sys_menu' AND t.name = 'sys_menu'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 10);

-- ============================================================
-- Part 3 — Field Definitions (sys_window_field)
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
    INSERT INTO sys_window_field (id, tab_id, column_id, seq_no, is_same_line, num_lines, column_width, is_displayed, is_readonly, is_mandatory, label_override, is_active, created_at, updated_at)
    SELECT gen_random_uuid(), v_tab_id, v_column_id, p_seq_no, p_is_same_line, 1, 12, p_is_displayed, p_is_readonly, p_is_mandatory, p_label_override, true, now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_window_field WHERE tab_id = v_tab_id AND column_id = v_column_id);
  END IF;
END;
$$ LANGUAGE plpgsql;

-- Window 1: Table & Column
-- Tables tab (seq 10) — sys_table fields
SELECT ensure_field('sys_table', 10, 'name',         10, false, true, false, true);
SELECT ensure_field('sys_table', 10, 'label',        20, false, true, false, true);
SELECT ensure_field('sys_table', 10, 'plural_label', 30, false, true, false, false);
SELECT ensure_field('sys_table', 10, 'table_type',   40, false, true, false, true);
SELECT ensure_field('sys_table', 10, 'table_name',   50, false, true, false, true);
SELECT ensure_field('sys_table', 10, 'description',  60, false, true, false, false);

-- Columns tab (seq 20) — sys_column fields
SELECT ensure_field('sys_table', 20, 'code',          10, false, true, false, true);
SELECT ensure_field('sys_table', 20, 'label',         20, false, true, false, true);
SELECT ensure_field('sys_table', 20, 'type',          30, false, true, false, true);
SELECT ensure_field('sys_table', 20, 'required',      40, true,  true, false, false);
SELECT ensure_field('sys_table', 20, 'max_length',    50, false, true, false, false);
SELECT ensure_field('sys_table', 20, 'precision',     60, true,  true, false, false);
SELECT ensure_field('sys_table', 20, 'scale',         70, true,  true, false, false);
SELECT ensure_field('sys_table', 20, 'relation_table',80, false, true, false, false);
SELECT ensure_field('sys_table', 20, 'enum_options',  90, false, true, false, false);
SELECT ensure_field('sys_table', 20, 'position',      100, false, true, false, false);

-- Window 2: Window, Tab, Field & Access
-- Windows tab (seq 10) — sys_window fields
SELECT ensure_field('sys_window', 10, 'name',        10, false, true, false, true);
SELECT ensure_field('sys_window', 10, 'description', 20, false, true, false, false);

-- Tabs tab (seq 20) — sys_tab fields
SELECT ensure_field('sys_window', 20, 'name',          10, false, true, false, true);
SELECT ensure_field('sys_window', 20, 'seq_no',        20, true,  true, false, true);
SELECT ensure_field('sys_window', 20, 'is_single_row', 30, true,  true, false, false);
SELECT ensure_field('sys_window', 20, 'where_clause',  40, false, true, false, false);
SELECT ensure_field('sys_window', 20, 'parent_column', 50, false, true, false, false);

-- Fields tab (seq 30) — sys_window_field fields
SELECT ensure_field('sys_window', 30, 'seq_no',        10, false, true, false, true);
SELECT ensure_field('sys_window', 30, 'is_same_line',  20, true,  true, false, false);
SELECT ensure_field('sys_window', 30, 'num_lines',     30, true,  true, false, false);
SELECT ensure_field('sys_window', 30, 'column_width',  40, true,  true, false, false);
SELECT ensure_field('sys_window', 30, 'is_displayed',  50, true,  true, false, false);
SELECT ensure_field('sys_window', 30, 'is_readonly',   60, true,  true, false, false);
SELECT ensure_field('sys_window', 30, 'is_mandatory',  70, true,  true, false, false);
SELECT ensure_field('sys_window', 30, 'display_logic', 80, false, true, false, false);
SELECT ensure_field('sys_window', 30, 'readonly_logic',90, false, true, false, false);
SELECT ensure_field('sys_window', 30, 'label_override',100, false, true, false, false);

-- Access tab (seq 40) — sys_window_access fields
SELECT ensure_field('sys_window', 40, 'role_id',   10, false, true, false, true);
SELECT ensure_field('sys_window', 40, 'tenant_id', 20, false, true, false, false);

-- Window 3: Menu
-- Menu tab (seq 10) — sys_menu fields
SELECT ensure_field('sys_menu', 10, 'name',      10, false, true, false, true);
SELECT ensure_field('sys_menu', 10, 'type',      20, false, true, false, true);
SELECT ensure_field('sys_menu', 10, 'seq_no',    30, true,  true, false, true);
SELECT ensure_field('sys_menu', 10, 'icon',      40, false, true, false, false);

-- Clean up helper functions
DROP FUNCTION IF EXISTS ensure_field(TEXT, INTEGER, TEXT, INTEGER, BOOLEAN, BOOLEAN, BOOLEAN, BOOLEAN, TEXT);
DROP FUNCTION IF EXISTS col_id(TEXT, TEXT);
