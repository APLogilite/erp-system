# Analysis Rules

This document defines how to scan the codebase for documentation purposes.

Every Technical Writer agent must read this before analyzing code.

---

## Backend Analysis

- Scan for `@RestController`, `@Service`, `@Repository`, `@Configuration` annotations.
- Extract `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` paths and methods.
- Note `@PreAuthorize`, `@Secured`, `@RolesAllowed` annotations for auth requirements.
- Trace constructor-injected or `@Autowired` dependencies.
- Identify JPA entity classes and their table mappings.

---

## Frontend Analysis

- Scan React Router `<Route>` definitions and their paths, components, and lazy loading.
- Search for axios/fetch calls: `api.get(`, `api.post(`, `api.put(`, `api.delete(`, `axios.`, `fetch(`.
- Identify Zustand store `create()` calls and their state shape + actions.
- Identify React Query `useQuery`, `useMutation`, `useInfiniteQuery` hooks and their cache keys.
- Map API endpoint calls back to the backend controller methods they hit.

---

## Flow Analysis

- Start from a user-triggering action (button click, form submit, route change, menu selection).
- Follow the code path linearly through frontend → HTTP → backend → DB → response → frontend.
- Always include `file:line` references in every step.
- Document both the happy path and every error/failure path.
- Prefer a Mermaid sequence diagram as the visual overview, followed by detailed step breakdown.
