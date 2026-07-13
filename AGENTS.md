# AGENTS.md — Dynamic ERP System

## Repo structure

Two independent apps in one monorepo; no workspace tooling linking them.

| Directory | Stack | Port | Entrypoint |
|-----------|-------|------|------------|
| `backend/` | Spring Boot 3.3.4 / Java 17 / Maven / PostgreSQL | 8081 | `ErpApplication.java` |
| `frontend/` | React 18 / TypeScript (strict) / Vite / MUI 5 / Zustand / React Query / React Router v6 / AG Grid Enterprise | 5173 | `main.tsx` |

## Commands

### Backend

```bash
cd backend
./setup.sh            # verify Java+Maven, download deps
./start.sh            # mvn spring-boot:run
mvn test              # 36 unit tests (H2 in-memory, PostgreSQL mode)
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
- **Flyway** is disabled by default (`spring.flyway.enabled=false`); JPA `ddl-auto=update` handles schema. Set `spring.flyway.enabled=true` to run the 34 migrations (V1-V23 + undo scripts) that seed metadata tables and ERP seed data.
- **Test DB:** H2 in PostgreSQL compatibility mode (`jdbc:h2:mem:testdb;MODE=PostgreSQL`); 36 tests pass with BUILD SUCCESS
- **Demo users** seeded on startup; see `README.md` for credentials

## Architecture

- **Backend:** `platform/identity/` is the dominant module (auth, RBAC, multi-tenant admin hierarchy). 19 business modules under `modules/` (accounting, analytics, assets, auth, businesspartner, crm, hr, inventory, manufacturing, order, product, projects, purchase, reservation, sales, service, users, warehouse) are CRUD layers backed by JPA entities. The metadata-driven form engine uses Flyway migrations (V15-V23) to register dynamic form definitions in `sys_metadata_models`, `sys_table_columns`, `sys_form_fields`, `sys_form_layout_sections`, `sys_form_sub_forms`, etc.
- **Frontend:** metadata-driven runtime renderer with registries (field/layout/action/workflow/view). Metadata schema in `src/core/metadata/schema/` (Zod-validated); `engine/` contains the DynamicFormRenderer + FormFieldRenderer. The `app/` layer wires `QueryProvider + ThemeProvider + RegistryProvider + BrowserRouter`. AG Grid Enterprise powers grid views.
- **Code generator** (`CodeGenerator.java`) scaffolds DTO + Service for 5 hardcoded entities (Product, Warehouse, Order, OrderLine, StockMovement) — extend the list in its `main()` method.
- **PRD pipeline:** All 3 PRDs complete — PRD-001 (Dynamic Form Config v1.6.0), PRD-002 (Admin Config Forms v1.1.0), PRD-003 (ERP Order Flow v1.0.0). Planning artifacts in `ai/` (PRDs, tasks, changes, tests, docs).

## Test status

- **Backend:** 36 tests — `DatabaseConnectionTest` (3 integration tests, fixed in BUG-001) + `platform/identity` unit tests (JwtProvider 5, PasswordService 13, PermissionCache 6, PermissionEvaluator 9). All pass with BUILD SUCCESS.
- **Frontend:** No test framework (`pnpm test` echoes placeholder)

## Gotchas

- `.env*` files and `application-local.properties` are in `.gitignore` — don't commit secrets
- `db-setup.sql` is gitignored; template at `backend/db-setup-template.sql`
- `.m2/` is gitignored — local Maven repo cache lives in workspace
- Frontend `.env*` files exist on disk as defaults but are gitignored; Vite's `loadEnv` loads them directly
- `mvn exec:java` only generates code for the 5 entities hardcoded in `CodeGenerator.java`
- `ai/` directory contains PRD specs, task docs, change reports, test reports, and workflow docs — maintained by AI agents
