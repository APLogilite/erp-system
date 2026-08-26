-- Centralized DDL reference for pur_vendor_category

CREATE TABLE pur_vendor_category (
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

CREATE INDEX idx_pur_vendor_category_tenant ON pur_vendor_category(tenant_id);
