````md
# AI Code Agent Prompt — T3 State Management Setup

You are a senior frontend architect working on an enterprise-grade ERP platform.

Your task is to fully implement **T3 — State Management Setup** for the ERP runtime foundation.

IMPORTANT:

- Follow ALL architecture decisions exactly.
- Produce production-grade code.
- Use strict TypeScript.
- Keep architecture scalable and modular.
- Avoid overengineering.
- Maintain clean separation between:
  - UI state
  - Server state
  - Runtime metadata
  - Auth state
- No mock implementations unless explicitly necessary.
- Prefer composable and extensible patterns.

---

# PROJECT STACK

Framework:

- React
- TypeScript
- Vite

State Management:

- Zustand

Server State:

- React Query (@tanstack/react-query)

API Layer:

- Axios

Persistence:

- Zustand persist middleware
- localStorage/sessionStorage

Routing:

- React Router

---

# CORE ARCHITECTURE RULE

STRICTLY separate:

## Client/UI State

Handled ONLY by Zustand

Examples:

- sidebar state
- theme state
- dialogs
- active module
- runtime UI behavior
- auth session

## Server State

Handled ONLY by React Query

Examples:

- API responses
- metadata fetching
- CRUD operations
- caching
- background refetching

DO NOT mix responsibilities.

---

# IMPLEMENTATION OBJECTIVES

After implementation, the app must support:

✓ Zustand configured  
✓ React Query configured  
✓ Axios client configured  
✓ Auth store ready  
✓ UI store ready  
✓ Metadata store ready  
✓ Query provider integrated  
✓ Error handling foundation ready  
✓ Request interceptors ready  
✓ Cache strategy prepared

---

# T3.1 — INSTALL DEPENDENCIES

Install:

```bash
pnpm add zustand
pnpm add @tanstack/react-query
pnpm add axios
pnpm add @tanstack/react-query-devtools
```
````

Verify build succeeds.

---

# T3.2 — REACT QUERY SETUP

Create structure:

```txt
src/core/query/
 ├── queryClient.ts
 ├── QueryProvider.tsx
 ├── queryConfig.ts
 └── queryKeys.ts
```

Requirements:

- staleTime = 5 minutes
- retry = 1
- refetchOnWindowFocus = false
- centralized query defaults
- global QueryClient
- React Query Devtools enabled only in development
- support future invalidation strategy

Implement:

- query key factory pattern
- scalable cache architecture

Example:

```ts
export const queryKeys = {
  auth: {
    currentUser: ['auth', 'current-user'],
  },
  metadata: {
    all: ['metadata'],
    models: ['metadata', 'models'],
    views: ['metadata', 'views'],
  },
};
```

Wrap app with QueryProvider.

---

# T3.3 — AXIOS API LAYER

Create structure:

```txt
src/core/api/
 ├── client.ts
 ├── interceptors.ts
 ├── apiConfig.ts
 ├── endpoints.ts
 ├── errors.ts
 └── services/
```

Requirements:

- centralized axios instance
- configurable base URL
- request interceptors
- response interceptors
- auth token injection
- normalized API errors
- future refresh-token support
- typed responses

Implement:

- attach bearer token automatically
- normalize API errors into standard structure

Example normalized error:

```ts
type ApiError = {
  message: string;
  status: number;
  code?: string;
};
```

Handle:

- 401 unauthorized
- network errors
- timeout errors

NO direct axios usage in components.

---

# T3.4 — AUTH STORE (ZUSTAND)

Create structure:

```txt
src/core/auth/
 ├── authStore.ts
 ├── authTypes.ts
 ├── authActions.ts
 ├── authSelectors.ts
 └── storage.ts
```

State:

```ts
user;
token;
refreshToken;
roles;
permissions;
isAuthenticated;
```

Actions:

```ts
login();
logout();
setUser();
refreshSession();
```

Requirements:

- persisted auth session
- typed selectors
- minimal rerenders
- secure persistence design
- future refresh-token flow ready

Use Zustand persist middleware.

Persist:

- token
- refresh token
- auth user

Do NOT persist transient runtime state.

---

# T3.5 — UI STORE

Create structure:

```txt
src/core/store/ui/
 ├── uiStore.ts
 ├── uiTypes.ts
 ├── uiActions.ts
 └── uiSelectors.ts
```

State:

```ts
sidebarCollapsed;
currentTheme;
activeModule;
loadingStates;
dialogStates;
```

Requirements:

- ergonomic actions
- scalable UI runtime management
- no prop drilling

Persist ONLY:

- theme
- sidebarCollapsed

Do NOT persist:

- loading states
- dialog states

---

# T3.6 — METADATA STORE

Create structure:

```txt
src/core/metadata/
 ├── metadataStore.ts
 ├── metadataTypes.ts
 ├── metadataCache.ts
 ├── metadataSelectors.ts
 └── metadataActions.ts
```

State:

```ts
models;
views;
layouts;
workflows;
permissions;
```

Requirements:

- runtime metadata caching
- metadata invalidation
- lazy loading support
- optimized selectors
- scalable ERP runtime architecture

IMPORTANT:
Metadata fetching itself should use React Query.
Zustand should manage runtime metadata state coordination only.

---

# T3.7 — PERSIST MIDDLEWARE

Persist ONLY:

- auth session
- theme
- sidebar state

Do NOT persist:

- API cache
- loading states
- active forms
- temporary runtime data

Use:

- localStorage for durable state
- sessionStorage only where appropriate

---

# T3.8 — QUERY KEY STRATEGY

Create:

```txt
src/core/query/queryKeys.ts
```

Requirements:

- standardized query keys
- predictable invalidation
- nested domain-based keys

Include domains:

```ts
auth;
metadata;
models;
forms;
grids;
customers;
users;
```

Use factory functions where appropriate.

---

# T3.9 — GLOBAL ERROR HANDLING

Create centralized error layer.

Requirements:

- API error normalization
- user-friendly error mapping
- future logging hooks
- future telemetry compatibility

Create:

- parseApiError()
- getUserFriendlyErrorMessage()

Handle:

- 401
- 403
- 404
- 500
- network errors

401 should trigger:

- logout OR refresh flow placeholder

---

# T3.10 — LOADING STATE STRATEGY

Support:

```ts
page loading
table loading
button loading
metadata loading
```

Requirements:

- predictable loading APIs
- reusable loading selectors
- avoid duplicated loading state logic

---

# T3.11 — GLOBAL NOTIFICATION STORE

Create centralized notification system.

Suggested structure:

```txt
src/core/store/notifications/
```

Support:

- success
- error
- warning
- info

Requirements:

- enqueue notifications
- remove notifications
- future toast integration ready

---

# T3.12 — API SERVICE LAYER

Create:

```txt
src/core/api/services/
 ├── authService.ts
 ├── metadataService.ts
 ├── customerService.ts
```

Requirements:

- services wrap axios client
- normalize responses
- isolate endpoint definitions
- typed APIs
- reusable service architecture

Components must NEVER call axios directly.

---

# T3.13 — RUNTIME EVENT STRATEGY PREPARATION

Prepare lightweight architecture placeholder for future runtime events.

Future events:

```txt
form changed
workflow transitioned
metadata updated
relation selected
```

Requirements:

- lightweight event emitter placeholder
- no overengineering
- future middleware extensibility

Suggested structure:

```txt
src/core/runtime/
 ├── events.ts
 ├── eventBus.ts
 └── runtimeTypes.ts
```

---

# T3.14 — PROTECTED ROUTES

Create:

- AuthGuard
- ProtectedRoute
- GuestRoute

Requirements:

- unauthenticated users redirected to login
- authenticated users blocked from guest-only routes

Suggested structure:

```txt
src/core/router/guards/
```

Use React Router patterns.

---

# T3.15 — REACT QUERY DEVTOOLS

Requirements:

- enabled only in development
- integrated inside QueryProvider

---

# CODE QUALITY REQUIREMENTS

Use:

- strict TypeScript
- modular exports
- barrel exports where appropriate
- domain-driven folder organization
- scalable naming conventions
- strongly typed actions/selectors
- reusable utility patterns

Avoid:

- prop drilling
- duplicated logic
- giant stores
- direct mutable state
- tight coupling

---

# DELIVERABLES

Generate:

- all folder structures
- all required files
- all implementations
- all TypeScript types
- provider integrations
- route guards
- example service patterns
- example query usage
- example selectors
- persistence configuration
- error normalization utilities

Also:

- integrate everything into app bootstrap
- ensure imports resolve correctly
- ensure app builds successfully

---

# VALIDATION REQUIREMENTS

Run and verify:

```bash
pnpm lint
pnpm typecheck
pnpm build
```

Expected:

- zero type errors
- zero lint errors
- successful production build

---

# FINAL GOAL

At completion, the project should have:

```txt
Enterprise-grade frontend runtime foundation
```

This architecture will become the backbone of the ERP runtime engine.

Focus heavily on:

- scalability
- maintainability
- runtime extensibility
- clean separation of concerns
- future metadata-driven ERP architecture

```

```
