-- Table: identity_organizations
-- Created: V1
-- Last modified: V30 (tenant_id set to SYS tenant UUID for NULL entries)
CREATE TABLE identity_organizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    tenant_id UUID NOT NULL REFERENCES identity_tenants(id),
    parent_id UUID REFERENCES identity_organizations(id),
    level INTEGER NOT NULL DEFAULT 0,
    path VARCHAR(500)
);

CREATE INDEX idx_identity_orgs_tenant ON identity_organizations(tenant_id);
CREATE INDEX idx_identity_orgs_parent ON identity_organizations(parent_id);
