-- ============================================================
-- METADATA — sys_table_columns
-- Normalized storage for table column definitions
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_table_columns (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    table_id UUID NOT NULL REFERENCES sys_metadata_models(id) ON DELETE CASCADE,
    code VARCHAR(100) NOT NULL,
    label VARCHAR(200) NOT NULL,
    type VARCHAR(50) NOT NULL,
    required BOOLEAN DEFAULT FALSE,
    default_value TEXT,
    max_length INTEGER,
    precision INTEGER,
    scale INTEGER,
    relation_table VARCHAR(100),
    enum_options JSONB,
    position INTEGER NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP,
    UNIQUE (table_id, code)
);

CREATE INDEX IF NOT EXISTS idx_sys_table_columns_table ON sys_table_columns(table_id);
CREATE INDEX IF NOT EXISTS idx_sys_table_columns_code ON sys_table_columns(code);
CREATE INDEX IF NOT EXISTS idx_sys_table_columns_type ON sys_table_columns(type);
CREATE INDEX IF NOT EXISTS idx_sys_table_columns_active ON sys_table_columns(is_active) WHERE is_active = TRUE;
