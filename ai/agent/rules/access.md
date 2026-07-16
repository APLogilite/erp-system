# Centralized Access Control Rules

_AI agents read this file at startup to determine their permitted paths and branches._

## Agent Roles

| Role | Label | Allow Paths | Allow Branches |
|------|-------|-------------|----------------|
| `se` | Software Engineer | `backend/**`, `frontend/**`, `ai/project/tasks/**`, `ai/project/changes/**`, `ai/project/schema/**`, `ai/agent/project-board.md`, `ai/agent/changelog.md`, `ai/project/failures/**` | `feature/*`, `bugfix/*`, `enhancement/*`, `prd/*` |
| `qa` | QA Engineer | `ai/project/tasks/**`, `ai/project/tests/**`, `ai/project/scripts/**`, `ai/project/schema/**`, `ai/agent/project-board.md`, `ai/agent/changelog.md`, `ai/project/failures/**` | `prd/*` |
| `pm` | Product Manager | `ai/project/prd/**`, `ai/project/tasks/**`, `ai/agent/*.md`, `ai/agent/project-board.md`, `ai/agent/changelog.md`, `ai/project/schema/**`, `ai/project/failures/**` | `main` |
| `tw` | Technical Writer | `ai/project/modules/**`, `ai/project/flows/**`, `ai/project/schema/**`, `ai/project/failures/**` | `*` |

## Shared Paths

Paths that multiple agents are permitted to modify:

| Path | Agents |
|------|--------|
| `ai/agent/project-board.md` | se, qa, pm |
| `ai/agent/changelog.md` | se, qa, pm |

## Pattern Rules

| Pattern | Meaning |
|---------|---------|
| `**` | Matches zero or more path segments (recursive) |
| `*` | Matches any characters within a single path segment (no `/`) |
| `?` | Matches any single character except `/` |

## Enforcement

The pre-commit hook runs `scripts/check-access.mjs` which:

1. Reads `.agent-role` — if absent, assumes human and skips checks
2. Validates that the current branch matches `allow_branches` for the agent role
3. Validates that every staged file matches `allow_paths` for the agent role
4. Warns if `se` stages migration files without corresponding schema updates
