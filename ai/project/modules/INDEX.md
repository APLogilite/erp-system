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
| `sys-tables.md` | Sys window/tab/table/column/menu metadata — window hierarchy definitions |

## runtime/

| File | Description |
|------|-------------|
| `admin-modules.md` | Admin identity pages + form/table designer + user self-service pages |
| `api-client.md` | Axios HTTP client — interceptors, auth injection, error handling |
| `components.md` | Shared UI components — dialogs, fields, tables, registry system |
| `core-query.md` | React Query configuration — QueryClient, cache defaults, typed hooks |
| `core-registry.md` | Registry system — field/action/layout/view/workflow component registries |
| `engine-forms.md` | Rendering engine — DynamicFormRenderer, FormFieldRenderer, grids, layouts |
| `form-renderer.md` | Dynamic form renderer — metadata-driven field rendering with component registry |
| `router-guards.md` | Route definitions + guard components (auth, role, context) |
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

## business/

| File | Description |
|------|-------------|
| `accounting.md` | General ledger — chart of accounts, journal entries, posting engine |
| `analytics.md` | Reporting & BI — dashboards, KPI, charts, pivot tables, drill-down |
| `assets.md` | Fixed asset register — acquisition, depreciation, location tracking |
| `businesspartner.md` | Customer/supplier registry — companies, addresses, contacts |
| `codegen.md` | Code generation tool — scaffolds DTO + Service for entities |
| `crm.md` | Customer relationship — leads, opportunities, sales pipeline |
| `hr.md` | Human resources — employees, departments, organization hierarchy |
| `inventory.md` | Stock management — balances, transactions, movements, allocations |
| `manufacturing.md` | Production — BOM, work centers, routings, manufacturing orders, MRP |
| `modules-auth.md` | Business module-level authentication and access control |
| `platform.md` | Shared services — attachments, comments, audit, notifications, email, search |
| `projects.md` | Project & task management — projects, tasks, assignments |
| `purchase.md` | Purchase orders — supplier ordering, goods receipt |
| `reservation.md` | Stock reservations — earmark inventory for orders |
| `sales.md` | Sales orders — customer ordering, shipping, invoicing |
| `service.md` | Service management — support tickets, field service requests |
| `users.md` | Basic user account CRUD |
| `warehouse.md` | Warehouse & location management — storage hierarchy |

## services/

| File | Description |
|------|-------------|
| `order.md` | Order management — sales/purchase orders, order lines, status flow |
| `product.md` | Product catalog — definitions, categories, pricing, UOM, SKU tracking |

## pages/

| File | Description |
|------|-------------|
| `dashboard.md` | Main dashboard — activity overview, quick navigation, user context |
