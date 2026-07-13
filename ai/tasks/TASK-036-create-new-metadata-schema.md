---
id: TASK-036

title: Create New Metadata Schema (Flyway Migration)

type: Database

status: READY_FOR_TEST

priority: Critical

owner: software_engineer

assigned_to: software_engineer

assigned_branch: feature/TASK-036

locked: true

created: 2026-07-13

updated: 2026-07-13

started: 2026-07-13

completed: 2026-07-13

estimated_hours: 6

actual_hours: 1

parent_prd: PRD-004

prd_version: 1.0.0

prd_branch: prd/PRD-004-window-hierarchy-menu

base_branch: main

merge_target: prd/PRD-004-window-hierarchy-menu

depends_on: []

blocks: [TASK-037, TASK-038, TASK-039, TASK-040, TASK-041, TASK-043]

labels: [database, migration, flyway]

history:
  - 2026-07-13: Status PLANNING → IN_DEVELOPMENT. Assigned to software_engineer. Started implementation.
  - 2026-07-13: V24 Flyway migration created. Validation passed. Change report created. Status → READY_FOR_TEST.

review_required: true

test_required: true

change_report: ai/changes/CHANGE-TASK-036.md

---

# Goal

Create the new metadata schema tables via Flyway migration, replacing the old PRD-001 schema entirely.

---

# Description

Since the platform is still in initial development, drop all old metadata tables and create the new ones from scratch.

## Tables to create

### Layer 1: Database Schema

```sql
CREATE TABLE sys_table (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    label VARCHAR(100) NOT NULL,
    plural_label VARCHAR(100),
    table_type VARCHAR(20) NOT NULL DEFAULT 'dynamic',  -- 'dynamic' or 'static'
    table_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    -- BaseEntity fields
    tenant_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

CREATE TABLE sys_column (
    id UUID PRIMARY KEY,
    table_id UUID NOT NULL REFERENCES sys_table(id),
    code VARCHAR(100) NOT NULL,
    label VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,  -- string, text, integer, decimal, boolean, date, datetime, many2one, enum
    required BOOLEAN DEFAULT false,
    default_value TEXT,
    max_length INTEGER,
    precision INTEGER,
    scale INTEGER,
    relation_table VARCHAR(100),
    enum_options JSONB,
    position INTEGER,
    is_active BOOLEAN DEFAULT true,
    -- BaseEntity fields
    tenant_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP,
    UNIQUE (table_id, code)
);
```

### Layer 2: Window Design

```sql
CREATE TABLE sys_window (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    table_id UUID NOT NULL REFERENCES sys_table(id),
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    -- BaseEntity fields
    tenant_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);

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
    -- BaseEntity fields
    tenant_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP,
    UNIQUE (window_id, seq_no)
);

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
    -- BaseEntity fields
    tenant_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP,
    UNIQUE (tab_id, seq_no),
    UNIQUE (tab_id, column_id)
);

CREATE TABLE sys_window_access (
    id UUID PRIMARY KEY,
    window_id UUID NOT NULL REFERENCES sys_window(id),
    tenant_id UUID,
    role_id UUID NOT NULL,
    is_active BOOLEAN DEFAULT true,
    -- BaseEntity fields
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP,
    UNIQUE (window_id, tenant_id, role_id)
);
```

### Layer 3: Menu

```sql
CREATE TABLE sys_menu (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,  -- 'group' or 'window'
    parent_id UUID REFERENCES sys_menu(id),
    window_id UUID REFERENCES sys_window(id),
    seq_no INTEGER NOT NULL DEFAULT 10,
    icon VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    -- BaseEntity fields
    tenant_id UUID,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by UUID,
    updated_by UUID,
    deleted_at TIMESTAMP
);
```

## Old tables to drop

```sql
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
```

---

# Acceptance Criteria

- [ ] All 7 new tables created with correct columns, types, and constraints
- [ ] All old tables dropped
- [ ] Foreign key relationships match the ER diagram
- [ ] Indexes created on FK columns and unique constraints
- [ ] BaseEntity columns present on all tables
- [ ] Migration runs successfully on fresh PostgreSQL
- [ ] Rollback script provided (re-create old tables if needed)

---

# Technical Notes

- Use Flyway migration (V24 or next available version)
- Enable `spring.flyway.enabled=true` for this migration
- Set `spring.flyway.baseline-on-migrate=true` since previous migrations may have run
- All new tables use the same BaseEntity pattern as existing code
