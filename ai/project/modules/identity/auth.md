---
module: auth
type: backend
layer: controller + service
last_updated: 2026-07-16T18:32:56+05:30
last_updated_git_sha: 2958af1b0ecd41cb6d20403374d34e41c0917a0e
paths:
  - backend/src/main/java/com/erp/platform/identity/controller/AuthController.java
  - backend/src/main/java/com/erp/platform/identity/service/AuthenticationService.java
  - backend/src/main/java/com/erp/platform/identity/service/PasswordService.java
  - backend/src/main/java/com/erp/platform/identity/dto/LoginRequest.java
  - backend/src/main/java/com/erp/platform/identity/dto/LoginResponse.java
  - backend/src/main/java/com/erp/platform/identity/dto/RefreshRequest.java
  - backend/src/main/java/com/erp/platform/identity/dto/ChangePasswordRequest.java
  - backend/src/main/java/com/erp/platform/identity/dto/UserInfoResponse.java
---

# Backend Auth

## Purpose
Handles all authentication endpoints — login, token refresh, logout, current-user retrieval, and password change. Validates credentials against bcrypt hashes, enforces account lockout policy, creates sessions, and returns JWT tokens with resolved roles and permissions.

---

## Simple Instructions *(for non-developers)*

### What is this?
This is the part of the system that handles logging in and out. When you type your username and password on the login screen, this module checks if they are correct and gives you a "key" (a token) to access the rest of the system.

### What can you do here?
- **Log in** with your username and password
- **Log out** when you are done
- **Refresh your session** so you don't get logged out automatically
- **Check who you are** — the system can tell you your user info
- **Change your password**

### How to use it

1. Go to the **Login** page in your browser.
2. Type your **Username** and **Password** into the fields.
3. Click the **Sign In** button.
4. If your credentials are correct, you will be taken to the workspace selection page.
5. To log out later, click your user icon in the top-right and choose **Logout**.

### Diagram

```mermaid
graph TD
  A[User opens ERP] --> B[Login Page]
  B --> C[Enter Username + Password]
  C --> D[Click Sign In]
  D --> E{Valid credentials?}
  E -->|Yes| F[Go to Workspace Selection]
  E -->|No| G[Show error message]
  G --> C
  F --> H[Use the system]
  H --> I[Click Logout]
  I --> J[Return to Login Page]
```

### Common issues
| Problem | What to do |
|---------|-------------|
| "Invalid username or password" | Check that your username and password are typed correctly. Passwords are case-sensitive. |
| "Account is temporarily locked" | You entered the wrong password too many times. Wait a few minutes and try again, or contact your admin to unlock your account. |
| Page says "Authenticating..." forever | The server may be down. Check your internet connection or contact your system administrator. |
| You get logged out suddenly | Your session may have expired. Just log in again. |

---

## API Endpoints

| Method | Path | Handler | Auth |
|--------|------|---------|------|
| POST | `/api/v1/auth/login` | `AuthController.login()` | Public |
| POST | `/api/v1/auth/refresh` | `AuthController.refresh()` | Public |
| POST | `/api/v1/auth/logout` | `AuthController.logout()` | Public (reads Bearer) |
| GET | `/api/v1/auth/me` | `AuthController.me()` | Authenticated |
| POST | `/api/v1/auth/change-password` | `AuthController.changePassword()` | Authenticated |

## Key Classes

| Class | Role |
|-------|------|
| `AuthController` | REST endpoints for login, refresh, logout, me, change-password |
| `AuthenticationService` | Core login logic — validates credentials, checks account lockout, resolves roles/permissions, creates session, generates JWT, delegates password changes |
| `PasswordService` | bcrypt password matching, account lockout tracking (failed attempts), password policy validation |

## Dependencies

| Injected | Purpose |
|----------|---------|
| `UserAccountRepository` | Lookup user by username, save login timestamps/attempts |
| `UserSessionRepository` | Create and soft-delete user sessions |
| `JwtProvider` | Generate access + refresh tokens |
| `PasswordService` | Password verification + lockout handling |
| `UserRoleRepository` | Resolve user→role assignments |
| `PermissionResolver` | Resolve effective permissions from roles |

## Login Flow

```mermaid
sequenceDiagram
  participant Ctrl as AuthController
  participant Svc as AuthenticationService
  participant UserRepo as UserAccountRepository
  participant PwdSvc as PasswordService
  participant RoleRepo as UserRoleRepository
  participant Perm as PermissionResolver
  participant SessRepo as UserSessionRepository
  participant JWT as JwtProvider
  participant DB as PostgreSQL

  Ctrl->>Svc: login(request, ip, userAgent)
  Svc->>UserRepo: findByUsername(username)
  DB-->>UserRepo: UserAccount row
  alt user not found
    Svc-->>Ctrl: throw IllegalArgumentException
  end
  Svc->>Svc: Check isActive, status, lockout
  Svc->>PwdSvc: matches(password, hash)
  alt password mismatch
    Svc->>PwdSvc: handleFailedAttempt() → increment attempts, potential lock
    Svc-->>Ctrl: throw IllegalArgumentException
  end
  Svc->>PwdSvc: resetFailedAttempts()
  Svc->>UserRepo: save(lastLoginAt)
  Svc->>RoleRepo: findByUserId() → role codes
  Svc->>Perm: resolveUserPermissions() → permission strings
  Svc->>SessRepo: save(new UserSession)
  Svc->>JWT: generateAccessToken(userId, username, email, roles, sessionId)
  Svc->>JWT: generateRefreshToken(userId, sessionId)
  Svc-->>Ctrl: LoginResponse(accessToken, refreshToken, user, sessionId)
  Ctrl-->>Client: 200 ApiResponse<LoginResponse>
```

## Request/Response DTOs

### LoginRequest
```json
{ "username": "admin", "password": "..." }
```

### LoginResponse
```json
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ...",
  "expiresAt": "2026-07-10T19:07:40",
  "sessionId": "uuid",
  "user": {
    "id": "uuid", "username": "admin", "email": "admin@example.com",
    "firstName": "System", "lastName": "Admin", "displayName": "System Admin",
    "roles": ["sys_admin"], "permissions": ["form:products:read", ...]
  }
}
```

## Error Flows

| Condition | Error |
|-----------|-------|
| Invalid username/password | `IllegalArgumentException("Invalid username or password")` → 400 |
| Account inactive | `IllegalStateException("Invalid username or password")` → 400 |
| Account locked | `IllegalStateException("Account is temporarily locked")` → 400 |
| Invalid/expired refresh token | `IllegalArgumentException("Invalid or expired refresh token")` → 400 |
| Current password wrong (change) | `IllegalArgumentException("Current password is incorrect")` → 400 |

## Related Frontend
- `core-api/services/authService.ts` — `login()`, `logout()`, `refreshSession()`, `getCurrentUser()`, `changePassword()`
- `routes/auth/LoginPage.tsx` — login form that calls `authService.login()`, stores result in `authStore`
- `core-auth/authStore.ts` — Zustand store persisting token/user on login, clearing on logout
- `core-api/interceptors.ts` — injects token from authStore, handles 401 → logout redirect
