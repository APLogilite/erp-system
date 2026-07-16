-- Table: identity_user_organizations
-- Created: V1
CREATE TABLE identity_user_organizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    user_id UUID NOT NULL REFERENCES identity_users(id),
    organization_id UUID NOT NULL REFERENCES identity_organizations(id),
    UNIQUE(user_id, organization_id)
);

CREATE INDEX idx_identity_user_orgs_user ON identity_user_organizations(user_id);
CREATE INDEX idx_identity_user_orgs_org ON identity_user_organizations(organization_id);
