# AI Code Agent Prompt — T6 Runtime Renderer Design

You are a principal frontend architect building a metadata-driven ERP platform.

Your task is to design and implement the **Runtime Renderer Engine**.

IMPORTANT:

This is the heart of the frontend runtime.

The renderer must be capable of rendering entire ERP screens purely from metadata.

No business module should need to hardcode forms, grids, layouts, or workflows.

Architecture:

Backend Metadata
        ↓
Metadata Store
        ↓
Registry Resolution
        ↓
Runtime Renderer
        ↓
React UI

---

# CONTEXT

Completed:

✓ Phase 0 – Architecture Freeze
✓ T1 – Project Structure
✓ T2 – Design System
✓ T3 – State Management
✓ B1 – Metadata API Foundation
✓ T4 – Metadata Schema Design
✓ T5 – Registry System

Current Goal:

Build Runtime Rendering Engine.

---

# TARGET OUTCOME

After T6:

✓ Dynamic Form Renderer operational
✓ Dynamic Grid Renderer operational
✓ Dynamic Layout Renderer operational
✓ Dynamic View Renderer operational
✓ Recursive Layout Engine operational
✓ Runtime Context operational
✓ Metadata → UI pipeline operational
✓ Expression evaluation hooks ready
✓ Workflow rendering hooks ready
✓ Action rendering hooks ready

---

# PACKAGE STRUCTURE

```txt
src/runtime/

├── renderer/
│   ├── RuntimeRenderer.tsx
│   ├── ViewRenderer.tsx
│   ├── LayoutRenderer.tsx
│   ├── FieldRenderer.tsx
│   ├── ActionRenderer.tsx
│   ├── WorkflowRenderer.tsx
│   └── index.ts
│
├── context/
│   ├── RuntimeContext.tsx
│   ├── RuntimeProvider.tsx
│   └── runtime.types.ts
│
├── hooks/
│   ├── useRuntime.ts
│   ├── useRenderer.ts
│   ├── useRuntimeActions.ts
│   └── useRuntimeWorkflow.ts
│
├── expression/
│   ├── expressionEngine.ts
│   └── useExpression.ts
│
├── state/
│   ├── formState.ts
│   ├── gridState.ts
│   └── runtimeState.ts
│
└── index.ts
```

---

# T6.1 — Runtime Philosophy

The renderer receives:

```ts
RuntimeMetadataBundle
```

and produces:

```txt
React Screen
```

No renderer should know:

```txt
sales_order
product
customer
warehouse
```

Renderer must be completely generic.

---

# T6.2 — Runtime Context

Create:

```ts
RuntimeContext
```

Responsibilities:

```ts
metadata
model
view
record
workflow
permissions
loading
```

Must support nested rendering.

---

# T6.3 — Runtime Renderer

Create:

```tsx
<RuntimeRenderer />
```

Input:

```ts
metadataBundle
viewCode
recordId?
mode
```

Responsibilities:

- load metadata
- resolve view
- resolve layout
- render recursively

---

# T6.4 — View Renderer

Support:

```txt
FORM
GRID
KANBAN
DETAIL
DASHBOARD
```

Responsibilities:

```txt
resolve view component
delegate rendering
```

Must use:

```txt
View Registry
```

---

# T6.5 — Layout Renderer

Support:

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

Requirements:

- recursive rendering
- dynamic visibility
- responsive layouts
- nested layouts

---

# T6.6 — Field Renderer

Input:

```ts
FieldDefinition
```

Responsibilities:

```txt
resolve field component
apply expressions
bind form state
render validation state
```

Must use:

```txt
Field Registry
```

---

# T6.7 — Action Renderer

Support:

```txt
BUTTON
SERVER_ACTION
NAVIGATION
WORKFLOW
CUSTOM
```

Responsibilities:

```txt
render actions dynamically
resolve permissions
evaluate visibility
```

Must use:

```txt
Action Registry
```

---

# T6.8 — Workflow Renderer

Responsibilities:

```txt
render workflow state
render transitions
render actions
```

Must use:

```txt
Workflow Registry
```

---

# T6.9 — Expression Engine Integration

Support:

```txt
visibleWhen
readonlyWhen
requiredWhen
enabledWhen
```

Using:

```txt
JSON Logic
```

Do NOT implement full engine.

Create integration layer and hooks.

---

# T6.10 — Form State Engine

Create:

```ts
formState
```

Responsibilities:

```txt
field values
validation errors
dirty tracking
touched fields
```

Future:

```txt
autosave
undo
```

---

# T6.11 — Grid State Engine

Responsibilities:

```txt
sorting
filtering
pagination
selection
```

Future:

```txt
virtual scrolling
grouping
```

---

# T6.12 — Runtime State

Support:

```txt
current view
current record
mode
loading
permissions
workflow state
```

---

# T6.13 — Renderer Modes

Support:

```txt
CREATE
EDIT
VIEW
INLINE
DIALOG
```

---

# T6.14 — Runtime Hooks

Create:

```ts
useRuntime()
useRuntimeRecord()
useRuntimeActions()
useRuntimeWorkflow()
useRuntimePermissions()
```

---

# T6.15 — Runtime Pipeline

Renderer flow:

```txt
Metadata Bundle
       ↓
View Resolution
       ↓
Layout Resolution
       ↓
Field Resolution
       ↓
Expression Evaluation
       ↓
Permission Evaluation
       ↓
React Components
```

---

# T6.16 — Error Boundaries

Create:

```ts
RuntimeRenderError
MissingMetadataError
MissingRegistryComponentError
```

Provide fallback UI.

---

# T6.17 — Sample Implementation

Use:

```txt
business_partner
```

Metadata bundle.

The following should work:

```txt
GET metadata
      ↓
RuntimeRenderer
      ↓
Business Partner Form
```

without hardcoding.

---

# ACCEPTANCE TESTS

## Form Rendering

Expected:

```txt
Form renders completely from metadata.
```

---

## Grid Rendering

Expected:

```txt
Grid renders completely from metadata.
```

---

## Layout Rendering

Expected:

```txt
Recursive layouts render correctly.
```

---

## Missing Field Type

Expected:

```txt
Fallback error UI.
```

---

## Expression Evaluation

Expected:

```txt
Visibility changes dynamically.
```

---

## Workflow Rendering

Expected:

```txt
Transitions render correctly.
```

---

# CODE QUALITY REQUIREMENTS

Use:

- TypeScript strict mode
- Composition over inheritance
- Generic rendering
- Registry resolution
- Memoization where appropriate
- Error boundaries
- Future plugin support

Avoid:

- switch statements with business logic
- hardcoded modules
- direct imports of module components
- module-specific assumptions

---

# FINAL DELIVERABLE

Produce:

✓ Runtime Renderer
✓ View Renderer
✓ Layout Renderer
✓ Field Renderer
✓ Action Renderer
✓ Workflow Renderer
✓ Runtime Context
✓ Runtime Hooks
✓ Runtime State
✓ Expression Integration
✓ Error Boundaries

Result:

```txt
Metadata
      ↓
Registry Resolution
      ↓
Runtime Rendering
      ↓
ERP Screen
```
