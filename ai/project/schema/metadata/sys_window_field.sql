-- Table: sys_window_field
-- Created: V24
-- Last modified: V26 (seeded admin fields), V27 (seeded ERP fields), V29 (re-created admin fields), V30 (tenant_id set)
CREATE TABLE sys_window_field (
    id UUID PRIMARY KEY,
    tab_id UUID NOT NULL REFERENCES sys_tab(id),
    column_id UUID NOT NULL REFERENCES sys_column(id),
    seq_no INTEGER NOT NULL DEFAULT 10,
    is_same_line BOOLEAN DEFAULT false,
    num_lines INTEGER DEFAULT 1,
    column_width INTEGER DEFAULT 12,
    is_displayed BOOLEAN DEFAULT true,
    is_readonly BOOLEAN DEFAULT false,
    is_mandatory BOOLEAN DEFAULT false,
    display_logic TEXT,
    readonly_logic TEXT,
    default_value TEXT,
    label_override VARCHAR(200),
    is_active BOOLEAN DEFAULT true,
    tenant_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP,
    UNIQUE (tab_id, seq_no),
    UNIQUE (tab_id, column_id)
);

CREATE INDEX idx_sys_window_field_tab_id ON sys_window_field(tab_id);
CREATE INDEX idx_sys_window_field_column_id ON sys_window_field(column_id);
CREATE INDEX idx_sys_window_field_is_active ON sys_window_field(is_active);
