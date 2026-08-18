-- ============================================================
-- V11 — Price List tables + seed data (PRD-006 TASK-061)
-- ============================================================

-- ------------------------------------------------------------
-- Part 1 — md_price_list (header)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS md_price_list (
  id              UUID NOT NULL DEFAULT gen_random_uuid(),
  code            VARCHAR(50)  NOT NULL,
  name            VARCHAR(200) NOT NULL,
  currency        VARCHAR(3)   NOT NULL DEFAULT 'USD',
  valid_from      DATE,
  valid_to        DATE,
  is_default      BOOLEAN      NOT NULL DEFAULT false,
  partner_id      UUID,  -- nullable → customer-specific list
  tenant_id       UUID         NOT NULL DEFAULT '00000000-0000-0000-0000-000000000001',
  is_active       BOOLEAN      NOT NULL DEFAULT true,
  created_at      TIMESTAMP    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMP    NOT NULL DEFAULT now(),
  created_by      UUID,
  updated_by      UUID,
  deleted_at      TIMESTAMP,
  CONSTRAINT pk_md_price_list PRIMARY KEY (id),
  CONSTRAINT fk_price_list_partner FOREIGN KEY (partner_id) REFERENCES md_business_partner(id),
  CONSTRAINT uq_price_list_code UNIQUE (code, tenant_id)
);

-- ------------------------------------------------------------
-- Part 2 — md_price_list_line (lines)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS md_price_list_line (
  id              UUID NOT NULL DEFAULT gen_random_uuid(),
  price_list_id   UUID         NOT NULL,
  product_id      UUID         NOT NULL,
  min_quantity    NUMERIC(15,3),
  unit_price      NUMERIC(15,2) NOT NULL,
  tenant_id       UUID         NOT NULL DEFAULT '00000000-0000-0000-0000-000000000001',
  is_active       BOOLEAN      NOT NULL DEFAULT true,
  created_at      TIMESTAMP    NOT NULL DEFAULT now(),
  updated_at      TIMESTAMP    NOT NULL DEFAULT now(),
  created_by      UUID,
  updated_by      UUID,
  deleted_at      TIMESTAMP,
  CONSTRAINT pk_md_price_list_line PRIMARY KEY (id),
  CONSTRAINT fk_price_list_line_header FOREIGN KEY (price_list_id) REFERENCES md_price_list(id),
  CONSTRAINT fk_price_list_line_product FOREIGN KEY (product_id) REFERENCES md_product(id)
);

-- ------------------------------------------------------------
-- Part 3 — Register in sys_table
-- ------------------------------------------------------------
DO $$
BEGIN
  INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'md_price_list', 'Price List', 'Price Lists', 'static', 'md_price_list',
         'Product pricing rules per customer segment', true, '00000000-0000-0000-0000-000000000001', now(), now()
  WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_price_list');

  INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
  SELECT gen_random_uuid(), 'md_price_list_line', 'Price List Line', 'Price List Lines', 'static', 'md_price_list_line',
         'Individual product prices within a price list', true, '00000000-0000-0000-0000-000000000001', now(), now()
  WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_price_list_line');
END
$$;

-- Part 4 — Register columns in sys_column (idempotent via ensure_column function)
-- Using positional columns matching md_price_list
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'code',       'Code',        'string',   true,  50,  1, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name = 'md_price_list' AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'code');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'name',       'Name',        'string',   true,  200, 2, null, true, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name = 'md_price_list' AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'name');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'currency',   'Currency',    'string',   true,  3,   3, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name = 'md_price_list' AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'currency');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'valid_from',  'Valid From',  'date',     false, null, 4, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name = 'md_price_list' AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'valid_from');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'valid_to',    'Valid To',    'date',     false, null, 5, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name = 'md_price_list' AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'valid_to');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'is_default',  'Is Default',  'boolean',  false, null, 6, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name = 'md_price_list' AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'is_default');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'partner_id',  'Customer',    'many2one', false, null, 7, 'md_business_partner', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name = 'md_price_list' AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'partner_id');

-- md_price_list_line columns
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'price_list_id','Price List',  'many2one', true,  null, 1, 'md_price_list', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name = 'md_price_list_line' AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'price_list_id');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'product_id',   'Product',     'many2one', true,  null, 2, 'md_product', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name = 'md_price_list_line' AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'product_id');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'min_quantity', 'Min Qty',     'decimal',  false, null, 3, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name = 'md_price_list_line' AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'min_quantity');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'unit_price',   'Unit Price',  'decimal',  true,  null, 4, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name = 'md_price_list_line' AND NOT EXISTS (SELECT 1 FROM sys_column c WHERE c.table_id = t.id AND c.code = 'unit_price');

-- ------------------------------------------------------------
-- Part 5 — Seed default STANDARD price list with sample lines
-- ------------------------------------------------------------
DO $$
DECLARE
  v_price_list_id UUID;
BEGIN
  -- Create the default price list if it doesn't exist
  INSERT INTO md_price_list (id, code, name, currency, is_default, tenant_id)
  SELECT gen_random_uuid(), 'STANDARD', 'Standard Price List', 'USD', true, '00000000-0000-0000-0000-000000000001'
  WHERE NOT EXISTS (SELECT 1 FROM md_price_list WHERE code = 'STANDARD' AND tenant_id = '00000000-0000-0000-0000-000000000001');

  SELECT id INTO v_price_list_id FROM md_price_list WHERE code = 'STANDARD' AND tenant_id = '00000000-0000-0000-0000-000000000001';

  -- Seed sample lines referencing existing demo products (idempotent by price_list_id + product_id key)
  INSERT INTO md_price_list_line (id, price_list_id, product_id, min_quantity, unit_price, tenant_id)
  SELECT gen_random_uuid(), v_price_list_id, p.id, null, 1200.00, '00000000-0000-0000-0000-000000000001'
  FROM md_product p WHERE p.code = 'PROD-LAP-001' AND NOT EXISTS (SELECT 1 FROM md_price_list_line l WHERE l.price_list_id = v_price_list_id AND l.product_id = p.id);

  INSERT INTO md_price_list_line (id, price_list_id, product_id, min_quantity, unit_price, tenant_id)
  SELECT gen_random_uuid(), v_price_list_id, p.id, null, 25.00, '00000000-0000-0000-0000-000000000001'
  FROM md_product p WHERE p.code = 'PROD-MOU-001' AND NOT EXISTS (SELECT 1 FROM md_price_list_line l WHERE l.price_list_id = v_price_list_id AND l.product_id = p.id);

  INSERT INTO md_price_list_line (id, price_list_id, product_id, min_quantity, unit_price, tenant_id)
  SELECT gen_random_uuid(), v_price_list_id, p.id, null, 150.00, '00000000-0000-0000-0000-000000000001'
  FROM md_product p WHERE p.code = 'PROD-SVC-001' AND NOT EXISTS (SELECT 1 FROM md_price_list_line l WHERE l.price_list_id = v_price_list_id AND l.product_id = p.id);
END
$$;
