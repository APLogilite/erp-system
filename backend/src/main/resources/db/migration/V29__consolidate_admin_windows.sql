-- ============================================================
-- PRD-004 / BUG-009 — Restructure Admin Windows into 3 Groups
--
-- For existing databases that ran the original V26 (7 separate
-- admin windows) or the earlier intermediate V26 (1 consolidated
-- 'admin' window), this migration drops old windows and creates
-- 3 properly grouped admin windows:
--   1. admin_table_column      — Tables + Columns
--   2. admin_window_tab_field  — Windows + Tabs + Fields + Access
--   3. admin_menu_config       — Menu
-- ============================================================

-- ============================================================
-- Part 1 — Drop old admin windows (any version)
-- ============================================================

DROP TABLE IF EXISTS _old_admin_wins CASCADE;

-- Collect ALL old/consolidated admin window IDs
CREATE TEMP TABLE _old_admin_wins AS
SELECT id FROM sys_window WHERE name IN (
    'admin_table_definitions', 'admin_table_columns',
    'admin_window_definitions', 'admin_window_tabs',
    'admin_window_fields', 'admin_window_access',
    'admin_menu_configuration',
    'admin'   -- the earlier consolidated single-window version
);

-- Remove related data
DELETE FROM sys_window_access WHERE window_id IN (SELECT id FROM _old_admin_wins);
DELETE FROM sys_menu WHERE window_id IN (SELECT id FROM _old_admin_wins);
DELETE FROM sys_window WHERE id IN (SELECT id FROM _old_admin_wins);

DROP TABLE IF EXISTS _old_admin_wins;

-- ============================================================
-- Part 2 — Create 3 Admin Windows
-- ============================================================

-- Window 1: Table & Column
INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'admin_table_column', t.id, 'Manage table and column definitions', true, now(), now()
FROM sys_table t WHERE t.name = 'sys_table'
AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'admin_table_column');

-- Window 2: Window, Tab, Field & Access
INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'admin_window_tab_field', t.id, 'Manage windows, tabs, fields and access control', true, now(), now()
FROM sys_table t WHERE t.name = 'sys_window'
AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'admin_window_tab_field');

-- Window 3: Menu
INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'admin_menu_config', t.id, 'Manage menu tree configuration', true, now(), now()
FROM sys_table t WHERE t.name = 'sys_menu'
AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'admin_menu_config');

-- ============================================================
-- Part 3 — Create Tabs
-- ============================================================

-- Window 1: Tables + Columns
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Tables', t.id, 10, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_table_column' AND t.name = 'sys_table'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 10);

INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Columns', t.id, 20, false, NULL, 'table_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_table_column' AND t.name = 'sys_column'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 20);

-- Window 2: Windows + Tabs + Fields + Access
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Windows', t.id, 10, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_window_tab_field' AND t.name = 'sys_window'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 10);

INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Tabs', t.id, 20, false, NULL, 'window_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_window_tab_field' AND t.name = 'sys_tab'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 20);

INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Fields', t.id, 30, false, NULL, 'tab_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_window_tab_field' AND t.name = 'sys_window_field'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 30);

INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Access', t.id, 40, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_window_tab_field' AND t.name = 'sys_window_access'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 40);

-- Window 3: Menu
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Menu', t.id, 10, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin_menu_config' AND t.name = 'sys_menu'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 10);

-- ============================================================
-- Part 4 — Add 3 Admin Menu Items
-- ============================================================

DO $$
DECLARE
    v_parent_id UUID;
BEGIN
    SELECT id INTO v_parent_id FROM sys_menu WHERE name = 'Administration' AND type = 'group';

    IF v_parent_id IS NOT NULL THEN
        INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, created_at, updated_at)
        SELECT gen_random_uuid(), 'Table & Columns', 'window', v_parent_id, w.id, 10, true, now(), now()
        FROM sys_window w WHERE w.name = 'admin_table_column'
        AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = v_parent_id AND name = 'Table & Columns');

        INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, created_at, updated_at)
        SELECT gen_random_uuid(), 'Window, Tab & Field', 'window', v_parent_id, w.id, 20, true, now(), now()
        FROM sys_window w WHERE w.name = 'admin_window_tab_field'
        AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = v_parent_id AND name = 'Window, Tab & Field');

        INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, created_at, updated_at)
        SELECT gen_random_uuid(), 'Menu Configuration', 'window', v_parent_id, w.id, 30, true, now(), now()
        FROM sys_window w WHERE w.name = 'admin_menu_config'
        AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = v_parent_id AND name = 'Menu Configuration');
    END IF;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- Part 5 — Grant window access for sys_admin role
-- ============================================================

DO $$
DECLARE
    v_role_id UUID;
    v_wins UUID[];
    v_win_id UUID;
BEGIN
    SELECT id INTO v_role_id FROM identity_roles WHERE code = 'sys_admin';

    IF v_role_id IS NOT NULL THEN
        v_wins := ARRAY(SELECT id FROM sys_window WHERE name IN ('admin_table_column', 'admin_window_tab_field', 'admin_menu_config'));

        FOREACH v_win_id IN ARRAY v_wins LOOP
            INSERT INTO sys_window_access (id, window_id, tenant_id, role_id, is_active, created_at, updated_at)
            SELECT gen_random_uuid(), v_win_id, NULL, v_role_id, true, now(), now()
            WHERE NOT EXISTS (
                SELECT 1 FROM sys_window_access WHERE window_id = v_win_id AND role_id = v_role_id AND tenant_id IS NULL
            );
        END LOOP;
    END IF;
END;
$$ LANGUAGE plpgsql;
