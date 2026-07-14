-- Table: identity_permissions
-- Created: V1
-- Last modified: V31 (seeded 12 system permissions for user/role/tenant/perm)
CREATE TABLE identity_permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    resource_type VARCHAR(50) NOT NULL,
    resource VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    is_system BOOLEAN DEFAULT FALSE,
    module VARCHAR(50)
);

CREATE INDEX idx_identity_permissions_code ON identity_permissions(code);
CREATE INDEX idx_identity_permissions_resource ON identity_permissions(resource_type, resource);
