# Dynamic ERP System

A comprehensive Enterprise Resource Planning (ERP) system built with modern technologies for managing business operations.

## Project Structure

This repository is organized into separate backend and frontend applications:

- `backend/` - Spring Boot Java application
- `frontend/` - React TypeScript application with Vite
- `docs/` - Documentation and development prompts

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
- **Multi-Tenant Architecture** — Data isolated by tenant hierarchy (Tenant → Org → Company → Branch → Dept)
- **Identity & Administration** — Admin dashboard with CRUD for all entities (tenants, orgs, companies, branches, departments, users, roles, permissions)
- **Inventory Management**
- **Order Processing**
- **Product Management**
- **Warehouse Management**

## Development

See individual README files in `backend/` and `frontend/` for detailed setup and development instructions.

## Documentation

Development prompts and additional documentation are available in the `docs/` folder.