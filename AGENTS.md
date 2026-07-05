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
pnpm dev              # start dev server (manual alternative)
pnpm build            # runs typecheck first, then vite build
pnpm lint             # ESLint --max-warnings=0
pnpm typecheck        # tsc --noEmit
pnpm format           # Prettier --write .
pnpm preview          # vite preview (production build)
```

**Pre-commit check order:** `lint` → `typecheck → build`. Husky + lint-staged runs `eslint --fix` + `prettier --write` on staged `*.{ts,tsx}`.

## Key conventions

- **Frontend path alias:** `@/` → `src/` (configured in both vite.config.ts and tsconfig.json paths)
- **API envelope:** Every Spring endpoint returns `ApiResponse<T>` (`{ success, data, message, errorCode, details }`)
- **Backend persistence:** JPA `ddl-auto=update` in prod; `BaseEntity` provides UUID id, soft-delete, timestamps; `BaseService<T>` provides generic CRUD
- **Test DB:** H2 in PostgreSQL compatibility mode (`jdbc:h2:mem:testdb;MODE=PostgreSQL`)
- **Auth:** JWT tokens stored in Zustand `authStore`; axios interceptor injects Bearer token; 401 triggers auto-logout
- **API base:** `VITE_API_URL` env var (defaults to `http://localhost:3000/api` — note port mismatch with backend at 8081)

## Architecture

This is a **metadata-driven ERP runtime platform**, not a hardcoded CRUD app:

- **Backend:** metadata generation, dynamic CRUD, workflow execution, permission enforcement
- **Frontend:** metadata-driven runtime renderer with registries (field/layout/action/workflow/view)
- **Metadata schema** defined in `src/core/metadata/schema/` (Zod-validated); `engine/` is placeholder for dynamic renderers
- **Code generator** `CodeGenerator.java` handles DTO + Service scaffolding for new entities
- Full architectural blueprint at `docs/workspace-agent-prompt-P0.md` (885 lines) — read this before major decisions

## Test status

- **Backend:** 1 test (`DatabaseConnectionTest.java`), minimal coverage
- **Frontend:** No test framework configured (`pnpm test` echoes placeholder)

## Gotchas

- `.env` files and `application-local.properties` are in `.gitignore` — don't commit secrets
- Backend `VITE_API_URL` in `.env` points to `localhost:3000` but the real backend runs on `8081` — update `.env.development` if proxying directly
- `db-setup.sql` is gitignored; schema template at `backend/db-setup-template.sql`
- `.m2/` is gitignored — local Maven repo cache lives in workspace

## References worth preserving

- `docs/architecture-blueprint-P0.md`
- `docs/workspace-agent-prompt-P0.md`
