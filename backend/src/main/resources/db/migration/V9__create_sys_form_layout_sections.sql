-- ============================================================
-- METADATA — sys_form_layout_sections
-- Layout sections for organizing form fields
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_form_layout_sections (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    form_id UUID NOT NULL REFERENCES sys_metadata_views(id) ON DELETE CASCADE,
    code VARCHAR(100) NOT NULL,
    label VARCHAR(200) NOT NULL,
    collapsible BOOLEAN DEFAULT FALSE,
    columns INTEGER DEFAULT 1,
    position INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sys_form_layout_sections_form ON sys_form_layout_sections(form_id);
