# Documentation Rules

This document defines file ownership, synchronization rules, and documentation standards.

Every AI agent must read this before creating or modifying any project file.

---

## File Ownership

| Path | Owner | Purpose |
|------|-------|---------|
| `ai/project/prd/PRD-*.md` | Product Manager | PRD documents |
| `ai/project/tasks/TASK-*.md` | Product Manager | Implementation tasks |
| `ai/project/tasks/BUG-*.md` | Product Manager | Bug tasks |
| `ai/project/tasks/ENH-*.md` | Product Manager | Enhancement tasks |
| `ai/agent/*.md` | Product Manager (workflow) / AI Framework | Workflow docs and templates |
| `ai/agent/project-board.md` | Product Manager | Project execution status |
| `ai/agent/changelog.md` | Product Manager | Historical record |
| `ai/project/changes/CHANGE-*.md` | Software Engineer | Implementation change reports |
| `backend/*`, `frontend/*` | Software Engineer | Application source code |
| `ai/project/tests/TEST-*.md` | QA Engineer | Test reports |
| `ai/project/scripts/*` | QA Engineer | Reusable test scripts |
| `ai/project/schema/*` | Software Engineer | Centralized DDL reference |
| `ai/project/modules/*` | Technical Writer | Module docs |
| `ai/agent/rules/access.md`, `ai/agent/rules/access.json` | AI Framework | Access control rules |
| `ai/project/flows/*` | Technical Writer | Flow docs |
| `ai/project/failures/FAIL-*.md` | Agent that created it | Failure reports |
| `.opencode/*` | AI Framework | Agent config (no agent modifies) |

### Cross-boundary rules

- Product Manager may update `status` and `updated` fields in PRD docs when advancing lifecycle (IN_DEVELOPMENT→TESTING, TESTING→READY_FOR_DEPLOYMENT)
- Software Engineer may update `status` and `updated` fields in PRD docs when advancing APPROVED→IN_DEVELOPMENT or IN_DEVELOPMENT→TESTING
- QA Engineer may update `status` and `updated` fields in PRD docs when advancing TESTING→READY_FOR_DEPLOYMENT
- No agent modifies another agent's owned directories
- No agent modifies `.opencode/*` files
- Any agent may read `ai/project/schema/*` (reference only)
- Software Engineer writes `ai/project/schema/*` when schema changes

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
- `ai/agent/rules/access.md` is the source of truth for file ownership

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

- Location: `ai/project/changes/CHANGE-TASK-XXX.md`
- Template: `ai/agent/templates/change.md`
- Owner: Software Engineer
- Must exist BEFORE merging task branch to PRD branch
- Must document: files added/modified/removed, DB changes, API changes, validation results

---

## Test Reports

Every tested task creates a test report.

- Location: `ai/project/tests/TEST-TASK-XXX.md`
- Template: `ai/agent/templates/test.md`
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
- Always use official project templates from `ai/agent/`
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

---

## Writing for Two Audiences

Every module and flow document serves two audiences and must be structured accordingly:

### Audience 1 — Non-Developers (Simple Instructions)

The top section of every document must start with a **"Simple Instructions"** block written in plain English with zero code jargon.

Rules for Simple Instructions:
- No file paths, no `className.java`, no `interface Type { }`, no code snippets.
- Use everyday language: "click", "page", "button", "list", "form".
- Always include a short numbered step-by-step guide (3-7 steps).
- Always include a Mermaid **graph TD** diagram showing the user-facing flow.
- Always include a **Common Issues** table with problems and solutions a user would understand.

### Audience 2 — Developers (Technical Detail)

Below the Simple Instructions, provide the technical reference with `file:line` numbers, class names, SQL tables, API payloads, and the full sequence diagram. This is for engineers reading the docs.

---

## Diagram Policy

**Every** module doc must have at least one diagram. **Every** flow doc must have at least two diagrams (one user-facing graph TD in Simple Instructions, one technical sequence diagram).

Do NOT write a flow document without a sequence diagram. Do NOT write a module document without at least a graph TD showing how it fits into the system.

Use these diagram types:

| Scenario | Diagram Type |
|----------|-------------|
| User-facing steps (Simple Instructions) | `graph TD` (flowchart) |
| End-to-end flow steps | `sequenceDiagram` |
| Request routing (URL → component) | `graph TD` (flowchart) |
| Component tree / hierarchy | `graph TD` |
| API layer relationship | `graph LR` |
| Data flow through layers | `sequenceDiagram` |
| State transitions | `stateDiagram-v2` |
| DB table relationships | `erDiagram` |

---

## Module Document Front Matter

Every module document MUST start with this YAML front matter:

```yaml
---
module: <module-name>
type: backend | frontend
layer: <controller | service | repository | config | pages | components | hooks | stores | core>
last_updated: <ISO datetime>
last_updated_git_sha: <40-char commit hash>
paths:
  - backend/src/main/java/com/erp/...
  - frontend/src/...
---
```

---

## Incremental Update Strategy

To avoid re-scanning the entire codebase on every run:

1. **First run** — full scan of `backend/src/main/java/com/erp/` and `frontend/src/`, generate all module + flow docs.
2. **Subsequent runs:**
   - Read `last_updated_git_sha` from each module doc.
   - Run `git log --oneline <sha>..HEAD -- <paths>` scoped to that module's tracked paths.
   - Only re-analyze and update modules whose source files changed since the last recorded commit.
   - After updating, write the new HEAD commit SHA as `last_updated_git_sha`.
   - Re-validate any flow documents that reference updated modules.
