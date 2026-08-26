# Project Board

Last Updated: 2026-08-24

Updated By: Software Engineer

(PRD-007 COMPLETED and merged to main. PRD-008 IN_DEVELOPMENT.)

---

## Workflow Legend

```
PRD:  APPROVED → IN_DEVELOPMENT → TESTING → READY_FOR_DEPLOYMENT → COMPLETED
          ↓ (SE)        ↓ (SE)        ↓ (QA)        ↓ (PM+user)     ↓ (Release)
Task: PLANNED → READY_FOR_DEV → IN_DEVELOPMENT → READY_FOR_TEST → TESTING → TESTED → COMPLETED
          ↓ (PM)     ↓ (SE)           ↓ (SE)          ↓ (SE)        ↓ (QA)    ↓ (QA)   ↓ (cascade)
Bug:                                READY_FOR_DEV → IN_DEVELOPMENT → READY_FOR_TEST → TESTING → RESOLVED
```

See `ai/agent/rules/status-transitions.md` for full transition rules.

---

## Ready For Development

| Task | Title | PRD | Priority | Depends On |
|------|-------|-----|----------|------------|
| TASK-093 | Extend c_business_partner with vendor fields | PRD-010 | High | — |
| TASK-105 | Extend warehouse and create location type/locator tables | PRD-013 | High | — |
| TASK-116 | Create work center and operation tables | PRD-016 | High | — |
| TASK-125 | Create account type and account tables | PRD-018 | High | — |
| TASK-142 | Create project type and project tables | PRD-022 | Medium | — |
| TASK-151 | Create department and job position tables | PRD-024 | High | — |

---

## Ready For Test

| Task | Title | PRD | Priority |
|------|-------|-----|----------|
| (none) | | | |

---

## Ready For Development

| Task | Title | PRD | Priority | Depends On |
|------|-------|-----|----------|------------|
| (none) | | | |

---

## In Development

| Task | Title | PRD | Priority | Depends On |
|------|-------|-----|----------|------------|
| (none) | | | |

---

## Planned (awaiting dependencies)

| Task | Title | PRD | Priority | Depends On |
|------|-------|-----|----------|------------|
| TASK-079 | Create Lead table with metadata | PRD-008 | High | TASK-078 |
| TASK-080 | Create Opportunity table with metadata | PRD-008 | High | TASK-078 |
| TASK-082 | Implement Lead scoring and qualification workflow | PRD-008 | High | TASK-079 |
| TASK-083 | Implement Lead to Opportunity conversion | PRD-008 | High | TASK-079/080/082 |
| TASK-084 | Implement Opportunity pipeline workflow, quotation conversion, and commission | PRD-008 | High | TASK-080, TASK-081 |
| TASK-085 | Seed CRM forms (lead, opportunity, territory, commission) | PRD-008 | High | TASK-078/079/080/081 |
| TASK-086 | Implement CRM Dashboard API and deprecate hardcoded crm/ module | PRD-008 | Medium | TASK-085 |
| TASK-087 | Create Sales Return tables with metadata | PRD-009 | High | PRD-007 |
| TASK-088 | Create Credit Memo table with metadata | PRD-009 | High | PRD-007 |
| TASK-089 | Implement Return workflow and Credit Memo generation | PRD-009 | High | TASK-087, TASK-088 |
| TASK-090 | Implement Return inventory receipt integration | PRD-009 | Medium | TASK-089 |
| TASK-091 | Seed Return and Credit Memo forms | PRD-009 | High | TASK-087, TASK-088 |
| TASK-092 | Implement Sales Analytics dashboard | PRD-009 | Medium | TASK-091 |
| TASK-094 | Create vendor category and purchase price list tables | PRD-010 | High | TASK-093 |
| TASK-095 | Seed vendor and purchase price list windows/menus | PRD-010 | High | TASK-093/094 |
| TASK-096 | Implement purchase price resolution service | PRD-010 | High | TASK-094/095 |
| TASK-097 | Create purchase requisition and purchase order tables | PRD-011 | High | TASK-096 |
| TASK-098 | Seed requisition and PO windows/menus | PRD-011 | High | TASK-097 |
| TASK-099 | Implement PO auto-calculation service | PRD-011 | High | TASK-097/098 |
| TASK-100 | Implement PO workflow and requisition conversion | PRD-011 | High | TASK-097/098/099 |
| TASK-101 | Create goods receipt and supplier invoice tables | PRD-012 | High | TASK-100 |
| TASK-102 | Seed receipt and supplier invoice windows/menus | PRD-012 | High | TASK-101 |
| TASK-103 | Implement receipt completion with inventory update | PRD-012 | High | TASK-101/102 |
| TASK-104 | Implement supplier invoice validation and matching | PRD-012 | High | TASK-101/102/103 |
| TASK-106 | Seed warehouse and location windows/menus | PRD-013 | High | TASK-105 |
| TASK-107 | Implement default location rules and lookup service | PRD-013 | High | TASK-105/106 |
| TASK-108 | Create stock move, balance, and reservation tables | PRD-014 | High | TASK-105 |
| TASK-109 | Seed stock move and internal transfer windows/menus | PRD-014 | High | TASK-108 |
| TASK-110 | Implement stock move completion and balance update | PRD-014 | High | TASK-108/109 |
| TASK-111 | Implement inventory reservation hook | PRD-014 | Medium | TASK-108/110 |
| TASK-112 | Create physical inventory and adjustment tables | PRD-015 | High | TASK-108 |
| TASK-113 | Seed physical inventory and adjustment windows/menus | PRD-015 | High | TASK-112 |
| TASK-114 | Implement count completion and adjustment service | PRD-015 | High | TASK-112/113/110 |
| TASK-115 | Implement average cost and inventory valuation report | PRD-015 | High | TASK-112/114 |
| TASK-117 | Create routing and BOM tables | PRD-016 | High | TASK-116 |
| TASK-118 | Seed work center/operation/routing/BOM windows | PRD-016 | High | TASK-116/117 |
| TASK-119 | Implement BOM explosion service | PRD-016 | High | TASK-116/117/118 |
| TASK-120 | Create manufacturing order tables | PRD-017 | High | TASK-119 |
| TASK-121 | Seed manufacturing order window/menu | PRD-017 | High | TASK-120 |
| TASK-122 | Implement MO confirmation and component reservation | PRD-017 | High | TASK-120/121/111 |
| TASK-123 | Implement material consumption and finished goods production | PRD-017 | High | TASK-120/121/122/110/115 |
| TASK-124 | Implement basic MRP report | PRD-017 | Medium | TASK-120/123 |
| TASK-126 | Create tax and fiscal calendar tables | PRD-018 | High | TASK-125 |
| TASK-127 | Seed COA, tax, and fiscal calendar windows/menus | PRD-018 | High | TASK-125/126 |
| TASK-128 | Seed standard chart of accounts and sample tax rates | PRD-018 | High | TASK-125/126/127 |
| TASK-129 | Create journal entry and ledger posting tables | PRD-019 | High | TASK-128 |
| TASK-130 | Seed journal entry window/menu | PRD-019 | High | TASK-129 |
| TASK-131 | Implement posting engine with balance/period validation | PRD-019 | High | TASK-129/130/128 |
| TASK-132 | Implement trial balance report and reversal | PRD-019 | High | TASK-129/131 |
| TASK-133 | Extend customer and supplier invoices for GL posting | PRD-020 | High | TASK-131 |
| TASK-134 | Implement customer invoice posting service | PRD-020 | High | TASK-133/131 |
| TASK-135 | Implement supplier invoice and credit memo posting | PRD-020 | High | TASK-133/131/104 |
| TASK-136 | Implement AR/AP aging reports and invoice allocation | PRD-020 | High | TASK-133/134/135/132 |
| TASK-137 | Create bank account and cash journal tables | PRD-021 | High | TASK-128 |
| TASK-138 | Create receipt, payment, and bank statement tables | PRD-021 | High | TASK-137 |
| TASK-139 | Seed bank/cash/payment/statement windows/menus | PRD-021 | High | TASK-137/138 |
| TASK-140 | Implement receipt/payment completion and GL posting | PRD-021 | High | TASK-138/139/136 |
| TASK-141 | Implement bank reconciliation and cash position report | PRD-021 | High | TASK-138/139/140 |
| TASK-143 | Create task, milestone, and assignment tables | PRD-022 | Medium | TASK-142 |
| TASK-144 | Seed project/task/milestone/assignment windows | PRD-022 | Medium | TASK-142/143 |
| TASK-145 | Implement project dashboard API | PRD-022 | Medium | TASK-142/143/144 |
| TASK-146 | Create timesheet and billing rate tables | PRD-023 | Medium | TASK-145 |
| TASK-147 | Seed timesheet and billing rate windows/menus | PRD-023 | Medium | TASK-146 |
| TASK-148 | Implement timesheet approval workflow | PRD-023 | Medium | TASK-146/147 |
| TASK-149 | Implement project billing to AR invoice | PRD-023 | Medium | TASK-146/147/148/134 |
| TASK-150 | Implement project profitability report | PRD-023 | Medium | TASK-146/149 |
| TASK-152 | Create employee and contract tables | PRD-024 | High | TASK-151 |
| TASK-153 | Seed employee and contract windows/menus | PRD-024 | High | TASK-151/152 |
| TASK-154 | Implement employee directory API | PRD-024 | Low | TASK-151/152/153 |
| TASK-155 | Create leave type, balance, and request tables | PRD-025 | High | TASK-152 |
| TASK-156 | Create attendance table | PRD-025 | High | TASK-152 |
| TASK-157 | Create expense category and claim tables | PRD-025 | High | TASK-152 |
| TASK-158 | Seed leave/attendance/expense windows/menus | PRD-025 | High | TASK-155/156/157 |
| TASK-159 | Implement leave/expense approval and attendance summary | PRD-025 | High | TASK-155/156/157/158 |
| TASK-160 | Create salary rule category and rule tables | PRD-026 | High | TASK-128 |
| TASK-161 | Create salary structure and payroll run tables | PRD-026 | High | TASK-160 |
| TASK-162 | Create payslip tables | PRD-026 | High | TASK-161 |
| TASK-163 | Seed payroll windows/menus and default salary rules | PRD-026 | High | TASK-160/161/162 |
| TASK-164 | Implement payslip generation and approval | PRD-026 | High | TASK-160/161/162/163/159 |
| TASK-165 | Implement payroll payment, GL posting, and reports | PRD-026 | High | TASK-160/161/162/163/164/140 |

---

## PRD Status

| PRD | Title | Version | Status | Priority |
|-----|-------|---------|--------|----------|
| PRD-001 | Dynamic Form Configuration System | 1.6.0 | **COMPLETED** | High |
| PRD-002 | Admin Configuration Forms | 1.1.0 | **COMPLETED** | High |
| PRD-003 | ERP Order Flow — Transaction Forms | 1.0.0 | **COMPLETED** | High |
| PRD-004 | Window Hierarchy & Menu System | 1.1.0 | **COMPLETED** | High |
| PRD-005 | Backend-Frontend Separation & Code Standardization | 1.3.1 | **COMPLETED** | High |
| PRD-006 | Sales Quotation & Price Management | 1.0.0 | **COMPLETED** | High |
| PRD-007 | Sales Order Workflow & Customer Management | 1.0.0 | **COMPLETED** | High |
| PRD-008 | CRM Pipeline & Sales Team Management | 1.0.0 | **COMPLETED** | Medium |
| PRD-009 | Sales Return & Analytics | 1.0.0 | **APPROVED** | Medium |
| PRD-010 | Vendor Management & Purchase Price Lists | 1.0.0 | **APPROVED** | High |
| PRD-011 | Purchase Requisition & Purchase Order | 1.0.0 | **APPROVED** | High |
| PRD-012 | Goods Receipt & Supplier Invoice Matching | 1.0.0 | **APPROVED** | High |
| PRD-013 | Warehouse Structure & Location Management | 1.0.0 | **APPROVED** | High |
| PRD-014 | Stock Movements & Logistics | 1.0.0 | **APPROVED** | High |
| PRD-015 | Inventory Adjustments, Counting & Valuation | 1.0.0 | **APPROVED** | High |
| PRD-016 | Product BOM, Work Centers & Routings | 1.0.0 | **APPROVED** | High |
| PRD-017 | Manufacturing Orders & Basic MRP | 1.0.0 | **APPROVED** | High |
| PRD-018 | Chart of Accounts, Tax & Fiscal Calendar | 1.0.0 | **APPROVED** | High |
| PRD-019 | General Ledger & Journal Posting Engine | 1.0.0 | **APPROVED** | High |
| PRD-020 | Accounts Payable & Receivable | 1.0.0 | **APPROVED** | High |
| PRD-021 | Bank, Cash & Payments Reconciliation | 1.0.0 | **APPROVED** | High |
| PRD-022 | Projects, Tasks & Resource Planning | 1.0.0 | **APPROVED** | Medium |
| PRD-023 | Timesheets & Project Billing | 1.0.0 | **APPROVED** | Medium |
| PRD-024 | Employees, Contracts & Organization | 1.0.0 | **APPROVED** | High |
| PRD-025 | Attendance, Leave & Expenses | 1.0.0 | **APPROVED** | High |
| PRD-026 | Payroll Rules, Payslips & Compliance | 1.0.0 | **APPROVED** | High |

---


















---

## Ready For Test

*(none)*

---

## In Development

*(none)*

---

## Testing

*(none)*

---

## Completed

| Task | Completed | Version |
|------|-----------|---------|
| TASK-001 | 2026-07-09 | 1.6.0 |
| TASK-002 | 2026-07-09 | 1.6.0 |
| TASK-003 | 2026-07-09 | 1.6.0 |
| TASK-004 | 2026-07-09 | 1.6.0 |
| TASK-005 | 2026-07-09 | 1.6.0 |
| TASK-006 | 2026-07-09 | 1.6.0 |
| TASK-007 | 2026-07-09 | 1.6.0 |
| TASK-008 | 2026-07-09 | 1.6.0 |
| TASK-009 | 2026-07-09 | 1.6.0 |
| TASK-010 | 2026-07-09 | 1.6.0 |
| TASK-011 | 2026-07-09 | 1.6.0 |
| TASK-012 | 2026-07-09 | 1.6.0 |
| TASK-013 | 2026-07-09 | 1.6.0 |
| TASK-014 | 2026-07-09 | 1.6.0 |
| TASK-015 | 2026-07-09 | 1.6.0 |
| TASK-016 | 2026-07-09 | 1.6.0 |
| TASK-017 | 2026-07-09 | 1.6.0 |
| TASK-018 | 2026-07-09 | 1.6.0 |
| TASK-019 | 2026-07-09 | 1.6.0 |
| TASK-020 | 2026-07-09 | 1.6.0 |
| TASK-021 | 2026-07-09 | 1.6.0 |
| TASK-022 | 2026-07-09 | 1.6.0 |
| TASK-023 | 2026-07-09 | 1.6.0 |
| TASK-024 | 2026-07-09 | 1.6.0 |
| TASK-025 | 2026-07-09 | 1.6.0 |
| TASK-026 | 2026-07-09 | 1.6.0 |
| TASK-027 | 2026-07-09 | 1.6.0 |
| TASK-028 | 2026-07-13 | 1.0.0 |
| TASK-029 | 2026-07-13 | 1.0.0 |
| TASK-030 | 2026-07-13 | 1.0.0 |
| TASK-031 | 2026-07-13 | 1.0.0 |
| TASK-032 | 2026-07-13 | 1.0.0 |
| TASK-033 | 2026-07-10 | 1.1.0 |
| TASK-034 | 2026-07-10 | 1.1.0 |
| TASK-035 | 2026-07-10 | 1.1.0 |
| ENH-001 | 2026-07-09 | 1.6.0 |
| ENH-002 | 2026-07-10 | 1.1.0 |
| ENH-003 | 2026-07-14 | 1.0.0 |
| TASK-036 | 2026-07-14 | 1.0.0 |
| TASK-037 | 2026-07-14 | 1.0.0 |
| TASK-038 | 2026-07-14 | 1.0.0 |
| TASK-039 | 2026-07-14 | 1.0.0 |
| TASK-040 | 2026-07-14 | 1.0.0 |
| TASK-041 | 2026-07-14 | 1.0.0 |
| TASK-042 | 2026-07-14 | 1.0.0 |
| TASK-043 | 2026-07-14 | 1.0.0 |
| TASK-044 | 2026-07-14 | 1.0.0 |
| TASK-045 | 2026-07-14 | 1.0.0 |
| TASK-046 | 2026-07-17 | 1.3.1 |
| TASK-047 | 2026-07-17 | 1.3.1 |
| TASK-048 | 2026-07-17 | 1.3.1 |
| TASK-049 | 2026-07-17 | 1.3.1 |
| TASK-050 | 2026-07-17 | 1.3.1 |
| TASK-051 | 2026-07-17 | 1.3.1 |
| TASK-052 | 2026-07-17 | 1.3.1 |
| TASK-053 | 2026-07-17 | 1.3.1 |
| TASK-054 | 2026-07-17 | 1.3.1 |
| TASK-055 | 2026-07-17 | 1.3.1 |
| TASK-056 | 2026-07-17 | 1.3.1 |
| TASK-057 | 2026-07-17 | 1.3.1 |
| TASK-058 | 2026-07-17 | 1.3.1 |
| TASK-059 | 2026-07-17 | 1.3.1 |
| TASK-060 | 2026-08-12 | 1.0.0 |
| TASK-061 | 2026-08-12 | 1.0.0 |
| TASK-062 | 2026-08-12 | 1.0.0 |
| TASK-063 | 2026-08-12 | 1.0.0 |
| TASK-064 | 2026-08-12 | 1.0.0 |
| TASK-065 | 2026-08-12 | 1.0.0 |
| TASK-066 | 2026-08-12 | 1.0.0 |
| TASK-067 | 2026-08-12 | 1.0.0 |
| TASK-068 | 2026-08-12 | 1.0.0 |
| TASK-069 | 2026-08-12 | 1.0.0 |
| TASK-070 | 2026-08-24 | 1.0.0 |
| TASK-071 | 2026-08-24 | 1.0.0 |
| TASK-072 | 2026-08-24 | 1.0.0 |
| TASK-073 | 2026-08-24 | 1.0.0 |
| TASK-074 | 2026-08-24 | 1.0.0 |
| TASK-075 | 2026-08-24 | 1.0.0 |
| TASK-076 | 2026-08-24 | 1.0.0 |
| TASK-077 | 2026-08-24 | 1.0.0 |
| TASK-078 | 2026-08-24 | 1.0.0 |
| TASK-079 | 2026-08-24 | 1.0.0 |
| TASK-080 | 2026-08-24 | 1.0.0 |
| TASK-081 | 2026-08-24 | 1.0.0 |
| TASK-082 | 2026-08-24 | 1.0.0 |
| TASK-083 | 2026-08-24 | 1.0.0 |
| TASK-084 | 2026-08-24 | 1.0.0 |
| TASK-085 | 2026-08-24 | 1.0.0 |
| TASK-086 | 2026-08-24 | 1.0.0 |

---



## Bugs

| Bug | Parent Task | Severity | Status | Owner | Depends On |
|-----|-------------|----------|--------|-------|------------|
| BUG-001 | TASK-001 | Medium | COMPLETED | QA Engineer | — |
| BUG-002 | TASK-007 | High | COMPLETED | QA Engineer | — |
| BUG-003 | TASK-011 | High | COMPLETED | QA Engineer | — |
| BUG-004 | TASK-070 | Medium | READY_FOR_DEV | QA Engineer | — |
| BUG-005 | TASK-025 | Medium | CANCELLED | QA Engineer | — |
| BUG-006 | TASK-007 | Critical | COMPLETED | QA Engineer | — |
| BUG-007 | TASK-036 | Critical | COMPLETED | QA Engineer | — |
| BUG-008 | TASK-041 | Medium | COMPLETED | QA Engineer | BUG-007 |
| BUG-009 | TASK-036 | Critical | COMPLETED | QA Engineer | — |
| BUG-010 | TASK-039 | Critical | **RESOLVED** | QA Engineer | — |
| BUG-011 | TASK-041 | Critical | **RESOLVED** | QA Engineer | — |
| BUG-012 | TASK-047 | Critical | **RESOLVED** | QA Engineer | — |
| BUG-013 | TASK-046 | Critical | **RESOLVED** | QA Engineer | — |
| BUG-014 | (seed) | Medium | **RESOLVED** | Software Engineer | — |
| BUG-015 | TASK-046 | Medium | **RESOLVED** | Software Engineer | — |

---

## Enhancements

| Task | Parent PRD | Parent Task | Reason | Status |
|------|------------|-------------|--------|--------|
| ENH-001 | PRD-001 | TASK-007 | Form Designer API tenant authorization | COMPLETED |
| ENH-002 | PRD-002 | TASK-034, TASK-035 | Add tenant_id to all admin forms (REQ-ISSUE-001) | COMPLETED |
| ENH-003 | PRD-001 | TASK-026 | RuntimePage not integrated with API — uses hardcoded sample bundles instead of dynamic form rendering | COMPLETED |
| ENH-004 | PRD-005 | BUG-013 (QA) | Window definition cache auto-invalidation after DB reseeds (stale ghost-UUID errors) | COMPLETED |

---

## Blocked

*(none)*

---

## Statistics

| Status | Count |
|--------|-------|
| PRDs | 26 (8 COMPLETED, 0 TESTING, 18 APPROVED, 0 DRAFT) |
| Ready For Dev | 6 |
| Ready For Test | 0 |
| In Testing | 0 |
| In Development | 0 |
| Planned | 80 (TASK-079..086, TASK-087..092, TASK-094..165) |
| Bugs | 16 (8 COMPLETED, 1 CANCELLED, 6 RESOLVED, 1 READY_FOR_DEV) |
| Completed (PRD-001) | 27 tasks + 2 enhancements (ENH-001, ENH-003) |
| Completed (PRD-002) | 3 tasks + 1 enhancement (TASK-033/034/035 + ENH-002) |
| Completed (PRD-003) | 5 tasks (TASK-028/029/030/031/032) — merged to main |
| Completed (PRD-004) | 10 tasks (TASK-036..045) — merged to main |
| Completed (PRD-005) | 14 tasks (TASK-046..059) — BUG-013 opened, PRD-005 REOPENED |
| Completed (PRD-006) | 10 tasks (TASK-060..069) — all TESTED, 6 manual scenarios pending |
| Completed (PRD-007) | 8 tasks (TASK-070..077) — merged to main; BUG-004 unresolved |
| Completed (PRD-008) | 9 tasks (TASK-078..086) — merged to main; manual UI scenarios pending |
| Total Tasks | 71 completed + 6 ready + 80 planned = 157 total |
