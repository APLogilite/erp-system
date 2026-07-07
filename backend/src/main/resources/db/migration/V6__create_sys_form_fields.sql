-- ============================================================
-- METADATA — sys_form_fields
-- Normalized storage for form field configurations
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_form_fields (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    form_id UUID NOT NULL REFERENCES sys_metadata_views(id) ON DELETE CASCADE,
    column_code VARCHAR(100) NOT NULL,
    label_override VARCHAR(200),
    visible BOOLEAN DEFAULT TRUE,
    read_only BOOLEAN DEFAULT FALSE,
    required BOOLEAN DEFAULT FALSE,
    position INTEGER NOT NULL,
    default_value TEXT,
    placeholder VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP,
    UNIQUE (form_id, column_code)
);

CREATE INDEX IF NOT EXISTS idx_sys_form_fields_form ON sys_form_fields(form_id);
CREATE INDEX IF NOT EXISTS idx_sys_form_fields_column ON sys_form_fields(column_code);
CREATE INDEX IF NOT EXISTS idx_sys_form_fields_active ON sys_form_fields(is_active) WHERE is_active = TRUE;
