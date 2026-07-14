-- Table: identity_branches
-- Created: V1
CREATE TABLE identity_branches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    phone VARCHAR(30),
    email VARCHAR(255),
    is_head_office BOOLEAN DEFAULT FALSE,
    company_id UUID NOT NULL REFERENCES identity_companies(id)
);

CREATE INDEX idx_identity_branches_company ON identity_branches(company_id);
