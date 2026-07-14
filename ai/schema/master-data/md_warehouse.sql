-- Table: md_warehouse
-- Created: V19
CREATE TABLE md_warehouse (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    address TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted_at TIMESTAMP,
    CONSTRAINT uq_warehouse_code UNIQUE (code)
);

CREATE INDEX IF NOT EXISTS idx_md_warehouse_tenant ON md_warehouse(tenant_id);
