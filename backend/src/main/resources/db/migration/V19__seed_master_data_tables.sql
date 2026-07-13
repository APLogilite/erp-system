-- ============================================================
-- PRD-003 / TASK-028 — Seed Master Data Tables
-- Creates 5 master data tables for the Order Flow:
--   - md_business_partner  (Customers, suppliers, contacts)
--   - md_product           (Goods and services catalog)
--   - md_uom               (Units of measure)
--   - md_uom_conversion    (UOM conversion factors)
--   - md_warehouse         (Physical storage locations)
-- Registers all tables + columns in the metadata engine so
-- PRD-001's runtime renderer can discover and manage them.
--
-- IMPORTANT: Set spring.flyway.enabled=true in
-- application-local.properties before running this migration.
-- ============================================================

-- ============================================================
-- Part 1 — Drop Existing Tables (Idempotency)
-- Order matters: drop tables with foreign keys first.
-- ============================================================

DROP TABLE IF EXISTS md_uom_conversion CASCADE;
DROP TABLE IF EXISTS md_product CASCADE;
DROP TABLE IF EXISTS md_warehouse CASCADE;
DROP TABLE IF EXISTS md_business_partner CASCADE;
DROP TABLE IF EXISTS md_uom CASCADE;

-- ============================================================
-- Part 2 — Create Physical Tables
-- Each table includes the 8 system columns required by the
-- BaseEntity / DynamicCrudService pattern.
-- ============================================================

-- -------------------------------------------------------------------
-- md_business_partner — Customers, suppliers, and other contacts
-- -------------------------------------------------------------------
CREATE TABLE md_business_partner (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL,
    code        VARCHAR(50) NOT NULL,
    name        VARCHAR(200) NOT NULL,
    partner_type VARCHAR(20) NOT NULL,
    email       VARCHAR(100),
    phone       VARCHAR(30),
    address     TEXT,
    tax_id      VARCHAR(50),
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_by  UUID,
    is_active   BOOLEAN NOT NULL DEFAULT true,
    deleted_at  TIMESTAMP,
    CONSTRAINT uq_business_partner_code UNIQUE (code)
);

-- -------------------------------------------------------------------
-- md_uom — Units of Measure (referenced by product)
-- -------------------------------------------------------------------
CREATE TABLE md_uom (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL,
    code        VARCHAR(10) NOT NULL,
    name        VARCHAR(50) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_by  UUID,
    is_active   BOOLEAN NOT NULL DEFAULT true,
    deleted_at  TIMESTAMP,
    CONSTRAINT uq_uom_code UNIQUE (code)
);

-- -------------------------------------------------------------------
-- md_product — Goods and services catalog
-- -------------------------------------------------------------------
CREATE TABLE md_product (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL,
    code        VARCHAR(50) NOT NULL,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    product_type VARCHAR(20) NOT NULL,
    uom_id      UUID REFERENCES md_uom(id),
    unit_price  NUMERIC(15,2),
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_by  UUID,
    is_active   BOOLEAN NOT NULL DEFAULT true,
    deleted_at  TIMESTAMP,
    CONSTRAINT uq_product_code UNIQUE (code)
);

-- -------------------------------------------------------------------
-- md_uom_conversion — Conversion factors between UOMs
-- -------------------------------------------------------------------
CREATE TABLE md_uom_conversion (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL,
    from_uom_id UUID NOT NULL REFERENCES md_uom(id),
    to_uom_id   UUID NOT NULL REFERENCES md_uom(id),
    product_id  UUID REFERENCES md_product(id),
    factor      NUMERIC(15,6) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_by  UUID,
    is_active   BOOLEAN NOT NULL DEFAULT true,
    deleted_at  TIMESTAMP
);

-- -------------------------------------------------------------------
-- md_warehouse — Physical storage locations
-- -------------------------------------------------------------------
CREATE TABLE md_warehouse (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL,
    code        VARCHAR(20) NOT NULL,
    name        VARCHAR(100) NOT NULL,
    address     TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    created_by  UUID,
    updated_by  UUID,
    is_active   BOOLEAN NOT NULL DEFAULT true,
    deleted_at  TIMESTAMP,
    CONSTRAINT uq_warehouse_code UNIQUE (code)
);

-- ============================================================
-- Part 3 — Register Tables in sys_metadata_models
-- Uses DELETE + INSERT pattern for idempotency.
-- ============================================================

-- Clean existing metadata registrations for these tables
DELETE FROM sys_table_columns
    WHERE table_id IN (SELECT id FROM sys_metadata_models WHERE name IN (
        'md_business_partner', 'md_product', 'md_uom',
        'md_uom_conversion', 'md_warehouse'
    ));

DELETE FROM sys_metadata_models WHERE name IN (
    'md_business_partner', 'md_product', 'md_uom',
    'md_uom_conversion', 'md_warehouse'
);

-- Insert table definitions
INSERT INTO sys_metadata_models (id, name, label, plural_label, table_type, table_name, description, definition, is_active, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'md_business_partner', 'Business Partner', 'Business Partners', 'dynamic', 'md_business_partner', 'Customers, suppliers, and other business contacts', '{}', true, now(), now()),
    (gen_random_uuid(), 'md_product',          'Product',          'Products',          'dynamic', 'md_product',          'Goods and services catalog',                        '{}', true, now(), now()),
    (gen_random_uuid(), 'md_uom',              'UOM',              'Units of Measure',  'dynamic', 'md_uom',              'Units of measure',                                  '{}', true, now(), now()),
    (gen_random_uuid(), 'md_uom_conversion',   'UOM Conversion',   'UOM Conversions',   'dynamic', 'md_uom_conversion',   'Conversion factors between units of measure',       '{}', true, now(), now()),
    (gen_random_uuid(), 'md_warehouse',        'Warehouse',        'Warehouses',        'dynamic', 'md_warehouse',        'Physical storage locations',                        '{}', true, now(), now())
ON CONFLICT (name) DO UPDATE SET
    label = EXCLUDED.label,
    plural_label = EXCLUDED.plural_label,
    table_type = EXCLUDED.table_type,
    table_name = EXCLUDED.table_name,
    description = EXCLUDED.description,
    updated_at = now();

-- ============================================================
-- Part 4 — Register Columns in sys_table_columns
-- ============================================================

-- -------------------------------------------------------------------
-- System columns added by BaseEntity (shared across all tables)
-- -------------------------------------------------------------------
-- These are handled by the DynamicCrudService at runtime:
-- id, tenant_id, created_at, updated_at, created_by, updated_by,
-- is_active, deleted_at

-- -------------------------------------------------------------------
-- md_business_partner columns
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'code',         'Code',         'string',  true,  50,  1, true, now(), now() FROM sys_metadata_models WHERE name = 'md_business_partner';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'name',         'Name',         'string',  true,  200, 2, true, now(), now() FROM sys_metadata_models WHERE name = 'md_business_partner';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'partner_type', 'Partner Type', 'enum',   true,  20,  3, '["customer","supplier","both"]', true, now(), now() FROM sys_metadata_models WHERE name = 'md_business_partner';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'email',        'Email',        'string',  false, 100, 4, true, now(), now() FROM sys_metadata_models WHERE name = 'md_business_partner';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'phone',        'Phone',        'string',  false, 30,  5, true, now(), now() FROM sys_metadata_models WHERE name = 'md_business_partner';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'address',      'Address',      'text',    false,      6, true, now(), now() FROM sys_metadata_models WHERE name = 'md_business_partner';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'tax_id',       'Tax ID',       'string',  false, 50,  7, true, now(), now() FROM sys_metadata_models WHERE name = 'md_business_partner';

-- -------------------------------------------------------------------
-- md_product columns
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'code',         'Code',         'string',  true,  50,  1, true, now(), now() FROM sys_metadata_models WHERE name = 'md_product';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'name',         'Name',         'string',  true,  200, 2, true, now(), now() FROM sys_metadata_models WHERE name = 'md_product';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'description',  'Description',  'text',    false,      3, true, now(), now() FROM sys_metadata_models WHERE name = 'md_product';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, enum_options, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'product_type', 'Product Type', 'enum',   true,  20,  4, '["goods","service"]', true, now(), now() FROM sys_metadata_models WHERE name = 'md_product';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'uom_id',       'UOM',          'many2one', false,     5, 'md_uom',     true, now(), now() FROM sys_metadata_models WHERE name = 'md_product';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'unit_price',   'Unit Price',   'decimal', false, 15,  2,    6, true, now(), now() FROM sys_metadata_models WHERE name = 'md_product';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'is_active',    'Active',       'boolean', false,      7, true, now(), now() FROM sys_metadata_models WHERE name = 'md_product';

-- -------------------------------------------------------------------
-- md_uom columns
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'code',         'Code',         'string',  true,  10,  1, true, now(), now() FROM sys_metadata_models WHERE name = 'md_uom';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'name',         'Name',         'string',  true,  50,  2, true, now(), now() FROM sys_metadata_models WHERE name = 'md_uom';

-- -------------------------------------------------------------------
-- md_uom_conversion columns
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'from_uom_id',  'From UOM',     'many2one', true,  1, 'md_uom',  true, now(), now() FROM sys_metadata_models WHERE name = 'md_uom_conversion';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'to_uom_id',    'To UOM',       'many2one', true,  2, 'md_uom',  true, now(), now() FROM sys_metadata_models WHERE name = 'md_uom_conversion';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, relation_table, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'product_id',   'Product',      'many2one', false, 3, 'md_product', true, now(), now() FROM sys_metadata_models WHERE name = 'md_uom_conversion';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, precision, scale, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'factor',       'Factor',       'decimal',  true,  15,  6,    4, true, now(), now() FROM sys_metadata_models WHERE name = 'md_uom_conversion';

-- -------------------------------------------------------------------
-- md_warehouse columns
-- -------------------------------------------------------------------
INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'code',         'Code',         'string',  true,  20,  1, true, now(), now() FROM sys_metadata_models WHERE name = 'md_warehouse';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, max_length, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'name',         'Name',         'string',  true,  100, 2, true, now(), now() FROM sys_metadata_models WHERE name = 'md_warehouse';

INSERT INTO sys_table_columns (id, table_id, code, label, type, required, position, is_active, created_at, updated_at)
SELECT gen_random_uuid(), id, 'address',      'Address',      'text',    false,      3, true, now(), now() FROM sys_metadata_models WHERE name = 'md_warehouse';

-- ============================================================
-- Part 5 — Foreign Key Indexes
-- Improves query performance on many2one joins.
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_md_product_uom           ON md_product(uom_id);
CREATE INDEX IF NOT EXISTS idx_md_uom_conv_from         ON md_uom_conversion(from_uom_id);
CREATE INDEX IF NOT EXISTS idx_md_uom_conv_to           ON md_uom_conversion(to_uom_id);
CREATE INDEX IF NOT EXISTS idx_md_uom_conv_product      ON md_uom_conversion(product_id);
CREATE INDEX IF NOT EXISTS idx_md_uom_conv_tenant       ON md_uom_conversion(tenant_id);
CREATE INDEX IF NOT EXISTS idx_md_product_tenant        ON md_product(tenant_id);
CREATE INDEX IF NOT EXISTS idx_md_bp_tenant             ON md_business_partner(tenant_id);
CREATE INDEX IF NOT EXISTS idx_md_uom_tenant            ON md_uom(tenant_id);
CREATE INDEX IF NOT EXISTS idx_md_warehouse_tenant      ON md_warehouse(tenant_id);
