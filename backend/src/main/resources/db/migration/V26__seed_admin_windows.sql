-- ============================================================
-- PRD-004 / TASK-043 — Seed Admin Windows for Metadata Management
--
-- Creates a single "admin" window with 6 hierarchy tabs for
-- managing tables, columns, windows, tabs, fields, access and menu.
-- All admin configuration is consolidated into ONE window per
-- the design decision (single menu item, single window with tabs).
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
-- Part 1 — Single Admin Window (sys_window)
-- ============================================================

INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'admin', t.id, 'System Administration — manage tables, windows, fields, access, and menu', true, now(), now()
FROM sys_table t WHERE t.name = 'sys_table'
AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'admin');

-- ============================================================
-- Part 2 — Tab Definitions (sys_tab) — one tab per admin table
-- ============================================================

-- Tab 1: Table Definitions (sys_table) — parent for Columns
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Tables', t.id, 10, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin' AND t.name = 'sys_table'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 10);

-- Tab 2: Columns (sys_column) — child of Tables via table_id
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Columns', t.id, 20, false, NULL, 'table_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin' AND t.name = 'sys_column'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 20);

-- Tab 3: Windows (sys_window) — parent for Tabs, Fields
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Windows', t.id, 30, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin' AND t.name = 'sys_window'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 30);

-- Tab 4: Tabs (sys_tab) — child of Windows via window_id
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Tabs', t.id, 40, false, NULL, 'window_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin' AND t.name = 'sys_tab'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 40);

-- Tab 5: Fields (sys_window_field) — child of Tabs via tab_id
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Fields', t.id, 50, false, NULL, 'tab_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin' AND t.name = 'sys_window_field'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 50);

-- Tab 6: Access (sys_window_access)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Access', t.id, 60, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin' AND t.name = 'sys_window_access'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 60);

-- Tab 7: Menu (sys_menu)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Menu', t.id, 70, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin' AND t.name = 'sys_menu'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 70);

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

-- ============================================================
-- 3a. Tables Tab (seq 10) — sys_table fields
-- ============================================================
SELECT ensure_field('admin', 10, 'name',         10, false, true, false, true);
SELECT ensure_field('admin', 10, 'label',        20, false, true, false, true);
SELECT ensure_field('admin', 10, 'plural_label', 30, false, true, false, false);
SELECT ensure_field('admin', 10, 'table_type',   40, false, true, false, true);
SELECT ensure_field('admin', 10, 'table_name',   50, false, true, false, true);
SELECT ensure_field('admin', 10, 'description',  60, false, true, false, false);

-- ============================================================
-- 3b. Columns Tab (seq 20) — sys_column fields
-- ============================================================
SELECT ensure_field('admin', 20, 'code',          10, false, true, false, true);
SELECT ensure_field('admin', 20, 'label',         20, false, true, false, true);
SELECT ensure_field('admin', 20, 'type',          30, false, true, false, true);
SELECT ensure_field('admin', 20, 'required',      40, true,  true, false, false);
SELECT ensure_field('admin', 20, 'max_length',    50, false, true, false, false);
SELECT ensure_field('admin', 20, 'precision',     60, true,  true, false, false);
SELECT ensure_field('admin', 20, 'scale',         70, true,  true, false, false);
SELECT ensure_field('admin', 20, 'relation_table',80, false, true, false, false);
SELECT ensure_field('admin', 20, 'enum_options',  90, false, true, false, false);
SELECT ensure_field('admin', 20, 'position',      100, false, true, false, false);

-- ============================================================
-- 3c. Windows Tab (seq 30) — sys_window fields
-- ============================================================
SELECT ensure_field('admin', 30, 'name',        10, false, true, false, true);
SELECT ensure_field('admin', 30, 'description', 20, false, true, false, false);

-- ============================================================
-- 3d. Tabs Tab (seq 40) — sys_tab fields
-- ============================================================
SELECT ensure_field('admin', 40, 'name',          10, false, true, false, true);
SELECT ensure_field('admin', 40, 'seq_no',        20, true,  true, false, true);
SELECT ensure_field('admin', 40, 'is_single_row', 30, true,  true, false, false);
SELECT ensure_field('admin', 40, 'where_clause',  40, false, true, false, false);
SELECT ensure_field('admin', 40, 'parent_column', 50, false, true, false, false);

-- ============================================================
-- 3e. Fields Tab (seq 50) — sys_window_field fields
-- ============================================================
SELECT ensure_field('admin', 50, 'seq_no',        10, false, true, false, true);
SELECT ensure_field('admin', 50, 'is_same_line',  20, true,  true, false, false);
SELECT ensure_field('admin', 50, 'num_lines',     30, true,  true, false, false);
SELECT ensure_field('admin', 50, 'column_width',  40, true,  true, false, false);
SELECT ensure_field('admin', 50, 'is_displayed',  50, true,  true, false, false);
SELECT ensure_field('admin', 50, 'is_readonly',   60, true,  true, false, false);
SELECT ensure_field('admin', 50, 'is_mandatory',  70, true,  true, false, false);
SELECT ensure_field('admin', 50, 'display_logic', 80, false, true, false, false);
SELECT ensure_field('admin', 50, 'readonly_logic',90, false, true, false, false);
SELECT ensure_field('admin', 50, 'label_override',100, false, true, false, false);

-- ============================================================
-- 3f. Access Tab (seq 60) — sys_window_access fields
-- ============================================================
SELECT ensure_field('admin', 60, 'role_id',   10, false, true, false, true);
SELECT ensure_field('admin', 60, 'tenant_id', 20, false, true, false, false);

-- ============================================================
-- 3g. Menu Tab (seq 70) — sys_menu fields
-- ============================================================
SELECT ensure_field('admin', 70, 'name',      10, false, true, false, true);
SELECT ensure_field('admin', 70, 'type',      20, false, true, false, true);
SELECT ensure_field('admin', 70, 'seq_no',    30, true,  true, false, true);
SELECT ensure_field('admin', 70, 'icon',      40, false, true, false, false);

-- Clean up helper functions
DROP FUNCTION IF EXISTS ensure_field(TEXT, INTEGER, TEXT, INTEGER, BOOLEAN, BOOLEAN, BOOLEAN, BOOLEAN, TEXT);
DROP FUNCTION IF EXISTS col_id(TEXT, TEXT);
