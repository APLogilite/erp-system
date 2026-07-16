-- Table: identity_user_companies
-- Created: V1
CREATE TABLE identity_user_companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    user_id UUID NOT NULL REFERENCES identity_users(id),
    company_id UUID NOT NULL REFERENCES identity_companies(id),
    is_default BOOLEAN DEFAULT FALSE,
    UNIQUE(user_id, company_id)
);

CREATE INDEX idx_identity_user_companies_user ON identity_user_companies(user_id);
CREATE INDEX idx_identity_user_companies_company ON identity_user_companies(company_id);
