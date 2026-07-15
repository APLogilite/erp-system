# Centralized Access Control Rules

_AI agents read this file at startup to determine their permitted paths and branches._

## Agent Roles

| Role | Label | Allow Paths | Allow Branches |
|------|-------|-------------|----------------|
| `se` | Software Engineer | `backend/**`, `frontend/**`, `ai/tasks/**`, `ai/changes/**`, `ai/schema/**`, `ai/PROJECT_BOARD.md`, `ai/docs/CHANGELOG.md`, `ai/failures/**` | `feature/*`, `bugfix/*`, `enhancement/*`, `prd/*` |
| `qa` | QA Engineer | `ai/tasks/**`, `ai/tests/**`, `ai/scripts/**`, `ai/schema/**`, `ai/PROJECT_BOARD.md`, `ai/docs/CHANGELOG.md`, `ai/failures/**` | `prd/*` |
| `pm` | Product Manager | `ai/prd/**`, `ai/tasks/**`, `ai/docs/*.md`, `ai/PROJECT_BOARD.md`, `ai/docs/CHANGELOG.md`, `ai/schema/**`, `ai/failures/**` | `main` |
| `tw` | Technical Writer | `ai/modules/**`, `ai/flows/**`, `ai/schema/**`, `ai/failures/**` | `*` |

## Shared Paths

Paths that multiple agents are permitted to modify:

| Path | Agents |
|------|--------|
| `ai/PROJECT_BOARD.md` | se, qa, pm |
| `ai/docs/CHANGELOG.md` | se, qa, pm |

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
