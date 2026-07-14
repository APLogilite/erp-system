-- Table: identity_tenants
-- Created: V1
-- Last modified: V31 (seeded SYS tenant with fixed UUID)
CREATE TABLE identity_tenants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    domain VARCHAR(255),
    logo_url VARCHAR(500),
    default_language VARCHAR(10),
    default_timezone VARCHAR(50),
    default_currency VARCHAR(3)
);

CREATE INDEX idx_identity_tenants_domain ON identity_tenants(domain) WHERE is_active = TRUE;
CREATE INDEX idx_identity_tenants_code ON identity_tenants(code);
