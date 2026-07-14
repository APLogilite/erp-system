-- Table: identity_users
-- Created: V1
-- JPA additions: birth_date, website, employee_id, address (created by ddl-auto=update)
CREATE TABLE identity_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(30),
    avatar_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN DEFAULT FALSE,
    last_login_at TIMESTAMP,
    failed_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    password_changed_at TIMESTAMP,
    birth_date DATE,
    website VARCHAR(500),
    employee_id VARCHAR(50),
    address TEXT
);

CREATE INDEX idx_identity_users_username ON identity_users(username) WHERE is_active = TRUE;
CREATE INDEX idx_identity_users_email ON identity_users(email) WHERE is_active = TRUE;
CREATE INDEX idx_identity_users_status ON identity_users(status);
