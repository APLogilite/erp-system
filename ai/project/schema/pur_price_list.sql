-- Centralized DDL reference for pur_price_list

CREATE TABLE pur_price_list (
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

CREATE INDEX idx_pur_price_list_vendor ON pur_price_list(vendor_id);
CREATE INDEX idx_pur_price_list_tenant ON pur_price_list(tenant_id);
