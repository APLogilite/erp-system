# Dynamic ERP System

A comprehensive Enterprise Resource Planning (ERP) system built with modern technologies for managing business operations. Features a metadata-driven form engine, multi-tenant architecture, and a full ERP order flow.

## Project Structure

```
.
├── ai/                  # AI planning artifacts (PRDs, tasks, changes, tests, docs)
├── backend/             # Spring Boot Java 17 / Maven / PostgreSQL
├── frontend/            # React 18 / TypeScript / Vite / MUI 5
├── docs/                # Development prompts and additional documentation
├── setup.sh             # Full-stack setup (backend + frontend)
├── start.sh             # Start both servers in parallel
└── stop.sh              # Stop both servers
```

## Getting Started

You can set up both the frontend and backend environments using the top-level full-stack setup script or manually set up each folder.

### 🚀 Full-Stack Setup (Recommended)

Run the root-level setup script from the root directory to sequentially configure the backend (downloads and compiles Maven resources) and the frontend (downloads local Node.js and `pnpm` tools locally without requiring global permissions):

```bash
# Run full-stack setup
./setup.sh
```

---

### 💻 Backend Setup & Execution

#### Prerequisites:
- **Java 17** (or higher)
- **Maven 3.6+**
- **PostgreSQL** database (running on port `5432` with a database named `erp_db`)

#### Installation:
```bash
cd backend
./setup.sh
```

#### Run Backend Server:
Start the backend Spring Boot server on port `8081` using either the quickstart start script or manual maven task:
```bash
cd backend
./start.sh          # Quickstart wrapper
# OR
mvn spring-boot:run # Manual maven command
```

---

### 🎨 Frontend Setup & Execution

#### Prerequisites:
- Node.js 22+ & pnpm (managed automatically by local setup script)

#### Installation:
```bash
cd frontend
./setup.sh
```

#### Run Frontend Server:
Start the development server on port `5173` using either the quickstart start script or passing PATH parameters:
```bash
cd frontend
./start.sh                              # Quickstart wrapper (highly recommended)
# OR
PATH=./.local/nodejs/bin:$PATH pnpm dev # Manual command using local Node.js binary
```

## Demo Users

The system seeds test data on first startup. Use these credentials to test different permission scopes:

| Username | Password | Role | Tenant Scope | Description |
|----------|----------|------|-------------|-------------|
| `admin` | `Admin@123` | `sys_admin` | Cross-tenant | Full system access — sees all data |
| `jane.smith` | `User@123` | `tnt_admin` | ACME | Tenant admin for Acme Corporation |
| `diana.prince` | `User@123` | `tnt_admin` | Globex | Tenant admin for Globex Industries |
| `john.doe` | `User@123` | `user` | ACME | Regular user in ACME org |
| `alice.johnson` | `User@123` | `user` | ACME | Regular user in ACME org |
| `bob.wilson` | `User@123` | `viewer` | Globex | Read-only user in Globex |
| `charlie.brown` | `User@123` | `user` | Globex | Regular user in Globex org |

> **Note:** Each user sees **only data within their tenant scope**. `sys_admin` (admin) sees everything across all tenants. Regular users only see orgs/companies/branches/departments/roles belonging to their tenant.

## Features

- **Authentication & User Management** — JWT auth, role & permission management, session control
- **Multi-Tenant Architecture** — Data isolated by tenant hierarchy (Tenant → Org → Company → Branch → Dept), enforced via Hibernate `@Filter` annotations
- **Identity & Administration** — Admin dashboard with CRUD for all entities (tenants, orgs, companies, branches, departments, users, roles, permissions)
- **Metadata-Driven Form Engine** — Dynamic form configuration system with table designer, form designer, and runtime form renderer. Forms are defined via metadata tables and rendered at runtime — zero code changes needed for new forms
- **Admin Configuration Forms** — Metadata-backed admin UI for managing form definitions, table schemas, field rules, validations, layout sections, sub-forms, and role access
- **ERP Order Flow** — Complete purchase-to-pay and order-to-cash flow with master data (products, business partners, warehouses, UOM) and transactions (orders, invoices, payments, shipments, material receipts)
- **Dynamic List View** — Runtime grid views powered by AG Grid Enterprise with search, sort, and filter
- **Role-Based Navigation** — Dynamic menu configured per-role via metadata
- **Header Form Search** — Global Ctrl+K search across accessible forms
- **Inventory Management** — Stock levels, movements, balances, transactions
- **Order Processing** — Sales and purchase orders with line items and status tracking
- **Product Management** — Product catalog with categories, UOM, pricing
- **Warehouse Management** — Multi-warehouse with location tracking
- **Business Partner Management** — Customers, suppliers with contact management
- **CRM** — Contact and opportunity management
- **HR** — Employee and department management
- **Manufacturing** — BOM, work orders, routing, work centers
- **Project Management** — Projects and tasks
- **Analytics** — Dashboards, KPI definitions, scheduled reports
- **Service Management** — Service requests and tracking

## Development

See individual README files in `backend/` and `frontend/` for detailed setup and development instructions.

## Documentation

- `backend/README.md` — Backend architecture, modules, Flyway migrations, API
- `frontend/README.md` — Frontend architecture, components, state management
- `ai/` — AI planning artifacts including PRD specs, task documents, change reports, test reports, and workflow documentation
- `docs/` — Development prompts and additional documentation
