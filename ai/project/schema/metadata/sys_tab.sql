-- Table: sys_tab
-- Created: V3 (consolidated metadata schema)
-- Modified: V6 (renamed parent_column → parent_link_column_id, type VARCHAR→UUID)
CREATE TABLE sys_tab (
    id UUID PRIMARY KEY,
    window_id UUID NOT NULL REFERENCES sys_window(id),
    name VARCHAR(100) NOT NULL,
    table_id UUID NOT NULL REFERENCES sys_table(id),
    seq_no INTEGER NOT NULL DEFAULT 10,
    is_single_row BOOLEAN DEFAULT false,
    where_clause TEXT,
    parent_link_column_id UUID REFERENCES sys_column(id),
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
CREATE INDEX idx_sys_tab_parent_link ON sys_tab(parent_link_column_id);
