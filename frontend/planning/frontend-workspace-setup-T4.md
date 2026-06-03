# T4 — Metadata Schema Design

## Objective

Design the complete metadata contract system for the ERP runtime engine.

This is the MOST important phase of the entire architecture.

Everything depends on this:

* forms
* grids
* layouts
* workflows
* permissions
* actions
* relations
* plugins
* runtime rendering

If T4 is designed badly:
the entire platform becomes unstable later.

---

# CORE PRINCIPLE

We are NOT building:

```txt id="t4a"
hardcoded React screens
```

We ARE building:

```txt id="t4b"
a runtime rendering engine
```

Meaning:

* backend sends metadata
* frontend interprets metadata
* runtime generates UI dynamically

---

# FINAL ARCHITECTURE DECISION

We will use:

| Area            | Approach                  |
| --------------- | ------------------------- |
| Metadata Format | JSON                      |
| Validation      | Zod                       |
| Type Safety     | TypeScript                |
| Storage         | PostgreSQL JSONB + tables |
| Runtime         | Metadata-driven           |
| Extensibility   | Plugin-ready              |

---

# TARGET OUTCOME

After T4:

```txt id="t4c"
✓ Metadata contracts finalized
✓ Runtime schemas validated
✓ TypeScript interfaces ready
✓ Zod validators ready
✓ Relation contracts ready
✓ View system ready
✓ Layout schema ready
✓ Action schema ready
✓ Workflow schema ready
✓ Permission schema ready
✓ Plugin extension points prepared
```

---

# MOST IMPORTANT DESIGN RULE

We separate:

| Concept    | Responsibility      |
| ---------- | ------------------- |
| Model      | Data structure      |
| Field      | Data definition     |
| View       | Rendering structure |
| Layout     | UI arrangement      |
| Action     | Runtime behavior    |
| Workflow   | State machine       |
| Permission | Access rules        |

This separation is critical.

---

# STEP-BY-STEP TASKS

---

# T4.1 — Create Metadata Module Structure

## Objective

Create centralized metadata architecture.

---

## Required Structure

```txt id="t4d"
src/core/metadata/
 ├── schemas/
 │    ├── model.schema.ts
 │    ├── field.schema.ts
 │    ├── view.schema.ts
 │    ├── layout.schema.ts
 │    ├── action.schema.ts
 │    ├── workflow.schema.ts
 │    └── permission.schema.ts
 │
 ├── types/
 │    ├── model.types.ts
 │    ├── field.types.ts
 │    ├── view.types.ts
 │    └── common.types.ts
 │
 ├── validators/
 ├── parsers/
 └── registry/
```

---

## Acceptance Criteria

* metadata structure modularized
* schemas isolated cleanly

---

# T4.2 — Define Core Base Metadata Types

## Objective

Create reusable metadata foundations.

---

## Required Base Fields

All metadata entities should support:

```ts id="t4e"
id
name
code
label
description
version
isActive
module
```

---

## Create Shared Types

Example:

```ts id="t4f"
interface BaseMetadata {
  id: string;
  code: string;
  label: string;
}
```

---

## Acceptance Criteria

* base metadata reusable
* common typing standardized

---

# T4.3 — Design Model Metadata Schema

## Objective

Define business entity structure.

---

## Example

```json id="t4g"
{
  "name": "customer",
  "table": "customers",
  "label": "Customer",
  "fields": []
}
```

---

## Required Properties

```txt id="t4h"
- name
- table
- label
- description
- fields
- primaryKey
- timestamps
- softDelete
- auditEnabled
```

---

## Requirements

Support:

* standard models
* custom models
* plugin models
* tenant-specific extensions

---

## Acceptance Criteria

* models validate correctly
* extensible architecture supported

---

## Test Cases

### TC-1

Load valid model.

Expected:

```txt id="t4i"
Schema validation succeeds
```

---

### TC-2

Missing required field.

Expected:

```txt id="t4j"
Validation error returned
```

---

# T4.4 — Design Field Metadata Schema

## Objective

Define field rendering + behavior contracts.

---

# THIS IS ONE OF THE MOST IMPORTANT TASKS

Fields drive:

* forms
* grids
* validation
* relations
* workflows
* permissions

---

## Example

```json id="t4k"
{
  "name": "customer_name",
  "type": "text",
  "label": "Customer Name",
  "required": true,
  "readonly": false
}
```

---

## Required Field Types

```txt id="t4l"
text
textarea
number
currency
boolean
date
datetime
email
phone
password
select
multiselect
many2one
one2many
many2many
json
file
image
richtext
```

---

## Required Properties

```txt id="t4m"
- name
- label
- type
- required
- readonly
- hidden
- defaultValue
- validations
- relation
- ui
- permissions
```

---

## Acceptance Criteria

* all field types supported
* future extensibility possible

---

## Test Cases

### TC-1

Load relation field.

Expected:

```txt id="t4n"
Relation metadata validates
```

---

# T4.5 — Design Relation Metadata Schema

## Objective

Define ERP relation architecture.

---

# THIS IS CRITICAL

ERP systems become difficult here.

---

## Supported Relations

| Type      | Example            |
| --------- | ------------------ |
| many2one  | customer           |
| one2many  | order lines        |
| many2many | tags               |
| tree      | category hierarchy |

---

## Example

```json id="t4o"
{
  "type": "many2one",
  "relation": "customer",
  "displayField": "name"
}
```

---

## Required Properties

```txt id="t4p"
- relationModel
- displayField
- valueField
- lazy
- searchable
- cascade
```

---

## Acceptance Criteria

* relation contracts reusable
* nested relations supported

---

# T4.6 — Design View Metadata Schema

## Objective

Define runtime screen rendering.

---

## Supported Views

```txt id="t4q"
- form
- list
- kanban
- dashboard
- detail
- wizard
```

---

## Example

```json id="t4r"
{
  "type": "form",
  "model": "customer",
  "layout": {}
}
```

---

## Requirements

Views should reference:

* layouts
* actions
* permissions
* workflows

---

## Acceptance Criteria

* views modularized
* runtime rendering supported

---

# T4.7 — Design Layout Metadata Schema

## Objective

Define visual UI arrangement.

---

## Supported Layouts

```txt id="t4s"
- tabs
- sections
- grids
- accordions
- split layouts
- cards
```

---

## Example

```json id="t4t"
{
  "type": "tabs",
  "tabs": []
}
```

---

## Requirements

Support:

* nested layouts
* responsive layouts
* conditional layouts

---

## Acceptance Criteria

* recursive layouts supported

---

# T4.8 — Design Validation Schema

## Objective

Define runtime validation contracts.

---

## Supported Validations

```txt id="t4u"
- required
- regex
- min/max
- length
- conditional
- expression-based
```

---

## Example

```json id="t4v"
{
  "type": "regex",
  "value": "^[A-Z]+$"
}
```

---

## Acceptance Criteria

* validations serializable
* frontend/backend reusable

---

# T4.9 — Design Action Metadata Schema

## Objective

Define runtime actions.

---

## Supported Actions

```txt id="t4w"
- save
- delete
- approve
- reject
- submit
- API action
- dialog action
- navigation action
```

---

## Example

```json id="t4x"
{
  "name": "approve",
  "type": "api",
  "endpoint": "/orders/approve"
}
```

---

## Acceptance Criteria

* actions dynamically executable

---

# T4.10 — Design Workflow Metadata Schema

## Objective

Define state machine architecture.

---

## Example

```json id="t4y"
{
  "states": ["draft", "approved"],
  "transitions": []
}
```

---

## Required Properties

```txt id="t4z"
- states
- transitions
- guards
- actions
- permissions
```

---

## Acceptance Criteria

* workflow transitions configurable

---

# T4.11 — Design Permission Metadata Schema

## Objective

Define access control contracts.

---

## Supported Levels

```txt id="t4aa"
- module
- view
- field
- action
- row
```

---

## Example

```json id="t4ab"
{
  "field": "amount",
  "readonlyRoles": ["sales"]
}
```

---

## Acceptance Criteria

* permission system extensible

---

# T4.12 — Design UI Metadata Schema

## Objective

Allow UI customization separately from business logic.

---

## Examples

```txt id="t4ac"
- width
- placeholder
- icons
- colors
- density
- alignment
```

---

## Acceptance Criteria

* UI concerns isolated cleanly

---

# T4.13 — Design Expression Metadata Schema

## Objective

Support runtime dynamic logic.

---

## Engine Choice

Use:

```txt id="t4ad"
JSON Logic
```

---

## Supported Use Cases

```txt id="t4ae"
- visibility
- readonly
- formulas
- validation
- conditional actions
```

---

## Example

```json id="t4af"
{
  "if": [
    { ">": [ { "var": "amount" }, 1000 ] },
    true,
    false
  ]
}
```

---

## Acceptance Criteria

* expressions serializable
* frontend/backend compatible

---

# T4.14 — Design Menu Metadata Schema

## Objective

Prepare dynamic navigation.

---

## Required Properties

```txt id="t4ag"
- label
- route
- icon
- parent
- permissions
- sequence
```

---

## Acceptance Criteria

* nested menus supported

---

# T4.15 — Design Plugin Metadata Schema

## Objective

Prepare installable ERP modules.

---

## Plugin Can Register

```txt id="t4ah"
- models
- views
- actions
- menus
- workflows
- permissions
```

---

## Acceptance Criteria

* plugin extension points ready

---

# T4.16 — Create Zod Validators

## Objective

Runtime metadata validation.

---

## Requirements

Every schema must have:

* TypeScript interface
* Zod validator

---

## Acceptance Criteria

* invalid metadata blocked safely

---

## Test Cases

### TC-1

Load malformed metadata.

Expected:

```txt id="t4ai"
Validation fails safely
```

---

# T4.17 — Create Metadata Parser Layer

## Objective

Prepare runtime interpretation.

---

## Responsibilities

Parser should:

* validate
* normalize
* enrich defaults
* resolve references

---

## Acceptance Criteria

* metadata normalized consistently

---

# T4.18 — Create Sample Metadata Definitions

## Objective

Validate architecture using real examples.

---

## Required Samples

Create:

* customer form
* sales order form
* product grid
* approval workflow

---

## Acceptance Criteria

* real ERP scenarios supported

---

# T4.19 — Define Metadata Versioning Strategy

## Objective

Prepare future migrations safely.

---

## Requirements

Support:

* schema versioning
* backward compatibility
* migration preparation

---

## Acceptance Criteria

* metadata evolution supported

---

# T4.20 — Create Metadata Documentation

## Objective

Document runtime contracts.

---

## Requirements

Document:

* all schemas
* field types
* layout types
* action types
* relation behavior

---

## Acceptance Criteria

* developers can extend safely

---

# FINAL ACCEPTANCE CRITERIA FOR T4

Developer is DONE only when:

```txt id="t4aj"
✓ Model schema complete
✓ Field schema complete
✓ Relation schema complete
✓ View schema complete
✓ Layout schema complete
✓ Workflow schema complete
✓ Action schema complete
✓ Permission schema complete
✓ Expression schema complete
✓ Zod validators operational
✓ Metadata parser operational
✓ Sample metadata working
✓ Runtime contracts finalized
```

---

# FINAL VALIDATION TESTS

Developer must validate:

```txt id="t4ak"
✓ Customer form metadata
✓ Sales order metadata
✓ Relation metadata
✓ Workflow metadata
✓ Nested layouts
✓ Conditional expressions
✓ Permission rules
```

All must parse successfully.

---

# OUTPUT OF T4

After T4 we will have:

```txt id="t4al"
Complete ERP Runtime Metadata Foundation
```