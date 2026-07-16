-- Table: identity_companies
-- Created: V1
CREATE TABLE identity_companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    tax_id VARCHAR(50),
    registration_number VARCHAR(50),
    address TEXT,
    phone VARCHAR(30),
    email VARCHAR(255),
    currency VARCHAR(3),
    organization_id UUID NOT NULL REFERENCES identity_organizations(id)
);

CREATE INDEX idx_identity_companies_org ON identity_companies(organization_id);
