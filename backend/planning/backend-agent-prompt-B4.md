# AI Code Agent Prompt — B4 Workflow Engine

You are a principal ERP architect and senior Spring Boot developer.

Your task is to build the **Metadata-Driven Workflow Engine**.

IMPORTANT:

This is NOT a hardcoded approval system.

This engine must support ANY future workflow entirely through metadata.

Examples:

- Sales Order Lifecycle
- Purchase Order Approval
- Inventory Document Posting
- Invoice Approval
- Custom Plugin Workflows

Architecture:

Workflow Metadata
        ↓
Workflow Engine
        ↓
Runtime CRUD
        ↓
Business Actions
        ↓
Notifications / Audit / Inventory

The engine must be generic and reusable across all ERP modules.

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
✓ T6 – Runtime Renderer
✓ B2 – Runtime CRUD Engine
✓ B3 – Relation Engine

Current Goal:

Build Workflow Engine.

---

# TARGET OUTCOME

After B4:

✓ State machine operational
✓ Transition engine operational
✓ Workflow validation operational
✓ Guards operational
✓ Workflow actions operational
✓ Workflow permissions operational
✓ Workflow history operational
✓ Events operational
✓ Frontend integration ready

---

# PACKAGE STRUCTURE

```txt
com.erp.core.workflow

├── controller
├── service
├── engine
├── action
├── guard
├── history
├── event
├── dto
├── validator
├── repository
├── exception
└── mapper
```

---

# B4.1 — Workflow Philosophy

Workflow must be metadata driven.

Example:

```txt
Draft
   ↓
Submitted
   ↓
Approved
   ↓
Completed
   ↓
Closed
```

No workflow states should be hardcoded in modules.

---

# B4.2 — Workflow Definition

Use T4 contracts:

```java
WorkflowDefinition
WorkflowStateDefinition
WorkflowTransitionDefinition
```

Transition:

```java
code
fromState
toState
guardExpression
actions
permissions
```

---

# B4.3 — Workflow Service

Create:

```java
WorkflowService
WorkflowServiceImpl
```

Responsibilities:

```java
getCurrentState()

getAvailableTransitions()

transition()

validateTransition()

executeActions()

getHistory()
```

---

# B4.4 — Workflow State Machine

Create:

```java
WorkflowEngine
```

Responsibilities:

```java
load workflow
validate transition
execute transition
update state
publish events
```

---

# B4.5 — Transition API

Create:

```txt
POST /api/workflow/{model}/{id}/transition
```

Body:

```json
{
  "transitionCode": "APPROVE"
}
```

---

Create:

```txt
GET /api/workflow/{model}/{id}/transitions
```

Returns:

```json
[
  {
    "code": "APPROVE",
    "name": "Approve"
  }
]
```

---

# B4.6 — Workflow Guards

Support:

```txt
JSON Logic
```

Examples:

```txt
Amount > 1000
Status = Draft
Inventory Available
```

Create:

```java
WorkflowGuardService
```

Responsibilities:

```java
evaluate()
validate()
```

---

# B4.7 — Workflow Actions

Transitions may execute:

```txt
server action
inventory action
notification action
custom action
```

Create:

```java
WorkflowActionExecutor
```

Interface:

```java
execute()
supports()
```

Must support plugin registration.

---

# B4.8 — Workflow Permissions

Transition may require:

```txt
role
permission
expression
```

Create:

```java
WorkflowPermissionService
```

Responsibilities:

```java
canExecute()
```

---

# B4.9 — Workflow History

Create:

```java
WorkflowHistory
```

Fields:

```java
recordId
modelCode
fromState
toState
transitionCode
executedBy
executedAt
comments
```

---

# APIs

```txt
GET /api/workflow/{model}/{id}/history
```

---

# B4.10 — Workflow Events

Publish:

```txt
workflow.started
workflow.transitioned
workflow.completed
workflow.closed
```

Future consumers:

```txt
notifications
inventory
audit
plugins
```

---

# B4.11 — Workflow Validation

Validate:

```txt
invalid state
invalid transition
invalid workflow
guard failure
permission failure
```

Create:

```java
WorkflowValidationException
```

---

# B4.12 — Workflow Runtime Context

Create:

```java
WorkflowContext
```

Contains:

```java
modelCode
recordId
currentState
record
user
tenant
metadata
```

---

# B4.13 — Workflow DTOs

Create:

```java
WorkflowTransitionRequestDto
WorkflowTransitionResponseDto
WorkflowStateDto
WorkflowHistoryDto
AvailableTransitionDto
```

---

# B4.14 — Frontend Contract

Frontend Runtime Renderer must support:

```txt
workflow state display
transition buttons
history display
state badges
permission-based actions
```

---

# B4.15 — Sample Workflow

Create sample workflow:

## Sales Order

```txt
Draft
 ↓
Submitted
 ↓
Approved
 ↓
Completed
 ↓
Closed
```

Transitions:

```txt
SUBMIT
APPROVE
COMPLETE
CLOSE
REOPEN
```

---

# B4.16 — Acceptance Tests

## Valid Transition

Expected:

```txt
State updated.
History created.
Events published.
```

---

## Invalid Transition

Expected:

```txt
WorkflowValidationException
```

---

## Guard Failure

Expected:

```txt
Transition denied.
```

---

## Permission Failure

Expected:

```txt
Access denied.
```

---

## History Query

Expected:

```txt
Complete transition history returned.
```

---

# CODE QUALITY REQUIREMENTS

Use:

- State machine pattern
- Strategy pattern for actions
- Event-driven architecture
- Constructor injection
- SOLID principles
- Metadata-driven execution

Avoid:

- Hardcoded workflows
- Module-specific workflow services
- Giant if/else transition logic
- Business-specific assumptions

---

# FINAL DELIVERABLE

Produce:

✓ Workflow Engine
✓ State Machine
✓ Transition APIs
✓ Guard Engine
✓ Workflow Actions
✓ Workflow Permissions
✓ Workflow History
✓ Workflow Events
✓ Validation
✓ Frontend Integration Contract

Result:

```txt
Workflow Metadata
        ↓
Workflow Engine
        ↓
State Transitions
        ↓
ERP Business Processes
```
