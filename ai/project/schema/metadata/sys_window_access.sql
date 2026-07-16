-- Table: sys_window_access
-- Created: V24
-- Last modified: V28 (seeded access for sys_admin role), V29 (re-seeded for consolidated windows), V30 (tenant_id set)
CREATE TABLE sys_window_access (
    id UUID PRIMARY KEY,
    window_id UUID NOT NULL REFERENCES sys_window(id),
    tenant_id UUID,
    role_id UUID NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP,
    UNIQUE (window_id, tenant_id, role_id)
);

CREATE INDEX idx_sys_window_access_window_id ON sys_window_access(window_id);
CREATE INDEX idx_sys_window_access_tenant_id ON sys_window_access(tenant_id);
CREATE INDEX idx_sys_window_access_role_id ON sys_window_access(role_id);
CREATE INDEX idx_sys_window_access_is_active ON sys_window_access(is_active);
