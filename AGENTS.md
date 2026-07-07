# AGENTS.md — Dynamic ERP System

## Repo structure

Two independent apps in one monorepo; no workspace tooling linking them.

| Directory | Stack | Port | Entrypoint |
|-----------|-------|------|------------|
| `backend/` | Spring Boot 3.3.4 / Java 17 / Maven / PostgreSQL | 8081 | `ErpApplication.java` |
| `frontend/` | React 18 / TypeScript (strict) / Vite / MUI 5 / Zustand / React Query / React Router v6 | 5173 | `main.tsx` |

## Commands

### Backend

```bash
cd backend
./setup.sh            # verify Java+Maven, download deps
./start.sh            # mvn spring-boot:run
mvn test              # unit tests (H2 in-memory, PostgreSQL mode)
mvn clean compile     # build without tests
mvn exec:java         # run CodeGenerator.java (generates DTOs + Services)
```

Profile `local` is auto-imported from `application-local.properties` (gitignored — contains DB creds).

### Frontend

```bash
cd frontend
./setup.sh            # download Node.js 22 + pnpm locally into .local/
./start.sh            # uses local Node.js from .local/nodejs/bin
pnpm dev              # start dev server
pnpm build            # runs typecheck first, then vite build
pnpm lint             # ESLint --max-warnings=0
pnpm typecheck        # tsc --noEmit
pnpm format           # Prettier --write .
pnpm preview          # vite preview (production build)
```

**Pre-commit:** Husky runs `lint-staged → typecheck`. `lint-staged` runs `eslint --fix` + `prettier --write` on staged `*.{ts,tsx}` and Prettier on `*.{json,md,css,scss}`.

## Key conventions

- **Frontend path alias:** `@/` → `src/` (configured in vite.config.ts and tsconfig.json)
- **API envelope:** Every endpoint returns `ApiResponse<T>` (`{ success, data, message, errorCode, details }`)
- **API base:** `/api/v1` (defined in `ApiVersionConfig.java`; frontend `VITE_API_URL` defaults to `http://localhost:8081/api/v1`)
- **Backend persistence:** JPA `ddl-auto=update` in prod; `BaseEntity` provides UUID id, soft-delete, timestamps; `BaseService<T>` provides generic CRUD with lifecycle hooks
- **Multi-tenancy:** Data isolated by `Tenant → Org → Company → Branch → Dept` hierarchy, enforced via Hibernate `@Filter` annotations. The `sys_admin` role bypasses all filters.
- **Auth:** JWT tokens in Zustand `authStore`; axios interceptor injects Bearer token; 401 triggers auto-logout
- **Flyway** is disabled by default (`spring.flyway.enabled=false`); JPA `ddl-auto=update` handles schema
- **Test DB:** H2 in PostgreSQL compatibility mode (`jdbc:h2:mem:testdb;MODE=PostgreSQL`)
- **Demo users** seeded on startup; see `README.md` for credentials

## Architecture

- **Backend:** `platform/identity/` is the dominant module (auth, RBAC, multi-tenant admin hierarchy). Other modules under `modules/` (inventory, order, product, warehouse, etc.) are thin CRUD layers.
- **Frontend:** metadata-driven runtime renderer with registries (field/layout/action/workflow/view). Metadata schema in `src/core/metadata/schema/` (Zod-validated); `engine/` contains placeholder renderers. The `app/` layer wires `QueryProvider + ThemeProvider + RegistryProvider + BrowserRouter`.
- **Code generator** (`CodeGenerator.java`) scaffolds DTO + Service for 5 hardcoded entities (Product, Warehouse, Order, OrderLine, StockMovement) — extend the list in its `main()` method.

## Test status

- **Backend:** 5 tests — `DatabaseConnectionTest` (1 file) + `platform/identity` unit tests (JwtProvider, PasswordService, PermissionCache, PermissionEvaluator)
- **Frontend:** No test framework (`pnpm test` echoes placeholder)

## Gotchas

- `.env*` files and `application-local.properties` are in `.gitignore` — don't commit secrets
- `db-setup.sql` is gitignored; template at `backend/db-setup-template.sql`
- `.m2/` is gitignored — local Maven repo cache lives in workspace
- Frontend `.env*` files exist on disk as defaults but are gitignored; Vite's `loadEnv` loads them directly
- `mvn exec:java` only generates code for the 5 entities hardcoded in `CodeGenerator.java`
