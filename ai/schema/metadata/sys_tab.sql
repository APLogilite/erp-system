-- Table: sys_tab
-- Created: V24
-- Last modified: V26 (seeded admin tabs), V27 (seeded ERP tabs), V29 (consolidated admin tabs), V30 (tenant_id set)
CREATE TABLE sys_tab (
    id UUID PRIMARY KEY,
    window_id UUID NOT NULL REFERENCES sys_window(id),
    name VARCHAR(100) NOT NULL,
    table_id UUID NOT NULL REFERENCES sys_table(id),
    seq_no INTEGER NOT NULL DEFAULT 10,
    is_single_row BOOLEAN DEFAULT false,
    where_clause TEXT,
    parent_column VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    tenant_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP,
    UNIQUE (window_id, seq_no)
);

CREATE INDEX idx_sys_tab_window_id ON sys_tab(window_id);
CREATE INDEX idx_sys_tab_table_id ON sys_tab(table_id);
CREATE INDEX idx_sys_tab_is_active ON sys_tab(is_active);
