-- ============================================================
-- V25 — Sales Territory table + seed data + metadata (PRD-008 TASK-078)
-- ============================================================

-- ------------------------------------------------------------
-- Part 1 — Create table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS md_sales_territory (
  id         UUID NOT NULL DEFAULT gen_random_uuid(),
  code       VARCHAR(20)  NOT NULL,
  name       VARCHAR(100) NOT NULL,
  manager_id UUID,
  tenant_id  UUID         NOT NULL DEFAULT '00000000-0000-0000-0000-000000000001',
  is_active  BOOLEAN      NOT NULL DEFAULT true,
  created_at TIMESTAMP    NOT NULL DEFAULT now(),
  updated_at TIMESTAMP    NOT NULL DEFAULT now(),
  created_by UUID,
  updated_by UUID,
  deleted_at TIMESTAMP,
  CONSTRAINT pk_md_sales_territory PRIMARY KEY (id),
  CONSTRAINT uq_md_sales_territory_code UNIQUE (code, tenant_id),
  CONSTRAINT fk_md_sales_territory_manager FOREIGN KEY (manager_id) REFERENCES identity_users(id)
);

-- ------------------------------------------------------------
-- Part 2 — Seed standard sales territories
-- ------------------------------------------------------------
INSERT INTO md_sales_territory (id, code, name, tenant_id)
SELECT gen_random_uuid(), 'NORTH', 'North Region', '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM md_sales_territory WHERE code='NORTH' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO md_sales_territory (id, code, name, tenant_id)
SELECT gen_random_uuid(), 'SOUTH', 'South Region', '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM md_sales_territory WHERE code='SOUTH' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO md_sales_territory (id, code, name, tenant_id)
SELECT gen_random_uuid(), 'EAST', 'East Region', '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM md_sales_territory WHERE code='EAST' AND tenant_id='00000000-0000-0000-0000-000000000001');

INSERT INTO md_sales_territory (id, code, name, tenant_id)
SELECT gen_random_uuid(), 'WEST', 'West Region', '00000000-0000-0000-0000-000000000001'
WHERE NOT EXISTS (SELECT 1 FROM md_sales_territory WHERE code='WEST' AND tenant_id='00000000-0000-0000-0000-000000000001');

-- ------------------------------------------------------------
-- Part 3 — Register in sys_table / sys_column metadata
-- ------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'md_sales_territory', 'Sales Territory', 'Sales Territories', 'master', 'md_sales_territory',
       'Sales territories for team and customer assignment', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'md_sales_territory');

DO $$
DECLARE
  v_table_id UUID;
BEGIN
  SELECT id INTO v_table_id FROM sys_table WHERE name = 'md_sales_territory';
  IF v_table_id IS NOT NULL THEN
    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'code',       'Code',          'string',  true,  20, 1, false, null,              true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'code');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'name',       'Name',          'string',  true, 100, 2, true,  null,              true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'name');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, relation_table, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'manager_id', 'Manager',       'many2one', false, null, 3, false, 'identity_users', true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'manager_id');
  END IF;
END
$$;
