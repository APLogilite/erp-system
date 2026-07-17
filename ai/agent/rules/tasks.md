---
document: TASK_RULES
version: 1.0.0
status: ACTIVE
---

# Task Generation Rules

## Purpose

This document defines how the Product Manager converts a PRD into implementation tasks.

Tasks should be:

- Independent
- Small
- Testable
- Traceable

---

# Task Size

A task should represent one logical unit of work.

Ideal implementation time:

- Minimum: 30 minutes
- Target: 2-4 hours
- Maximum: 1 working day

If estimated work exceeds one day, split it into multiple tasks.

---

# Task Categories

Allowed task types

- Feature
- Enhancement
- Bug
- Refactor
- Documentation
- Research
- Infrastructure
- Database
- API
- UI
- Testing

---

# Task Naming

Use clear action-based titles.

Good

- Create Login API
- Add JWT Authentication
- Create User Repository

Bad

- Login
- Auth
- User Stuff

---

# Task Creation Rules

Create a new task when:

- New functionality is required
- A new API endpoint is required
- A database migration is required
- A UI screen is required
- A service/module is required
- Documentation is required
- Testing requires separate implementation

---

# Do NOT Combine

Never combine unrelated work.

Bad

TASK

- Login API
- Dashboard UI
- User Import

Good

TASK-001 Login API

TASK-002 Dashboard UI

TASK-003 User Import

---

# Dependencies

Use depends_on only when required.

Avoid unnecessary dependencies.

Example

Dashboard depends on Login.

Reporting depends on Database Migration.

---

# Priority Rules

Critical

Production issue

Security

Data Loss

High

Core functionality

Medium

Normal feature

Low

Nice to have

---

# Enhancement Rules

Create Enhancement Task when

- PRD changes after implementation started
- User requests additional functionality
- Existing feature requires extension

Do NOT modify completed tasks.

---

# Bug Rules

Every bug becomes its own task.

Bug must reference

- Parent PRD
- Parent Task

Bug fixes never overwrite original task history.

---

# Acceptance Criteria

Every task MUST contain measurable acceptance criteria.

### Tag Convention

Each acceptance criterion MUST be prefixed with a tag indicating which agent validates it:

| Tag | Validated By | When |
|-----|-------------|------|
| `[SE]` | Software Engineer | During development, before READY_FOR_TEST |
| `[QA]` | QA Engineer | During testing, before TESTED |
| `[SE][QA]` | Both | SE validates during dev, QA validates during testing |

PM assigns tags when creating the task.

### Unmet Criteria

If SE or QA cannot meet a criterion:
1. Document it in the task's **Unmet Criteria** table with the reason
2. Set task status → `PENDING_APPROVAL`
3. PM discusses with user to decide: approve deviation or reject back to dev

A task at `PENDING_APPROVAL` blocks its dependents — no dependent task will be activated until resolved.

Bad

"Should work."

Good

- [ ] `[SE]` User can login with email and password
- [ ] `[QA]` Error message shows on invalid credentials
- [ ] `[SE][QA]` JWT expires after 15 minutes

---

# Completion Rules

A task is complete only when:

- Code implemented
- Build passes
- Lint passes
- Required tests pass
- Change summary created
- Task metadata updated

---

# Product Manager Checklist

Before creating tasks verify:

✓ Requirements are complete

✓ Open questions resolved

✓ Dependencies identified

✓ Acceptance criteria defined

✓ Task size appropriate

✓ Priorities assigned
