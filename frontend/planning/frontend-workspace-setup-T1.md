# T1 → Frontend Workspace Setup

## Objective

Create a scalable enterprise-grade frontend foundation for the dynamic ERP runtime.

This task establishes:

- project structure
- code standards
- build tooling
- TypeScript architecture
- developer workflow

This is the base for everything else.

---

# Expected Stack

| Area               | Technology         |
| ------------------ | ------------------ |
| Framework          | React              |
| Language           | TypeScript         |
| Build Tool         | Vite               |
| Package Manager    | pnpm (recommended) |
| Lint               | ESLint             |
| Formatter          | Prettier           |
| Git Hooks          | Husky              |
| Commit Validation  | lint-staged        |
| Import Aliases     | tsconfig paths     |
| Environment Config | dotenv             |

---

# Deliverables

Developer must deliver:

```txt id="t1a"
- Working React app
- TypeScript strict mode
- Vite configured
- ESLint configured
- Prettier configured
- Husky pre-commit hooks
- Path aliases working
- Environment variable setup
- Modular folder structure
- Build passing
```

---

# STEP-BY-STEP TASKS

---

# T1.1 — Initialize Project

## Objective

Create Vite React TypeScript project.

## Steps

```bash id="t1b"
pnpm create vite dynamic-erp --template react-ts
```

OR

```bash id="t1c"
npm create vite@latest dynamic-erp -- --template react-ts
```

---

## Acceptance Criteria

- project runs successfully
- TypeScript compiles
- Vite dev server works

---

## Test Cases

### TC-1

Run:

```bash id="t1d"
pnpm dev
```

Expected:

```txt id="t1e"
Application opens successfully in browser
```

---

# T1.2 — Setup Folder Structure

## Objective

Create scalable enterprise structure.

## Required Structure

```txt id="t1f"
src/
 ├── app/
 ├── core/
 │    ├── api/
 │    ├── auth/
 │    ├── metadata/
 │    ├── registry/
 │    ├── runtime/
 │    └── store/
 │
 ├── engine/
 │    ├── forms/
 │    ├── grids/
 │    ├── layouts/
 │    ├── workflows/
 │    └── actions/
 │
 ├── components/
 │    ├── fields/
 │    ├── layouts/
 │    ├── tables/
 │    ├── dialogs/
 │    └── widgets/
 │
 ├── hooks/
 ├── routes/
 ├── themes/
 ├── utils/
 ├── modules/
 └── assets/
```

---

## Acceptance Criteria

- structure created
- folders properly grouped
- no feature chaos

---

# T1.3 — Configure TypeScript Strict Mode

## Objective

Enforce enterprise-level type safety.

## Steps

Update:

```txt id="t1g"
tsconfig.json
```

Enable:

```json id="t1h"
{
  "compilerOptions": {
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noImplicitReturns": true
  }
}
```

---

## Acceptance Criteria

- strict mode enabled
- invalid typing causes compile failure

---

## Test Cases

### TC-1

Add invalid type:

```ts id="t1i"
const x: string = 123;
```

Expected:

```txt id="t1j"
TypeScript compilation error
```

---

# T1.4 — Setup Path Aliases

## Objective

Avoid relative import hell.

## Required Aliases

```txt id="t1k"
@/components
@/core
@/engine
@/hooks
@/utils
```

---

## Steps

Update:

```txt id="t1l"
vite.config.ts
tsconfig.json
```

Example:

```json id="t1m"
{
  "paths": {
    "@/*": ["src/*"]
  }
}
```

---

## Acceptance Criteria

- aliases resolve correctly

---

## Test Cases

### TC-1

```ts id="t1n"
import Button from '@/components/Button';
```

Expected:

```txt id="t1o"
Import resolves correctly
```

---

# T1.5 — Setup ESLint

## Objective

Enforce code quality standards.

## Requirements

Rules:

- no unused vars
- import ordering
- consistent types
- React hooks rules

---

## Packages

```bash id="t1p"
pnpm add -D eslint
```

Plus:

- typescript-eslint
- eslint-plugin-react
- eslint-plugin-import
- eslint-plugin-react-hooks

---

## Acceptance Criteria

- lint command passes
- bad code flagged

---

## Test Cases

### TC-1

Unused variable:

```ts id="t1q"
const abc = 1;
```

Expected:

```txt id="t1r"
ESLint warning/error
```

---

# T1.6 — Setup Prettier

## Objective

Enforce consistent formatting.

## Requirements

Configure:

- semicolons
- quotes
- trailing commas
- line width

---

## Acceptance Criteria

- formatting automatic
- no style inconsistencies

---

## Test Cases

### TC-1

Run:

```bash id="t1s"
pnpm prettier --write .
```

Expected:

```txt id="t1t"
Files formatted consistently
```

---

# T1.7 — Setup Husky + lint-staged

## Objective

Prevent broken commits.

## Requirements

Pre-commit should run:

- lint
- typecheck

---

## Acceptance Criteria

- invalid code cannot commit

---

## Test Cases

### TC-1

Commit with lint error.

Expected:

```txt id="t1u"
Commit blocked
```

---

# T1.8 — Setup Environment Configuration

## Objective

Support multiple environments.

## Required Files

```txt id="t1v"
.env
.env.development
.env.production
```

---

## Example Variables

```txt id="t1w"
VITE_API_URL=
VITE_APP_NAME=
```

---

## Acceptance Criteria

- environment variables accessible

---

## Test Cases

### TC-1

```ts id="t1x"
import.meta.env.VITE_API_URL;
```

Expected:

```txt id="t1y"
Correct environment value returned
```

---

# T1.9 — Setup Global App Entry

## Objective

Prepare root application structure.

## Requirements

Create:

- AppProviders
- RouterProvider
- ErrorBoundary placeholder
- Theme placeholder

---

## Acceptance Criteria

- app wrapped cleanly
- providers centralized

---

# T1.10 — Setup Basic Routing

## Objective

Prepare future dynamic routing.

## Requirements

Install:

- React Router

Create:

- route placeholders
- layout placeholders

---

## Acceptance Criteria

- routing operational

---

## Test Cases

### TC-1

Navigate:

```txt id="t1z"
/dashboard
```

Expected:

```txt id="t1aa"
Dashboard route renders
```

---

# T1.11 — Setup Build Scripts

## Objective

Standardize developer commands.

## Required Scripts

```json id="t1ab"
{
  "dev": "",
  "build": "",
  "preview": "",
  "lint": "",
  "typecheck": "",
  "test": ""
}
```

---

## Acceptance Criteria

All scripts execute successfully.

---

# T1.12 — Setup Git Ignore

## Objective

Prevent unnecessary commits.

## Must Ignore

```txt id="t1ac"
node_modules
dist
.env
coverage
```

---

# FINAL ACCEPTANCE CRITERIA FOR T1

Developer is DONE only when:

```txt id="t1ad"
✓ Vite app works
✓ TypeScript strict mode works
✓ ESLint works
✓ Prettier works
✓ Husky blocks bad commits
✓ Aliases work
✓ Routing works
✓ Environment variables work
✓ Folder structure finalized
✓ Build passes
```

---

# FINAL VALIDATION COMMANDS

Developer must run:

```bash id="t1ae"
pnpm lint
pnpm typecheck
pnpm build
```

Expected:

```txt id="t1af"
All commands pass successfully
```

---

# OUTPUT OF T1

After T1 we should have:

```txt id="t1ag"
Enterprise-ready React foundation
```
