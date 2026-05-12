Here’s a refined, production-grade AI agent prompt you can give to Cursor, Claude Code, Windsurf, Devin, Copilot Workspace, or any autonomous development agent.

---

# AI AGENT PROMPT — T1 Frontend Workspace Setup

You are a senior frontend architect responsible for creating the foundational frontend workspace for a large-scale enterprise Dynamic ERP platform.

Your goal is to build a scalable, maintainable, enterprise-grade React frontend foundation using modern best practices.

You must fully implement all requirements below.

---

# PROJECT OBJECTIVE

Create the foundational frontend workspace that establishes:

- scalable architecture
- strict TypeScript standards
- enterprise folder organization
- development tooling
- linting + formatting
- Git commit protections
- routing foundation
- environment management
- maintainable developer workflow

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

- Ensure application starts successfully
- Ensure Vite dev server works
- Ensure TypeScript compilation works

## Validation

Run:

```bash
pnpm dev
```

Expected:

- Browser opens successfully
- React app loads correctly

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

- Maintain clean modular separation
- Avoid feature chaos
- Follow enterprise architecture conventions

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

- Invalid types must fail compilation
- Strict enterprise type safety enforced

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

- `vite.config.ts`
- `tsconfig.json`

## Example

```json
{
  "paths": {
    "@/*": ["src/*"]
  }
}
```

## Requirements

- Aliases must resolve correctly
- No relative import hell

## Validation

This import must work:

```ts
import Button from '@/components/Button';
```

---

# TASK T1.5 — Setup ESLint

Install and configure ESLint with enterprise rules.

## Required Packages

- eslint
- typescript-eslint
- eslint-plugin-react
- eslint-plugin-import
- eslint-plugin-react-hooks

## Rules

Enforce:

- no unused vars
- import ordering
- consistent type imports
- React hooks rules
- TypeScript best practices

## Requirements

- lint command passes
- invalid code flagged properly

## Validation

This must produce lint error/warning:

```ts
const abc = 1;
```

---

# TASK T1.6 — Setup Prettier

Configure Prettier.

## Enforce

- semicolons
- single quotes
- trailing commas
- consistent line width

## Requirements

- formatting automated
- style consistency enforced

## Validation

Run:

```bash
pnpm prettier --write .
```

Expected:

- files formatted consistently

---

# TASK T1.7 — Setup Husky + lint-staged

Configure Git hooks.

## Pre-commit Requirements

Before commit automatically run:

- lint
- typecheck

## Requirements

- broken code cannot be committed

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

- environment variables accessible through Vite

## Validation

This must work:

```ts
import.meta.env.VITE_API_URL;
```

---

# TASK T1.9 — Setup Global Application Entry

Create scalable root application architecture.

## Implement

- AppProviders
- RouterProvider
- ErrorBoundary placeholder
- Theme placeholder

## Requirements

- providers centralized
- app composition clean and scalable

---

# TASK T1.10 — Setup Routing

Install React Router.

Create:

- route placeholders
- layout placeholders
- dashboard route

## Requirements

- routing operational
- future dynamic routing supported

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

- enterprise-grade scalability
- modular architecture
- strict typing
- maintainable structure
- clean code principles
- low coupling
- high cohesion
- future extensibility
- production readiness

---

# IMPORTANT IMPLEMENTATION RULES

- Prefer composition over monoliths
- Keep folders responsibility-driven
- Avoid premature business logic
- Create placeholders where future systems will integrate
- Keep architecture extensible for metadata-driven ERP runtime
- Use modern React patterns
- Use functional components only
- Avoid unnecessary dependencies

---

# EXPECTED FINAL RESULT

The result should be:

```txt
Enterprise-ready React + TypeScript frontend foundation for Dynamic ERP platform
```

---

# AI AGENT PROMPT — T2 Enterprise UI Framework Setup

You are a senior frontend architect responsible for building the enterprise UI foundation for a Dynamic ERP platform.

Your task is to implement a scalable, enterprise-grade UI architecture using React, TypeScript, Vite, and MUI.

The result must be production-ready, responsive, extensible, and prepared for future metadata-driven ERP runtime features.

This task builds on top of the existing T1 frontend foundation.

---

# PROJECT OBJECTIVE

Create the complete UI foundation layer for the ERP runtime including:

- enterprise application shell
- responsive navigation
- centralized theme system
- dark/light mode
- reusable layout architecture
- page container standards
- loading/error states
- routing integration
- scalable design system

This foundation will be reused across all ERP modules.

---

# REQUIRED STACK

| Area               | Technology            |
| ------------------ | --------------------- |
| UI Framework       | MUI                   |
| Styling            | MUI Theme System      |
| Icons              | MUI Icons             |
| Layout             | Flex/Grid             |
| Theme Persistence  | local state initially |
| Future Persistence | Zustand/localStorage  |
| Responsive System  | MUI Breakpoints       |

---

# PRIMARY GOAL

Create a scalable enterprise UI shell architecture suitable for large ERP systems.

---

# TASK T2.1 — Install UI Dependencies

Install required packages:

```bash
pnpm add @mui/material
pnpm add @emotion/react
pnpm add @emotion/styled
pnpm add @mui/icons-material
```

Optional but recommended:

```bash
pnpm add @fontsource/inter
```

## Requirements

- no dependency conflicts
- application builds successfully
- MUI fully operational

## Validation

Run:

```bash
pnpm build
```

Expected:

```txt
Build succeeds without errors
```

---

# TASK T2.2 — Create Global Theme System

Create centralized enterprise theme architecture.

---

# REQUIRED STRUCTURE

```txt
src/themes/
 ├── index.ts
 ├── palette.ts
 ├── typography.ts
 ├── shadows.ts
 ├── lightTheme.ts
 └── darkTheme.ts
```

---

# REQUIREMENTS

Theme architecture must support:

- light mode
- dark mode
- future tenant branding
- runtime customization
- component overrides
- scalable design tokens

---

# IMPLEMENTATION REQUIREMENTS

Create theme factory:

```ts
createAppTheme(mode);
```

Theme should centralize:

- palette
- typography
- spacing
- shadows
- border radius
- z-indexes
- breakpoints
- component overrides

---

# ACCEPTANCE CRITERIA

- no inline hardcoded styling patterns
- theme fully centralized
- dark/light switching supported globally

---

# VALIDATION

Switch theme mode.

Expected:

```txt
Entire app updates correctly
```

---

# TASK T2.3 — Setup Typography System

Create enterprise typography standards.

---

# REQUIREMENTS

Define typography system for:

- headings
- body text
- captions
- labels
- table text
- monospace/code text

Recommended font:

- Inter

---

# ACCEPTANCE CRITERIA

- typography globally consistent
- reusable typography scales
- responsive readability maintained

---

# TASK T2.4 — Setup Global Theme Provider

Create centralized application providers.

---

# CREATE

```txt
src/app/AppProviders.tsx
```

---

# REQUIREMENTS

AppProviders must include:

- ThemeProvider
- CssBaseline
- ThemeMode context/provider
- future provider placeholders
- notification provider placeholder

---

# ACCEPTANCE CRITERIA

- global styles applied
- theme accessible globally
- providers cleanly composed

---

# VALIDATION

Theme object accessible inside any component.

---

# TASK T2.5 — Build Enterprise Layout Shell

Create reusable ERP application shell.

---

# REQUIRED COMPONENTS

```txt
src/components/layouts/
 ├── AppLayout/
 ├── Sidebar/
 ├── Header/
 ├── ContentArea/
 └── Footer/
```

---

# LAYOUT REQUIREMENTS

Layout must support:

- sidebar navigation
- top header
- content area
- isolated content scrolling
- responsive collapse behavior
- future dynamic menu loading
- future plugin injection zones

---

# TARGET STRUCTURE

```txt
┌───────────────────────────┐
│ Header                    │
├──────────┬────────────────┤
│ Sidebar  │ Content        │
│          │                │
└──────────┴────────────────┘
```

---

# ACCEPTANCE CRITERIA

- responsive layout operational
- shell reusable globally
- layout scalable for ERP complexity

---

# VALIDATION

Resize browser.

Expected:

```txt
Sidebar adapts responsively
```

---

# TASK T2.6 — Create Responsive ERP Sidebar

Build scalable ERP navigation system.

---

# REQUIREMENTS

Sidebar must support:

- nested menus
- menu groups
- icons
- collapse mode
- active route highlighting
- mobile drawer behavior
- future permission-based rendering
- future favorites/recent items
- future plugin menus

---

# ACCEPTANCE CRITERIA

- navigation state maintained
- responsive behavior works
- active routes highlighted correctly

---

# VALIDATION

### Mobile Width

Expected:

```txt
Sidebar becomes drawer
```

### Route Selection

Expected:

```txt
Active menu highlighted
```

---

# TASK T2.7 — Create Enterprise Header

Build reusable topbar/header.

---

# REQUIREMENTS

Header must support:

- page title
- breadcrumb placeholder
- theme toggle
- notifications placeholder
- profile menu placeholder
- future global search placeholder

---

# ACCEPTANCE CRITERIA

- reusable globally
- responsive layout
- clean enterprise spacing

---

# VALIDATION

Toggle theme.

Expected:

```txt
Theme changes globally
```

---

# TASK T2.8 — Implement Dark/Light Theme Toggle

Create runtime theme switching.

---

# REQUIREMENTS

Initial implementation:

- local React state/context

Future-ready for:

- Zustand persistence
- localStorage persistence

---

# ACCEPTANCE CRITERIA

- instant theme switching
- no page reload required
- entire UI updates dynamically

---

# VALIDATION

Toggle theme.

Expected:

```txt
Entire UI updates dynamically
```

---

# TASK T2.9 — Create Reusable PageContainer

Standardize ERP page layouts.

---

# CREATE

```txt
src/components/layouts/PageContainer
```

---

# REQUIREMENTS

PageContainer must support:

- title
- subtitle
- breadcrumbs placeholder
- actions area
- toolbar placeholder
- tabs placeholder
- consistent content padding

---

# ACCEPTANCE CRITERIA

- all pages visually consistent
- reusable page composition pattern

---

# TASK T2.10 — Create Dashboard Placeholder

Verify layout architecture.

---

# REQUIREMENTS

Create dashboard page containing:

- sample metric cards
- sample widgets
- responsive grid
- placeholder ERP widgets

---

# ACCEPTANCE CRITERIA

- dashboard renders correctly
- responsive behavior verified
- layout shell integration verified

---

# VALIDATION

Open dashboard.

Expected:

```txt
Dashboard renders inside layout shell
```

---

# TASK T2.11 — Create Design Tokens System

Centralize spacing and sizing standards.

---

# REQUIREMENTS

Define centralized tokens for:

- spacing scale
- border radius
- shadows
- z-indexes
- layout widths
- sidebar widths
- header heights

---

# ACCEPTANCE CRITERIA

- no arbitrary spacing values
- design system fully centralized

---

# TASK T2.12 — Create Global UX State Components

Prepare reusable UX states.

---

# REQUIRED COMPONENTS

```txt
- Loader
- FullPageLoader
- EmptyState
- ErrorState
- NoDataState
```

---

# REQUIREMENTS

Components must be:

- reusable globally
- theme-aware
- visually consistent
- accessible

---

# ACCEPTANCE CRITERIA

- shared UX states standardized

---

# TASK T2.13 — Setup Notification System Placeholder

Prepare future runtime notifications.

---

# REQUIREMENTS

Setup architecture for:

- snackbar provider
- global toast API
- future centralized notifications

Recommended:

- notistack OR MUI Snackbar

---

# ACCEPTANCE CRITERIA

- notification API callable globally
- provider architecture prepared

---

# TASK T2.14 — Setup Error Boundary

Prevent runtime crashes.

---

# REQUIREMENTS

Create:

- ErrorBoundary
- fallback UI
- error logging placeholder
- retry action placeholder

---

# ACCEPTANCE CRITERIA

- application survives component crashes
- graceful fallback UI shown

---

# VALIDATION

Throw component error.

Expected:

```txt
Fallback UI shown
```

---

# TASK T2.15 — Integrate Layout with Routing

Integrate React Router with layout shell.

---

# ROUTE STRUCTURE

```txt
/login
/app/*
```

---

# REQUIREMENTS

ERP layout shell must wrap only authenticated routes.

Public routes must remain outside shell.

Prepare architecture for future:

- auth guards
- permissions
- role-based layouts

---

# ACCEPTANCE CRITERIA

- layout wraps app routes correctly
- public routes excluded from shell

---

# REQUIRED ENGINEERING STANDARDS

You MUST follow:

- enterprise scalability
- modular architecture
- strict typing
- reusable composition
- clean code principles
- low coupling
- high cohesion
- responsive-first design
- accessibility best practices
- future extensibility

---

# IMPORTANT IMPLEMENTATION RULES

- Use functional React components only
- Prefer composition over inheritance
- Avoid hardcoded values
- Use theme tokens everywhere
- Avoid inline styling when reusable styles belong in theme/system
- Keep all UI runtime-ready for metadata-driven rendering
- Prepare placeholders for future runtime engine integrations
- Keep components responsibility-driven

---

# FINAL ACCEPTANCE CRITERIA

The task is COMPLETE only if ALL conditions below pass:

```txt
✓ MUI integrated
✓ Theme system centralized
✓ Dark/light mode works
✓ App shell works
✓ Sidebar responsive
✓ Header implemented
✓ Page container reusable
✓ Layout routing integrated
✓ Error boundary works
✓ Loading states reusable
✓ Build passes
```

---

# FINAL VALIDATION COMMANDS

The AI agent MUST run:

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

1. Full implemented codebase
2. Updated folder structure
3. Theme architecture explanation
4. Layout architecture explanation
5. Routing architecture explanation
6. Installed dependencies
7. Validation results
8. Commands to run the project
9. Any assumptions made

---

# EXPECTED FINAL RESULT

The final result should be:

```txt
Enterprise-ready ERP UI shell with scalable MUI architecture
```
