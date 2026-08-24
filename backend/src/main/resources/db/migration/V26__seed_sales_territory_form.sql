-- ============================================================
-- V26 — Sales Territory window + menu (PRD-008 TASK-078)
-- ============================================================

DO $$
DECLARE
  v_tenant CONSTANT UUID := '00000000-0000-0000-0000-000000000001';
  v_admin_role UUID;
BEGIN
  SELECT id INTO v_admin_role FROM identity_roles WHERE name = 'sys_admin' LIMIT 1;

  -- ============================================================
  -- 1. Sales Territory window
  -- ============================================================
  INSERT INTO sys_window (id, name, table_id, description, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Sales Territories', t.id, 'Sales territory master', true, v_tenant, now(), now()
  FROM sys_table t WHERE t.name = 'md_sales_territory'
  AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'Sales Territories');

  INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), w.id, 'Territory', t.id, 10, false, true, v_tenant, now(), now()
  FROM sys_window w, sys_table t WHERE w.name='Sales Territories' AND t.name='md_sales_territory'
  AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id=w.id AND name='Territory');

  INSERT INTO sys_window_field (id, tab_id, column_id, seq_no, is_displayed, is_readonly, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), tab.id, c.id, c.position, true, false, true, v_tenant, now(), now()
  FROM sys_tab tab JOIN sys_window w ON w.id=tab.window_id
  JOIN sys_column c ON c.table_id=tab.table_id AND c.code IN ('code','name','manager_id')
  WHERE w.name='Sales Territories' AND tab.name='Territory'
  AND NOT EXISTS (SELECT 1 FROM sys_window_field WHERE tab_id=tab.id AND column_id=c.id);

  -- ============================================================
  -- 2. Grant sys_admin access
  -- ============================================================
  IF v_admin_role IS NOT NULL THEN
    INSERT INTO sys_window_access (id, window_id, role_id, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), w.id, v_admin_role, true, v_tenant, now(), now()
    FROM sys_window w
    WHERE w.name = 'Sales Territories'
      AND NOT EXISTS (SELECT 1 FROM sys_window_access WHERE window_id=w.id AND role_id=v_admin_role);
  END IF;

  -- ============================================================
  -- 3. Menu entry: Master Data → Sales Territories
  -- ============================================================
  INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Sales Territories', 'window',
    (SELECT id FROM sys_menu WHERE name='Master Data'),
    w.id, 80, true, v_tenant, now(), now()
  FROM sys_window w WHERE w.name='Sales Territories'
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE name='Sales Territories');

END $$;
