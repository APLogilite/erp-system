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
-- Part 3 — Foreign Key Indexes
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
