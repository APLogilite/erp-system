# Identity Platform — Frozen Standards (P0.5)

This document freezes all Identity Platform standards. Every future module depends on these contracts.

---

## 1. Authentication Standards

| Aspect | Standard |
|--------|----------|
| Mechanism | JWT (stateless) + Refresh Tokens |
| Token transport | Bearer token in HTTP `Authorization` header |
| Token type | `Bearer` |
| Hash algorithm | BCrypt for password storage |
| JWT signing | HMAC-SHA256 (HS256) with configurable secret |
| Session storage | `identity_user_sessions` table for refresh + audit |
| Multi-tenant | Tenant context extracted from JWT `tenantId` claim |
| Future SSO | All auth paths go through `AuthenticationManager` interface |

---

## 2. JWT Claims Contract

```json
{
  "sub":           "user-uuid",
  "username":      "jdoe",
  "email":         "jdoe@company.com",
  "tenantId":      "tenant-uuid",
  "tenantCode":    "acme",
  "organizationId":"org-uuid",
  "companyId":     "company-uuid",
  "branchId":      "branch-uuid",
  "sessionId":     "session-uuid",
  "roles":         ["sales_manager", "inventory_viewer"],
  "iat":           1700000000,
  "exp":           1700003600
}
```

| Claim | Type | Required | Description |
|-------|------|----------|-------------|
| `sub` | UUID | yes | UserAccount.id |
| `username` | string | yes | Login username |
| `email` | string | yes | User email |
| `tenantId` | UUID | yes | Active tenant |
| `tenantCode` | string | yes | Tenant code |
| `organizationId` | UUID | no | Selected org |
| `companyId` | UUID | no | Selected company |
| `branchId` | UUID | no | Selected branch |
| `sessionId` | UUID | yes | UserSession.id |
| `roles` | string[] | yes | Role codes |
| `iat` | epoch | yes | Issued at |
| `exp` | epoch | yes | Expires at |

---

## 3. Permission Naming Standard

Format: `{resource_type}.{resource}.{action}`

| Segment | Values | Examples |
|---------|--------|----------|
| resource_type | module, menu, window, tab, field, action, workflow, process, report, dashboard, plugin | `module`, `action` |
| resource | lowercase, underscore-separated model/entity code | `sales_order`, `product`, `employee` |
| action | READ, CREATE, UPDATE, DELETE, EXECUTE, APPROVE, EXPORT | `READ`, `CREATE` |

Examples:
- `module.product.READ`
- `module.sales_order.CREATE`
- `action.sales_order.APPROVE`
- `workflow.purchase_order.EXECUTE`
- `report.financial_summary.EXPORT`
- `field.employee.salary.READ`
- `menu.administration.ACCESS`

Database: stored in `identity_permissions` as `resource_type` + `resource` + `action`.

---

## 4. Role Naming Standard

Format: `{scope}_{domain}_{function}`

| Scope | Examples |
|-------|----------|
| sys | System-wide roles (cross-tenant) |
| tnt | Tenant-scoped roles |
| org | Organization-scoped roles |

| Category | Examples |
|----------|----------|
| admin | `sys_admin`, `tnt_admin`, `org_admin` |
| manager | `tnt_sales_manager`, `org_inventory_manager` |
| user | `tnt_sales_user`, `org_hr_user` |
| viewer | `tnt_report_viewer`, `org_finance_viewer` |

System roles (pre-defined, `is_system = true`):
| Code | Name | Description |
|------|------|-------------|
| `sys_admin` | System Administrator | Full cross-tenant access |
| `tnt_admin` | Tenant Administrator | Full tenant access |
| `tnt_user` | Tenant User | Basic authenticated user |

Business roles (created per tenant):
| Example Code | Example Name |
|-------------|--------------|
| `tnt_sales_manager` | Sales Manager |
| `tnt_inventory_user` | Inventory User |
| `org_finance_viewer` | Finance Viewer |

---

## 5. Tenant Standards

| Aspect | Standard |
|--------|----------|
| Code format | lowercase alphanumeric + hyphen, max 50 chars |
| Code uniqueness | Global unique |
| Domain | Optional. Used for domain-based tenant resolution |
| Default language | ISO 639-1 (e.g. `en`, `fr`, `de`) |
| Default timezone | IANA timezone (e.g. `America/New_York`) |
| Default currency | ISO 4217 (e.g. `USD`, `EUR`) |
| Deletion | Soft delete only (is_active = false) |

---

## 6. Organization Hierarchy Standards

| Aspect | Standard |
|--------|----------|
| Max depth | Unlimited (controlled by `level` + `path`) |
| `level` | Root = 0, increments by 1 per level |
| `path` | Materialized path: `/root-id/parent-id/self-id` |
| Root org | Must reference a valid `tenant_id` |
| Parent | Optional null for root |

---

## 7. Session Lifecycle

| Stage | Description |
|-------|-------------|
| **Created** | On successful authentication. Access token + refresh token generated. |
| **Active** | Access token used for API calls; `last_activity_at` updated periodically. |
| **Refreshed** | When access token expires, refresh token exchanged for new pair. |
| **Expired** | Access token TTL reached without refresh. |
| **Revoked** | Explicit logout or admin action deletes the session row. |
| **Cleaned** | Expired sessions purged by scheduled job. |

| Token | TTL | Storage |
|-------|-----|---------|
| Access token (JWT) | 15 minutes | Client-side (memory / httpOnly cookie) |
| Refresh token | 7 days | `identity_user_sessions` table |

---

## 8. Password Policy

| Rule | Standard |
|------|----------|
| Minimum length | 8 characters |
| Require uppercase | Yes (1+) |
| Require lowercase | Yes (1+) |
| Require digit | Yes (1+) |
| Require special char | Yes (1+ from `!@#$%^&*()_+-=[]{}|;:,.<>?`) |
| Max failed attempts | 5 before account lockout |
| Lockout duration | 30 minutes |
| Password expiry | 90 days |
| Password history | Last 5 passwords remembered |
| Hash algorithm | BCrypt with strength 12 |

---

## 9. Default User States

| State | Description | Allow Login |
|-------|-------------|-------------|
| `ACTIVE` | Normal operational state | Yes |
| `INACTIVE` | Manually disabled | No |
| `LOCKED` | Locked due to failed attempts | No |
| `PENDING` | Created but not yet activated | No |
| `EXPIRED` | Password expired, must change | No (except to change password) |

Default for new users: `PENDING` (requires activation or email verification).

---

## 10. Database Naming Standards

| Aspect | Standard | Example |
|--------|----------|---------|
| Schema | No separate schema; use `identity_` prefix | `identity_users` |
| Table names | plural, lowercase, snake_case | `identity_user_roles` |
| Join tables | singular pair, alphabetical | `identity_role_permissions` (role < permission) |
| Primary key | `id` (UUID) | `id UUID PRIMARY KEY` |
| Foreign key | `{referenced_table_singular}_id` | `user_id`, `role_id` |
| Timestamps | `created_at`, `updated_at` | `created_at TIMESTAMP` |
| Soft delete | `is_active`, `deleted_at` | always present |
| Audit columns | `created_by`, `updated_by` | UUID |

---

## 11. API Naming Standards

| Aspect | Standard |
|--------|----------|
| Base path | `/api/v1/identity` |
| Resource names | plural, kebab-case |
| Examples | `GET /api/v1/identity/users`, `POST /api/v1/identity/auth/login` |
| Response envelope | `ApiResponse<T>` (existing project standard) |
| Pagination | `page`, `size`, `sort` query params |
| Error codes | `IDENTITY_001`, `IDENTITY_002`, etc. |

---

## 12. RuntimeContext Contract

### Resolution
1. JWT extracted from `Authorization: Bearer <token>` header
2. JWT validated (signature, expiry, blacklist)
3. Claims mapped to RuntimeContext fields
4. RuntimeContext injected as a request-scoped bean

### Lifecycle
| Stage | Event |
|-------|-------|
| **Created** | At start of every authenticated HTTP request |
| **Available** | Via `@CurrentUser` annotation or `RuntimeContextHolder.get()` |
| **Propagation** | Passed to service layer via method parameter or AOP |
| **Destroyed** | At end of request (request scope) |

### Thread Safety
- Request-scoped bean (one instance per request)
- NOT shared between requests
- NOT shared between threads

---

## 13. Audit Standards

| Aspect | Standard |
|--------|----------|
| Audit table | `audit_logs` (existing platform table) |
| Events to audit | Login, logout, password change, role assignment, permission change, user create/update/delete |
| Fields captured | actor, action, resource_type, resource_id, old_value, new_value, timestamp |
| Immutability | Audit logs are append-only (no delete, no update) |
| Retention | 1 year online, 7 years archived |

---

## 14. Exception Strategy

| Exception | HTTP Status | Error Code |
|-----------|-------------|------------|
| Invalid credentials | 401 | `IDENTITY_AUTH_001` |
| Account locked | 423 | `IDENTITY_AUTH_002` |
| Token expired | 401 | `IDENTITY_AUTH_003` |
| Invalid token | 401 | `IDENTITY_AUTH_004` |
| Insufficient permissions | 403 | `IDENTITY_AUTH_005` |
| User not found | 404 | `IDENTITY_USER_001` |
| Duplicate user | 409 | `IDENTITY_USER_002` |
| Password policy violation | 422 | `IDENTITY_PASS_001` |
| Session expired | 401 | `IDENTITY_SESSION_001` |

All exceptions return standard `ApiResponse<T>` with `success=false`.

---

## 15. Validation Strategy

| Layer | Responsibility |
|-------|---------------|
| Controller | Syntax validation (`@Valid`, `@NotBlank`, etc.) |
| Service | Business rule validation |
| Entity | JPA constraints (nullable, unique, length) |
| Database | Foreign keys, unique indexes, NOT NULL |

Validation is centralized in service layer. Controllers handle format only.

---

## 16. Caching Strategy

| Cache | Type | TTL | Invalidation |
|-------|------|-----|--------------|
| User permissions | In-memory (Caffeine) | 5 minutes | On role/permission change |
| User preferences | In-memory (Caffeine) | 10 minutes | On preference update |
| JWT blacklist | In-memory (Caffeine) | Until token expiry | On logout |
| Tenant config | In-memory (Caffeine) | 1 hour | On tenant update |

---

## 17. Future Compatibility

| Feature | Strategy |
|---------|----------|
| OAuth 2.0 | `AuthenticationManager` interface allows pluggable providers |
| LDAP/AD | `UserDetailsService` abstraction; LDAP provider implements same interface |
| SAML | Spring Security SAML2 adapter on `/auth/saml2/{registrationId}` |
| SSO | All auth flows go through `AuthenticationManager` — new providers register there |
| WebAuthn | Additional `AuthenticationProvider` in the provider chain |
| SCIM | Standard REST endpoints at `/api/v1/identity/scim/v2/` |
