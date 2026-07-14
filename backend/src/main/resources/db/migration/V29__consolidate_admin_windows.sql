-- ============================================================
-- PRD-004 / BUG-009 — Consolidate Admin Windows into Single Window
--
-- Replaces 7 separate admin windows (admin_table_definitions,
-- admin_table_columns, admin_window_definitions, admin_window_tabs,
-- admin_window_fields, admin_window_access, admin_menu_configuration)
-- with a single "admin" window containing 6 tabs.
--
-- This fix addresses the user feedback that the sidebar menu showed
-- too many individual admin items instead of a single Administration window.
-- ============================================================

-- ============================================================
-- Part 1 — Drop old admin windows (CASCADE removes tabs + fields + access)
-- ============================================================

DROP TABLE IF EXISTS _admin_win_ids CASCADE;

-- Collect old admin window IDs into a temp table
CREATE TEMP TABLE _admin_win_ids AS
SELECT id FROM sys_window WHERE name IN (
    'admin_table_definitions',
    'admin_table_columns',
    'admin_window_definitions',
    'admin_window_tabs',
    'admin_window_fields',
    'admin_window_access',
    'admin_menu_configuration'
);

-- Delete window access entries for old windows
DELETE FROM sys_window_access WHERE window_id IN (SELECT id FROM _admin_win_ids);

-- Delete menu entries referencing old windows
DELETE FROM sys_menu WHERE window_id IN (SELECT id FROM _admin_win_ids);

-- Delete old windows (CASCADE drops tabs + fields)
DELETE FROM sys_window WHERE id IN (SELECT id FROM _admin_win_ids);

DROP TABLE IF EXISTS _admin_win_ids;

-- ============================================================
-- Part 2 — Create single "admin" window
-- ============================================================

-- Create the admin window (using sys_table as the main table reference)
INSERT INTO sys_window (id, name, table_id, description, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'admin', t.id, 'System Administration — manage tables, windows, fields, access, and menu', true, now(), now()
FROM sys_table t WHERE t.name = 'sys_table'
AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'admin');

-- ============================================================
-- Part 3 — Create 6 Tabs (one per admin configuration table)
-- ============================================================

-- Tab 1: Table Definitions (sys_table)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Tables', t.id, 10, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin' AND t.name = 'sys_table'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 10);

-- Tab 1b: Columns child tab (for Table Definitions)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Columns', t.id, 20, false, NULL, 'table_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin' AND t.name = 'sys_column'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 20);

-- Tab 2: Window Definitions (sys_window)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Windows', t.id, 30, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin' AND t.name = 'sys_window'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 30);

-- Tab 2b: Tabs child tab (for Window Definitions)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Tabs', t.id, 40, false, NULL, 'window_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin' AND t.name = 'sys_tab'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 40);

-- Tab 2c: Fields child tab (for Window Definitions → Tabs)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Fields', t.id, 50, false, NULL, 'tab_id', true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin' AND t.name = 'sys_window_field'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 50);

-- Tab 3: Window Access (sys_window_access)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Access', t.id, 60, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin' AND t.name = 'sys_window_access'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 60);

-- Tab 4: Menu Configuration (sys_menu)
INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, where_clause, parent_column, is_active, created_at, updated_at)
SELECT gen_random_uuid(), w.id, 'Menu', t.id, 70, false, NULL, NULL, true, now(), now()
FROM sys_window w, sys_table t WHERE w.name = 'admin' AND t.name = 'sys_menu'
AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id = w.id AND seq_no = 70);

-- ============================================================
-- Part 4 — Update Menu to point to the new admin window
-- ============================================================

-- Update the "Administration" menu group's window children
-- Replace the 6 separate menu items with a single "Administration" item

-- First, remove old menu items that referenced the deleted admin windows
-- (already done above in Part 1)

-- Add a single "Administration" menu item under the Administration group
DO $$
DECLARE
    v_parent_id UUID;
    v_win_id UUID;
BEGIN
    SELECT id INTO v_parent_id FROM sys_menu WHERE name = 'Administration' AND type = 'group';
    SELECT id INTO v_win_id FROM sys_window WHERE name = 'admin';

    IF v_parent_id IS NOT NULL AND v_win_id IS NOT NULL THEN
        INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, created_at, updated_at)
        SELECT gen_random_uuid(), 'Administration', 'window', v_parent_id, v_win_id, 10, true, now(), now()
        WHERE NOT EXISTS (
            SELECT 1 FROM sys_menu WHERE parent_id = v_parent_id AND type = 'window' AND window_id = v_win_id
        );
    END IF;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- Part 5 — Grant window access for sys_admin role (idempotent)
-- ============================================================

DO $$
DECLARE
    v_role_id UUID;
    v_win_id UUID;
BEGIN
    SELECT id INTO v_role_id FROM identity_roles WHERE code = 'sys_admin';
    SELECT id INTO v_win_id FROM sys_window WHERE name = 'admin';

    IF v_role_id IS NOT NULL AND v_win_id IS NOT NULL THEN
        INSERT INTO sys_window_access (id, window_id, tenant_id, role_id, is_active, created_at, updated_at)
        SELECT gen_random_uuid(), v_win_id, NULL, v_role_id, true, now(), now()
        WHERE NOT EXISTS (
            SELECT 1 FROM sys_window_access WHERE window_id = v_win_id AND role_id = v_role_id AND tenant_id IS NULL
        );
    END IF;
END;
$$ LANGUAGE plpgsql;
