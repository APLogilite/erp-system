-- ============================================================
-- METADATA — sys_form_field_rules
-- Per-field condition/action rules
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_form_field_rules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    field_id UUID NOT NULL REFERENCES sys_form_fields(id) ON DELETE CASCADE,
    condition_field VARCHAR(100) NOT NULL,
    condition_operator VARCHAR(50) NOT NULL,
    condition_value VARCHAR(255),
    action VARCHAR(50) NOT NULL,
    logic_group INTEGER DEFAULT 0,
    position INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_sys_form_field_rules_field ON sys_form_field_rules(field_id);
CREATE INDEX IF NOT EXISTS idx_sys_form_field_rules_condition_field ON sys_form_field_rules(condition_field);
