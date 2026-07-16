-- ============================================================
-- V3 — Metadata Schema: Table/Column/Window/Tab/Field/Access/Menu
-- ============================================================

-- ============================================================
-- Part 1 — Layer 1: Database Schema
-- ============================================================

-- sys_table: Table definitions
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

-- sys_column: Column definitions
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
    is_display_column BOOLEAN DEFAULT false,
    filter_where_clause TEXT,
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

-- ============================================================
-- Part 2 — Layer 2: Window Design
-- ============================================================

-- sys_window: Window definitions
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
CREATE INDEX idx_sys_window_is_active ON sys_window(is_active);

-- sys_tab: Tab definitions within a window
CREATE TABLE sys_tab (
    id UUID PRIMARY KEY,
    window_id UUID NOT NULL REFERENCES sys_window(id),
    name VARCHAR(100) NOT NULL,
    table_id UUID NOT NULL REFERENCES sys_table(id),
    seq_no INTEGER NOT NULL,
    is_single_row BOOLEAN DEFAULT false,
    where_clause TEXT,
    parent_column VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    tenant_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);
CREATE INDEX idx_sys_tab_window ON sys_tab(window_id);
CREATE INDEX idx_sys_tab_is_active ON sys_tab(is_active);

-- sys_window_field: Field definitions within a tab
CREATE TABLE sys_window_field (
    id UUID PRIMARY KEY,
    tab_id UUID NOT NULL REFERENCES sys_tab(id),
    column_id UUID NOT NULL REFERENCES sys_column(id),
    seq_no INTEGER NOT NULL,
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
    filter_where_clause TEXT,
    is_active BOOLEAN DEFAULT true,
    tenant_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);
CREATE INDEX idx_sys_window_field_tab ON sys_window_field(tab_id);
CREATE INDEX idx_sys_window_field_is_active ON sys_window_field(is_active);

-- sys_window_access: Role-based window access control
CREATE TABLE sys_window_access (
    id UUID PRIMARY KEY,
    window_id UUID NOT NULL REFERENCES sys_window(id),
    tenant_id UUID,
    role_id UUID,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP,
    UNIQUE (window_id, tenant_id, role_id)
);
CREATE INDEX idx_sys_window_access_window ON sys_window_access(window_id);

-- ============================================================
-- Part 3 — Layer 3: Menu System
-- ============================================================

-- sys_menu: Hierarchical menu entries
CREATE TABLE sys_menu (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    parent_id UUID REFERENCES sys_menu(id),
    window_id UUID REFERENCES sys_window(id),
    seq_no INTEGER NOT NULL,
    icon VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    tenant_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);
CREATE INDEX idx_sys_menu_parent ON sys_menu(parent_id);
CREATE INDEX idx_sys_menu_is_active ON sys_menu(is_active);
