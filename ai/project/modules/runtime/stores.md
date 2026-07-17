---
module: stores
type: frontend
layer: stores
last_updated: 2026-07-17T00:00:00+05:30
last_updated_git_sha: 19daf230d090cda6fed91577c3b50848c2e4da64
paths:
  - frontend/src/core/auth/authStore.ts
  - frontend/src/core/store/ui/uiStore.ts
  - frontend/src/core/store/notifications/notificationStore.ts
  - frontend/src/core/metadata/metadataStore.ts
---

# Stores Frontend

## Purpose
Zustand-based state management stores for the frontend. Handles authentication, UI state (sidebar, theme), notifications, and metadata cache. Each store is independent and follows the Zustand pattern with `set()` and `get()` for immutable state updates.

---

## Simple Instructions *(for non-developers)*

### What is this?
These are the "memory" of the frontend application. They store information that needs to be available across different pages — like who you are (auth), whether the sidebar is open (UI), and notification messages. You don't interact with them directly; the pages and components read from and write to them automatically.

### What can you do here?
These stores work automatically in the background:
- **Auth Store**: Remembers your login session, user info, and permissions
- **UI Store**: Remembers whether the sidebar is collapsed, your theme preference
- **Notification Store**: Shows success/error/info messages as pop-ups
- **Metadata Store**: Caches form definitions so they load faster

### How to use it
You never call these stores directly. They are used by:
- The **sidebar** to know if it should be expanded or collapsed
- The **login page** to save your session after login
- **All pages** to show toast notifications (e.g., "Record saved successfully")
- **Dynamic forms** to access cached form definitions

### Diagram

```mermaid
graph LR
  A[LoginPage] -->|login()| B[authStore]
  B -->|user + token| C[All pages via context]
  D[Sidebar] -->|toggle| E[uiStore]
  E -->|sidebar state| D
  F[Any page] -->|show toast| G[notificationStore]
  G -->|pop-up message| F
  H[DynamicForm] -->|get cached| I[metadataStore]
  I -->|form bundle| H
```

### Common issues
| Problem | Solution |
|---------|----------|
| Logged out unexpectedly | Your session token has expired. The auth store clears on 401 response from API. |
| Sidebar keeps closing | The UI store saves the sidebar state in localStorage. Check `uiStore.sidebarCollapsed`. |
| Duplicate notifications | Each notification has a unique ID. The store limits to the latest 5 messages. |

---

## Key Files *(developers)*

| File | State | Actions |
|------|-------|---------|
| `authStore.ts` | `{ user, token, refreshToken, isAuthenticated, currentTenant }` | `login()`, `logout()`, `setUser()`, `refreshToken()`, `setTenant()` |
| `uiStore.ts` | `{ sidebarCollapsed, themeMode }` | `toggleSidebar()`, `setSidebar()`, `setTheme()` |
| `notificationStore.ts` | `{ notifications[] }` | `showSuccess()`, `showError()`, `showInfo()`, `dismiss()`, `clearAll()` |
| `metadataStore.ts` | `{ formBundles: Map }` | `getBundle()`, `setBundle()`, `invalidate()` |

---

## Dependencies
- `zustand` — state management library
- `apiClient.ts` — Auth store calls login/refresh endpoints
- `localStorage` — Auth store persists token across page reloads
- `React Query` — Metadata store works alongside React Query cache for server state

---

## Related Backend
- `backend-auth.md` — Login/refresh endpoints called by authStore
- `core-metadata-runtime.md` — Form bundle API cached by metadataStore
