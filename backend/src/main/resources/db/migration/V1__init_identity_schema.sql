CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- IDENTITY PLATFORM — CORE SCHEMA
-- ============================================================

-- Tenant: top-level multi-tenant isolation
CREATE TABLE identity_tenants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    domain VARCHAR(255),
    logo_url VARCHAR(500),
    default_language VARCHAR(10),
    default_timezone VARCHAR(50),
    default_currency VARCHAR(3)
);
CREATE INDEX idx_identity_tenants_domain ON identity_tenants(domain) WHERE is_active = TRUE;
CREATE INDEX idx_identity_tenants_code ON identity_tenants(code);

-- Organization: hierarchical org structure within a tenant
CREATE TABLE identity_organizations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    tenant_id UUID NOT NULL REFERENCES identity_tenants(id),
    parent_id UUID REFERENCES identity_organizations(id),
    level INTEGER NOT NULL DEFAULT 0,
    path VARCHAR(500)
);
CREATE INDEX idx_identity_orgs_tenant ON identity_organizations(tenant_id);
CREATE INDEX idx_identity_orgs_parent ON identity_organizations(parent_id);

-- Company: legal entity within an organization
CREATE TABLE identity_companies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    tax_id VARCHAR(50),
    registration_number VARCHAR(50),
    address TEXT,
    phone VARCHAR(30),
    email VARCHAR(255),
    currency VARCHAR(3),
    organization_id UUID NOT NULL REFERENCES identity_organizations(id)
);
CREATE INDEX idx_identity_companies_org ON identity_companies(organization_id);

-- Branch: physical location within a company
CREATE TABLE identity_branches (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
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

-- Department: hierarchical dept structure within a branch
CREATE TABLE identity_departments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
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

-- UserAccount: core user identity
CREATE TABLE identity_users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
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
    password_changed_at TIMESTAMP
);
CREATE INDEX idx_identity_users_username ON identity_users(username) WHERE is_active = TRUE;
CREATE INDEX idx_identity_users_email ON identity_users(email) WHERE is_active = TRUE;
CREATE INDEX idx_identity_users_status ON identity_users(status);

-- Role: named collection of permissions
CREATE TABLE identity_roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_system BOOLEAN DEFAULT FALSE,
    tenant_id UUID REFERENCES identity_tenants(id),
    UNIQUE (code, tenant_id)
);
CREATE INDEX idx_identity_roles_code ON identity_roles(code);

-- Permission: atomic access right
CREATE TABLE identity_permissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
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

-- Join: User ←→ Role
CREATE TABLE identity_user_roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
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

-- Join: Role ←→ Permission
CREATE TABLE identity_role_permissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
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

-- Join: User ←→ Organization
CREATE TABLE identity_user_organizations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    user_id UUID NOT NULL REFERENCES identity_users(id),
    organization_id UUID NOT NULL REFERENCES identity_organizations(id),
    UNIQUE(user_id, organization_id)
);
CREATE INDEX idx_identity_user_orgs_user ON identity_user_organizations(user_id);
CREATE INDEX idx_identity_user_orgs_org ON identity_user_organizations(organization_id);

-- Join: User ←→ Company (with default flag)
CREATE TABLE identity_user_companies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    user_id UUID NOT NULL REFERENCES identity_users(id),
    company_id UUID NOT NULL REFERENCES identity_companies(id),
    is_default BOOLEAN DEFAULT FALSE,
    UNIQUE(user_id, company_id)
);
CREATE INDEX idx_identity_user_companies_user ON identity_user_companies(user_id);
CREATE INDEX idx_identity_user_companies_company ON identity_user_companies(company_id);

-- UserSession: authentication sessions
CREATE TABLE identity_user_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
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

-- UserPreference: per-user settings (one-to-one with User)
CREATE TABLE identity_user_preferences (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
CREATE INDEX idx_identity_prefs_user ON identity_user_preferences(user_id);
