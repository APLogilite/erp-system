-- Table: md_business_partner
-- Created: V19
CREATE TABLE md_business_partner (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    partner_type VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(30),
    address TEXT,
    tax_id VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT true,
    deleted_at TIMESTAMP,
    CONSTRAINT uq_business_partner_code UNIQUE (code)
);

CREATE INDEX IF NOT EXISTS idx_md_bp_tenant ON md_business_partner(tenant_id);
