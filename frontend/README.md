# Dynamic ERP Frontend

React 18 / TypeScript frontend for the Dynamic ERP system. Runs on port `5173`.

## Technology Stack

- **Framework**: React 18 with TypeScript (strict mode)
- **Build Tool**: Vite
- **UI Library**: MUI 5 (Material-UI), sx prop, styled components
- **State Management**: Zustand (client state) + React Query / TanStack Query (server state)
- **Routing**: React Router v6
- **Grid**: AG Grid Enterprise
- **Code Quality**: ESLint (max-warnings=0), Prettier, Husky, lint-staged
- **Validation**: Zod (metadata schema validation)
- **API Client**: Axios with JWT interceptor

## Project Structure

```
frontend/
├── src/
│   ├── main.tsx                          # Application entry point
│   ├── App.tsx                           # Root component
│   ├── ErrorBoundary.tsx                 # Error handling
│   ├── vite-env.d.ts
│   ├── app/
│   │   └── providers/                    # Query + Theme + Registry + Router providers
│   ├── core/
│   │   ├── api/                          # Axios client, interceptors, request helpers
│   │   ├── auth/                         # JWT auth logic (login, logout, token refresh)
│   │   ├── metadata/                     # Metadata store, cache, selectors, types, schema
│   │   │   └── schema/                   # Zod-validated metadata schemas
│   │   ├── query/                        # React Query hooks and configuration
│   │   ├── registry/                     # Field/layout/action/workflow/view registries
│   │   ├── router/                       # Route configuration
│   │   ├── runtime/                      # Runtime config, constants
│   │   └── store/                        # Zustand stores (UI, notifications)
│   ├── engine/
│   │   ├── forms/                        # DynamicFormRenderer, FormFieldRenderer
│   │   ├── grids/                        # AG Grid configuration and adapters
│   │   ├── layouts/                      # Layout section renderers
│   │   ├── actions/                      # Toolbar action handlers
│   │   └── workflows/                    # Workflow step definitions
│   ├── components/
│   │   ├── fields/                       # Form field components (text, number, date, select...)
│   │   ├── layouts/                      # Layout components (tabs, columns, sections)
│   │   ├── tables/                       # Table/grid components
│   │   ├── dialogs/                      # Modal dialog components
│   │   ├── widgets/                      # Reusable widget components
│   │   └── ui/                           # Basic UI components (buttons, inputs, cards)
│   ├── modules/
│   │   ├── admin/                        # Admin configuration forms UI
│   │   └── identity/                     # Identity management UI
│   ├── hooks/                            # Custom React hooks
│   ├── routes/                           # Route definitions
│   ├── themes/                           # MUI theme configuration
│   ├── styles/                           # Global CSS and style utilities
│   ├── types/                            # TypeScript type definitions
│   ├── utils/                            # Utility functions
│   └── assets/                           # Static assets (images, icons)
├── .husky/                               # Git hooks (pre-commit → lint-staged → typecheck)
├── setup.sh                              # Local Node.js 22 + pnpm installation
├── start.sh                              # Dev server launcher
├── vite.config.ts                        # Vite config (aliases, plugins, proxy)
├── tsconfig.json                         # TypeScript configuration
├── .eslintrc.cjs                         # ESLint rules (strict)
├── .prettierrc                           # Prettier formatting
└── .lintstagedrc.json                    # Lint-staged configuration
```

## Getting Started

### Prerequisites

- Node.js 22+ and pnpm (or use the setup script)

### Environment Setup

Run the automated setup script:

```bash
./setup.sh
```

This will:

- Download and install Node.js 22 locally into `.local/`
- Install pnpm
- Install project dependencies

### Available Scripts

```bash
# Development
pnpm dev          # Start development server (port 5173)
pnpm build        # Typecheck + production build
pnpm preview      # Preview production build

# Code Quality
pnpm lint         # ESLint with --max-warnings=0
pnpm typecheck    # tsc --noEmit
pnpm format       # Prettier --write .

# Git Hooks
pnpm prepare      # Set up Husky git hooks
```

## Architecture

### Metadata-Driven Runtime Renderer

The frontend renders forms dynamically from backend metadata — no hardcoded form JSX:

```
Backend API ──→ Form Definition Bundle (cached 5 min)
             ──→ Record Data (always fresh)
                      │
                      ▼
            DynamicFormRenderer
                      │
            ┌─────────┼─────────┐
            ▼         ▼         ▼
      FormFields   Layout     SubForms
      (registry)  (sections)  (tabs)
```

1. **Form Definition Bundle** — Cached JSON with field definitions, layout sections, sub-form configs, rules, and validations
2. **Record Data** — Fresh data payload for the current record
3. **Renderers** — `DynamicFormRenderer` orchestrates layout sections; `FormFieldRenderer` picks the right field component from the registry

### Key Patterns

- **Two-request loading**: Form definition (cached) + data (fresh) fetched separately, abstracted by `useForm()` hook
- **Component registry**: Field/action/layout/view components registered by type — the engine resolves them at runtime
- **AG Grid Enterprise**: All list/grid views use AG Grid with server-side sorting, filtering, and pagination
- **Zustand**: Client state (auth, UI preferences, metadata cache)
- **React Query**: Server state (form data, record data) with automatic cache invalidation

### State Management

| Concern             | Tool                  | Store                         |
| ------------------- | --------------------- | ----------------------------- |
| Auth (JWT, user)    | Zustand               | `authStore`                   |
| UI (sidebar, theme) | Zustand               | `uiStore`                     |
| Notifications       | Zustand               | `notificationStore`           |
| Metadata cache      | Zustand + React Query | `metadataStore` + query cache |
| Record/form data    | React Query           | Query cache (auto-refetch)    |

### Component Registry

Components are registered by type and resolved dynamically:

- **Field registry** — Maps field types (string, number, date, enum, many2one, boolean) to React components
- **Layout registry** — Maps layout types (tabs, columns, sections) to layout components
- **Action registry** — Maps toolbar actions (save, delete, refresh, prev/next) to handlers
- **Workflow registry** — Maps workflow steps (draft, confirm, approve, complete) to state machines
- **View registry** — Maps view types (form, grid, dashboard) to view components

## API Integration

- **Base URL**: `http://localhost:8081/api/v1` (configurable via `VITE_API_URL`)
- **Envelope**: All responses wrapped in `ApiResponse<T>` (`{ success, data, message, errorCode, details }`)
- **Auth**: Axios interceptor injects JWT Bearer token; 401 triggers auto-logout
- **React Query**: Server state caching with automatic refetch on mutation

## Environment Variables

Create `.env.development` for development settings:

```env
VITE_API_URL=http://localhost:8081/api/v1
VITE_APP_TITLE=Dynamic ERP
```

## Development

1. Start the backend server first (port 8081)
2. `pnpm dev` — starts Vite dev server on port 5173
3. Vite proxies `/api/*` to `http://localhost:8081`
4. Run `pnpm lint` and `pnpm typecheck` before committing

## Code Quality

- **ESLint**: `--max-warnings=0` — zero warnings policy
- **TypeScript**: `strict: true` in `tsconfig.json`
- **Pre-commit**: Husky runs `lint-staged` → `typecheck`. `lint-staged` runs `eslint --fix` + `prettier --write` on staged `*.{ts,tsx}` and Prettier on `*.{json,md,css,scss}`
- **Formatting**: Prettier with consistent config across the project
