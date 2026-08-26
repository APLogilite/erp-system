---
id: CHANGE-TASK-095
task_id: TASK-095
parent_prd: PRD-010
branch: feature/TASK-095
type: Feature
status: IMPLEMENTED
developer: Software Engineer
started: 2026-08-26
completed: 2026-08-26
duration: 1h
related_commits:
  - TBD
related_files:
  - backend/src/main/resources/db/migration/V35__seed_vendor_and_price_list_windows.sql
review_required: true
test_required: true
---

# Summary

Seeded metadata windows, tabs, fields, menus, and window access for Vendors, Vendor Categories, and Purchase Price Lists so they are available in the runtime UI.

---

# Scope Verification

- [ ] Frontend
- [ ] Backend
- [x] Database
- [ ] Configuration

---

# Business Requirements Implemented

- Vendors window filtered to `is_vendor = true`
- Vendor Categories window
- Purchase Price Lists window with lines tab
- Purchasing menu entries
- Window access for sys_admin

---

# Files Added

| File | Purpose |
|------|---------|
| `backend/src/main/resources/db/migration/V35__seed_vendor_and_price_list_windows.sql` | Flyway seed migration |

---

# Files Modified

None.

---

# Database Changes

## Tables Added

None.

## Tables Modified

- `sys_window`, `sys_tab`, `sys_field`, `sys_window_field`, `sys_menu`, `sys_window_access` populated.

## Migrations

- `V35__seed_vendor_and_price_list_windows.sql`

---

# Validation

## Build

PASS

Notes: `mvn clean compile` succeeded.

---

# Manual Verification

- Verified metadata seeding statements are guarded with `WHERE NOT EXISTS`.
- Verified `add_child_tab` column lookup is correct.

---

# Breaking Changes

None.

---

# Known Issues

None.

---

# Related Documents

- Task: `ai/project/tasks/TASK-095-seed-vendor-price-list-windows-menus.md`
- PRD: `ai/project/prd/PRD-010-purchase-management.md`
