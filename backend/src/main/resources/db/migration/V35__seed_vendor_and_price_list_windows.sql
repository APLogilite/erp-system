-- ============================================================
-- V35 — Seed vendor and purchase price list windows, tabs, fields, and menus (PRD-010 TASK-095)
-- ============================================================

-- ------------------------------------------------------------
-- Part 1 — Vendor window (filtered by is_vendor = true)
-- ------------------------------------------------------------
SELECT create_window('Vendors', 'md_business_partner', 'Manage vendor records', 'Vendor', 10);
UPDATE sys_tab SET where_clause = 'is_vendor = true' WHERE name = 'Vendor' AND window_id = (SELECT id FROM sys_window WHERE name = 'Vendors') AND seq_no = 10;
SELECT ensure_field('Vendors', 10, 'code',                        10, false, true, false, true);
SELECT ensure_field('Vendors', 10, 'name',                        20, true,  true, false, true);
SELECT ensure_field('Vendors', 10, 'is_vendor',                   30, false, true, false, true);
SELECT ensure_field('Vendors', 10, 'vendor_category_id',          40, false, true, false, false);
SELECT ensure_field('Vendors', 10, 'payment_term_id',             50, false, true, false, false);
SELECT ensure_field('Vendors', 10, 'default_purchase_currency',   60, false, true, false, false);
SELECT ensure_field('Vendors', 10, 'email',                       70, false, true, false, false);
SELECT ensure_field('Vendors', 10, 'phone',                       80, true,  true, false, false);
SELECT ensure_field('Vendors', 10, 'tax_id',                      90, false, true, false, false);

-- ------------------------------------------------------------
-- Part 2 — Vendor Category window
-- ------------------------------------------------------------
SELECT create_window('Vendor Categories', 'pur_vendor_category', 'Manage vendor categories', 'Category', 10);
SELECT ensure_field('Vendor Categories', 10, 'code',        10, false, true, false, true);
SELECT ensure_field('Vendor Categories', 10, 'name',        20, true,  true, false, true);
SELECT ensure_field('Vendor Categories', 10, 'description', 30, false, true, false, false);

-- ------------------------------------------------------------
-- Part 3 — Purchase Price List window with lines tab
-- ------------------------------------------------------------
SELECT create_window('Purchase Price Lists', 'pur_price_list', 'Manage vendor purchase price lists', 'Header', 10);
SELECT ensure_field('Purchase Price Lists', 10, 'code',        10, false, true, false, true);
SELECT ensure_field('Purchase Price Lists', 10, 'name',        20, true,  true, false, true);
SELECT ensure_field('Purchase Price Lists', 10, 'vendor_id',   30, false, true, false, true);
SELECT ensure_field('Purchase Price Lists', 10, 'currency',    40, false, true, false, false);
SELECT ensure_field('Purchase Price Lists', 10, 'valid_from',  50, false, true, false, false);
SELECT ensure_field('Purchase Price Lists', 10, 'valid_to',    60, false, true, false, false);
SELECT ensure_field('Purchase Price Lists', 10, 'is_active',   70, false, true, false, false);

-- Lines child tab
SELECT add_child_tab('Purchase Price Lists', 'Lines', 'pur_price_list_line', 20,
  (SELECT id FROM sys_column WHERE table_id = (SELECT id FROM sys_table WHERE name = 'pur_price_list_line') AND code = 'price_list_id'));
SELECT ensure_field('Purchase Price Lists', 20, 'product_id',  10, false, true, false, true);
SELECT ensure_field('Purchase Price Lists', 20, 'uom_id',      20, false, true, false, false);
SELECT ensure_field('Purchase Price Lists', 20, 'min_qty',     30, false, true, false, false);
SELECT ensure_field('Purchase Price Lists', 20, 'unit_price',  40, false, true, false, false);
SELECT ensure_field('Purchase Price Lists', 20, 'is_active',   50, false, true, false, false);

-- ------------------------------------------------------------
-- Part 4 — Menu entries under Purchasing
-- ------------------------------------------------------------
-- Purchasing → Vendor Categories
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Vendor Categories', 'window',
  (SELECT id FROM sys_menu WHERE name = 'Purchasing' AND parent_id = (SELECT id FROM sys_menu WHERE name = 'Transactions' AND type = 'group')),
  (SELECT id FROM sys_window WHERE name = 'Vendor Categories'),
  5, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Vendor Categories' AND type = 'window');

-- Purchasing → Vendors
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Vendors', 'window',
  (SELECT id FROM sys_menu WHERE name = 'Purchasing' AND parent_id = (SELECT id FROM sys_menu WHERE name = 'Transactions' AND type = 'group')),
  (SELECT id FROM sys_window WHERE name = 'Vendors'),
  6, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Vendors' AND type = 'window');

-- Purchasing → Purchase Price Lists
INSERT INTO sys_menu (id, name, type, parent_id, window_id, seq_no, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'Purchase Price Lists', 'window',
  (SELECT id FROM sys_menu WHERE name = 'Purchasing' AND parent_id = (SELECT id FROM sys_menu WHERE name = 'Transactions' AND type = 'group')),
  (SELECT id FROM sys_window WHERE name = 'Purchase Price Lists'),
  7, true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE name = 'Purchase Price Lists' AND type = 'window');

-- ------------------------------------------------------------
-- Part 5 — Window Access for sys_admin
-- ------------------------------------------------------------
INSERT INTO sys_window_access (id, window_id, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), w.id, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_window w
WHERE w.name IN ('Vendors', 'Vendor Categories', 'Purchase Price Lists')
  AND NOT EXISTS (SELECT 1 FROM sys_window_access wa WHERE wa.window_id = w.id AND wa.tenant_id = '00000000-0000-0000-0000-000000000001');
