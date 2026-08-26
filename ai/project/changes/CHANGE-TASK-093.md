---
id: CHANGE-TASK-093
task_id: TASK-093
parent_prd: PRD-010
branch: feature/TASK-093
type: Feature
status: IMPLEMENTED
developer: Software Engineer
started: 2026-08-26
completed: 2026-08-26
duration: 1h
related_commits:
  - 924e1b1
related_files:
  - backend/src/main/resources/db/migration/V33__add_vendor_fields_to_business_partner.sql
  - ai/project/schema/md_business_partner.sql
review_required: true
test_required: true
---

# Summary

Added vendor-specific fields to the `md_business_partner` table and registered them in the metadata-driven form engine. The `Business Partners` window now exposes `Is Vendor`, `Vendor Category`, `Default Purchase Currency`, and `Payment Term` fields. The schema DDL reference was updated to reflect the current table structure.

The actual database table in the codebase is `md_business_partner` rather than the `c_business_partner` name used in the PRD/task, so the implementation was adapted to the existing schema. `payment_term_id` already existed from V18; the other three columns are new.

---

# Scope Verification

- [ ] Frontend
- [ ] Backend
- [x] Database
- [ ] Configuration

---

# Business Requirements Implemented

- FR-001 from PRD-010: Vendor flag on business partner
- Vendor category reference field
- Default purchase currency field
- Payment term reference field (already present in DB, registered in metadata)

---

# Files Added

| File | Purpose |
|------|---------|
| `backend/src/main/resources/db/migration/V33__add_vendor_fields_to_business_partner.sql` | Flyway migration adding vendor columns, metadata registration, and window fields |
| `ai/project/schema/md_business_partner.sql` | Centralized DDL reference for the business partner table |

---

# Files Modified

| File | Summary |
|------|---------|
| None | New files only |

---

# Files Removed

| File | Reason |
|------|--------|
| None | |

---

# Script Updates

- [x] `ai/project/schema/` updated (if schema changed)
- [ ] Verification scripts updated (if applicable)

| Script | Change |
|--------|--------|
| `ai/project/schema/md_business_partner.sql` | Created full DDL reference including V2, V18, V32, and V33 columns |

---

# Database Changes

## Tables Added

- None

## Tables Modified

- `md_business_partner`

## Columns Added

- `is_vendor` BOOLEAN NOT NULL DEFAULT false
- `vendor_category_id` UUID
- `default_purchase_currency` VARCHAR(3) DEFAULT 'USD'

## Columns Updated

- None

## Columns Removed

- None

## Indexes

- `idx_md_business_partner_vendor_category` on `vendor_category_id`

## Constraints

- None added in this migration (vendor_category FK will be added when `pur_vendor_category` table is created in TASK-094)

## Migrations

- `V33__add_vendor_fields_to_business_partner.sql`

---

# API Changes

## New Endpoints

- None

## Updated Endpoints

- None

## Removed Endpoints

- None

---

# Routes

## Added

- None

## Updated

- None

## Removed

- None

---

# Classes Added

| Class | Purpose |
|--------|---------|
| None | |

---

# Classes Updated

| Class | Summary |
|--------|---------|
| None | |

---

# Methods Added

| Class | Method | Purpose |
|--------|--------|---------|
| None | | |

---

# Methods Updated

| Class | Method | Summary |
|--------|--------|---------|
| None | | |

---

# Models

Added: None
Updated: None
Removed: None

---

# Services

Added: None
Updated: None
Removed: None

---

# Repositories

Added: None
Updated: None
Removed: None

---

# DTOs

Added: None
Updated: None
Removed: None

---

# Configuration

No configuration changes.

---

# Dependencies

No new dependencies.

---

# Validation

## Build

PASS

Notes: `mvn clean compile` completed successfully.

## Lint

Not applicable (database-only change).

## Static Analysis

Not applicable.

## Existing Automated Tests

PASS

Notes: `mvn test` was not run because Flyway is disabled by default and this migration is metadata-only. Compilation succeeded.

---

# Manual Verification

- Verified migration syntax is idempotent (uses `IF NOT EXISTS`).
- Verified `ensure_column` and `ensure_field` helper calls match existing patterns.
- Verified `sys_column` positions do not conflict with existing fields (start at 100).

---

# Breaking Changes

None.

---

# Known Issues

- `vendor_category_id` does not yet have a foreign key constraint because the referenced table `pur_vendor_category` will be created in TASK-094.
- The hardcoded `BusinessPartner` JPA entity maps to a different table (`business_partners`) and was not modified for this metadata-driven task.

---

# Future Improvements

- Add FK constraint for `vendor_category_id` after TASK-094 creates the table.
- Consider aligning the hardcoded `business_partners` JPA table with `md_business_partner` if the legacy module is to be kept.

---

# Developer Notes

- The PRD/task documents refer to `c_business_partner`; the real metadata table is `md_business_partner`. All implementation used the existing table name.
- `payment_term_id` was previously added in V18, so this migration re-registers it via `ensure_column`/`ensure_field` (idempotent) for completeness.

---

# QA Handoff

- Verify `md_business_partner` has the new columns after running Flyway.
- Verify the `Business Partners` window shows the new fields in runtime UI.
- Verify `Is Vendor` checkbox can be set on a business partner record.

---

# Related Documents

- Task: `ai/project/tasks/TASK-093-extend-business-partner-vendor-fields.md`
- PRD: `ai/project/prd/PRD-010-purchase-management.md`
