-- Table: md_product
-- Created: V19
CREATE TABLE md_product (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    product_type VARCHAR(20) NOT NULL,
    uom_id UUID REFERENCES md_uom(id),
    unit_price NUMERIC(15,2),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted_at TIMESTAMP,
    CONSTRAINT uq_product_code UNIQUE (code)
);

CREATE INDEX IF NOT EXISTS idx_md_product_uom ON md_product(uom_id);
CREATE INDEX IF NOT EXISTS idx_md_product_tenant ON md_product(tenant_id);
