-- ============================================================
-- METADATA — sys_form_section_fields
-- Maps fields to layout sections
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_form_section_fields (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    section_id UUID NOT NULL REFERENCES sys_form_layout_sections(id) ON DELETE CASCADE,
    field_id UUID NOT NULL REFERENCES sys_form_fields(id) ON DELETE CASCADE,
    position INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP,
    UNIQUE (section_id, field_id),
    UNIQUE (field_id)
);

CREATE INDEX IF NOT EXISTS idx_sys_form_section_fields_section ON sys_form_section_fields(section_id);
CREATE INDEX IF NOT EXISTS idx_sys_form_section_fields_field ON sys_form_section_fields(field_id);
