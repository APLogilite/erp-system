-- Table: identity_role_permissions
-- Created: V1
-- Last modified: V31 (seeded all permissions for sys_admin role)
CREATE TABLE identity_role_permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    role_id UUID NOT NULL REFERENCES identity_roles(id),
    permission_id UUID NOT NULL REFERENCES identity_permissions(id),
    UNIQUE(role_id, permission_id)
);

CREATE INDEX idx_identity_role_perms_role ON identity_role_permissions(role_id);
CREATE INDEX idx_identity_role_perms_perm ON identity_role_permissions(permission_id);
