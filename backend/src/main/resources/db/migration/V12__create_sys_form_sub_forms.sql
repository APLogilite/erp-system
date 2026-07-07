-- ============================================================
-- METADATA — sys_form_sub_forms
-- Sub-form tab references for multi-level nested forms
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_form_sub_forms (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    parent_form_id UUID NOT NULL REFERENCES sys_metadata_views(id) ON DELETE CASCADE,
    relation_code VARCHAR(100) NOT NULL,
    child_form_code VARCHAR(100) NOT NULL,
    label VARCHAR(200) NOT NULL,
    display_as VARCHAR(50) DEFAULT 'tab',
    position INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sys_form_sub_forms_parent ON sys_form_sub_forms(parent_form_id);
CREATE INDEX IF NOT EXISTS idx_sys_form_sub_forms_child ON sys_form_sub_forms(child_form_code);
