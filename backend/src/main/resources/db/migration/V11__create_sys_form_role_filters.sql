-- ============================================================
-- METADATA — sys_form_role_filters
-- Role-based row-level data filters
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_form_role_filters (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    form_id UUID NOT NULL REFERENCES sys_metadata_views(id) ON DELETE CASCADE,
    role_id UUID NOT NULL,
    condition_field VARCHAR(100) NOT NULL,
    condition_operator VARCHAR(50) NOT NULL,
    condition_value VARCHAR(255),
    position INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sys_form_role_filters_form ON sys_form_role_filters(form_id);
CREATE INDEX IF NOT EXISTS idx_sys_form_role_filters_role ON sys_form_role_filters(role_id);
CREATE INDEX IF NOT EXISTS idx_sys_form_role_filters_form_role ON sys_form_role_filters(form_id, role_id);
