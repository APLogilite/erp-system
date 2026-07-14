-- ============================================================
-- PRD-004 / BUG-009 — Seed System Tenant & Admin Role
--
-- Creates the SYS tenant with a fixed UUID so Flyway migrations
-- can reference it for tenant_id. Also creates the sys_admin
-- role with all permissions.
--
-- DEPENDS ON: V1 (identity_tenants, identity_roles tables)
-- ============================================================

-- ============================================================
-- Part 1 — SYS Tenant (fixed UUID for FK references)
-- ============================================================
INSERT INTO identity_tenants (id, code, name, domain, default_language, default_timezone, default_currency, is_active, created_at, updated_at)
SELECT '00000000-0000-0000-0000-000000000001', 'SYS', 'System', 'system.erp.local', 'en', 'UTC', 'USD', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_tenants WHERE code = 'SYS');

-- ============================================================
-- Part 2 — All System Permissions
-- ============================================================
INSERT INTO identity_permissions (id, code, name, resource_type, resource, action, module, is_system, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'user:read',     'View Users',        'user',       '*', 'read',   'identity', true, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_permissions WHERE code = 'user:read');
INSERT INTO identity_permissions (id, code, name, resource_type, resource, action, module, is_system, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'user:create',   'Create Users',      'user',       '*', 'create', 'identity', true, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_permissions WHERE code = 'user:create');
INSERT INTO identity_permissions (id, code, name, resource_type, resource, action, module, is_system, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'user:update',   'Update Users',      'user',       '*', 'update', 'identity', true, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_permissions WHERE code = 'user:update');
INSERT INTO identity_permissions (id, code, name, resource_type, resource, action, module, is_system, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'user:delete',   'Delete Users',      'user',       '*', 'delete', 'identity', true, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_permissions WHERE code = 'user:delete');
INSERT INTO identity_permissions (id, code, name, resource_type, resource, action, module, is_system, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'role:read',     'View Roles',        'role',       '*', 'read',   'identity', true, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_permissions WHERE code = 'role:read');
INSERT INTO identity_permissions (id, code, name, resource_type, resource, action, module, is_system, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'role:create',   'Create Roles',      'role',       '*', 'create', 'identity', true, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_permissions WHERE code = 'role:create');
INSERT INTO identity_permissions (id, code, name, resource_type, resource, action, module, is_system, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'role:update',   'Update Roles',      'role',       '*', 'update', 'identity', true, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_permissions WHERE code = 'role:update');
INSERT INTO identity_permissions (id, code, name, resource_type, resource, action, module, is_system, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'role:delete',   'Delete Roles',      'role',       '*', 'delete', 'identity', true, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_permissions WHERE code = 'role:delete');
INSERT INTO identity_permissions (id, code, name, resource_type, resource, action, module, is_system, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tenant:read',   'View Tenants',      'tenant',     '*', 'read',   'identity', true, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_permissions WHERE code = 'tenant:read');
INSERT INTO identity_permissions (id, code, name, resource_type, resource, action, module, is_system, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tenant:create', 'Create Tenants',    'tenant',     '*', 'create', 'identity', true, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_permissions WHERE code = 'tenant:create');
INSERT INTO identity_permissions (id, code, name, resource_type, resource, action, module, is_system, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'tenant:update', 'Update Tenants',    'tenant',     '*', 'update', 'identity', true, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_permissions WHERE code = 'tenant:update');
INSERT INTO identity_permissions (id, code, name, resource_type, resource, action, module, is_system, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'perm:read',     'View Permissions',  'permission', '*', 'read',   'identity', true, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_permissions WHERE code = 'perm:read');
INSERT INTO identity_permissions (id, code, name, resource_type, resource, action, module, is_system, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'perm:assign',   'Assign Permissions','permission', '*', 'assign', 'identity', true, true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_permissions WHERE code = 'perm:assign');

-- ============================================================
-- Part 3 — sys_admin Role (all permissions)
-- ============================================================
INSERT INTO identity_roles (id, code, name, description, is_system, tenant_id, is_active, created_at, updated_at)
SELECT gen_random_uuid(), 'sys_admin', 'System Administrator', 'Full system access - bypasses all tenant filters', true, '00000000-0000-0000-0000-000000000001', true, now(), now()
WHERE NOT EXISTS (SELECT 1 FROM identity_roles WHERE code = 'sys_admin' AND tenant_id = '00000000-0000-0000-0000-000000000001');

INSERT INTO identity_role_permissions (id, role_id, permission_id, is_active, created_at, updated_at)
SELECT gen_random_uuid(), r.id, p.id, true, now(), now()
FROM identity_roles r, identity_permissions p
WHERE r.code = 'sys_admin' AND r.tenant_id = '00000000-0000-0000-0000-000000000001'
AND NOT EXISTS (SELECT 1 FROM identity_role_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id);
