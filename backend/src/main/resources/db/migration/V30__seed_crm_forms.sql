-- ============================================================
-- V30 — CRM windows + menu entries (PRD-008 TASK-085)
-- ============================================================

DO $$
DECLARE
  v_tenant CONSTANT UUID := '00000000-0000-0000-0000-000000000001';
  v_admin_role UUID;
BEGIN
  SELECT id INTO v_admin_role FROM identity_roles WHERE name = 'sys_admin' LIMIT 1;

  -- ============================================================
  -- 1. Lead window
  -- ============================================================
  INSERT INTO sys_window (id, name, table_id, description, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Leads', t.id, 'CRM leads', true, v_tenant, now(), now()
  FROM sys_table t WHERE t.name = 'tx_lead'
  AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'Leads');

  INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), w.id, 'Lead', t.id, 10, false, true, v_tenant, now(), now()
  FROM sys_window w, sys_table t WHERE w.name='Leads' AND t.name='tx_lead'
  AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id=w.id AND name='Lead');

  INSERT INTO sys_window_field (id, tab_id, column_id, seq_no, is_displayed, is_readonly, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), tab.id, c.id, c.position, true,
    CASE WHEN c.code IN ('lead_number','score') THEN true ELSE false END,
    true, v_tenant, now(), now()
  FROM sys_tab tab JOIN sys_window w ON w.id=tab.window_id
  JOIN sys_column c ON c.table_id=tab.table_id AND c.code IN ('lead_number','company_name','contact_name','email','phone','source','status','score','expected_revenue','salesperson_id','territory_id','notes','converted_partner_id','converted_opportunity_id')
  WHERE w.name='Leads' AND tab.name='Lead'
  AND NOT EXISTS (SELECT 1 FROM sys_window_field WHERE tab_id=tab.id AND column_id=c.id);

  -- ============================================================
  -- 2. Opportunity window
  -- ============================================================
  INSERT INTO sys_window (id, name, table_id, description, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Opportunities', t.id, 'CRM opportunities', true, v_tenant, now(), now()
  FROM sys_table t WHERE t.name = 'tx_opportunity'
  AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'Opportunities');

  INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), w.id, 'Opportunity', t.id, 10, false, true, v_tenant, now(), now()
  FROM sys_window w, sys_table t WHERE w.name='Opportunities' AND t.name='tx_opportunity'
  AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id=w.id AND name='Opportunity');

  INSERT INTO sys_window_field (id, tab_id, column_id, seq_no, is_displayed, is_readonly, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), tab.id, c.id, c.position, true,
    CASE WHEN c.code IN ('opportunity_number') THEN true ELSE false END,
    true, v_tenant, now(), now()
  FROM sys_tab tab JOIN sys_window w ON w.id=tab.window_id
  JOIN sys_column c ON c.table_id=tab.table_id AND c.code IN ('opportunity_number','name','partner_id','lead_id','stage','probability','expected_revenue','expected_close_date','salesperson_id','territory_id','notes','converted_quotation_id','converted_order_id')
  WHERE w.name='Opportunities' AND tab.name='Opportunity'
  AND NOT EXISTS (SELECT 1 FROM sys_window_field WHERE tab_id=tab.id AND column_id=c.id);

  -- ============================================================
  -- 3. Commission window
  -- ============================================================
  INSERT INTO sys_window (id, name, table_id, description, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Commissions', t.id, 'Sales commissions', true, v_tenant, now(), now()
  FROM sys_table t WHERE t.name = 'tx_commission'
  AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'Commissions');

  INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), w.id, 'Commission', t.id, 10, false, true, v_tenant, now(), now()
  FROM sys_window w, sys_table t WHERE w.name='Commissions' AND t.name='tx_commission'
  AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id=w.id AND name='Commission');

  INSERT INTO sys_window_field (id, tab_id, column_id, seq_no, is_displayed, is_readonly, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), tab.id, c.id, c.position, true, false, true, v_tenant, now(), now()
  FROM sys_tab tab JOIN sys_window w ON w.id=tab.window_id
  JOIN sys_column c ON c.table_id=tab.table_id AND c.code IN ('salesperson_id','opportunity_id','order_id','commission_percent','commission_amount','status','payment_date')
  WHERE w.name='Commissions' AND tab.name='Commission'
  AND NOT EXISTS (SELECT 1 FROM sys_window_field WHERE tab_id=tab.id AND column_id=c.id);

  -- ============================================================
  -- 4. Grant sys_admin access
  -- ============================================================
  IF v_admin_role IS NOT NULL THEN
    INSERT INTO sys_window_access (id, window_id, role_id, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), w.id, v_admin_role, true, v_tenant, now(), now()
    FROM sys_window w
    WHERE w.name IN ('Leads','Opportunities','Commissions','Sales Territories')
      AND NOT EXISTS (SELECT 1 FROM sys_window_access WHERE window_id=w.id AND role_id=v_admin_role);
  END IF;

  -- ============================================================
  -- 5. CRM menu group + entries
  -- ============================================================
  INSERT INTO sys_menu (id, name, type, parent_id, seq_no, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'CRM', 'group', NULL, 35, true, v_tenant, now(), now()
  WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name='CRM' AND type='group');

  INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Leads', 'window',
    (SELECT id FROM sys_menu WHERE name='CRM' AND type='group'),
    w.id, 10, true, v_tenant, now(), now()
  FROM sys_window w WHERE w.name='Leads'
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE name='Leads');

  INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Opportunities', 'window',
    (SELECT id FROM sys_menu WHERE name='CRM' AND type='group'),
    w.id, 20, true, v_tenant, now(), now()
  FROM sys_window w WHERE w.name='Opportunities'
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE name='Opportunities');

  INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'CRM Dashboard', 'window',
    (SELECT id FROM sys_menu WHERE name='CRM' AND type='group'),
    NULL, 30, true, v_tenant, now(), now()
  WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name='CRM Dashboard');

  INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Commissions', 'window',
    (SELECT id FROM sys_menu WHERE name='Sales'),
    w.id, 50, true, v_tenant, now(), now()
  FROM sys_window w WHERE w.name='Commissions'
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE name='Commissions');

END $$;
