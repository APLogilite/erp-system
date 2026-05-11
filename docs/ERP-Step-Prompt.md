Here’s a refined, production-grade AI agent prompt you can give to Cursor, Claude Code, Windsurf, Devin, Copilot Workspace, or any autonomous development agent.

---

# AI AGENT PROMPT — T1 Frontend Workspace Setup

You are a senior frontend architect responsible for creating the foundational frontend workspace for a large-scale enterprise Dynamic ERP platform.

Your goal is to build a scalable, maintainable, enterprise-grade React frontend foundation using modern best practices.

You must fully implement all requirements below.

---

# PROJECT OBJECTIVE

Create the foundational frontend workspace that establishes:

* scalable architecture
* strict TypeScript standards
* enterprise folder organization
* development tooling
* linting + formatting
* Git commit protections
* routing foundation
* environment management
* maintainable developer workflow

This setup will serve as the base platform for all future ERP modules.

---

# REQUIRED STACK

| Area              | Technology     |
| ----------------- | -------------- |
| Framework         | React          |
| Language          | TypeScript     |
| Build Tool        | Vite           |
| Package Manager   | pnpm           |
| Routing           | React Router   |
| Linting           | ESLint         |
| Formatting        | Prettier       |
| Git Hooks         | Husky          |
| Commit Validation | lint-staged    |
| Env Config        | dotenv         |
| Import Aliases    | tsconfig paths |

---

# PRIMARY GOAL

Create a production-ready frontend workspace with strict enterprise standards.

---

# TASK T1.1 — Initialize Project

## Create Project

Use:

```bash
pnpm create vite dynamic-erp --template react-ts
```

OR

```bash
npm create vite@latest dynamic-erp -- --template react-ts
```

## Requirements

* Ensure application starts successfully
* Ensure Vite dev server works
* Ensure TypeScript compilation works

## Validation

Run:

```bash
pnpm dev
```

Expected:

* Browser opens successfully
* React app loads correctly

---

# TASK T1.2 — Create Enterprise Folder Structure

Inside `src/` create the following structure:

```txt
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

## Requirements

* Maintain clean modular separation
* Avoid feature chaos
* Follow enterprise architecture conventions

---

# TASK T1.3 — Configure TypeScript Strict Mode

Update `tsconfig.json`.

Enable:

```json
{
  "compilerOptions": {
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noImplicitReturns": true
  }
}
```

## Requirements

* Invalid types must fail compilation
* Strict enterprise type safety enforced

## Validation

This should fail:

```ts
const x: string = 123;
```

---

# TASK T1.4 — Configure Path Aliases

Setup aliases for:

```txt
@/components
@/core
@/engine
@/hooks
@/utils
```

## Update

* `vite.config.ts`
* `tsconfig.json`

## Example

```json
{
  "paths": {
    "@/*": ["src/*"]
  }
}
```

## Requirements

* Aliases must resolve correctly
* No relative import hell

## Validation

This import must work:

```ts
import Button from '@/components/Button';
```

---

# TASK T1.5 — Setup ESLint

Install and configure ESLint with enterprise rules.

## Required Packages

* eslint
* typescript-eslint
* eslint-plugin-react
* eslint-plugin-import
* eslint-plugin-react-hooks

## Rules

Enforce:

* no unused vars
* import ordering
* consistent type imports
* React hooks rules
* TypeScript best practices

## Requirements

* lint command passes
* invalid code flagged properly

## Validation

This must produce lint error/warning:

```ts
const abc = 1;
```

---

# TASK T1.6 — Setup Prettier

Configure Prettier.

## Enforce

* semicolons
* single quotes
* trailing commas
* consistent line width

## Requirements

* formatting automated
* style consistency enforced

## Validation

Run:

```bash
pnpm prettier --write .
```

Expected:

* files formatted consistently

---

# TASK T1.7 — Setup Husky + lint-staged

Configure Git hooks.

## Pre-commit Requirements

Before commit automatically run:

* lint
* typecheck

## Requirements

* broken code cannot be committed

## Validation

Commit with lint error should fail.

---

# TASK T1.8 — Setup Environment Configuration

Create:

```txt
.env
.env.development
.env.production
```

## Variables

```env
VITE_API_URL=
VITE_APP_NAME=
```

## Requirements

* environment variables accessible through Vite

## Validation

This must work:

```ts
import.meta.env.VITE_API_URL
```

---

# TASK T1.9 — Setup Global Application Entry

Create scalable root application architecture.

## Implement

* AppProviders
* RouterProvider
* ErrorBoundary placeholder
* Theme placeholder

## Requirements

* providers centralized
* app composition clean and scalable

---

# TASK T1.10 — Setup Routing

Install React Router.

Create:

* route placeholders
* layout placeholders
* dashboard route

## Requirements

* routing operational
* future dynamic routing supported

## Validation

Navigating to:

```txt
/dashboard
```

Must render dashboard page.

---

# TASK T1.11 — Configure Build Scripts

Ensure `package.json` contains:

```json
{
  "scripts": {
    "dev": "",
    "build": "",
    "preview": "",
    "lint": "",
    "typecheck": "",
    "test": ""
  }
}
```

## Requirements

All scripts execute successfully.

---

# TASK T1.12 — Configure Git Ignore

Ensure `.gitignore` includes:

```txt
node_modules
dist
.env
coverage
```

---

# FINAL ACCEPTANCE CRITERIA

The task is COMPLETE only if ALL conditions below pass:

```txt
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

You MUST run and verify:

```bash
pnpm lint
pnpm typecheck
pnpm build
```

Expected:

```txt
All commands pass successfully
```

---

# REQUIRED OUTPUT FROM AI AGENT

The AI agent must provide:

1. Complete implemented codebase
2. All configuration files
3. Installed dependencies
4. Final folder structure
5. Summary of implemented tooling
6. Validation results
7. Any assumptions made
8. Commands required to run the project

---

# ENGINEERING STANDARDS

You MUST follow these standards:

* enterprise-grade scalability
* modular architecture
* strict typing
* maintainable structure
* clean code principles
* low coupling
* high cohesion
* future extensibility
* production readiness

---

# IMPORTANT IMPLEMENTATION RULES

* Prefer composition over monoliths
* Keep folders responsibility-driven
* Avoid premature business logic
* Create placeholders where future systems will integrate
* Keep architecture extensible for metadata-driven ERP runtime
* Use modern React patterns
* Use functional components only
* Avoid unnecessary dependencies

---

# EXPECTED FINAL RESULT

The result should be:

```txt
Enterprise-ready React + TypeScript frontend foundation for Dynamic ERP platform
```
