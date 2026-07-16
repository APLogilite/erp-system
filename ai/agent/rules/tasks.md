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

Bad

"Should work."

Good

User can login with email and password.

JWT expires after 15 minutes.

Refresh token is supported.

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
