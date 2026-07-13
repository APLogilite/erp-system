-- ============================================================
-- PRD-004 / TASK-036 — Drop Old Metadata Schema, Create New
--
-- Since the platform is still in initial development with no
-- production data, drop the old PRD-001 metadata schema and
-- create the new iDempiere-inspired three-layer design:
--   1. Database Schema (sys_table / sys_column)
--   2. Window Design (sys_window / sys_tab / sys_window_field / sys_window_access)
--   3. Menu (sys_menu)
--
-- DEPENDS ON: Nothing (replaces all old metadata)
-- IMPORTANT: Set spring.flyway.enabled=true before running.
-- ============================================================

-- ============================================================
-- Part 1 — Drop Old Metadata Tables (FK-safe order)
-- ============================================================

DROP TABLE IF EXISTS sys_form_role_filters CASCADE;
DROP TABLE IF EXISTS sys_form_section_fields CASCADE;
DROP TABLE IF EXISTS sys_form_layout_sections CASCADE;
DROP TABLE IF EXISTS sys_form_field_validations CASCADE;
DROP TABLE IF EXISTS sys_form_field_rules CASCADE;
DROP TABLE IF EXISTS sys_form_tenant_role CASCADE;
DROP TABLE IF EXISTS sys_form_fields CASCADE;
DROP TABLE IF EXISTS sys_form_sub_forms CASCADE;
DROP TABLE IF EXISTS sys_metadata_views CASCADE;
DROP TABLE IF EXISTS sys_table_columns CASCADE;
DROP TABLE IF EXISTS sys_metadata_models CASCADE;

-- Drop the old form bundle cache table if it exists
DROP TABLE IF EXISTS sys_form_bundle_cache CASCADE;

-- ============================================================
-- Part 2 — Layer 1: Database Schema
-- ============================================================

-- sys_table: Table definitions (replaces sys_metadata_models)
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

-- sys_column: Column definitions (replaces sys_table_columns)
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

-- ============================================================
-- Part 3 — Layer 2: Window Design
-- ============================================================

-- sys_window: Window definitions (replaces sys_metadata_views)
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

-- sys_tab: Tab definitions (replaces sys_form_sub_forms)
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

-- sys_window_field: Field definitions (replaces sys_form_fields)
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

-- sys_window_access: Role-based window access (replaces sys_form_tenant_role)
CREATE TABLE sys_window_access (
    id UUID PRIMARY KEY,
    window_id UUID NOT NULL REFERENCES sys_window(id),
    tenant_id UUID,
    role_id UUID NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP,
    UNIQUE (window_id, tenant_id, role_id)
);

CREATE INDEX idx_sys_window_access_window_id ON sys_window_access(window_id);
CREATE INDEX idx_sys_window_access_tenant_id ON sys_window_access(tenant_id);
CREATE INDEX idx_sys_window_access_role_id ON sys_window_access(role_id);
CREATE INDEX idx_sys_window_access_is_active ON sys_window_access(is_active);

-- ============================================================
-- Part 4 — Layer 3: Menu System
-- ============================================================

-- sys_menu: Hierarchical menu entries (NEW)
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
