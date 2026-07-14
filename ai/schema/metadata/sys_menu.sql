-- Table: sys_menu
-- Created: V24
-- Last modified: V28 (seeded full menu tree), V29 (re-added 3 admin menu items), V30 (tenant_id set)
CREATE TABLE sys_menu (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    parent_id UUID REFERENCES sys_menu(id),
    window_id UUID REFERENCES sys_window(id),
    seq_no INTEGER NOT NULL DEFAULT 10,
    icon VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    tenant_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_sys_menu_parent_id ON sys_menu(parent_id);
CREATE INDEX idx_sys_menu_window_id ON sys_menu(window_id);
CREATE INDEX idx_sys_menu_type ON sys_menu(type);
CREATE INDEX idx_sys_menu_is_active ON sys_menu(is_active);
