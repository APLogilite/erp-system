-- Table: identity_user_roles
-- Created: V1
CREATE TABLE identity_user_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    user_id UUID NOT NULL REFERENCES identity_users(id),
    role_id UUID NOT NULL REFERENCES identity_roles(id),
    UNIQUE(user_id, role_id)
);

CREATE INDEX idx_identity_user_roles_user ON identity_user_roles(user_id);
CREATE INDEX idx_identity_user_roles_role ON identity_user_roles(role_id);
