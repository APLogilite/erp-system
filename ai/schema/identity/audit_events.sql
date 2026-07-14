-- Table: identity_audit_records
-- Created: V2
CREATE TABLE identity_audit_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    event_type VARCHAR(50) NOT NULL,
    user_id UUID,
    username VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent TEXT,
    session_id UUID,
    old_value TEXT,
    new_value TEXT,
    occurred_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_identity_audit_user ON identity_audit_records(user_id, occurred_at DESC);
CREATE INDEX idx_identity_audit_username ON identity_audit_records(username, occurred_at DESC);
CREATE INDEX idx_identity_audit_type ON identity_audit_records(event_type, occurred_at DESC);
CREATE INDEX idx_identity_audit_date ON identity_audit_records(occurred_at DESC);
