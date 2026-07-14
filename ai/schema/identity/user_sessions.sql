-- Table: identity_user_sessions
-- Created: V1
-- Last modified: V30 (tenant_id set to SYS tenant UUID for NULL entries)
CREATE TABLE identity_user_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    user_id UUID NOT NULL REFERENCES identity_users(id),
    token VARCHAR(500) NOT NULL UNIQUE,
    refresh_token VARCHAR(500),
    ip_address VARCHAR(45),
    user_agent TEXT,
    expires_at TIMESTAMP NOT NULL,
    last_activity_at TIMESTAMP,
    tenant_id UUID,
    organization_id UUID,
    company_id UUID
);

CREATE INDEX idx_identity_sessions_user ON identity_user_sessions(user_id);
CREATE INDEX idx_identity_sessions_token ON identity_user_sessions(token);
