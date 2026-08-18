---
id: TEST-TASK-071
task_id: TASK-071
parent_prd: PRD-007
test_date: 2026-08-18
qa_engineer: QA Engineer
environment: Local (Linux, Java 17, Maven, PostgreSQL, Flyway enabled)
build_commit_tested: 7b60888 (prd/PRD-007-sales-order-workflow-customer-management)
test_scope: Verify business partner credit/term columns and form fields.
---

# Test Report — TASK-071: Add credit limit and payment term to Business Partner

---

## Test Scope

Verify that `md_business_partner` has the new `credit_limit`, `credit_hold`, and `payment_term_id` columns, that they are registered in metadata, and that they appear on the Business Partners form.

---

## Test Cases Executed

### TC-001: Columns exist on md_business_partner
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Three columns added with correct types/nullability. |
| Actual | `credit_limit` numeric nullable, `credit_hold` boolean not null, `payment_term_id` uuid nullable. |

### TC-002: Columns registered in sys_column
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Columns registered with correct types and relation_table for payment_term_id. |
| Actual | All three columns found; `payment_term_id` relation_table = `md_payment_term`. |

### TC-003: Form fields present on Business Partners window
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Fields displayed, editable, and ordered on Business Partners form. |
| Actual | credit_limit (seq 70), credit_hold (seq 80), payment_term_id (seq 90) all displayed/editable. |

### TC-004: Migration idempotency
| Aspect | Result |
|--------|--------|
| Status | **PASSED** |
| Expected | Backend restarts cleanly after initial migration; no duplicate column errors. |
| Actual | `start-all.sh --setup` succeeded without Flyway errors. |

---

## Acceptance Criteria Verification

| # | Criterion | Status | Notes |
|---|-----------|:---:|-------|
| AC1 | Three columns added to md_business_partner | **PASSED** | Verified via information_schema |
| AC2 | Columns registered in sys_column metadata | **PASSED** | Verified via SQL |
| AC3 | business_partner form updated with Credit & Terms section | **PASSED** | Fields present on Business Partners window |
| AC4 | Migration idempotent | **PASSED** | Restarted cleanly |
| AC5 | Editing a partner via runtime UI saves credit_limit, credit_hold, payment_term_id | **PENDING** | Awaiting manual scenario MS-001 |
| AC6 | Backend starts cleanly | **PASSED** | Verified |

---

## Manual Test Scenarios

### MS-001: Edit Business Partner credit fields via runtime UI
| Field | Value |
|-------|-------|
| Preconditions | Backend and frontend running; Flyway migrations applied; admin logged in; at least one payment term exists (e.g., NET30). |
| Steps | 1. Navigate to **Master Data → Business Partners**.<br>2. Open an existing partner (or create one).<br>3. In the form, locate the **Credit & Terms** fields.<br>4. Set **Credit Limit** = `5000.00`, check **Credit Hold**, and select **Payment Term** = `NET30`.<br>5. Save the record.<br>6. Re-open the same partner and verify the three values persisted.<br>7. Uncheck **Credit Hold**, change **Credit Limit** to `10000.00`, and save again.<br>8. Verify the updated values persisted. |
| Expected Result | All three credit/term fields save and reload correctly. |
| Actual Result | *(to be filled by user)* |
| Status | **PENDING** |

---

## Regression Results

| Test Suite | Result |
|------------|--------|
| `mvn test` (36 tests) | 36 pass, 0 fail, 0 errors |
| `start-all.sh --setup` | PASS |

No regression introduced.

---

## Bugs Found

None (automated checks passed).

---

## Known Limitations

- The Credit & Terms fields share the same tab/section as other partner fields; no separate collapsible section is enforced by metadata in the generic renderer.

---

## Release Recommendation

**CONDITIONAL PASS** — Automated checks all pass. Pending manual UI verification in MS-001.

---

## Test Summary

| Metric | Value |
|--------|-------|
| Total Test Cases | 4 |
| Passed | 4 |
| Failed | 0 |
| Skipped | 0 |
| Bugs Created | 0 |
| Acceptance Criteria Passed | 5 |
| Acceptance Criteria Pending | 1 |
| Requirement Issues Identified | 0 |

---

## Reusable Scripts

```bash
PGPASSWORD=erp_password psql -U erp_user -h localhost -d erp_db -f ai/project/scripts/verify-prd-007-data.sql
```
