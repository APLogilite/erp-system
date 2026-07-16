-- Table: identity_departments
-- Created: V1
CREATE TABLE identity_departments (
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
    cost_center VARCHAR(50),
    branch_id UUID NOT NULL REFERENCES identity_branches(id),
    parent_id UUID REFERENCES identity_departments(id),
    level INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_identity_depts_branch ON identity_departments(branch_id);
CREATE INDEX idx_identity_depts_parent ON identity_departments(parent_id);
