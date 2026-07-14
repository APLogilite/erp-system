-- Table: sys_table
-- Created: V24
-- Last modified: V25 (registered metadata table definitions), V30 (tenant_id set to SYS tenant)
CREATE TABLE sys_table (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    label VARCHAR(100) NOT NULL,
    plural_label VARCHAR(100),
    table_type VARCHAR(20) NOT NULL DEFAULT 'dynamic',
    table_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    tenant_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_sys_table_name ON sys_table(name);
CREATE INDEX idx_sys_table_is_active ON sys_table(is_active);
