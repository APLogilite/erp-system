-- Centralized DDL reference for md_business_partner
-- Accumulated from V2, V18, V32, V33

CREATE TABLE md_business_partner (
    id                         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id                  UUID NOT NULL,
    code                       VARCHAR(50) NOT NULL,
    name                       VARCHAR(200) NOT NULL,
    partner_type               VARCHAR(20) NOT NULL,
    email                      VARCHAR(100),
    phone                      VARCHAR(30),
    address                    TEXT,
    tax_id                     VARCHAR(50),
    credit_limit               DECIMAL(15,2),
    credit_hold                BOOLEAN NOT NULL DEFAULT false,
    payment_term_id            UUID REFERENCES md_payment_term(id),
    territory_id               UUID REFERENCES md_sales_territory(id),
    is_vendor                  BOOLEAN NOT NULL DEFAULT false,
    vendor_category_id         UUID,
    default_purchase_currency  VARCHAR(3) DEFAULT 'USD',
    created_at                 TIMESTAMP NOT NULL DEFAULT now(),
    updated_at                 TIMESTAMP NOT NULL DEFAULT now(),
    created_by                 UUID,
    updated_by                 UUID,
    is_active                  BOOLEAN NOT NULL DEFAULT true,
    deleted_at                 TIMESTAMP,
    CONSTRAINT uq_business_partner_code UNIQUE (code)
);

CREATE INDEX idx_md_business_partner_payment_term ON md_business_partner(payment_term_id);
CREATE INDEX idx_md_business_partner_territory ON md_business_partner(territory_id);
CREATE INDEX idx_md_business_partner_vendor_category ON md_business_partner(vendor_category_id);
