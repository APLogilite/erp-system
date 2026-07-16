-- Table: sys_column
-- Created: V24
-- Last modified: V25 (registered column definitions), V30 (tenant_id set to SYS tenant)
CREATE TABLE sys_column (
    id UUID PRIMARY KEY,
    table_id UUID NOT NULL REFERENCES sys_table(id),
    code VARCHAR(100) NOT NULL,
    label VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    required BOOLEAN DEFAULT false,
    default_value TEXT,
    max_length INTEGER,
    precision INTEGER,
    scale INTEGER,
    relation_table VARCHAR(100),
    enum_options JSONB,
    position INTEGER,
    is_active BOOLEAN DEFAULT true,
    tenant_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP,
    UNIQUE (table_id, code)
);

CREATE INDEX idx_sys_column_table_id ON sys_column(table_id);
CREATE INDEX idx_sys_column_is_active ON sys_column(is_active);
