-- ============================================================
-- PRD-004 / TASK-045 — Seed Menu Entries + Window Access
--
-- Creates the hierarchical menu tree and default window access
-- entries for the system admin role.
--
-- DEPENDS ON: V26 (admin windows), V27 (ERP windows)
-- ============================================================

-- ============================================================
-- Helper: get window ID by name
-- ============================================================
CREATE OR REPLACE FUNCTION win_id(p_name TEXT) RETURNS UUID AS $$
  SELECT id FROM sys_window WHERE name = p_name;
$$ LANGUAGE SQL IMMUTABLE;

-- ============================================================
-- Helper: insert menu item if not exists
-- ============================================================
CREATE OR REPLACE FUNCTION add_menu(
  p_name TEXT, p_type TEXT, p_parent_name TEXT DEFAULT NULL,
  p_window_name TEXT DEFAULT NULL, p_seq INTEGER DEFAULT 10,
  p_icon TEXT DEFAULT NULL
) RETURNS UUID AS $$
DECLARE
  v_parent_id UUID;
  v_window_id UUID;
  v_menu_id UUID;
BEGIN
  IF p_parent_name IS NOT NULL THEN
    SELECT id INTO v_parent_id FROM sys_menu WHERE name = p_parent_name AND type = 'group';
  END IF;
  IF p_window_name IS NOT NULL THEN
    v_window_id := win_id(p_window_name);
  END IF;

  INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, icon, is_active, created_at, updated_at)
  SELECT gen_random_uuid(), p_name, p_type, v_parent_id, v_window_id, p_seq, p_icon, true, now(), now()
  WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = p_name AND (parent_id IS NULL AND v_parent_id IS NULL OR parent_id = v_parent_id))
  RETURNING id INTO v_menu_id;

  RETURN v_menu_id;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- Part 1 — Menu Tree
-- ============================================================

-- 1a. Root-level groups
SELECT add_menu('Administration', 'group', NULL, NULL, 10, 'settings');
SELECT add_menu('Master Data',    'group', NULL, NULL, 20, 'storage');
SELECT add_menu('Transactions',   'group', NULL, NULL, 30, 'receipt');

-- 1b. Administration menu items — 3 entries for admin configuration windows
SELECT add_menu('Table & Columns',     'window', 'Administration', 'sys_table',    10);
SELECT add_menu('Window, Tab & Field',  'window', 'Administration', 'sys_window', 20);
SELECT add_menu('Menu Configuration',   'window', 'Administration', 'sys_menu',      30);

-- 1c. Master Data menu items
SELECT add_menu('Business Partners', 'window', 'Master Data', 'Business Partners', 10);
SELECT add_menu('Products',          'window', 'Master Data', 'Products',          20);
SELECT add_menu('Units of Measure',  'window', 'Master Data', 'UOM',               30);
SELECT add_menu('Warehouses',        'window', 'Master Data', 'Warehouses',        40);

-- 1d. Transactions sub-groups
SELECT add_menu('Sales',       'group', 'Transactions', NULL, 10);
SELECT add_menu('Purchasing',  'group', 'Transactions', NULL, 20);

-- Transaction items
SELECT add_menu('Sales Orders',    'window', 'Sales',      'Sales Orders',    10);
SELECT add_menu('Sales Invoices',  'window', 'Sales',      'Sales Invoices',  20);
SELECT add_menu('Payments',        'window', 'Sales',      'Payments',        30);
SELECT add_menu('Shipments',       'window', 'Sales',      'Shipments',       40);

SELECT add_menu('Purchase Orders',   'window', 'Purchasing', 'Purchase Orders',   10);
SELECT add_menu('Purchase Invoices', 'window', 'Purchasing', 'Purchase Invoices', 20);

-- ============================================================
-- Part 2 — Window Access (grant access to system admin role)
-- ============================================================

-- Get system admin role ID (created by IdentitySeedData)
-- The sys_admin role has code = 'sys_admin'
DO $$
DECLARE
  v_role_id UUID;
  v_windows UUID[];
  v_win_id UUID;
BEGIN
  SELECT id INTO v_role_id FROM identity_roles WHERE code = 'sys_admin';

  IF v_role_id IS NOT NULL THEN
    -- Collect all window IDs
    v_windows := ARRAY(
      SELECT id FROM sys_window WHERE is_active = true
    );

    -- Grant access to each window
    FOREACH v_win_id IN ARRAY v_windows LOOP
      INSERT INTO sys_window_access (id, window_id, tenant_id, role_id, is_active, created_at, updated_at)
      SELECT gen_random_uuid(), v_win_id, NULL, v_role_id, true, now(), now()
      WHERE NOT EXISTS (
        SELECT 1 FROM sys_window_access
        WHERE window_id = v_win_id AND role_id = v_role_id AND tenant_id IS NULL
      );
    END LOOP;
  END IF;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- Cleanup
-- ============================================================
DROP FUNCTION IF EXISTS add_menu(TEXT, TEXT, TEXT, TEXT, INTEGER, TEXT);
DROP FUNCTION IF EXISTS win_id(TEXT);
