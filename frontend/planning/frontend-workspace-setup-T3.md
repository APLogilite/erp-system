# T3 — State Management Setup

## Objective

Create the application-wide state architecture for the ERP runtime.

This task establishes:

* global UI state
* metadata state
* auth state
* server state handling
* caching strategy
* runtime state management

This becomes the backbone of the frontend runtime engine.

---

# Core Architecture Decision

We will use:

| Purpose               | Technology                  |
| --------------------- | --------------------------- |
| Client State          | Zustand                     |
| Server State          | React Query                 |
| API Layer             | Axios                       |
| Persistence           | localStorage/sessionStorage |
| Future Runtime Events | Zustand middleware          |

---

# IMPORTANT DESIGN PRINCIPLE

## Separate:

### UI State

from

### Server State

---

## Zustand handles:

* sidebar state
* theme state
* current module
* auth session
* runtime UI behavior

---

## React Query handles:

* API data
* metadata loading
* caching
* CRUD mutations
* background refetching

This separation is critical.

---

# Target Outcome

After T3:

```txt id="t3a"
✓ Zustand configured
✓ React Query configured
✓ API client configured
✓ Auth store ready
✓ UI store ready
✓ Metadata store ready
✓ Query provider integrated
✓ Error handling foundation ready
✓ Request interceptors ready
✓ Cache strategy prepared
```

---

# STEP-BY-STEP TASKS

---

# T3.1 — Install Dependencies

## Objective

Install state and API libraries.

---

## Required Packages

```bash id="t3b"
pnpm add zustand
pnpm add @tanstack/react-query
pnpm add axios
```

Recommended:

```bash id="t3c"
pnpm add @tanstack/react-query-devtools
```

---

## Acceptance Criteria

* packages installed
* app builds successfully

---

## Test Cases

### TC-1

Run:

```bash id="t3d"
pnpm build
```

Expected:

```txt id="t3e"
Build succeeds successfully
```

---

# T3.2 — Setup React Query Provider

## Objective

Configure global server-state management.

---

## Required Structure

```txt id="t3f"
src/core/query/
 ├── queryClient.ts
 ├── QueryProvider.tsx
 └── queryConfig.ts
```

---

## Requirements

Configure:

* stale time
* retry logic
* cache time
* error handling

---

## Recommended Defaults

```txt id="t3g"
staleTime: 5 minutes
retry: 1
refetchOnWindowFocus: false
```

---

## Acceptance Criteria

* React Query available globally
* Devtools integrated
* Query cache operational

---

## Test Cases

### TC-1

Create sample query.

Expected:

```txt id="t3h"
Data cached successfully
```

---

# T3.3 — Setup Axios API Client

## Objective

Create centralized API layer.

---

## Required Structure

```txt id="t3i"
src/core/api/
 ├── client.ts
 ├── interceptors.ts
 ├── apiConfig.ts
 └── endpoints.ts
```

---

## Requirements

Configure:

* base URL
* auth headers
* request interceptors
* response interceptors
* error normalization

---

## Acceptance Criteria

* API requests centralized
* interceptors working

---

## Test Cases

### TC-1

Send API request.

Expected:

```txt id="t3j"
Correct headers attached
```

---

### TC-2

Receive API error.

Expected:

```txt id="t3k"
Normalized error returned
```

---

# T3.4 — Setup Auth Store (Zustand)

## Objective

Create authentication state management.

---

## Required Structure

```txt id="t3l"
src/core/auth/
 ├── authStore.ts
 ├── authTypes.ts
 ├── authActions.ts
 └── authSelectors.ts
```

---

## Required State

```txt id="t3m"
- user
- token
- refresh token
- roles
- permissions
- isAuthenticated
```

---

## Required Actions

```txt id="t3n"
- login()
- logout()
- setUser()
- refreshSession()
```

---

## Acceptance Criteria

* auth updates reactively
* persisted session supported

---

## Test Cases

### TC-1

Call:

```ts id="t3o"
login(user)
```

Expected:

```txt id="t3p"
User state updates globally
```

---

### TC-2

Reload browser.

Expected:

```txt id="t3q"
Session persists correctly
```

---

# T3.5 — Setup UI Store

## Objective

Manage runtime UI behavior.

---

## Required Structure

```txt id="t3r"
src/core/store/ui/
 ├── uiStore.ts
 ├── uiTypes.ts
 └── uiActions.ts
```

---

## Required State

```txt id="t3s"
- sidebarCollapsed
- currentTheme
- activeModule
- loadingStates
- dialogStates
```

---

## Acceptance Criteria

* UI updates globally
* no prop drilling required

---

## Test Cases

### TC-1

Toggle sidebar.

Expected:

```txt id="t3t"
Layout updates immediately
```

---

# T3.6 — Setup Metadata Store

## Objective

Manage runtime metadata state.

---

## Required Structure

```txt id="t3u"
src/core/metadata/
 ├── metadataStore.ts
 ├── metadataTypes.ts
 ├── metadataCache.ts
 └── metadataSelectors.ts
```

---

## Required State

```txt id="t3v"
- models
- views
- layouts
- workflows
- permissions
```

---

## Requirements

Support:

* runtime caching
* metadata invalidation
* lazy loading

---

## Acceptance Criteria

* metadata accessible globally
* cache works correctly

---

## Test Cases

### TC-1

Load model metadata.

Expected:

```txt id="t3w"
Metadata cached successfully
```

---

# T3.7 — Setup Persist Middleware

## Objective

Persist critical state safely.

---

## Requirements

Persist:

* auth session
* theme mode
* sidebar state

Do NOT persist:

* temporary runtime state
* active forms
* API cache

---

## Acceptance Criteria

* required state survives reload

---

## Test Cases

### TC-1

Refresh page.

Expected:

```txt id="t3x"
Theme and auth remain persisted
```

---

# T3.8 — Setup Query Keys Strategy

## Objective

Standardize React Query cache keys.

---

## Required Structure

```txt id="t3y"
src/core/query/queryKeys.ts
```

---

## Example

```ts id="t3z"
queryKeys = {
  metadata: {},
  models: {},
  forms: {},
  grids: {},
}
```

---

## Acceptance Criteria

* query keys standardized
* invalidation predictable

---

# T3.9 — Setup Error Handling Layer

## Objective

Centralize API/runtime errors.

---

## Requirements

Create:

* API error parser
* user-friendly error mapping
* future logging hooks

---

## Acceptance Criteria

* errors normalized globally

---

## Test Cases

### TC-1

API returns 401.

Expected:

```txt id="t3aa"
Logout or refresh flow triggered
```

---

# T3.10 — Setup Loading State Strategy

## Objective

Standardize loading behavior.

---

## Requirements

Support:

* page loading
* table loading
* button loading
* metadata loading

---

## Acceptance Criteria

* loading states predictable

---

# T3.11 — Setup Global Notification Store

## Objective

Prepare centralized notification handling.

---

## Required Features

Support:

* success messages
* warnings
* errors
* info messages

---

## Acceptance Criteria

* notifications trigger globally

---

# T3.12 — Setup API Service Layer Pattern

## Objective

Create scalable API architecture.

---

## Required Structure

```txt id="t3ab"
src/core/api/services/
 ├── authService.ts
 ├── metadataService.ts
 ├── customerService.ts
```

---

## Requirements

Services should:

* wrap axios
* normalize responses
* isolate endpoints

---

## Acceptance Criteria

* no direct axios calls in components

---

# T3.13 — Setup Runtime Event Strategy (Preparation)

## Objective

Prepare future ERP runtime events.

---

## Future Events

```txt id="t3ac"
- form changed
- workflow transitioned
- metadata updated
- relation selected
```

---

## Requirement

Create architecture placeholder only.

Do NOT overengineer yet.

---

# T3.14 — Setup Protected Route Logic

## Objective

Protect authenticated ERP routes.

---

## Requirements

Create:

* AuthGuard
* guest routes
* protected routes

---

## Acceptance Criteria

* unauthorized access blocked

---

## Test Cases

### TC-1

Open protected route unauthenticated.

Expected:

```txt id="t3ad"
Redirect to login
```

---

# T3.15 — Setup Query Devtools

## Objective

Enable runtime debugging.

---

## Requirements

Add:

* React Query Devtools

Development only.

---

## Acceptance Criteria

* query cache inspectable

---

# FINAL ACCEPTANCE CRITERIA FOR T3

Developer is DONE only when:

```txt id="t3ae"
✓ Zustand operational
✓ React Query operational
✓ Axios client centralized
✓ Auth store works
✓ Metadata store works
✓ UI store works
✓ Persist middleware works
✓ Protected routes work
✓ Query cache operational
✓ Error handling centralized
✓ Notifications ready
✓ API services standardized
✓ Build passes
```

---

# FINAL VALIDATION COMMANDS

```bash id="t3af"
pnpm lint
pnpm typecheck
pnpm build
```

Expected:

```txt id="t3ag"
All commands pass successfully
```

---

# OUTPUT OF T3

After T3 we should have:

```txt id="t3ah"
Enterprise-grade frontend runtime foundation
```

This is the point where:
the actual ERP runtime engine can begin.

After T3, we move to:

# T4 — Metadata Schema Design

This becomes the MOST important architectural phase of the entire system.
