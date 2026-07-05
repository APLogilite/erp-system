# AI Code Agent Prompt — T5 Registry System Design

You are a principal frontend architect building a metadata-driven ERP platform.

Your task is to design and implement the **Registry System** that sits between Metadata Definitions and the Runtime Renderer.

IMPORTANT:

This is NOT the renderer.

This is NOT business module development.

This is the resolution engine that converts metadata into actual React components.

Architecture Flow:

Backend Metadata
↓
Metadata Store
↓
Registry Resolution
↓
Runtime Renderer
↓
React Components

The registry system must be extensible, plugin-friendly, and support future ERP growth.

---

# CONTEXT

Already completed:

✓ Phase 0 – Architecture Freeze
✓ T1 – Project Structure
✓ T2 – Design System
✓ T3 – State Management
✓ B1 – Metadata API Foundation
✓ T4 – Metadata Schema Design

Current Goal:

Build the Runtime Registry System.

---

# TARGET OUTCOME

After T5:

✓ Field Registry operational
✓ Layout Registry operational
✓ Action Registry operational
✓ Workflow Registry operational
✓ View Registry operational
✓ Plugin extension points ready
✓ Registry override strategy ready
✓ Lazy loading support ready
✓ Runtime component resolution ready

---

# PACKAGE STRUCTURE

Create:

```txt
src/core/registry/

├── field/
│   ├── fieldRegistry.ts
│   ├── fieldRegistry.types.ts
│   └── index.ts
│
├── layout/
│   ├── layoutRegistry.ts
│   ├── layoutRegistry.types.ts
│   └── index.ts
│
├── action/
│   ├── actionRegistry.ts
│   ├── actionRegistry.types.ts
│   └── index.ts
│
├── workflow/
│   ├── workflowRegistry.ts
│   ├── workflowRegistry.types.ts
│   └── index.ts
│
├── view/
│   ├── viewRegistry.ts
│   ├── viewRegistry.types.ts
│   └── index.ts
│
├── registry.types.ts
├── registry.constants.ts
├── registry.errors.ts
├── registry.provider.tsx
└── index.ts
```

---

# T5.1 — Registry Philosophy

The registry is a lookup engine.

Metadata never imports components directly.

Bad:

```ts
field.component = TextField;
```

Good:

```ts
field.type = "TEXT"

↓

FieldRegistry.resolve("TEXT")

↓

TextFieldComponent
```

All runtime rendering must go through registries.

---

# T5.2 — Base Registry Contract

Create generic registry contract:

```ts
interface Registry<T> {
  register(key: string, value: T): void;

  unregister(key: string): void;

  resolve(key: string): T;

  has(key: string): boolean;

  getAll(): Record<string, T>;

  clear(): void;
}
```

Must support:

- type safety
- duplicate protection
- plugin extensions
- overrides

---

# T5.3 — Field Registry

Purpose:

Resolve metadata field types to React components.

Supported types:

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

Registry contract:

```ts
registerField();

resolveField();

hasField();

getFields();
```

Must support:

```txt
plugin field types
field overrides
lazy loading
```

---

# T5.4 — Layout Registry

Purpose:

Resolve layout metadata into layout components.

Supported layouts:

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

Must support recursive rendering.

---

# T5.5 — Action Registry

Purpose:

Resolve:

```txt
BUTTON
SERVER_ACTION
NAVIGATION
WORKFLOW
CUSTOM
```

Requirements:

- dynamic actions
- custom actions
- plugin actions

---

# T5.6 — Workflow Registry

Purpose:

Resolve workflow UI components.

Support:

```txt
approval workflow
document workflow
custom workflow
```

---

# T5.7 — View Registry

Purpose:

Resolve:

```txt
FORM
GRID
KANBAN
DETAIL
DASHBOARD
```

to runtime view components.

---

# T5.8 — Registry Provider

Create:

```txt
RegistryProvider
```

Responsibilities:

- bootstrap registries
- register default components
- expose registry context

Must support:

```txt
future plugins
dynamic registration
runtime overrides
```

---

# T5.9 — Registry Errors

Create:

```ts
RegistryNotFoundError;
DuplicateRegistrationError;
```

Examples:

```txt
Field type not registered.

Layout type not registered.

Action not registered.
```

---

# T5.10 — Override Strategy

Support:

```txt
Default Component
        ↓
Module Override
        ↓
Plugin Override
```

Highest priority wins.

Requirements:

```ts
register(key, component, priority);
```

---

# T5.11 — Lazy Loading Strategy

Registries must support:

```ts
() => import(...)
```

for:

- heavy views
- dashboards
- plugin components

Do not implement renderer.

Only support registration.

---

# T5.12 — Plugin Extension Points

Support future:

```ts
plugin.registerFields();
plugin.registerLayouts();
plugin.registerActions();
plugin.registerViews();
```

Do NOT implement plugins.

Only prepare extension APIs.

---

# T5.13 — Default Registrations

Create default registrations for:

Field Types:

```txt
TEXT
TEXTAREA
NUMBER
BOOLEAN
DATE
SELECT
```

Layout Types:

```txt
PAGE
SECTION
ROW
COLUMN
TABS
GRID
```

View Types:

```txt
FORM
GRID
DETAIL
```

Action Types:

```txt
BUTTON
SERVER_ACTION
NAVIGATION
```

Workflow Types:

```txt
DEFAULT
```

Use placeholder components if necessary.

---

# T5.14 — Registry Hooks

Create hooks:

```ts
useFieldRegistry();
useLayoutRegistry();
useActionRegistry();
useWorkflowRegistry();
useViewRegistry();
```

Requirements:

- typed APIs
- no prop drilling
- runtime-safe

---

# T5.15 — Acceptance Tests

Test:

## Field Registration

```ts
registerField('TEXT', TextField);
```

Expected:

```txt
resolveField("TEXT")
returns TextField
```

---

## Duplicate Registration

Expected:

```txt
DuplicateRegistrationError
```

---

## Missing Registration

Expected:

```txt
RegistryNotFoundError
```

---

## Override Registration

Expected:

```txt
Highest priority component returned
```

---

## Lazy Component

Expected:

```txt
Component resolved successfully
```

---

# CODE QUALITY REQUIREMENTS

Use:

- TypeScript strict mode
- Generics
- Strong typing
- Immutable APIs
- Dependency inversion
- Future plugin support

Avoid:

- switch statements everywhere
- hardcoded component imports in renderer
- module-specific assumptions
- business logic

---

# FINAL DELIVERABLE

Produce:

✓ Base Registry System
✓ Field Registry
✓ Layout Registry
✓ Action Registry
✓ Workflow Registry
✓ View Registry
✓ Registry Provider
✓ Hooks
✓ Errors
✓ Override Strategy
✓ Plugin Extension Points
✓ Default Registrations

Result:

```txt
Metadata
     ↓
Registry Resolution
     ↓
Resolved Runtime Components
```
