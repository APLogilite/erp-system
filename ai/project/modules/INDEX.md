# Module Index

> Auto-maintained by Technical Writer. Update this file when creating, updating, or deleting module documents.

## identity/

| File | Description |
|------|-------------|
| `access-rules.md` | AI agent access control — pre-commit hook enforces file/branch permissions per role |
| `auth.md` | JWT authentication — login, logout, token refresh, password change |
| `auth-frontend.md` | Frontend auth state — Zustand store with JWT persistence |
| `identity-admin.md` | Multi-tenant CRUD — tenants, orgs, companies, branches, depts, users, roles, permissions |
| `identity-pages.md` | Identity UI pages — login, context selection, admin CRUD screens |
| `login.md` | Login page — username/password/tenant form, JWT storage, dashboard redirect |
| `security.md` | Security framework — JWT filter chain, RuntimeContext, auth annotations |

## metadata/

| File | Description |
|------|-------------|
| `form-designer.md` | Form designer APIs — CRUD on form definitions, fields, layouts, rules, sub-forms |
| `runtime.md` | Runtime metadata API — assembles form definitions, executes dynamic CRUD |
| `table-designer.md` | Table designer APIs — register tables, define columns, execute DDL |
| `window.md` | Window hierarchy schema (PRD-004) — windows, tabs, fields, menus, role access |

## runtime/

| File | Description |
|------|-------------|
| `api-client.md` | Axios HTTP client — interceptors, auth injection, error handling |
| `components.md` | Shared UI components — dialogs, fields, tables, registry system |
| `form-renderer.md` | Dynamic form renderer — metadata-driven field rendering with component registry |
| `router.md` | React Router v6 — route definitions, guard components (auth, role, context) |
| `runtime-hooks.md` | Custom hooks — form state, dirty tracking, record navigation, keyboard shortcuts |
| `runtime-window.md` | Window runtime UI — dynamic list/detail views, drill-down, breadcrumbs |
| `stores.md` | Zustand stores — auth, UI state, notifications, metadata cache |

## infrastructure/

| File | Description |
|------|-------------|
| `business-modules.md` | 19 business module CRUD layers — pattern overview (entity/service/controller) |
| `common.md` | Shared backend — ApiResponse envelope, BaseEntity, BaseService, global config |
| `context.md` | Multi-tenant context — resolution, switching, user preference persistence |
| `schema-ddl.md` | DDL reference — centralized CREATE TABLE statements for all tables |

## services/

| File | Description |
|------|-------------|
| `order.md` | Order management — sales/purchase orders, order lines, status flow |
| `product.md` | Product catalog — definitions, categories, pricing, UOM, SKU tracking |

## pages/

| File | Description |
|------|-------------|
| `dashboard.md` | Main dashboard — activity overview, quick navigation, user context |
