---
document: CHANGE_REPORT
task_id: TASK-081
version: 1.0.0
status: FINAL
author: Software Engineer
date: 2026-08-24
---

# Change Report — TASK-081

## Task

Create Commission table with metadata (PRD-008).

## Files Changed

- `backend/src/main/resources/db/migration/V29__create_commission_table.sql`
- `ai/project/schema/crm/tx_commission.sql`

## Summary

Created the `tx_commission` table to track salesperson commissions on closed opportunities. Captures salesperson, opportunity, linked order, commission percent, calculated amount, status (pending/approved/paid), and payment date. Registered in `sys_table`/`sys_column` with enum metadata.

## Schema

- `tx_commission` with FKs to `identity_users`, `tx_opportunity`, `tx_order`
