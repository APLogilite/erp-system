# Dynamic ERP System

A comprehensive Enterprise Resource Planning (ERP) system built with modern technologies for managing business operations.

## Project Structure

This repository is organized into separate backend and frontend applications:

- `backend/` - Spring Boot Java application
- `frontend/` - React TypeScript application with Vite
- `docs/` - Documentation and development prompts

## Getting Started

### Prerequisites

- Java 17+ (for backend)
- Node.js 22+ (for frontend, or use the setup script)
- Maven (for backend)
- pnpm (for frontend, or use the setup script)

### Backend Setup

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend Setup

```bash
cd frontend
./setup.sh  # Sets up local Node.js and pnpm
pnpm dev
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