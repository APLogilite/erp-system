# ERP Project Rules

## Backend Stack
- **Framework**: Spring Boot 3.x (Java 17+)
- **Build Tool**: Maven
- **Database**: PostgreSQL/MySQL
- **Architecture**: Layered (Controller → Service → Repository)
- **Primary Key**: UUID (GenerationType.UUID)
- **Soft Delete**: Boolean isActive field with deletedAt timestamp
- **Audit**: createdAt, updatedAt, createdBy, updatedBy fields
- **DTO Pattern**: Never expose entities directly
- **BaseService**: Generic CRUD with lifecycle hooks
- **Transactions**: @Transactional in service layer
- **REST APIs**: /api/v1/ prefix
- **Module Isolation**: Each module is self-contained

## Frontend Stack
- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite
- **Package Manager**: pnpm
- **State Management**: React Context (extensible to Zustand/Redux)
- **Routing**: React Router v6
- **Styling**: CSS Modules/Global CSS
- **Code Quality**: ESLint, Prettier, Husky, lint-staged
- **Architecture**: Modular with core/engine/components separation

## Implemented Modules
- **auth**: Authentication and authorization
- **users**: User management
- **product**: Product catalog with SKU-based identity
- **inventory**: Stock management with transaction-based ledger
- **order**: Unified sales/purchase order processing

## Backend Structure

```
backend/
├── src/main/java/com/erp/
│   ├── ErpApplication.java
│   ├── common/
│   │   ├── base/           # BaseEntity, BaseService
│   │   └── utils/          # UuidUtils
│   ├── config/             # ApiVersionConfig, DatabaseConfig
│   ├── codegen/            # CodeGenerator
│   └── modules/
│       ├── auth/
│       ├── inventory/
│       ├── order/
│       ├── product/
│       └── users/
│           ├── controller/
│           ├── service/
│           ├── repository/
│           ├── entity/
│           └── dto/
├── src/main/resources/
│   ├── application.properties
│   ├── application-local.properties
│   └── db/
├── src/test/java/
└── planning/               # Backend planning docs
```

## Frontend Structure

```
frontend/
├── src/
│   ├── main.tsx
│   ├── app/
│   │   ├── App.tsx
│   │   ├── ErrorBoundary.tsx
│   │   └── providers/
│   ├── core/               # Core functionality
│   │   ├── api/           # API client
│   │   ├── auth/          # Authentication
│   │   ├── runtime/       # Environment config
│   │   ├── store/         # State management
│   │   └── metadata/      # App metadata
│   ├── engine/            # Business logic
│   │   ├── actions/       # Business actions
│   │   ├── forms/         # Form configs
│   │   ├── grids/         # Grid configs
│   │   ├── layouts/       # Layout configs
│   │   └── workflows/     # Workflow definitions
│   ├── components/        # Reusable UI
│   ├── routes/            # Routing
│   ├── styles/            # Global styles
│   ├── themes/            # Theme configs
│   ├── hooks/             # Custom hooks
│   ├── utils/             # Utilities
│   └── assets/            # Static assets
├── .local/                # Local Node.js/pnpm
├── planning/              # Frontend planning docs
└── setup.sh               # Environment setup script
```

## Development Rules

### Backend
- All entities extend BaseEntity (UUID, audit, soft delete)
- Use BaseService for CRUD operations
- Implement lifecycle hooks (beforeCreate, afterCreate, etc.)
- Use DTOs for all API responses
- Repository methods return only active entities
- Controllers use services, never repositories directly

### Frontend
- Use TypeScript for all new code
- Follow established folder structure
- Use React hooks and functional components
- Implement proper error boundaries
- Follow ESLint and Prettier rules
- Use path aliases for imports

### General
- Each module is isolated and self-contained
- Follow clean architecture principles
- Write tests for business logic
- Document API endpoints
- Use meaningful commit messages
- Keep planning docs updated