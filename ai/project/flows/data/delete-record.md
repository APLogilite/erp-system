---
module: delete-record
type: flow
last_updated: 2026-07-16T18:32:56+05:30
last_updated_git_sha: 2958af1b0ecd41cb6d20403374d34e41c0917a0e
---

# Flow: Delete Record

## Simple Instructions *(for non-developers)*

### What happens here?
This is what happens when you delete a record (like a tenant, user, or product) from a list page. The system does not permanently remove it — it just marks it as inactive so it no longer shows up.

### Step-by-step *(what the user sees)*

1. You are on a **list page** with records displayed in a table.
2. You click the **Delete** icon (red trash can) on the row you want to remove.
3. A confirmation popup asks: *"Are you sure you want to delete this record?"*
4. If you click **Cancel**, nothing happens.
5. If you click **OK**, the record disappears from the table and the list refreshes.
6. The record is not truly gone — it is just marked as inactive in the database.

### Diagram *(overview for non-developers)*

```mermaid
graph TD
  A[User is on List Page] --> B[Click Delete icon on a row]
  B --> C[Confirmation popup appears]
  C --> D{User choice}
  D -->|Cancel| E[No action - stays on list]
  D -->|OK| F[System marks record as inactive]
  F --> G[Record disappears from table]
  G --> H[List refreshes]
```

### Common issues
| Problem | What to do |
|---------|-------------|
| The record is still there after deleting | The list may need a manual refresh. Click the browser refresh button. |
| You accidentally deleted a record | Contact your system administrator. Records are soft-deleted and can be restored. |
| Delete button is missing | You may not have permission to delete records. Contact your admin. |

---

## Sequence Diagram

```mermaid
sequenceDiagram
  actor User
  participant List as AdminListPage
  participant Page as TenantsAdminPage
  participant ApiClient as apiClient (axios)
  participant QueryClient as React Query
  participant Ctrl as TenantAdminController.java
  participant Svc as AdminService.java
  participant Repo as TenantRepository.java
  participant DB as PostgreSQL

  User->>List: Click Delete icon (red trash) on row
  List->>Page: onDelete(item)
  Page->>User: window.confirm('Delete tenant "X"?')
  
  alt User cancels
    User-->>Page: Click "Cancel"
    Note over Page: No action
  else User confirms
    Page->>ApiClient: DELETE /identity/tenants/:id
    ApiClient->>Ctrl: DELETE /api/v1/identity/tenants/{id}
    Ctrl->>Svc: deactivateTenant(id)
    Svc->>Repo: findById(id)
    Repo->>DB: SELECT * FROM identity_tenants WHERE id = ?
    DB-->>Repo: Tenant row
    Repo-->>Svc: Tenant entity
    Svc->>Svc: t.setIsActive(false)
    Svc->>Repo: save(Tenant)
    Repo->>DB: UPDATE identity_tenants SET is_active=false, deleted_at=NOW()
    DB-->>Repo: updated
    Svc-->>Ctrl: void
    Ctrl-->>ApiClient: 200 ApiResponse.successMessage("Tenant deactivated")
    ApiClient-->>Page: OK

    Page->>QueryClient: invalidateQueries(['identity','tenants'])
    Page->>List: Table re-renders without deleted row
  end
```

## Trigger
User clicks the red Delete icon on a row in an admin list page.

## Preconditions
- User is on an admin list page with data loaded
- User has admin role
- The target record exists

## Flow Steps

### Step 1: User clicks Delete
- **File:** `frontend/src/modules/identity/admin/AdminListPage.tsx:122-129`
  - AdminListPage renders a red Delete `<IconButton>` per row
  - Click fires `onDelete(item)` passed from the parent page

### Step 2: Confirmation dialog
- **File:** `frontend/src/modules/identity/admin/tenants/TenantsAdminPage.tsx:76-84`
  - `window.confirm(\`Delete tenant "${item.name}"?\`)` — native browser confirm
  - If cancelled, returns early with no action

### Step 3: API call to deactivate
- **File:** `frontend/src/modules/identity/admin/tenants/TenantsAdminPage.tsx:78-83`
  - `apiClient.delete(ENDPOINTS.identity.tenant(item.id))`
  - Catches errors silently (handled by interceptor pattern)

### Step 4: Backend soft-deletes via `setIsActive(false)`
- **File:** `backend/src/main/java/com/erp/platform/identity/service/AdminService.java:53`
  - `deactivateTenant(id)` → loads entity → `t.setIsActive(false)` → `tenantRepository.save(t)`
  - This is a **soft delete**: row stays in DB, just marked inactive

### Step 5: JPA PreUpdate fires
- **File:** `backend/src/main/java/com/erp/common/base/BaseEntity.java:46-49`
  - `@PreUpdate` sets `updatedAt` to current timestamp
  - Note: `deletedAt` is NOT set by `deactivateTenant` (unlike `softDelete()` on BaseEntity)

### Step 6: Cache invalidation
- **File:** `frontend/src/modules/identity/admin/tenants/TenantsAdminPage.tsx:80`
  - `queryClient.invalidateQueries({ queryKey: ['identity', 'tenants'] })`
  - Table refetches, removed row disappears

## Postconditions
- Record remains in database with `is_active = false`
- React Query cache invalidated
- Table refreshed showing remaining records

## Error Flows

### Entity Not Found
- **Condition:** Record deleted concurrently
- **Backend:** `getTenant(id)` throws `IllegalArgumentException("Tenant not found")`
- **Frontend:** Error handling in catch block (silently handled currently)

### Network / Auth Error
- **Condition:** Token expired, network down
- **Frontend:** Axios error → `responseErrorInterceptor` may trigger logout if 401

## Note: Difference from BaseEntity.softDelete()
The admin controllers call `setIsActive(false)` directly on entities rather than using `BaseEntity.softDelete()`. The distinction:
- `BaseEntity.softDelete()`: sets `isActive=false` AND `deletedAt=now`
- Admin service `deactivateXxx()`: only sets `isActive=false`

The `BaseService.delete()` method calls `entity.softDelete()` which sets both fields.
