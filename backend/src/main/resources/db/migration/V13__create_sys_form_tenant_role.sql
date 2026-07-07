-- ============================================================
-- METADATA — sys_form_tenant_role
-- Per-tenant role assignments for form access
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_form_tenant_role (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    form_id UUID NOT NULL REFERENCES sys_metadata_views(id) ON DELETE CASCADE,
    tenant_id UUID NOT NULL,
    role_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    UNIQUE (form_id, tenant_id, role_id)
);

CREATE INDEX IF NOT EXISTS idx_sys_form_tenant_role_form ON sys_form_tenant_role(form_id);
CREATE INDEX IF NOT EXISTS idx_sys_form_tenant_role_tenant ON sys_form_tenant_role(tenant_id);
CREATE INDEX IF NOT EXISTS idx_sys_form_tenant_role_role ON sys_form_tenant_role(role_id);
