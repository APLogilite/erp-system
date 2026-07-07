-- ERP System Full Database Schema
-- Copy this file to db-setup.sql, update password, then run as PostgreSQL superuser:
--   psql -U postgres -f db-setup.sql
--
-- NOTE: Hibernate's ddl-auto=update handles schema changes at runtime.
-- This file exists for bootstrapping a fresh DB and as a reference.
-- If you add entities or columns, update the Java entities (not this file) and let Hibernate handle it.

-- Create database
CREATE DATABASE erp_db
    WITH OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TEMPLATE = template0;

-- Create user (CHANGE PASSWORD HERE)
CREATE USER erp_user WITH PASSWORD 'CHANGE_THIS_PASSWORD';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE erp_db TO erp_user;

-- Connect to the database
\c erp_db;

-- Enable UUID extension (required for the application)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Grant permissions on future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO erp_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO erp_user;

-- Grant permissions on existing tables
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO erp_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO erp_user;

-- ============================================================
-- TABLES
-- ============================================================

-- 1. Tenants (root of the hierarchy)
CREATE TABLE identity_tenants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    domain VARCHAR(255),
    logo_url VARCHAR(500),
    default_language VARCHAR(10),
    default_timezone VARCHAR(50),
    default_currency VARCHAR(3),
    CONSTRAINT uk_tenants_code UNIQUE (code)
);

-- 2. Organizations (tenant-scoped)
CREATE TABLE identity_organizations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    tenant_id UUID NOT NULL REFERENCES identity_tenants(id),
    parent_id UUID REFERENCES identity_organizations(id),
    level INTEGER NOT NULL DEFAULT 0,
    path VARCHAR(500),
    CONSTRAINT uk_organizations_code UNIQUE (code)
);

-- 3. Companies (organization-scoped, tenant-scoped)
CREATE TABLE identity_companies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    tax_id VARCHAR(50),
    registration_number VARCHAR(50),
    address TEXT,
    phone VARCHAR(30),
    email VARCHAR(255),
    currency VARCHAR(3),
    organization_id UUID NOT NULL REFERENCES identity_organizations(id),
    tenant_id UUID NOT NULL REFERENCES identity_tenants(id),
    CONSTRAINT uk_companies_code UNIQUE (code)
);

-- 4. Branches (company-scoped, tenant-scoped)
CREATE TABLE identity_branches (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    phone VARCHAR(30),
    email VARCHAR(255),
    is_head_office BOOLEAN DEFAULT FALSE,
    company_id UUID NOT NULL REFERENCES identity_companies(id),
    tenant_id UUID NOT NULL REFERENCES identity_tenants(id),
    CONSTRAINT uk_branches_code UNIQUE (code)
);

-- 5. Departments (branch-scoped, tenant-scoped)
CREATE TABLE identity_departments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    cost_center VARCHAR(50),
    branch_id UUID NOT NULL REFERENCES identity_branches(id),
    parent_id UUID REFERENCES identity_departments(id),
    tenant_id UUID NOT NULL REFERENCES identity_tenants(id),
    level INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT uk_departments_code UNIQUE (code)
);

-- 6. Roles (optionally tenant-scoped; null tenant_id = global/system role)
CREATE TABLE identity_roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_system BOOLEAN DEFAULT FALSE,
    tenant_id UUID REFERENCES identity_tenants(id),
    CONSTRAINT uk_roles_code UNIQUE (code)
);

-- 7. Users (accounts, no tenant scope — users are global)
CREATE TABLE identity_users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
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
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email)
);

-- 8. User sessions (context-aware: tenant, org, company can be selected)
CREATE TABLE identity_user_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    user_id UUID NOT NULL REFERENCES identity_users(id),
    token VARCHAR(500) NOT NULL,
    refresh_token VARCHAR(500),
    ip_address VARCHAR(45),
    user_agent TEXT,
    expires_at TIMESTAMP NOT NULL,
    last_activity_at TIMESTAMP,
    tenant_id UUID,
    organization_id UUID,
    company_id UUID,
    CONSTRAINT uk_sessions_token UNIQUE (token)
);

-- 9. User-Role assignments
CREATE TABLE identity_user_roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    user_id UUID NOT NULL REFERENCES identity_users(id),
    role_id UUID NOT NULL REFERENCES identity_roles(id),
    CONSTRAINT uk_user_roles UNIQUE (user_id, role_id)
);

-- 10. User-Organization assignments
CREATE TABLE identity_user_organizations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    user_id UUID NOT NULL REFERENCES identity_users(id),
    organization_id UUID NOT NULL REFERENCES identity_organizations(id),
    CONSTRAINT uk_user_organizations UNIQUE (user_id, organization_id)
);

-- 11. User-Company assignments
CREATE TABLE identity_user_companies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    user_id UUID NOT NULL REFERENCES identity_users(id),
    company_id UUID NOT NULL REFERENCES identity_companies(id),
    is_default BOOLEAN DEFAULT FALSE,
    CONSTRAINT uk_user_companies UNIQUE (user_id, company_id)
);

-- 12. User preferences
CREATE TABLE identity_user_preferences (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    user_id UUID NOT NULL UNIQUE REFERENCES identity_users(id),
    language VARCHAR(10) DEFAULT 'en',
    timezone VARCHAR(50) DEFAULT 'UTC',
    date_format VARCHAR(20) DEFAULT 'YYYY-MM-DD',
    time_format VARCHAR(20) DEFAULT 'HH:mm',
    number_format VARCHAR(20) DEFAULT '#,##0.00',
    currency VARCHAR(3) DEFAULT 'USD',
    theme VARCHAR(20) DEFAULT 'light',
    notifications_enabled BOOLEAN DEFAULT TRUE,
    items_per_page INTEGER DEFAULT 25
);

-- 13. Permissions
CREATE TABLE identity_permissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    resource_type VARCHAR(50) NOT NULL,
    resource VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    is_system BOOLEAN DEFAULT FALSE,
    module VARCHAR(50),
    CONSTRAINT uk_permissions_code UNIQUE (code)
);

-- 14. Role-Permission assignments
CREATE TABLE identity_role_permissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    role_id UUID NOT NULL REFERENCES identity_roles(id),
    permission_id UUID NOT NULL REFERENCES identity_permissions(id),
    CONSTRAINT uk_role_permissions UNIQUE (role_id, permission_id)
);

-- 15. Audit records
CREATE TABLE identity_audit_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
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
    occurred_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ============================================================
-- INDEXES (for performance)
-- ============================================================

-- Tenant filter indexes
CREATE INDEX idx_organizations_tenant_id ON identity_organizations(tenant_id);
CREATE INDEX idx_companies_tenant_id ON identity_companies(tenant_id);
CREATE INDEX idx_branches_tenant_id ON identity_branches(tenant_id);
CREATE INDEX idx_departments_tenant_id ON identity_departments(tenant_id);
CREATE INDEX idx_roles_tenant_id ON identity_roles(tenant_id);

-- Parent hierarchy indexes
CREATE INDEX idx_organizations_parent_id ON identity_organizations(parent_id);
CREATE INDEX idx_departments_parent_id ON identity_departments(parent_id);
CREATE INDEX idx_departments_branch_id ON identity_departments(branch_id);
CREATE INDEX idx_branches_company_id ON identity_branches(company_id);
CREATE INDEX idx_companies_organization_id ON identity_companies(organization_id);

-- User assignment indexes
CREATE INDEX idx_user_roles_user_id ON identity_user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON identity_user_roles(role_id);
CREATE INDEX idx_user_organizations_user_id ON identity_user_organizations(user_id);
CREATE INDEX idx_user_organizations_org_id ON identity_user_organizations(organization_id);
CREATE INDEX idx_user_companies_user_id ON identity_user_companies(user_id);
CREATE INDEX idx_user_companies_company_id ON identity_user_companies(company_id);

-- Session indexes
CREATE INDEX idx_user_sessions_user_id ON identity_user_sessions(user_id);
CREATE INDEX idx_user_sessions_token ON identity_user_sessions(token);
CREATE INDEX idx_user_sessions_expires_at ON identity_user_sessions(expires_at);

-- Permission indexes
CREATE INDEX idx_role_permissions_role_id ON identity_role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON identity_role_permissions(permission_id);

-- Audit indexes
CREATE INDEX idx_audit_records_event_type ON identity_audit_records(event_type);
CREATE INDEX idx_audit_records_user_id ON identity_audit_records(user_id);
CREATE INDEX idx_audit_records_occurred_at ON identity_audit_records(occurred_at DESC);

-- Soft-delete + active lookup indexes
CREATE INDEX idx_tenants_active ON identity_tenants(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_organizations_active ON identity_organizations(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_companies_active ON identity_companies(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_branches_active ON identity_branches(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_departments_active ON identity_departments(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_roles_active ON identity_roles(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_users_active ON identity_users(is_active) WHERE is_active = TRUE;

-- Verify setup
SELECT 'Database schema created successfully!' as status;
