---
id: TEST-TASK-032
task_id: TASK-032
parent_prd: PRD-003
test_date: 2026-07-13
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, H2 test DB)
build_commit_tested: df1f900 (prd/PRD-003-erp-order-flow-forms)
test_scope: Structural verification of V23__seed_line_forms_and_sub_forms.sql — 4 line forms + 7 sub-form configs
status: PASSED
---

# Test Report — TASK-032: Seed Line Forms and Sub-Form Configurations

---

## Test Scope

Structural verification of `V23__seed_line_forms_and_sub_forms.sql`. Validates 4 line-item form definitions, 35 fields, single-section layout, section-field mappings, and 7 sub-form configs linking header forms to line forms.

---

## Test Cases Executed

### TC-001: Migration File Existence
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | `V23__seed_line_forms_and_sub_forms.sql` exists |
| Actual | File exists at correct path |

### TC-002: Line Form Definitions
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | 4 line forms: order_line, invoice_line, shipment_line, mr_line |
| Actual | All 4 present with correct model_name references |

### TC-003: Form Fields (35)
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | order_line(9), invoice_line(10), shipment_line(7), mr_line(9) = 35 |
| Actual | 35 INSERT INTO sys_form_fields statements |

### TC-004: Layout Sections
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Single 'items' section per form with 2 columns |
| Actual | 1 INSERT with IN clause covering all 4 forms |

### TC-005: Section-Field Mappings
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | All 35 fields mapped to their 'items' section |
| Actual | 4 INSERT statements using position = f.position pattern |

### TC-006: Sub-Form Configurations
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | 7 sub-form links |
| Actual | 7 INSERT INTO sys_form_sub_forms statements |

### TC-007: Sub-Form relation_code Correctness
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | purchase/sales_order → order_id, purchase/sales_invoice → invoice_id, purchase/sales_shipment → shipment_id, material_receipt → receipt_id |
| Actual | All relation_code values match child table FK columns |

### TC-008: Shared order_line Form
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Both purchase_order and sales_order use order_line child form |
| Actual | Two sub-form config rows reference same child_form_code='order_line' |

### TC-009: Parent FK in Line Forms
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Parent FK fields included (order_id, invoice_id, etc.) with placeholder 'Auto-set from parent' |
| Actual | All parent FK fields present with correct placeholder text |

### TC-010: Backend Compilation
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | `mvn clean compile` succeeds |
| Actual | Compiled without errors |

---

## Acceptance Criteria Verification

| # | Criterion | Status | Notes |
|---|-----------|:---:|-------|
| AC1 | Migration file exists at V23 | **PASSED** | V23__seed_line_forms_and_sub_forms.sql |
| AC2 | Clean existing before insert | **PASSED** | Sub-forms cleaned first, then line forms |
| AC3 | 4 rows in sys_metadata_views (line forms) | **PASSED** | order_line, invoice_line, shipment_line, mr_line |
| AC4 | ~35 field rows | **PASSED** | 9+10+7+9 = 35 |
| AC5 | 4 layout sections | **PASSED** | Single 'items' per form |
| AC6 | 7 sub-form config rows | **PASSED** | All header-to-line links |
| AC7 | relation_code matches child FK | **PASSED** | order_id, invoice_id, shipment_id, receipt_id |
| AC8 | child_form_code matches view name | **PASSED** | order_line, invoice_line, shipment_line, mr_line |
| AC9 | Shared order_line for both purchase/sales | **PASSED** | Two sub-form configs reference same child |

---

## Bugs Found

None

---

## Reusable Test Scripts

- `ai/scripts/verify-prd-003-data.sql` — Part E covers V23 line form and sub-form config verification
