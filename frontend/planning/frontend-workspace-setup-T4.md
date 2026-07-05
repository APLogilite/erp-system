# AI Code Agent Prompt — T4 Metadata Schema Design

You are a principal ERP architect.

Your task is to design and implement the definitive metadata schema for the ERP Runtime Platform.

IMPORTANT:

This is NOT feature development.

This is NOT UI development.

This is NOT CRUD development.

This is the permanent metadata contract between:

Backend Runtime Engine
↔
Metadata APIs
↔
Frontend Registry System
↔
Runtime Renderer

This schema becomes one of the most important architectural assets in the system.

Think carefully.

Future modules must be buildable almost entirely through metadata.

---

# ARCHITECTURE CONTEXT

Already completed:

✓ Phase 0 Architecture Freeze
✓ T1 Project Structure
✓ T2 UI Foundation
✓ T3 State Management
✓ B1 Metadata API Foundation

Current Goal:

Freeze Runtime Metadata Contract v1.

---

# TARGET OUTCOME

After T4:

✓ Metadata contracts finalized
✓ Metadata validation schemas created
✓ Runtime renderer contract established
✓ Registry contract established
✓ Workflow contract established
✓ Relation contract established
✓ Permission contract established
✓ Expression contract established

---

# PACKAGE STRUCTURE

Frontend:

```txt
src/core/metadata/schema/

├── model/
├── field/
├── view/
├── layout/
├── workflow/
├── action/
├── permission/
├── expression/
├── relation/
├── validators/
└── index.ts
```

Backend equivalent:

```txt
com.erp.core.metadata.schema
```

Both sides must represent the same contract.

---

# T4.1 — Base Metadata Definition

Create common metadata foundation.

Required:

```ts
BaseMetadata;
```

Fields:

```ts
id: string
code: string
name: string
description?: string

version: number

active: boolean

properties?: Record<string, unknown>
```

All metadata definitions inherit from this.

---

# T4.2 — Model Definition

Create:

```ts
ModelDefinition;
```

Purpose:

Represents ERP business object.

Examples:

```txt
business_partner
product
sales_order
warehouse
```

Required:

```ts
code
name
description

tableName

auditable
workflowEnabled
tenantAware

fields: FieldDefinition[]
```

Must support:

```txt
dynamic CRUD
runtime forms
runtime grids
runtime workflows
```

---

# T4.3 — Field Definition

Create:

```ts
FieldDefinition;
```

Supported field types:

```txt
TEXT
TEXTAREA
NUMBER
DECIMAL
BOOLEAN
DATE
DATETIME
EMAIL
PHONE
SELECT
MULTI_SELECT
MANY_TO_ONE
ONE_TO_MANY
MANY_TO_MANY
TREE
JSON
```

Required fields:

```ts
code;
name;
type;

required;
readonly;
hidden;

defaultValue;

searchable;
filterable;
sortable;
```

Validation:

```ts
minLength;
maxLength;
minValue;
maxValue;
pattern;
```

UI:

```ts
placeholder;
helperText;
```

Expressions:

```ts
visibleWhen;
readonlyWhen;
requiredWhen;
```

---

# T4.4 — Relation Definition

Create:

```ts
RelationDefinition;
```

Supported:

```txt
many2one
one2many
many2many
tree
```

Required:

```ts
relationType;

targetModel;

displayField;

valueField;

cascadeSave;
```

Loading strategy:

```ts
LAZY;
EAGER;
PAGINATED;
```

Must support future lookup APIs.

---

# T4.5 — View Definition

Create:

```ts
ViewDefinition;
```

Supported:

```txt
FORM
GRID
KANBAN
DETAIL
DASHBOARD
```

Required:

```ts
code;
modelCode;
viewType;
title;

layout;
```

Purpose:

Entry point for runtime rendering.

---

# T4.6 — Layout Definition

Create recursive layout engine.

Supported layout types:

```txt
PAGE
SECTION
GROUP
ROW
COLUMN
TABS
TAB
GRID
PANEL
```

Required:

```ts
type;

children;

config;
```

Must support:

```txt
deep recursion
responsive layouts
dynamic visibility
```

---

# T4.7 — Workflow Definition

Create:

```ts
WorkflowDefinition;
```

Required:

```ts
states;
transitions;
```

State:

```ts
code;
name;
initial;
final;
```

Transition:

```ts
code;

fromState;
toState;

guardExpression;

actions;

permissions;
```

Must support:

```txt
approval flows
inventory flows
document lifecycle
```

---

# T4.8 — Action Definition

Create:

```ts
ActionDefinition;
```

Supported:

```txt
BUTTON
SERVER_ACTION
NAVIGATION
WORKFLOW
CUSTOM
```

Required:

```ts
code;
name;

actionType;

icon;

visibleWhen;

enabledWhen;
```

Future:

```txt
plugin actions
```

---

# T4.9 — Permission Definition

Create:

```ts
PermissionDefinition;
```

Supported levels:

```txt
MODULE
MENU
VIEW
FIELD
ACTION
ROW
```

Required:

```ts
resource;
permissionType;

expression;
```

Purpose:

Runtime permission evaluation.

---

# T4.10 — Expression Definition

Engine:

```txt
JSON Logic
```

Create:

```ts
ExpressionDefinition;
```

Supported usage:

```txt
visibility
readonly
validation
workflow guards
permissions
calculated fields
```

Example:

```json
{
  ">": [{ "var": "amount" }, 1000]
}
```

---

# T4.11 — Runtime Metadata Bundle

Create:

```ts
RuntimeMetadataBundle;
```

Contains:

```ts
model;

views;

workflow;

actions;

permissions;
```

Purpose:

Single payload delivered from B1.

---

# T4.12 — Zod Validation Schemas

Create validation schema for EVERY metadata definition.

Required:

```ts
ModelDefinitionSchema;
FieldDefinitionSchema;
ViewDefinitionSchema;
LayoutDefinitionSchema;
WorkflowDefinitionSchema;
ActionDefinitionSchema;
PermissionDefinitionSchema;
ExpressionDefinitionSchema;
```

Requirements:

```txt
strict validation
safe parsing
runtime validation
```

---

# T4.13 — Metadata Registry Contracts

Define contracts for T5.

Create interfaces only.

```ts
FieldRegistry;
LayoutRegistry;
ActionRegistry;
WorkflowRegistry;
```

Do NOT implement.

Only define contracts.

---

# T4.14 — Sample Metadata

Create complete sample metadata for:

```txt
business_partner
```

Include:

```txt
model
form view
grid view
layout
workflow
actions
permissions
```

This becomes the reference implementation.

---

# ACCEPTANCE CRITERIA

Developer is DONE only when:

```txt
✓ ModelDefinition complete
✓ FieldDefinition complete
✓ RelationDefinition complete
✓ ViewDefinition complete
✓ LayoutDefinition complete
✓ WorkflowDefinition complete
✓ ActionDefinition complete
✓ PermissionDefinition complete
✓ ExpressionDefinition complete
✓ RuntimeMetadataBundle complete
✓ Zod schemas complete
✓ Registry contracts complete
✓ Sample metadata complete
```

---

# CODE QUALITY REQUIREMENTS

Use:

- TypeScript strict mode
- Zod
- discriminated unions where useful
- strong typing
- future plugin support
- immutable contracts

Avoid:

- UI-specific logic
- module-specific assumptions
- hardcoded workflows
- hardcoded permissions

---

# FINAL DELIVERABLE

Produce:

ERP Runtime Metadata Contract v1

This contract must support:

✓ Dynamic Forms
✓ Dynamic Grids
✓ Dynamic Layouts
✓ Dynamic Workflows
✓ Dynamic Permissions
✓ Dynamic Actions
✓ Dynamic Relations
✓ Multi-module ERP Growth
✓ Plugin Extensions

The output should become the permanent metadata language of the ERP platform.
