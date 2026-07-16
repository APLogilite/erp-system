-- Table: sys_window
-- Created: V24
-- Last modified: V26 (seeded admin windows), V27 (seeded ERP windows), V29 (consolidated admin windows), V30 (tenant_id set)
CREATE TABLE sys_window (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    table_id UUID NOT NULL REFERENCES sys_table(id),
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    tenant_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_sys_window_name ON sys_window(name);
CREATE INDEX idx_sys_window_table_id ON sys_window(table_id);
CREATE INDEX idx_sys_window_is_active ON sys_window(is_active);
