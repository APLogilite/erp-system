# Documentation Rules

This document defines file ownership, synchronization rules, and documentation standards.

Every AI agent must read this before creating or modifying any project file.

---

## File Ownership

| Path | Owner | Purpose |
|------|-------|---------|
| `ai/prd/PRD-*.md` | Product Manager | PRD documents |
| `ai/tasks/TASK-*.md` | Product Manager | Implementation tasks |
| `ai/tasks/BUG-*.md` | Product Manager | Bug tasks |
| `ai/tasks/ENH-*.md` | Product Manager | Enhancement tasks |
| `ai/docs/*.md` | Product Manager (workflow) / AI Framework | Workflow docs and templates |
| `ai/PROJECT_BOARD.md` | Product Manager | Project execution status |
| `ai/docs/changelog.md` | Product Manager | Historical record |
| `ai/changes/CHANGE-*.md` | Software Engineer | Implementation change reports |
| `backend/*`, `frontend/*` | Software Engineer | Application source code |
| `ai/tests/TEST-*.md` | QA Engineer | Test reports |
| `ai/scripts/*` | QA Engineer | Reusable test scripts |
| `ai/schema/*` | Software Engineer | Centralized DDL reference |
| `ai/modules/*` | Technical Writer | Module docs |
| `ai/docs/rules/access.md`, `ai/docs/rules/access.json` | AI Framework | Access control rules |
| `ai/flows/*` | Technical Writer | Flow docs |
| `ai/failures/FAIL-*.md` | Agent that created it | Failure reports |
| `.opencode/*` | AI Framework | Agent config (no agent modifies) |

### Cross-boundary rules

- Product Manager may update `status` and `updated` fields in PRD docs when advancing lifecycle (IN_DEVELOPMENT→TESTING, TESTING→READY_FOR_DEPLOYMENT)
- Software Engineer may update `status` and `updated` fields in PRD docs when advancing APPROVED→IN_DEVELOPMENT or IN_DEVELOPMENT→TESTING
- QA Engineer may update `status` and `updated` fields in PRD docs when advancing TESTING→READY_FOR_DEPLOYMENT
- No agent modifies another agent's owned directories
- No agent modifies `.opencode/*` files
- Any agent may read `ai/schema/*` (reference only)
- Software Engineer writes `ai/schema/*` when schema changes

---

## PROJECT_BOARD.md Management

PROJECT_BOARD.md is the single source of truth for project execution status.

### When to update

Update the board on EVERY status change:

- PRD status changes
- Task status changes
- Bug creation or resolution
- Enhancement creation or completion
- Blocked or unblocked tasks
- New PRD or task creation

### Synchronization rules

- Task doc and PROJECT_BOARD.md must always match
- PRD doc and PROJECT_BOARD.md must always match
- Never update one without the other
- If inconsistency is found: stop, correct, continue
- `ai/docs/rules/access.md` is the source of truth for file ownership

### Field ownership

Product Manager owns:
- PRD rows, status, priority, task entries, planning status, dependencies

Software Engineer owns:
- Implementation status, assigned branch, change report reference

QA Engineer owns:
- Test status, test report reference, bug links, verification notes

---

## Change Reports

Every completed task creates a change report.

- Location: `ai/changes/CHANGE-TASK-XXX.md`
- Template: `ai/docs/templates/change.md`
- Owner: Software Engineer
- Must exist BEFORE merging task branch to PRD branch
- Must document: files added/modified/removed, DB changes, API changes, validation results

---

## Test Reports

Every tested task creates a test report.

- Location: `ai/tests/TEST-TASK-XXX.md`
- Template: `ai/docs/templates/test.md`
- Owner: QA Engineer
- Must document: test cases, results, bugs found, acceptance criteria status

---

## CHANGELOG.md Management

- Append new entries — never overwrite existing ones
- Record: PRD transitions (IN_DEVELOPMENT→TESTING→READY_FOR_DEPLOYMENT→COMPLETED)
- Record: significant planning changes (PRD versions, task generation)
- Owner: Product Manager (with contributions from SE and QA for PRD transitions)

---

## General Documentation Rules

### Templates
- Always use official project templates from `ai/docs/`
- Never deviate from template structure
- Never leave sections blank — write "None" or "N/A" with a reason

### Cleanliness
- Never leave stale information
- Always synchronize after every change
- Never overwrite previous history entries — append
- Never modify completed or historical records
- Preserve project history permanently

### Validation
- Before completing any session, verify:
  - All docs reference each other correctly
  - PROJECT_BOARD.md matches task docs
  - No orphan tasks exist
  - No duplicate IDs exist

### File changes
- Run `git diff --cached --name-only` before every commit
- Verify only expected files are staged
- Never stage files owned by another agent
