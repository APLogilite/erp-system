-- ============================================================
-- METADATA — sys_form_field_validations
-- Per-field validation constraints
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_form_field_validations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    field_id UUID NOT NULL REFERENCES sys_form_fields(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    value VARCHAR(255),
    message VARCHAR(500),
    position INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sys_form_field_validations_field ON sys_form_field_validations(field_id);
CREATE INDEX IF NOT EXISTS idx_sys_form_field_validations_type ON sys_form_field_validations(type);
