---
document: CHANGELOG
version: 1.0.0
status: ACTIVE
owner: Product Manager
last_updated: 2026-07-16
---

# Changelog

## 2026-07-16 (PRD-004 Merged to Main — COMPLETED)

### Summary
QA verification complete. Both BUG-010 and BUG-011 TESTED. PRD-004 advanced to READY_FOR_DEPLOYMENT (v1.1.0).

### QA Results
| Bug | Tests | Passed | Failed | Bugs Created |
|-----|-------|--------|--------|-------------|
| BUG-010 | 10 | 10 | 0 | 0 |
| BUG-011 | 15 | 15 | 0 | 0 |

### PRD Status Changed
- **PRD-004** — Window Hierarchy & Menu System v1.0.0 → v1.1.0: **READY_FOR_DEPLOYMENT → COMPLETED** (merged to main)

---

## 2026-07-16 (PRD-004 Advanced to TESTING)

### Summary
PRD-004 advanced to TESTING after completion of BUG-010 and BUG-011 fixes, consolidation of 14 migrations into 6 clean V1-V6 scripts, and multiple feature enhancements.

### Changes since REOPENED
- **BUG-010** (FIXED): POST create record 500 error — added required field validation + DataAccessException handler
- **BUG-011** (FIXED): Child tab record data not loading — added resolveDisplayNames at all drill levels + grandchild fetching
- **Migrations consolidated**: 14 old scripts → 6 clean V1-V6 by module
- **Display column**: `is_display_column` flag on `sys_column` for metadata-driven FK display values
- **Field labels**: Pre-resolved in backend (`labelOverride ?? column.label`), frontend uses `field.label`
- **Filter where clause**: Per-field `filter_where_clause` on `sys_window_field` with `@tab.field@` placeholder resolution
- **Lookup filters**: Column-level + field-level filter support, resolved server-side via full drill context
- **Breadcrumb**: Shows navigation path tab names with display values
- **Refresh button**: Added to header bar for one-click cache clear

### PRD Status Changed
- **PRD-004** — Window Hierarchy & Menu System v1.0.0: **IN_DEVELOPMENT → TESTING**

---

## 2026-07-15 (BUG-011 — Child Tab Record Data Not Loading)

### Summary
Second post-release bug filed against PRD-004 (already REOPENED). Child tab form/layout renders correctly (fields visible) but the actual record data within the child tab does not load — the grid/table is empty for Lines, Shipments, Payments, etc.

### Bug Created
| Bug | Severity | Status | Parent PRD | Parent Task |
|-----|----------|--------|------------|-------------|
| BUG-011 | Critical | READY_FOR_DEV | PRD-004 | TASK-041 |

### PRD-004 Status
- Already REOPENED (BUG-010) — no status change needed

---

## 2026-07-15 (BUG-010 — POST Sales Orders Record Returns HTTP 500)

### Summary
Post-release bug filed against PRD-004 (COMPLETED). Saving a Sales Order record via `POST /api/v1/runtime/windows/Sales%20Orders/records` returns HTTP 500 Internal Server Error. PRD-004 status changed to REOPENED. BUG-010 created with Critical severity.

### Bug Created
| Bug | Severity | Status | Parent PRD | Parent Task |
|-----|----------|--------|------------|-------------|
| BUG-010 | Critical | READY_FOR_DEV | PRD-004 | TASK-039 |

### PRD Status Changed
- **PRD-004** — Window Hierarchy & Menu System v1.0.0: **COMPLETED → REOPENED**

---

## 2026-07-14 (BUG-009 — Flyway Migration Chain Broken on Fresh DB)

### Summary
When enabling Flyway on a **fresh PostgreSQL database**, V3__create_sys_table_columns.sql fails with `ERROR: relation "sys_metadata_models" does not exist`. The old V1–V23 migration chain was designed assuming JPA `ddl-auto=update` would pre-create base tables. On a fresh DB, the FK constraint in V3 references a table that no migration creates.

### Root Cause
- V3 creates `sys_table_columns` with `REFERENCES sys_metadata_models(id)` but no migration creates `sys_metadata_models`
- Old migrations (V3–V23) create/seed old metadata schema that V24 drops anyway
- Business table DDL (V19–V20) is interleaved with old metadata registration
- The entire V3–V23 chain is partially obsolete scaffolding for the old PRD-001 schema

### Bug Created
| Bug | Severity | Status | Description |
|-----|----------|--------|-------------|
| BUG-009 | Critical | READY_FOR_DEV | Flyway chain breaks on fresh DB at V3 — needs migration consolidation |

---

## 2026-07-14 (Bugs Found — PRD-004 Features Not Working in Runtime)

### Summary
User reports three issues with PRD-004 features after merge to main — all caused by the same root cause: **Flyway migrations V24–V28 never ran**. 

Both bugs created:
- **BUG-007** (Critical): Flyway disabled — schema not applied, seed data missing. Sidebar has no menu, old tables still exist.
- **BUG-008** (Medium): Ctrl+K search still references old PRD-001 form schema instead of new Window names.

### Root Cause
`spring.flyway.enabled=false` was the default setting. Since PRD-004 relies entirely on Flyway migrations (V24–V28) for schema changes and seed data, none of the new features were operational at runtime.

### QA Testing Gap
All PRD-004 QA tests were **structural** (file existence, code compilation, SQL file content review) — no **runtime functional testing** was performed. This allowed the Flyway issue to go undetected.

### New Bugs
| Bug | Severity | Status | Description | Depends On |
|-----|----------|--------|-------------|------------|
| BUG-007 | Critical | READY_FOR_DEV | Flyway disabled — schema + seed data not applied | — |
| BUG-008 | Medium | READY_FOR_DEV | Ctrl+K search still uses old schema | BUG-007 |

---

## 2026-07-13 (PRD-004 Advanced to TESTING — All 10 Tasks Complete)

### Summary
All 10 PRD-004 implementation tasks completed and advanced to READY_FOR_TEST. The new Window Hierarchy & Menu System is fully implemented:
- New metadata schema (V24): sys_table, sys_column, sys_window, sys_tab, sys_window_field, sys_window_access, sys_menu
- JPA entities + CRUD services for all 7 metadata tables
- Runtime API: window definition endpoint, window data CRUD endpoints, menu endpoint
- Frontend: WindowPage with /window/{windowName} routing, MenuNavigation component with collapsible hierarchy
- Seed data: 12 business tables registered (V25), 7 admin windows with tabs/fields (V26), 10 ERP windows (V27), menu tree + access (V28)

### PRD-004 Status
- **PRD-004** — Window Hierarchy & Menu System → **TESTING** (v1.0.0)
  - 10 tasks completed (TASK-036 through TASK-045)
  - All 36 backend tests pass
  - Frontend typecheck passes

## 2026-07-14 (BUG-002/BUG-003/BUG-004 Marked TESTED — Fixes Already Verified)

### Summary
Three bugs (BUG-002, BUG-003, BUG-004) had test reports showing COMPLETED but their task files still showed IN_DEVELOPMENT/READY_FOR_TEST. Verified codebase confirms all fixes are in place. Updated task statuses to TESTED.

| Bug | Was | Now | Fix verified |
|-----|:---:|:---:|:------------|
| BUG-002 | READY_FOR_TEST | **TESTED** | ApiVersionConfig.API_BASE = "/api/v1" ✅ |
| BUG-003 | IN_DEVELOPMENT | **TESTED** | AppLayout has marginLeft compensation ✅ |
| BUG-004 | IN_DEVELOPMENT | **TESTED** | FormSearchBar has search icon + loading ✅ |

---

## 2026-07-14 (PRD-003 Marked COMPLETED — Branch Merged to Main)

### Summary
PRD-003's 5 tasks (TASK-028..032) were in TESTED status but the branch had already been merged to main. Updated to COMPLETED. PRD-003 status: READY_FOR_DEPLOYMENT → COMPLETED.

---

## 2026-07-14 (PRD-004 Merged to Main — COMPLETED)

### Summary
PRD-004 (Window Hierarchy & Menu System v1.0.0) merged to `main`. All 10 tasks, all changes, and the PRD itself advanced to COMPLETED. All 4 PRDs are now COMPLETED.

### PRD Completed
- **PRD-004** — Window Hierarchy & Menu System v1.0.0 → **COMPLETED** (merged to main)

### Changes Merged (143 files, +8647/-5894)
- **New Metadata Schema:** 7 tables (sys_table, sys_column, sys_window, sys_tab, sys_window_field, sys_window_access, sys_menu) via V24
- **JPA Entities + Services:** All 7 entities, repositories, and CRUD services in `modules/metadata/`
- **Runtime APIs:** Window definition endpoint, window data CRUD endpoint, menu endpoint
- **Frontend:** WindowPage with `/window/{name}` routing, MenuNavigation collapsible hierarchy
- **Seed Data:** V25 (12 business tables), V26 (7 admin windows), V27 (10 ERP windows), V28 (menu tree + access)
- **Documentation:** 10 change reports, 10 test reports, schema verification script

### Project State
- **4 PRDs ALL COMPLETED** — PRD-001, PRD-002, PRD-003, PRD-004
- **47 tasks COMPLETED** + 6 bug tasks (BUG-001..006 at TESTED or READY_FOR_TEST)
- **2 enhancements COMPLETED** (ENH-001, ENH-002), 1 pending (ENH-003 at READY_FOR_TEST)

---

## 2026-07-14 (PRD-004 QA Complete — READY_FOR_DEPLOYMENT)

### Summary
QA verification completed for all 10 PRD-004 tasks. Full structural verification performed — 118 test cases across all tasks, 0 bugs found. Reusable PRD-004 schema verification script created.

### QA Results
| Task | Tests Passed | Tests Skipped | Bugs |
|------|:---:|:---:|:---:|
| TASK-036 — New Metadata Schema | 15 | 2 (PSQL runtime, rollback) | 0 |
| TASK-037 — JPA Entities + Services | 10 | 2 (FK pattern, runtime query) | 0 |
| TASK-038 — Window Definition API | 5 | 0 | 0 |
| TASK-039 — Window Data API | 9 | 0 | 0 |
| TASK-040 — Menu Component | 11 | 0 | 0 |
| TASK-041 — WindowPage + Routing | 11 | 0 | 0 |
| TASK-042 — Register Business Tables | 19 | 0 | 0 |
| TASK-043 — Admin Windows | 10 | 0 | 0 |
| TASK-044 — ERP Windows | 14 | 0 | 0 |
| TASK-045 — Menu + Access | 12 | 0 | 0 |
| **Total** | **118** | **4** | **0** |

### State
- **PRD-004** → **READY_FOR_DEPLOYMENT** (v1.0.0)
- All 10 tasks verified and TESTED
- 0 bugs created
- Ready for PostgreSQL runtime validation and production deployment

## 2026-07-13 (PRD-004 Created — Window Hierarchy & Menu System)

### Summary
New PRD-004 created to replace the old PRD-001/002/003 metadata schema with an iDempiere-inspired Window/Tab/Field/Menu hierarchy. The old metadata tables are dropped and replaced with a clean three-layer design: Database Schema (sys_table/sys_column), Window Design (sys_window/sys_tab/sys_window_field/sys_window_access), and Menu (sys_menu). The RuntimePage is fixed to use real API data instead of hardcoded bundles. Routes change from /runtime/{formCode} to /window/{windowName}.

### PRD Created
- **PRD-004** — Window Hierarchy & Menu System (v1.0.0, APPROVED)
  - 7 new metadata tables: sys_table, sys_column, sys_window, sys_tab, sys_window_field, sys_window_access, sys_menu
  - 3-layer architecture: Database → Window Design → Menu
  - Admin windows for all metadata tables (replaces PRD-002)
  - ERP windows seeded for Sales/Purchase Orders, Invoices, Payments, Shipments (replaces PRD-003)
  - Hierarchical menu system with collapsible groups
  - RuntimePage fixed to render actual dynamic forms
  - Routes changed to /window/{windowName}
  - Field display/readonly logic inline on field (iDempiere style)

### Tasks Generated (10)
| Task | Priority | Description | Depends On |
|------|----------|-------------|------------|
| TASK-036 | Critical | Create new metadata schema (Flyway migration) | — |
| TASK-037 | Critical | Backend: JPA entities + services for new tables | TASK-036 |
| TASK-038 | Critical | Backend: Runtime window definition API (bundle) | TASK-037 |
| TASK-039 | Critical | Backend: Runtime window data API (CRUD records) | TASK-037, TASK-038 |
| TASK-040 | Critical | Frontend: Menu component + navigation | TASK-037 |
| TASK-041 | Critical | Frontend: Update routing to /window/{name} + fix RuntimePage | TASK-038, TASK-039, TASK-040, TASK-037 |
| TASK-042 | High | Seed data: Register business tables as sys_table + sys_column | TASK-036 |
| TASK-043 | High | Seed data: Admin windows for metadata management | TASK-037, TASK-042 |
| TASK-044 | High | Seed data: ERP windows with tabs/fields | TASK-042, TASK-037 |
| TASK-045 | High | Seed data: Menu entries + window access | TASK-043, TASK-044 |

### State
- PRD-004: 10 tasks at PLANNING
- 3 older PRDs: 2 COMPLETED, 1 READY_FOR_DEPLOYMENT
- All 10 tasks require PRD branch `prd/PRD-004-window-hierarchy-menu` to be created from `main`

---

## 2026-07-13 (BUG-001 Created — DatabaseConnectionTest Failures)

### Summary
Created BUG-001 to track the 3 pre-existing test errors in `DatabaseConnectionTest`. Previously documented as a known limitation in PROJECT_MEMORY.md but had no actionable task assigned. The bug causes `mvn test` to always end with `BUILD FAILURE` despite all functional tests passing (33/33 pass).

### Bug Created
- **BUG-001** — Fix pre-existing DatabaseConnectionTest failures (3 errors)
  - Priority: High | Severity: Medium | Status: READY_FOR_DEV
  - Parent PRD: PRD-001 (foundational architecture PRD)
  - Root causes identified: INFORMATION_SCHEMA case sensitivity, wrong table name for warehouse (`warehouses` vs `m1_warehouses`), test queries legacy JPA entity tables

### Project State
- **3 PRDs**: all COMPLETED or READY_FOR_DEPLOYMENT
- **37 tasks**: all completed
- **1 bug**: BUG-001 at READY_FOR_DEV

### Documentation Updated
- **BUG-001** created at `ai/project/tasks/BUG-001-database-connection-test-failures.md`
- **PROJECT_BOARD.md** — Bugs section populated with BUG-001
- **PROJECT_MEMORY.md** — Known Limitations updated (pre-existing test failures now tracked as BUG-001)

---

## 2026-07-13 (PRD-003 QA Complete — All 37 Tasks Done)

### Summary
QA completed structural verification of all 5 PRD-003 tasks (V19-V23 migrations). All 5 tasks PASSED with zero failures. PRD-003 advanced to READY_FOR_DEPLOYMENT. Project fully complete — all 37 tasks across 3 PRDs are done.

### PRD Advanced
- **PRD-003** — ERP Order Flow — Transaction Forms v1.0.0: TESTING → **READY_FOR_DEPLOYMENT**

### Tasks Tested (5)
| Task | Migration | Result |
|------|-----------|--------|
| TASK-028 | V19 — 5 master data tables | **PASSED** (12 tests) |
| TASK-029 | V20 — 9 transaction tables | **PASSED** (11 tests) |
| TASK-030 | V21 — 5 master data forms | **PASSED** (9 tests) |
| TASK-031 | V22 — 9 transaction header forms | **PASSED** (9 tests) |
| TASK-032 | V23 — 4 line forms + 7 sub-form configs | **PASSED** (10 tests) |

### Full Project State
- **3 PRDs**: 2 COMPLETED, 1 READY_FOR_DEPLOYMENT
- **37 tasks**: all completed and tested
- **0 bugs**, 0 blocked, 0 pending (before BUG-001)

---

### Summary
All 5 PRD-003 tasks implemented via Flyway migrations V19-V23. The ERP Order Flow module now has 5 master data tables, 9 transaction tables, 5 master data forms, 9 transaction header forms, 4 line forms, and 7 sub-form tab configurations — all seeded as metadata for PRD-001's runtime engine.

### PRD Advanced
- **PRD-003** — ERP Order Flow — Transaction Forms v1.0.0: IN_DEVELOPMENT → **TESTING**

### Tasks Implemented (5)
| Task | Migration | Content |
|------|-----------|---------|
| TASK-028 | V19 | 5 master data tables (Business Partner, Product, UOM, UOM Conversion, Warehouse) |
| TASK-029 | V20 | 9 transaction tables (Order, Invoice, Payment, Shipment, Material Receipt + 4 line tables) |
| TASK-030 | V21 | 5 master data forms with layout sections |
| TASK-031 | V22 | 9 transaction header forms with purchase/sales where_clause variants |
| TASK-032 | V23 | 4 line-item forms + 7 sub-form tab configurations |

All tasks at READY_FOR_TEST — awaiting QA Engineer.

### Project State
- **3 PRDs**: 2 COMPLETED, 1 TESTING
- **37 tasks**: 32 completed + 5 ready for test
- **0 bugs**, 0 blocked, 0 planning

---

## 2026-07-13 (PRD-001 + PRD-002 Marked COMPLETED)

### Summary
All 27 PRD-001 tasks (v1.6.0) + ENH-001 and all 3 PRD-002 tasks (v1.1.0) + ENH-002 have passed QA testing with zero failures. Advanced all tasks and PRDs to COMPLETED status.

### PRDs Completed
- **PRD-001** — Dynamic Form Configuration System v1.6.0 → **COMPLETED**
- **PRD-002** — Admin Configuration Forms v1.1.0 → **COMPLETED**

### Tasks Advanced to COMPLETED (32)
- **PRD-001 (28):** TASK-001 through TASK-027 + ENH-001
- **PRD-002 (4):** TASK-033, TASK-034, TASK-035 + ENH-002

### Current Project State
- **2 PRDs COMPLETED** — Core engine (PRD-001) and Admin Forms (PRD-002) fully delivered
- **1 PRD APPROVED** — PRD-003 (ERP Order Flow) awaiting development
  - TASK-028: READY_FOR_DEV (seed master data tables)
  - TASK-029-032: PLANNED (awaiting TASK-028)
- **32 tasks COMPLETED**, 1 READY_FOR_DEV, 4 PLANNED
- **0 bugs**, 0 blocked tasks

---

## 2026-07-10 (PRD-002 QA Complete + ENH-002 Created)

### Summary
QA completed structural verification of all 3 PRD-002 tasks (52 tests, 0 failures). Identified REQ-ISSUE-001: tenant_id field missing from 10 of 11 admin forms — a tenant-isolation concern in a multi-tenant platform. PRD-002 updated to v1.1.0 with strengthened tenant_id requirement. ENH-002 created for correction.

### PRD Updated
- **PRD-002** — Admin Configuration Forms v1.0.0 → v1.1.0
  - Strengthened tenant_id requirement: ALL 11 admin forms MUST display tenant_id (read-only) for tenant isolation auditability
  - Fixed ambiguous language ("where relevant" → mandatory on every form)
  - Fixed duplicate tenant_id spec on admin_tenant_role_access

### Tasks Tested
- **TASK-033** (Register Metadata Tables — V15): TESTED — 16/16 structural tests passed
- **TASK-034** (Core Admin Forms — V16): TESTED — 15/15 structural tests passed
- **TASK-035** (Remaining Admin Forms — V17): TESTED — 21/21 structural tests passed
- Test reports: `ai/project/tests/TEST-TASK-033.md`, `TEST-TASK-034.md`, `TEST-TASK-035.md`

### Enhancement Created
- **ENH-002** — Add tenant_id Field to All Admin Forms (Critical, READY_FOR_DEV)
  - Parent PRD: PRD-002 v1.1.0
  - Parent Tasks: TASK-034, TASK-035
  - Scope: 1 Flyway migration (V18) adding 11 column registrations + 10 form fields + 10 section-field mappings

### State
- PRD-002: 3 tasks TESTED, 1 enhancement pending (ENH-002)
- PRD-002 v1.1.0: tenant_id requirement now mandatory on all admin forms
- PRD-003: TASK-028 ready for development
- 2 tasks available for development: ENH-002 (Critical), TASK-028 (High)

---

## 2026-07-10 (PRD-002 Created — Admin Configuration Forms)

### Summary
New PRD-002 created for Admin Configuration Forms — dynamic forms that manage PRD-001's own metadata tables. Enables administrators to use the runtime form renderer to view/edit table definitions, columns, form configs, fields, rules, validations, layouts, sub-forms, and role access.

### PRD Created
- **PRD-002** — Admin Configuration Forms — Metadata Table Management (v1.0.0, DRAFT)
  - Register 11 metadata tables in sys_metadata_models as static tables
  - Create ~14 CRUD forms for metadata entities
  - Sub-form links for parent-child relationships (Table → Columns, Form → Fields)
  - Pending user review — 4 open questions

### State
- PRD-002: 11 static tables registered, ~11 admin forms, 3 tasks (TASK-033 READY_FOR_DEV, 2 PLANNED)
- PRD-003: 14 tables, 17 forms, 5 tasks (TASK-028 READY_FOR_DEV, 4 PLANNED)
- 2 tasks available for development immediately (TASK-028, TASK-033)

---

## 2026-07-10 (PRD Renumbering — PRD-002 → PRD-003)

### Summary
Renumbered the ERP Order Flow Transaction Forms PRD from PRD-002 to PRD-003. New PRD-002 will be Admin Configuration Forms (forms for managing PRD-001's metadata entities).

### Changes
- **PRD-003** (was PRD-002) — ERP Order Flow — Transaction Forms v1.0.0 APPROVED
- All 5 tasks (TASK-028 to TASK-032) updated: `parent_prd: PRD-003`, `prd_branch`, `merge_target`
- PROJECT_BOARD.md and PRD-001 updated to reference PRD-003
- Old PRD-002 file retained for reference; new PRD-003 file created with full content

### State
- PRD-003: 14 tables, 17 forms, 5 tasks (TASK-028 READY_FOR_DEV, 4 PLANNED)
- PRD-002: To be created — Admin Configuration Forms

---

## 2026-07-10 (PRD-002 Created — ERP Order Flow Transaction Forms)

### Summary
New PRD created to seed the platform with standard ERP transaction forms using PRD-001's dynamic form engine. Pure metadata approach — zero new code, only Flyway migrations. 14 tables, 17 forms.

### PRD Created
- **PRD-002** — ERP Order Flow — Transaction Forms (v1.0.0, APPROVED)
  - 5 master data tables: Business Partner, Product, UOM, UOM Conversion, Warehouse
  - 9 transaction tables: Order, Order Line, Invoice, Invoice Line, Payment, Shipment, Shipment Line, Material Receipt, MR Line
  - 17 forms with CRUD via PRD-001's runtime renderer
  - Purchase/Sales variants via form-level where_clause
  - Header-line sub-form tabs with breadcrumb navigation
  - All implementation via Flyway migrations — no new Java/TypeScript code

### Tasks Generated (5)
- **TASK-028** — Seed Master Data Tables (Flyway migration: DDL + metadata for 5 tables)
- **TASK-029** — Seed Transaction Tables (Flyway migration: DDL + metadata for 9 tables)
- **TASK-030** — Seed Master Data Forms (Flyway migration: form definitions for 5 master data forms)
- **TASK-031** — Seed Transaction Header Forms (Flyway migration: 9 header forms with layouts and where_clause)
- **TASK-032** — Seed Line Forms and Sub-Form Configs (Flyway migration: 4 line forms + 7 sub-form links)

### Dependencies
```
TASK-028 ──┬── TASK-029 ──┬── TASK-031 ── TASK-032
           │              │
           └── TASK-030 ──┘
```

### State
- All 5 tasks at **PLANNED** — awaiting PRD-001 completion before activation
- No tasks at READY_FOR_DEV (PRD-001 must reach COMPLETED first)
- PRD-002 APPROVED — requirements final, tasks ready for activation

### Notes
- PRD-002 depends on PRD-001 (runtime engine + metadata tables)
- Tasks will auto-activate to READY_FOR_DEV when PRD-001 is COMPLETED
- Flyway must be temporarily enabled (`spring.flyway.enabled=true`) for migrations

---

## 2026-07-08 (Documentation Audit — Change Report Restoration)

### Summary
Repository-wide documentation audit executed to restore missing implementation documentation per the new CHANGE_TEMPLATE.md standard. 28 tasks (27 TASK + 1 ENH) audited. All 21 READY_FOR_TEST tasks now have complete change reports.

### Change Reports Created — Round 1 (4)
- **CHANGE-TASK-010.md** — Per-Tenant Role Assignment APIs: FormTenantRoleController (60L, 4 endpoints), FormTenantRoleService (131L), 3 DTOs
- **CHANGE-TASK-018.md** — Breadcrumb Service: BreadcrumbService (106L), BreadcrumbEntry DTO, ParentContext DTO
- **CHANGE-TASK-024.md** — FormBreadcrumb Component: MUI Breadcrumbs navigation (43L)
- **CHANGE-TASK-025.md** — FormSearchBar Component: Ctrl+K search dialog (89L) + useAccessibleForms hook (23L)

### Change Reports Created — Round 2 (3)
- **CHANGE-TASK-011.md** — Form Designer Admin UI Core: FormListPage (127L), FormDesignerPage (79L), FieldsTab (119L), LayoutTab (105L), CreateFormDialog (104L), 3 hooks (190L), types (47L)
- **CHANGE-TASK-012.md** — Rules & Validation UI: RulesTab (121L), ValidationTab (110L), 2 hooks (124L)
- **CHANGE-TASK-013.md** — Sub-Forms Tab: SubFormsTab (172L) with available relations API integration

### Change Reports Reformatted (4)
Converted from compact format to full CHANGE_TEMPLATE.md format:
- **CHANGE-TASK-016.md** — Form Definition Bundle API
- **CHANGE-TASK-017.md** — Record Data APIs
- **CHANGE-TASK-019.md** — useForm() Hook
- **CHANGE-TASK-020.md** — Dynamic Form Renderer

### Task Documents Updated (7)
Added `change_summary` references: TASK-010, 011, 012, 013, 018, 024, 025

### PROJECT_BOARD Corrected
- Fixed 8 false "MISSING" flags (TASK-004, 006, 008, 009, 016, 017, 019, 020)
- Added 7 new change report references (TASK-010, 011, 012, 013, 018, 024, 025)
- **All 21 READY_FOR_TEST tasks now have 100% change summary coverage**

### State After Audit
- **21 tasks READY_FOR_TEST** — All with complete change reports, ready for QA
- **6 tasks IN_DEVELOPMENT** — Active development (TASK-021, 022, 023, 026, 027, ENH-001)
- **1 task PLANNED** — TASK-014 (not yet implemented)
- **0 tasks COMPLETED** — QA has not yet begun
- **21 change reports** — Full CHANGE_TEMPLATE.md format

---

## 2026-07-08 (Re-evaluation)

### Correction
- The 2026-07-07 Workflow Sync CHANGELOG entry incorrectly stated "All dependency checks now require COMPLETED (not READY_FOR_TEST)". The actual WORKFLOW.md (lines 259, 281, 285) states **"READY_FOR_TEST/COMPLETED"** — either status satisfies dependencies. The CHANGELOG discrepancy has been corrected.

### Task Restorations (PLANNED → READY_FOR_DEV)
Under the correct WORKFLOW rule (READY_FOR_TEST satisfies dependencies), the following 7 tasks were restored to READY_FOR_DEV:

- **TASK-004** — Deps TASK-002 (READY_FOR_TEST), TASK-003 (READY_FOR_TEST) ✓
- **TASK-008** — Dep TASK-007 (READY_FOR_TEST) ✓
- **TASK-009** — Dep TASK-007 (READY_FOR_TEST) ✓
- **TASK-010** — Dep TASK-007 (READY_FOR_TEST) ✓
- **TASK-011** — Dep TASK-007 (READY_FOR_TEST) ✓
- **TASK-016** — Deps TASK-007 (READY_FOR_TEST), TASK-015 (READY_FOR_TEST) ✓
- **ENH-001** — Dep TASK-007 (READY_FOR_TEST) ✓

### State After Re-evaluation
- **7 tasks READY_FOR_DEV** — Available for Software Engineer immediately
- **6 tasks READY_FOR_TEST** — Awaiting QA Engineer
- **14 tasks PLANNED** — Awaiting downstream dependency completion
- **0 tasks COMPLETED** — QA has not yet begun

---

## 2026-07-08 (Planning Audit — Framework Migration)

### Summary
Full planning audit executed to migrate all planning artifacts to the current planning framework. PROJECT_BOARD.md was missing and recreated. Task dependencies and metadata were reviewed for consistency.

### NOTE: The demotions performed in this audit were subsequently reversed in the 2026-07-08 Re-evaluation above. See that entry for the correct, final state.

### PROJECT_BOARD
- **Created** `ai/agent/project-board.md` with full PRD, task, enhancement, and statistics tracking

### Task Status Corrections (READY_FOR_DEV → PLANNED)
The following 7 tasks were demoted from READY_FOR_DEV to PLANNED because their dependencies are at READY_FOR_TEST (not COMPLETED). The current WORKFLOW requires dependencies to reach COMPLETED before dependent tasks can activate.

- **TASK-004** → PLANNED (depends on TASK-002, TASK-003 — both READY_FOR_TEST)
- **TASK-008** → PLANNED (depends on TASK-007 — READY_FOR_TEST)
- **TASK-009** → PLANNED (depends on TASK-007 — READY_FOR_TEST)
- **TASK-010** → PLANNED (depends on TASK-007 — READY_FOR_TEST)
- **TASK-011** → PLANNED (depends on TASK-007 — READY_FOR_TEST)
- **TASK-016** → PLANNED (depends on TASK-007, TASK-015 — both READY_FOR_TEST)
- **ENH-001** → PLANNED (depends on TASK-007 — READY_FOR_TEST)

### Metadata Corrections
- **TASK-001** — prd_version: 1.5.0 → 1.6.0 (implementation scope already covered 1.6.0 requirements per change summary)
- **TASK-002** — prd_version: 1.5.0 → 1.6.0 (same)
- **TASK-005** — prd_version: 1.5.0 → 1.6.0 (same)

### State After Migration
- **0 tasks READY_FOR_DEV** — No dependencies are COMPLETED yet; development blocked until QA completes
- **6 tasks READY_FOR_TEST** — Awaiting QA Engineer (TASK-001, TASK-002, TASK-003, TASK-005, TASK-007, TASK-015)
- **21 tasks PLANNED** — All fully defined, awaiting dependency activation
- **1 enhancement PLANNED** — ENH-001 awaiting TASK-007 COMPLETED

### Known Issue (Flagged, Not Fixed)
- **PRD-001** FR-014 identifier is used twice (Sub-Forms section and Dynamic Form Rendering section). Documentation inconsistency — does not affect implementation. Should be resolved in next PRD version update.

### Verified
- All 27 tasks reference `parent_prd: PRD-001` ✅
- All tasks have acceptance criteria ✅
- All tasks have dependencies defined ✅
- All task Git workflow fields consistent ✅
- No circular dependencies ✅
- No BLOCKED or CANCELLED tasks ✅
- No failure reports ✅
- No Bug tasks (implementation not yet QA-tested) ✅

---

## 2026-07-07

### Added

- **PRD-001** — Dynamic Form Configuration System (v1.1.0, REVIEW)
  - Table Designer (System Admin)
  - Form Designer (System Admin + Tenant Admin)
  - Runtime Form Renderer (All Users)
  - Global forms + per-tenant role assignment
  - Tenant data isolation for shared forms
  - Header form search (Ctrl+K/Cmd+K)
  - Consolidated single-request bundle API (form + model + records in one call)
  - Multi-level sub-forms with breadcrumb navigation
  - Normalized storage (all metadata in relational tables, no JSONB for mutable data)
  - Two-request loading pattern (definition cached + data fresh)
  - Frontend useForm() hook abstraction
  - Form toolbar (Create, Save, Discard, Refresh, Delete, Previous/Next)
  - Keyboard shortcuts (Ctrl+S, Alt+arrows, F5, Escape)
  - Record-to-record navigation with "Record X of Y" context
  - Role-based row-level data access (tenant isolation + per-role row filters)
- **PRD-001** (v1.6.0 APPROVED)
- **Generated 27 implementation tasks** (TASK-001 to TASK-027)
  - Phase 1: Database migrations + JPA entities (TASK-001, TASK-002)
  - Phase 2: Table Designer backend + frontend (TASK-003 to TASK-006)
  - Phase 3: Form Designer backend (TASK-007 to TASK-010)
  - Phase 4: Form Designer frontend (TASK-011 to TASK-014)
  - Phase 5: Runtime backend (TASK-015 to TASK-018)
  - Phase 6: Runtime frontend (TASK-019 to TASK-027)

## 2026-07-07 (Workflow Sync)

### WORKFLOW.md Updated
- New task status `PLANNED` added between `PLANNING` and `READY_FOR_DEV`
- Automatic task activation: when a task reaches COMPLETED, dependent PLANNED tasks auto-advance to READY_FOR_DEV
- PROJECT_BOARD.md is now the single source of truth for execution
- All dependency checks now require READY_FOR_TEST or COMPLETED (either satisfies dependencies)
- Clearer ownership matrix per status

### Synchronization Actions
- **PROJECT_BOARD.md** rebuilt (v1.1.0) with correct PLANNED status
- **24 tasks** moved from PLANNING → PLANNED (all fully defined, awaiting dependencies)
- **3 tasks** remain at READY_FOR_TEST (TASK-001, TASK-002, TASK-005 — implemented by Developer)
- **prd_version** updated from 1.5.0 → 1.6.0 on all 24 PLANNED tasks (PRD was updated)
- **0 tasks** at READY_FOR_DEV (no dependencies are COMPLETED yet)
- **0 tasks** at PLANNING (no tasks are incomplete in their definition)

## 2026-07-07 (Sync)

### Updated
- **TASK-001, TASK-002** → READY_FOR_TEST (Developer completed implementation)
- **TASK-003, TASK-005, TASK-007, TASK-015** → READY_FOR_DEV (dependencies satisfied)
- Created **PROJECT_BOARD.md** with full task tracking

## 2026-07-07 (Planning Audit)

### Updated
- **TASK_TEMPLATE.md** — Added `PLANNED`, `TESTING`, `TESTED` statuses; added git workflow fields (`prd_branch`, `base_branch`, `merge_target`, `merge_strategy`)
- **PROJECT_MEMORY.md** — Filled with project decisions, architecture notes, known limitations
- **PROJECT_BOARD.md** — Added ENH-001 to Phase 4 and Summary

### Corrected
- **TASK-003** — Removed duplicate `PLANNED` status line; added `started`, `completed`, `actual_hours`
- **TASK-005** — Added `actual_hours: 0.5`
- **TASK-007** — Removed duplicate `PLANNED` status line; added `actual_hours: 0.5`
- **TASK-015** — Added `actual_hours: 0.5`

### Created
- **ENH-001** — Enhancement for Form Designer Tenant Authorization (recreated from lost filesystem state)

### Verified (No Changes Needed)
- **TASK-001, TASK-002** — Frontmatter complete, matches template, change summaries exist ✅
- **TASK-004, TASK-006, TASK-008-014, TASK-016-027** — All at `PLANNED` with correct `prd_version: 1.6.0` ✅
- All tasks reference `parent_prd: PRD-001` ✅
- All tasks have acceptance criteria ✅
- All tasks have dependencies defined ✅
- No BLOCKED or CANCELLED tasks ✅
- No failure reports exist ✅

---

## 2026-07-08 (Implementation Audit)

### Summary
Codebase audit revealed the Developer implemented **far more tasks** than task documents reflected — 23 out of 27 tasks + 1 enhancement have code on disk. Task documents were stale. Updated all statuses to match reality.

### Tasks Updated to READY_FOR_TEST (implementation found on disk)
- **TASK-004** — TableDesignerController (84L) + Service
- **TASK-006** — TableListPage (152L), CreateTablePage, TableDetailPage, 3 components, 2 hooks
- **TASK-008** — FormRuleController, FormValidationController, 3 services
- **TASK-009** — FormSubFormController (50L) + Service
- **TASK-010** — FormTenantRoleController (60L) + Service
- **TASK-016** — RuntimeFormController (280L) + FormDefinitionAssemblyService
- **TASK-017** — RecordCrudService, RecordValidationService
- **TASK-018** — BreadcrumbService
- **TASK-019** — useForm.ts (238L), types, runtimeApi.ts
- **TASK-020** — DynamicFormRenderer (137L), FormFieldRenderer (201L)
- **TASK-024** — FormBreadcrumb
- **TASK-025** — FormSearchBar (89L)

### Tasks IN_DEVELOPMENT (code exists, Developer working)
- TASK-011, TASK-012, TASK-021, TASK-022, TASK-023, TASK-026, TASK-027, ENH-001

### Tasks Still PLANNED (code missing or partial)
- **TASK-013** — SubFormsTab exists; GlobalFormsBrowser, RoleAccessDialog, RowAccessTab, 4 hooks missing
- **TASK-014** — GlobalFormTenantAccessTable not found

### Already Correct (6 tasks)
TASK-001, TASK-002, TASK-003, TASK-005, TASK-007, TASK-015

### ⚠ Outstanding
12 READY_FOR_TEST tasks are **missing change summaries**. Change summaries exist only for TASK-001, 002, 003, 005, 007, 015. QA cannot effectively test without them.

---

## 2026-07-08 (Final Re-verification)

### Tasks Completed (→ READY_FOR_TEST)
- **TASK-011** — FormDesignerPage with all 5 tabs (Fields, Layout, Rules, Validations, Sub-Forms)
- **TASK-012** — Rules & Validation tabs integrated into FormDesignerPage
- **TASK-013** — SubFormsTab created with available relations, add/delete sub-forms

### Change Summaries Restored (12 now exist)
TASK-004, 006, 008, 009, 010, 016, 017, 018, 019, 020, 024, 025 — all 12 previously missing change summaries now present on disk.

### State
- **21 READY_FOR_TEST** — 18 with change summaries (TASK-011, 012, 013 still missing)
- **6 IN_DEVELOPMENT** — TASK-021, 022, 023, 026, 027, ENH-001
- **1 PLANNED** — TASK-014

## 2026-07-17 (PRD-005 Advanced to TESTING — All 14 Tasks Implemented)

### Summary
Software Engineer completed all 14 PRD-005 tasks for backend-frontend separation, dead code removal, and package standardization. PRD-005 advanced from IN_DEVELOPMENT to TESTING. All 36 backend tests pass, frontend typecheck passes.

### Tasks Implemented (14)
| Task | Title | Scope | Priority |
|------|-------|-------|----------|
| TASK-046 | Add childTabIds to TabDefinitionResponse | backend | High |
| TASK-047 | Add htmlType and lookupOptions to FieldDefinitionResponse | both | High |
| TASK-048 | Backend Pre-Filters and Pre-Sorts Fields | backend | Medium |
| TASK-049 | Backend Type Coercion on Save | backend | Medium |
| TASK-050 | Guarantee _display on Every Record | both | High |
| TASK-051 | Backend Returns RuntimeMetadataBundle Directly | both | High |
| TASK-052 | Backend Search Endpoint for Ctrl+K | both | Medium |
| TASK-053 | Backend Guarantees Non-Empty Sections | backend | Low |
| TASK-054 | Remove Dead modules/auth/ Package | backend | High |
| TASK-055 | Remove Dead core/security/ Package | backend | High |
| TASK-056 | Move customerService.ts Out of core/api/services/ | frontend | Low |
| TASK-057 | Audit and Remove Stale Frontend API Endpoints | frontend | Low |
| TASK-058 | Move Window Schema to core/layout/ | backend | High |
| TASK-059 | Move Frontend Pages to routes/ | frontend | Medium |

### Branch
- `prd/PRD-005-backend-frontend-separation` — 14 feature branches merged
- All 36 backend tests pass, frontend typecheck passes

### PRD Status Changed
- **PRD-005** — Backend-Frontend Separation & Code Standardization v1.3.0 → v1.3.1: **IN_DEVELOPMENT → TESTING**

## 2026-07-16 (PRD-005 APPROVED v1.3.0)

### Summary
PRD-005 created for backend-frontend separation. 14 tasks covering:
- Moving data logic from frontend to backend (8 tasks)
- Removing dead code (modules/auth/, core/security/) (2 tasks)
- Package standardization (move window schema to core/layout/, move pages to routes/) (2 tasks)
- Cleanup stale services and endpoints (2 tasks)

### Tasks
- TASK-046 through TASK-059 created (status: PLANNING)

### Status
PRD-005: APPROVED — 14 tasks awaiting development
