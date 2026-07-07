# Task ID-STR-001-005: Remove Filter Infrastructure

## Plan Reference
Main plan: `final-identity-structure.md` (ID-STR-001)

## Objective
Remove all Hibernate `@Filter` annotations from entities and delete the `TenantFilterAspect` and `EnableTenantFilter` annotation. Simplify `ContextFilter` by removing the multi-level block.

## Files to Modify

### 1-5. Entity Files — Remove @Filter

| File | Remove |
|------|--------|
| `entity/Organization.java` | `@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")` + entire `@FilterDef` block + associated imports |
| `entity/Company.java` | `@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")` |
| `entity/Branch.java` | `@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")` |
| `entity/Department.java` | `@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")` |
| `entity/Role.java` | `@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId OR tenant_id IS NULL")` |

**Per file:**
- Remove `@Filter` annotation + its import (`org.hibernate.annotations.Filter`)
- `Organization.java` also remove `@FilterDef` + its import (`org.hibernate.annotations.FilterDef`, `org.hibernate.annotations.ParamDef`)

### 6. Delete `security/TenantFilterAspect.java`
- Entire file — already marked for removal in the plan

### 7. Delete `sdk/annotation/EnableTenantFilter.java`
- Entire file — already marked for removal in the plan

### 8. Modify `security/ContextFilter.java`
- Remove the multi-level context block (the `isContextOrAuthPath` check)
- Keep: resolving context and setting in RuntimeContextHolder
- Keep: the finally block that clears RuntimeContextHolder
- Simplify to: resolve → set → doFilter → clear

## Validation
- `mvn compile` passes
- All references to `@EnableTenantFilter` removed from services (done in Task 004)
- No more `@Filter` or `@FilterDef` in entity code
- `ContextFilter` no longer blocks requests based on context tenantId
