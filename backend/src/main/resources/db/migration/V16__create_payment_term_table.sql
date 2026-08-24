-- ============================================================
-- V16 — Payment Term table + seed data + metadata (PRD-007 TASK-070)
-- ============================================================

-- ------------------------------------------------------------
-- Part 1 — Create table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS md_payment_term (
  id                UUID NOT NULL DEFAULT gen_random_uuid(),
  code              VARCHAR(20)  NOT NULL,
  name              VARCHAR(100) NOT NULL,
  days              INTEGER      NOT NULL,
  discount_percent  DECIMAL(5,2),
  discount_days     INTEGER,
  tenant_id         UUID         NOT NULL DEFAULT '00000000-0000-0000-0000-000000000001',
  is_active         BOOLEAN      NOT NULL DEFAULT true,
  created_at        TIMESTAMP    NOT NULL DEFAULT now(),
  updated_at        TIMESTAMP    NOT NULL DEFAULT now(),
  created_by        UUID,
  updated_by        UUID,
  deleted_at        TIMESTAMP,
  CONSTRAINT pk_md_payment_term PRIMARY KEY (id),
  CONSTRAINT uq_md_payment_term_code UNIQUE (code, tenant_id)
);

-- ------------------------------------------------------------
-- Part 2 — Seed standard payment terms
-- ------------------------------------------------------------
INSERT INTO md_payment_term (id, code, name, days, discount_percent, discount_days, tenant_id)
SELECT gen_random_uuid(), 'NET0',  'Due on Receipt', 0,  null, null, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM md_payment_term WHERE code='NET0' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO md_payment_term (id, code, name, days, discount_percent, discount_days, tenant_id)
SELECT gen_random_uuid(), 'NET15', 'Net 15 Days',    15, null, null, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM md_payment_term WHERE code='NET15' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO md_payment_term (id, code, name, days, discount_percent, discount_days, tenant_id)
SELECT gen_random_uuid(), 'NET30', 'Net 30 Days',    30, null, null, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM md_payment_term WHERE code='NET30' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO md_payment_term (id, code, name, days, discount_percent, discount_days, tenant_id)
SELECT gen_random_uuid(), 'NET60', 'Net 60 Days',    60, null, null, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM md_payment_term WHERE code='NET60' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO md_payment_term (id, code, name, days, discount_percent, discount_days, tenant_id)
SELECT gen_random_uuid(), 'NET90', 'Net 90 Days',    90, null, null, '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM md_payment_term WHERE code='NET90' AND tenant_id='00000000-0000-0000-0000-000000000001');

-- ------------------------------------------------------------
-- Part 3 — Register in sys_table / sys_column metadata
-- ------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'md_payment_term', 'Payment Term', 'Payment Terms', 'master', 'md_payment_term',
       'Payment terms master (Net 30, Net 60, etc.)', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_payment_term');

DO $$
DECLARE
  v_table_id UUID;
BEGIN
  SELECT id INTO v_table_id FROM sys_table WHERE name = 'md_payment_term';
  IF v_table_id IS NOT NULL THEN
    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'code',             'Code',             'string',  true,  20, 1, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'code');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'name',             'Name',             'string',  true, 100, 2, true,  true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'name');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'days',             'Days',             'integer', true, null, 3, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'days');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'discount_percent', 'Discount Percent', 'decimal', false, null, 4, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'discount_percent');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'discount_days',    'Discount Days',    'integer', false, null, 5, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'discount_days');
  END IF;
END
$$;
