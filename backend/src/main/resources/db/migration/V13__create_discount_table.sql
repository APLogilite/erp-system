-- ============================================================
-- V13 — Discount Rule table + seed (PRD-006 TASK-062)
-- ============================================================

CREATE TABLE IF NOT EXISTS md_discount_rule (
  id              UUID NOT NULL DEFAULT gen_random_uuid(),
  code            VARCHAR(50)   NOT NULL,
  name            VARCHAR(200)  NOT NULL,
  discount_type   VARCHAR(20)   NOT NULL,
  discount_value  NUMERIC(15,2) NOT NULL,
  partner_id      UUID,
  product_id      UUID,
  min_quantity    NUMERIC(15,3),
  valid_from      DATE,
  valid_to        DATE,
  tenant_id       UUID          NOT NULL DEFAULT '00000000-0000-0000-0000-000000000001',
  is_active       BOOLEAN       NOT NULL DEFAULT true,
  created_at      TIMESTAMP     NOT NULL DEFAULT now(),
  updated_at      TIMESTAMP     NOT NULL DEFAULT now(),
  created_by      UUID,
  updated_by      UUID,
  deleted_at      TIMESTAMP,
  CONSTRAINT pk_md_discount_rule PRIMARY KEY (id),
  CONSTRAINT fk_discount_partner FOREIGN KEY (partner_id) REFERENCES md_business_partner(id),
  CONSTRAINT fk_discount_product FOREIGN KEY (product_id) REFERENCES md_product(id),
  CONSTRAINT uq_discount_code UNIQUE (code, tenant_id)
);

-- Register in sys_table
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'md_discount_rule', 'Discount Rule', 'Discount Rules', 'static', 'md_discount_rule',
       'Configurable discount rules (percentage or fixed amount)', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_discount_rule');

-- Register columns
INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'code',   'Code',   'string', true, 50, 1, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='md_discount_rule' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='code');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'name',   'Name',   'string', true, 200, 2, true, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='md_discount_rule' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='name');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, enum_options, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'discount_type', 'Type',  'enum', true, 20, 3, '["percentage","fixed_amount"]'::jsonb, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='md_discount_rule' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='discount_type');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'discount_value', 'Value',  'decimal', true, null, 4, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='md_discount_rule' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='discount_value');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'partner_id', 'Customer', 'many2one', false, null, 5, 'md_business_partner', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='md_discount_rule' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='partner_id');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'product_id', 'Product', 'many2one', false, null, 6, 'md_product', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='md_discount_rule' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='product_id');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'min_quantity', 'Min Qty', 'decimal', false, null, 7, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='md_discount_rule' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='min_quantity');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'valid_from', 'Valid From', 'date', false, null, 8, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='md_discount_rule' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='valid_from');

INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), t.id, 'valid_to', 'Valid To', 'date', false, null, 9, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
FROM sys_table t WHERE t.name='md_discount_rule' AND NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id=t.id AND code='valid_to');

-- Seed sample: 10% off on all products
INSERT INTO md_discount_rule (id, code, name, discount_type, discount_value, tenant_id)
SELECT gen_random_uuid(), 'VOLUME10', '10% Volume Discount', 'percentage', 10.00, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM md_discount_rule WHERE code='VOLUME10' AND tenant_id='00000000-0000-0000-0000-000000000001');

-- Seed sample: $50 fixed discount on orders of 1000+ units
INSERT INTO md_discount_rule (id, code, name, discount_type, discount_value, min_quantity, tenant_id)
SELECT gen_random_uuid(), 'BULK50', 'Bulk $50 Off', 'fixed_amount', 50.00, 1000, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM md_discount_rule WHERE code='BULK50' AND tenant_id='00000000-0000-0000-0000-000000000001');
