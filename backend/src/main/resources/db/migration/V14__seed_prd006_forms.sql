-- ============================================================
-- V14 — PRD-006 Window Seeds (Quotation / PriceList / Discount / Sequence)
-- ============================================================

-- Helper: reusable snippet ids
DO $$
DECLARE
  v_tenant CONSTANT UUID := '00000000-0000-0000-0000-000000000001';
  v_admin_role UUID;
BEGIN
  SELECT id INTO v_admin_role FROM identity_roles WHERE name = 'sys_admin' LIMIT 1;

  -- ============================================================
  -- 1. Document Sequence window
  -- ============================================================
  INSERT INTO sys_window (id, name, table_id, description, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Document Sequences', t.id, 'Auto-numbering sequences', true, v_tenant, now(), now()
  FROM sys_table t WHERE t.name = 'sys_document_sequence'
  AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name = 'Document Sequences');

  INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), w.id, 'Sequence', t.id, 10, false, true, v_tenant, now(), now()
  FROM sys_window w, sys_table t WHERE w.name='Document Sequences' AND t.name='sys_document_sequence'
  AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id=w.id AND name='Sequence');

  -- Fields: sequence_name, prefix, next_value, padding
  INSERT INTO sys_window_field (id, tab_id, column_id, seq_no, is_displayed, is_readonly, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), tab.id, c.id, c.position, true, false, true, v_tenant, now(), now()
  FROM sys_tab tab JOIN sys_window w ON w.id=tab.window_id
  JOIN sys_column c ON c.table_id=tab.table_id AND c.code IN ('sequence_name','prefix','next_value','padding')
  WHERE w.name='Document Sequences' AND tab.name='Sequence'
  AND NOT EXISTS (SELECT 1 FROM sys_window_field WHERE tab_id=tab.id AND column_id=c.id);

  -- ============================================================
  -- 2. Price List window (header + lines)
  -- ============================================================
  INSERT INTO sys_window (id, name, table_id, description, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Price Lists', t.id, 'Pricing rules', true, v_tenant, now(), now()
  FROM sys_table t WHERE t.name='md_price_list'
  AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name='Price Lists');

  INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), w.id, 'Price List', t.id, 10, false, true, v_tenant, now(), now()
  FROM sys_window w, sys_table t WHERE w.name='Price Lists' AND t.name='md_price_list'
  AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id=w.id AND name='Price List');

  -- Header fields
  INSERT INTO sys_window_field (id, tab_id, column_id, seq_no, is_displayed, is_readonly, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), tab.id, c.id, c.position, true, false, true, v_tenant, now(), now()
  FROM sys_tab tab JOIN sys_window w ON w.id=tab.window_id
  JOIN sys_column c ON c.table_id=tab.table_id AND c.code IN ('code','name','currency','valid_from','valid_to','is_default','partner_id')
  WHERE w.name='Price Lists' AND tab.name='Price List'
  AND NOT EXISTS (SELECT 1 FROM sys_window_field WHERE tab_id=tab.id AND column_id=c.id);

  -- Lines child tab
  INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, parent_link_column_id, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), w.id, 'Lines', t2.id, 20, false, c.id, true, v_tenant, now(), now()
  FROM sys_window w, sys_table t2, sys_column c
  WHERE w.name='Price Lists' AND t2.name='md_price_list_line' AND c.table_id=t2.id AND c.code='price_list_id'
  AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id=w.id AND name='Lines');

  -- Lines fields
  INSERT INTO sys_window_field (id, tab_id, column_id, seq_no, is_displayed, is_readonly, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), tab.id, c.id, c.position, true, false, true, v_tenant, now(), now()
  FROM sys_tab tab JOIN sys_window w ON w.id=tab.window_id
  JOIN sys_column c ON c.table_id=tab.table_id AND c.code IN ('product_id','min_quantity','unit_price')
  WHERE w.name='Price Lists' AND tab.name='Lines'
  AND NOT EXISTS (SELECT 1 FROM sys_window_field WHERE tab_id=tab.id AND column_id=c.id);

  -- ============================================================
  -- 3. Discount Rule window
  -- ============================================================
  INSERT INTO sys_window (id, name, table_id, description, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Discount Rules', t.id, 'Discount rules', true, v_tenant, now(), now()
  FROM sys_table t WHERE t.name='md_discount_rule'
  AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name='Discount Rules');

  INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), w.id, 'Rule', t.id, 10, false, true, v_tenant, now(), now()
  FROM sys_window w, sys_table t WHERE w.name='Discount Rules' AND t.name='md_discount_rule'
  AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id=w.id AND name='Rule');

  INSERT INTO sys_window_field (id, tab_id, column_id, seq_no, is_displayed, is_readonly, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), tab.id, c.id, c.position, true, false, true, v_tenant, now(), now()
  FROM sys_tab tab JOIN sys_window w ON w.id=tab.window_id
  JOIN sys_column c ON c.table_id=tab.table_id AND c.code IN ('code','name','discount_type','discount_value','partner_id','product_id','min_quantity','valid_from','valid_to')
  WHERE w.name='Discount Rules' AND tab.name='Rule'
  AND NOT EXISTS (SELECT 1 FROM sys_window_field WHERE tab_id=tab.id AND column_id=c.id);

  -- ============================================================
  -- 4. Sales Quotation window (header + lines)
  -- ============================================================
  INSERT INTO sys_window (id, name, table_id, description, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Sales Quotations', t.id, 'Sales quotation', true, v_tenant, now(), now()
  FROM sys_table t WHERE t.name='tx_quotation'
  AND NOT EXISTS (SELECT 1 FROM sys_window WHERE name='Sales Quotations');

  INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), w.id, 'Header', t.id, 10, false, true, v_tenant, now(), now()
  FROM sys_window w, sys_table t WHERE w.name='Sales Quotations' AND t.name='tx_quotation'
  AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id=w.id AND name='Header');

  INSERT INTO sys_window_field (id, tab_id, column_id, seq_no, is_displayed, is_readonly, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), tab.id, c.id, c.position, true, false, true, v_tenant, now(), now()
  FROM sys_tab tab JOIN sys_window w ON w.id=tab.window_id
  JOIN sys_column c ON c.table_id=tab.table_id AND c.code IN ('quote_number','quote_date','valid_until','partner_id','price_list_id','currency','grand_total','status','notes')
  WHERE w.name='Sales Quotations' AND tab.name='Header'
  AND NOT EXISTS (SELECT 1 FROM sys_window_field WHERE tab_id=tab.id AND column_id=c.id);

  -- Quotation Lines child tab
  INSERT INTO sys_tab (id, window_id, name, table_id, seq_no, is_single_row, parent_link_column_id, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), w.id, 'Lines', t2.id, 20, false, c.id, true, v_tenant, now(), now()
  FROM sys_window w, sys_table t2, sys_column c
  WHERE w.name='Sales Quotations' AND t2.name='tx_quotation_line' AND c.table_id=t2.id AND c.code='quotation_id'
  AND NOT EXISTS (SELECT 1 FROM sys_tab WHERE window_id=w.id AND name='Lines');

  INSERT INTO sys_window_field (id, tab_id, column_id, seq_no, is_displayed, is_readonly, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), tab.id, c.id, c.position, true, false, true, v_tenant, now(), now()
  FROM sys_tab tab JOIN sys_window w ON w.id=tab.window_id
  JOIN sys_column c ON c.table_id=tab.table_id AND c.code IN ('line_number','product_id','description','quantity','uom_id','unit_price','line_total')
  WHERE w.name='Sales Quotations' AND tab.name='Lines'
  AND NOT EXISTS (SELECT 1 FROM sys_window_field WHERE tab_id=tab.id AND column_id=c.id);

  -- ============================================================
  -- 5. Access: grant sys_admin access to all new windows
  -- ============================================================
  IF v_admin_role IS NOT NULL THEN
    INSERT INTO sys_window_access (id, window_id, role_id, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), w.id, v_admin_role, true, v_tenant, now(), now()
    FROM sys_window w
    WHERE w.name IN ('Document Sequences','Price Lists','Discount Rules','Sales Quotations')
      AND NOT EXISTS (SELECT 1 FROM sys_window_access WHERE window_id=w.id AND role_id=v_admin_role);
  END IF;

  -- ============================================================
  -- 6. Menu entries (Transactions → Sales → Sales Quotations)
  --      + Master Data → Price Lists, Discount Rules
  --      + Administration → Document Sequences
  -- ============================================================

  -- Find the parent groups
  INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Sales Quotations', 'window',
    (SELECT id FROM sys_menu WHERE name='Sales'),
    w.id, 40, true, v_tenant, now(), now()
  FROM sys_window w WHERE w.name='Sales Quotations'
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE name='Sales Quotations');

  INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Price Lists', 'window',
    (SELECT id FROM sys_menu WHERE name='Master Data'),
    w.id, 50, true, v_tenant, now(), now()
  FROM sys_window w WHERE w.name='Price Lists'
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE name='Price Lists');

  INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Discount Rules', 'window',
    (SELECT id FROM sys_menu WHERE name='Master Data'),
    w.id, 60, true, v_tenant, now(), now()
  FROM sys_window w WHERE w.name='Discount Rules'
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE name='Discount Rules');

  INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'Document Sequences', 'window',
    (SELECT id FROM sys_menu WHERE name='Administration'),
    w.id, 40, true, v_tenant, now(), now()
  FROM sys_window w WHERE w.name='Document Sequences'
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE name='Document Sequences');

END $$;
