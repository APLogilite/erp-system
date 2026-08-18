-- ============================================================
-- V21 — Customer 360 window + menu entry (PRD-007 TASK-076)
-- ============================================================

DO $$
DECLARE
  v_tenant CONSTANT UUID := '00000000-0000-0000-0000-000000000001';
  v_admin_role UUID;
  v_window_id UUID;
  v_table_id UUID;
BEGIN
  SELECT id INTO v_admin_role FROM identity_roles WHERE name = 'sys_admin' LIMIT 1;
  SELECT id INTO v_table_id FROM sys_table WHERE name = 'md_business_partner';

  -- Create a placeholder window used only to register the menu entry.
  -- The frontend intercepts "Customer 360" window clicks and navigates to /app/customer-360.
  INSERT INTO sys_window (id, name, table_id, description, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Customer 360', v_table_id, 'Customer 360 dashboard selector', true, v_tenant, now(), now()
  WHERE NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'Customer 360')
  RETURNING id INTO v_window_id;

  IF v_window_id IS NULL THEN
    SELECT id INTO v_window_id FROM sys_window WHERE name = 'Customer 360';
  END IF;

  -- Grant admin access
  IF v_admin_role IS NOT NULL AND v_window_id IS NOT NULL THEN
    INSERT INTO sys_window_access (id, window_id, role_id, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_window_id, v_admin_role, true, v_tenant, now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_window_access WHERE window_id = v_window_id AND role_id = v_admin_role);
  END IF;

  -- Menu entry: Master Data → Customer 360
  INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Customer 360', 'window',
    (SELECT id FROM sys_menu WHERE name='Master Data'),
    v_window_id, 80, true, v_tenant, now(), now()
  WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Customer 360');

END $$;
