-- Table: md_uom
-- Created: V19
CREATE TABLE md_uom (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    code VARCHAR(10) NOT NULL,
    name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted_at TIMESTAMP,
    CONSTRAINT uq_uom_code UNIQUE (code)
);

CREATE INDEX IF NOT EXISTS idx_md_uom_tenant ON md_uom(tenant_id);
