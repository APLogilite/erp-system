-- ============================================================
-- V1 — Identity Platform: Tables + SYS Tenant + Audit
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================
-- Part 1 — Tenant (top-level multi-tenant isolation)
-- ============================================================
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

-- ============================================================
-- Part 2 — Organization hierarchy
-- ============================================================
CREATE TABLE identity_organizations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    tenant_id UUID NOT NULL REFERENCES identity_tenants(id),
    parent_id UUID REFERENCES identity_organizations(id),
    level INTEGER DEFAULT 0,
    path TEXT,
    UNIQUE (code, tenant_id)
);
CREATE INDEX idx_identity_orgs_tenant ON identity_organizations(tenant_id) WHERE is_active = TRUE;
CREATE INDEX idx_identity_orgs_parent ON identity_organizations(parent_id);

-- ============================================================
-- Part 3 — Company
-- ============================================================
CREATE TABLE identity_companies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    organization_id UUID NOT NULL REFERENCES identity_organizations(id),
    tenant_id UUID NOT NULL REFERENCES identity_tenants(id),
    tax_id VARCHAR(50),
    registration_number VARCHAR(100),
    currency VARCHAR(3) DEFAULT 'USD',
    address TEXT,
    UNIQUE (code, tenant_id)
);
CREATE INDEX idx_identity_companies_org ON identity_companies(organization_id) WHERE is_active = TRUE;
CREATE INDEX idx_identity_companies_tenant ON identity_companies(tenant_id);

-- ============================================================
-- Part 4 — Branch
-- ============================================================
CREATE TABLE identity_branches (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    company_id UUID NOT NULL REFERENCES identity_companies(id),
    tenant_id UUID NOT NULL REFERENCES identity_tenants(id),
    is_head_office BOOLEAN DEFAULT FALSE,
    UNIQUE (code, tenant_id)
);
CREATE INDEX idx_identity_branches_company ON identity_branches(company_id) WHERE is_active = TRUE;
CREATE INDEX idx_identity_branches_tenant ON identity_branches(tenant_id);

-- ============================================================
-- Part 5 — Department
-- ============================================================
CREATE TABLE identity_departments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    branch_id UUID REFERENCES identity_branches(id),
    tenant_id UUID NOT NULL REFERENCES identity_tenants(id),
    cost_center VARCHAR(50),
    manager_id UUID,
    parent_dept_id UUID REFERENCES identity_departments(id),
    level INTEGER DEFAULT 0,
    UNIQUE (code, tenant_id)
);
CREATE INDEX idx_identity_depts_branch ON identity_departments(branch_id) WHERE is_active = TRUE;
CREATE INDEX idx_identity_depts_tenant ON identity_departments(tenant_id);

-- ============================================================
-- Part 6 — User
-- ============================================================
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
    status VARCHAR(30) DEFAULT 'ACTIVE',
    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,
    failed_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    last_login TIMESTAMP,
    last_password_change TIMESTAMP,
    password_history TEXT,
    birth_date DATE,
    gender VARCHAR(10),
    avatar_url VARCHAR(500),
    notes TEXT
);
CREATE INDEX idx_identity_users_username ON identity_users(username) WHERE is_active = TRUE;
CREATE INDEX idx_identity_users_email ON identity_users(email) WHERE is_active = TRUE;
CREATE INDEX idx_identity_users_status ON identity_users(status);

-- ============================================================
-- Part 7 — Role
-- ============================================================
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
CREATE INDEX idx_identity_roles_tenant ON identity_roles(tenant_id);

-- ============================================================
-- Part 8 — Permission
-- ============================================================
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
    resource VARCHAR(100) NOT NULL DEFAULT '*',
    action VARCHAR(50) NOT NULL,
    module VARCHAR(50),
    is_system BOOLEAN DEFAULT FALSE
);
CREATE INDEX idx_identity_permissions_code ON identity_permissions(code);

-- ============================================================
-- Part 9 — User-Role assignment
-- ============================================================
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
    UNIQUE (user_id, role_id)
);
CREATE INDEX idx_identity_user_roles_user ON identity_user_roles(user_id);
CREATE INDEX idx_identity_user_roles_role ON identity_user_roles(role_id);

-- ============================================================
-- Part 10 — Role-Permission assignment
-- ============================================================
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
    UNIQUE (role_id, permission_id)
);
CREATE INDEX idx_identity_role_perms_role ON identity_role_permissions(role_id);
CREATE INDEX idx_identity_role_perms_perm ON identity_role_permissions(permission_id);

-- ============================================================
-- Part 11 — Role scoping (Organization, Company, Branch)
-- ============================================================
CREATE TABLE identity_role_organizations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    role_id UUID NOT NULL REFERENCES identity_roles(id),
    organization_id UUID NOT NULL REFERENCES identity_organizations(id),
    UNIQUE (role_id, organization_id)
);
CREATE INDEX idx_identity_role_orgs_role ON identity_role_organizations(role_id);
CREATE INDEX idx_identity_role_orgs_org ON identity_role_organizations(organization_id);

CREATE TABLE identity_role_companies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    role_id UUID NOT NULL REFERENCES identity_roles(id),
    company_id UUID NOT NULL REFERENCES identity_companies(id),
    UNIQUE (role_id, company_id)
);
CREATE INDEX idx_identity_role_cos_role ON identity_role_companies(role_id);
CREATE INDEX idx_identity_role_cos_co ON identity_role_companies(company_id);

CREATE TABLE identity_role_branches (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted_at TIMESTAMP,
    role_id UUID NOT NULL REFERENCES identity_roles(id),
    branch_id UUID NOT NULL REFERENCES identity_branches(id),
    UNIQUE (role_id, branch_id)
);
CREATE INDEX idx_identity_role_brs_role ON identity_role_branches(role_id);
CREATE INDEX idx_identity_role_brs_br ON identity_role_branches(branch_id);

-- ============================================================
-- Part 12 — User sessions
-- ============================================================
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
    device_id VARCHAR(255),
    device_type VARCHAR(50),
    location VARCHAR(255),
    expires_at TIMESTAMP NOT NULL,
    last_activity TIMESTAMP,
    is_revoked BOOLEAN DEFAULT FALSE
);
CREATE INDEX idx_identity_sessions_user ON identity_user_sessions(user_id);
CREATE INDEX idx_identity_sessions_token ON identity_user_sessions(token);

-- ============================================================
-- Part 13 — User preferences
-- ============================================================
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
    items_per_page INTEGER DEFAULT 25,
    active_tenant_id UUID,
    active_organization_id UUID,
    active_company_id UUID,
    active_branch_id UUID,
    active_department_id UUID,
    active_role_code VARCHAR(50)
);

-- ============================================================
-- Part 14 — Audit records
-- ============================================================
CREATE TABLE identity_audit_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
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

-- ============================================================
-- Part 15 — SYS tenant (fixed UUID for FK references in seed data)
-- ============================================================
INSERT INTO identity_tenants (id, code, name, domain, default_language, default_timezone, default_currency, is_active, created_at, updated_at)
SELECT '00000000-0000-0000-0000-000000000001', 'SYS', 'System', 'system.erp.local', 'en', 'UTC', 'USD', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_tenants WHERE code = 'SYS');
