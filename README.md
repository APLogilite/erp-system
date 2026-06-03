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

## Features

- **Authentication & User Management**
- **Inventory Management**
- **Order Processing**
- **Product Management**
- **Warehouse Management**

## Development

See individual README files in `backend/` and `frontend/` for detailed setup and development instructions.

## Documentation

Development prompts and additional documentation are available in the `docs/` folder.