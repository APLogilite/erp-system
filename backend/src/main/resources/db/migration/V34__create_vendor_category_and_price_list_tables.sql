-- ============================================================
-- V34 — Create vendor category and purchase price list tables (PRD-010 TASK-094)
-- ============================================================

-- ------------------------------------------------------------
-- Part 1 — Vendor Category
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pur_vendor_category (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL,
    code        VARCHAR(50) NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    is_active   BOOLEAN NOT NULL DEFAULT true,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_by  UUID,
    deleted_at  TIMESTAMP,
    CONSTRAINT uq_pur_vendor_category_code UNIQUE (code, tenant_id)
);

CREATE INDEX IF NOT EXISTS idx_pur_vendor_category_tenant ON pur_vendor_category(tenant_id);

-- ------------------------------------------------------------
-- Part 2 — Purchase Price List Header
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pur_price_list (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL,
    code        VARCHAR(50) NOT NULL,
    name        VARCHAR(100) NOT NULL,
    vendor_id   UUID REFERENCES md_business_partner(id),
    currency    VARCHAR(3) DEFAULT 'USD',
    valid_from  DATE,
    valid_to    DATE,
    is_active   BOOLEAN NOT NULL DEFAULT true,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_by  UUID,
    deleted_at  TIMESTAMP,
    CONSTRAINT uq_pur_price_list_code UNIQUE (code, tenant_id)
);

CREATE INDEX IF NOT EXISTS idx_pur_price_list_vendor ON pur_price_list(vendor_id);
CREATE INDEX IF NOT EXISTS idx_pur_price_list_tenant ON pur_price_list(tenant_id);

-- ------------------------------------------------------------
-- Part 3 — Purchase Price List Lines
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pur_price_list_line (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    price_list_id   UUID NOT NULL REFERENCES pur_price_list(id),
    product_id      UUID NOT NULL REFERENCES md_product(id),
    uom_id          UUID REFERENCES md_uom(id),
    min_qty         NUMERIC(15,4) NOT NULL DEFAULT 0,
    unit_price      NUMERIC(15,4) NOT NULL DEFAULT 0,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now(),
    created_by      UUID,
    updated_by      UUID,
    deleted_at      TIMESTAMP,
    CONSTRAINT uq_pur_price_list_line UNIQUE (price_list_id, product_id, uom_id, min_qty)
);

CREATE INDEX IF NOT EXISTS idx_pur_price_list_line_list ON pur_price_list_line(price_list_id);
CREATE INDEX IF NOT EXISTS idx_pur_price_list_line_product ON pur_price_list_line(product_id);
CREATE INDEX IF NOT EXISTS idx_pur_price_list_line_tenant ON pur_price_list_line(tenant_id);

-- ------------------------------------------------------------
-- Part 4 — Add FK from md_business_partner.vendor_category_id (created in V33)
-- ------------------------------------------------------------
ALTER TABLE md_business_partner
  ADD CONSTRAINT fk_md_business_partner_vendor_category
  FOREIGN KEY (vendor_category_id) REFERENCES pur_vendor_category(id);

-- ------------------------------------------------------------
-- Part 5 — Register tables in sys_table / sys_column metadata
-- ------------------------------------------------------------
INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'pur_vendor_category', 'Vendor Category', 'Vendor Categories', 'master', 'pur_vendor_category',
       'Vendor classification categories', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'pur_vendor_category');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'pur_price_list', 'Purchase Price List', 'Purchase Price Lists', 'master', 'pur_price_list',
       'Vendor-specific purchase price lists', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'pur_price_list');

INSERT INTO sys_table (id, name, label, plural_label, table_type, table_name, description, is_active, tenant_id, created_at, updated_at)
SELECT gen_random_uuid(), 'pur_price_list_line', 'Purchase Price List Line', 'Purchase Price List Lines', 'master', 'pur_price_list_line',
       'Product lines within a purchase price list', true, '00000000-0000-0000-0000-000000000001', now(), now()
WHERE NOT EXISTS (SELECT 1 FROM sys_table WHERE name = 'pur_price_list_line');

DO $$
DECLARE
  v_table_id UUID;
BEGIN
  -- pur_vendor_category columns
  SELECT id INTO v_table_id FROM sys_table WHERE name = 'pur_vendor_category';
  IF v_table_id IS NOT NULL THEN
    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'code', 'Code', 'string', true, 50, 1, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'code');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'name', 'Name', 'string', true, 100, 2, true, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'name');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'description', 'Description', 'text', false, null, 3, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'description');
  END IF;

  -- pur_price_list columns
  SELECT id INTO v_table_id FROM sys_table WHERE name = 'pur_price_list';
  IF v_table_id IS NOT NULL THEN
    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'code', 'Code', 'string', true, 50, 1, null, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'code');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'name', 'Name', 'string', true, 100, 2, null, true, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'name');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'vendor_id', 'Vendor', 'many2one', false, null, 3, 'md_business_partner', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'vendor_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'currency', 'Currency', 'string', false, 3, 4, null, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'currency');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'valid_from', 'Valid From', 'date', false, null, 5, null, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'valid_from');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'valid_to', 'Valid To', 'date', false, null, 6, null, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'valid_to');
  END IF;

  -- pur_price_list_line columns
  SELECT id INTO v_table_id FROM sys_table WHERE name = 'pur_price_list_line';
  IF v_table_id IS NOT NULL THEN
    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'price_list_id', 'Price List', 'many2one', true, null, 1, 'pur_price_list', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'price_list_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'product_id', 'Product', 'many2one', true, null, 2, 'md_product', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'product_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'uom_id', 'UOM', 'many2one', false, null, 3, 'md_uom', false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'uom_id');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'min_qty', 'Minimum Quantity', 'decimal', true, null, 4, null, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'min_qty');

    INSERT INTO sys_column (id, table_id, code, label, type, required, max_length, position, relation_table, is_display_column, is_active, tenant_id, created_at, updated_at)
    SELECT gen_random_uuid(), v_table_id, 'unit_price', 'Unit Price', 'decimal', true, null, 5, null, false, true, '00000000-0000-0000-0000-000000000001', now(), now()
    WHERE NOT EXISTS (SELECT 1 FROM sys_column WHERE table_id = v_table_id AND code = 'unit_price');
  END IF;
END
$$;
