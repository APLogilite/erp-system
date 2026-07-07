# Task ID-STR-001-006: Remove Old User Entity Tables

## Plan Reference
Main plan: `final-identity-structure.md` (ID-STR-001)

## Objective
Delete the `UserOrganization`, `UserCompany`, and `UserBranch` entities and their repositories. These are fully replaced by `RoleOrganization`, `RoleCompany`, `RoleBranch` and the `AccessScopeService`.

## Files to Delete

### Entities (3 files)
| File | Path |
|------|------|
| `UserOrganization.java` | `entity/UserOrganization.java` |
| `UserCompany.java` | `entity/UserCompany.java` |
| `UserBranch.java` | `entity/UserBranch.java` |

### Repositories (3 files)
| File | Path |
|------|------|
| `UserOrganizationRepository.java` | `repository/UserOrganizationRepository.java` |
| `UserCompanyRepository.java` | `repository/UserCompanyRepository.java` |
| `UserBranchRepository.java` | `repository/UserBranchRepository.java` |

## Cleanup Required
After deletion, check for remaining references in:

1. **`RuntimeContextService.java`** — remove imports and any references to UserOrg/UserCo/UserBranch (likely leftover from old `getAvailableOptions()` or `resolve()`)
2. **`IdentitySeedData.java`** — remove calls to `assignOrg()`, `assignCompany()`, `assignBranch()` helper methods and the helper methods themselves
3. **`AdminService.java`** — remove any remaining imports
4. **Any other files** — `grep` for `UserOrganization`, `UserCompany`, `UserBranch` across the whole backend

## Validation
- `mvn compile` passes
- `grep -r "UserOrganization\|UserCompany\|UserBranch" backend/src/` returns no results
