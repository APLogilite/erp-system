# Schema Reference

This directory contains the canonical DDL for every table in the ERP system.

## Rules

1. **SE must update this file** whenever a migration creates or alters a table.
2. **Verification scripts** should reference these files instead of hardcoding expected columns.
3. **File naming**: match the physical table name exactly (e.g., `sys_table.sql` for table `sys_table`).

## Current schema version

Last updated: 2026-07-21
Current migration head: V7

## Table index

### Identity (V1-V2, V30-V31)
- identity_tenants, identity_organizations, identity_companies, identity_branches, identity_departments, identity_roles, identity_users, identity_permissions, identity_user_roles, identity_role_permissions, identity_user_organizations, identity_user_companies, identity_user_sessions, identity_user_preferences, identity_audit_records

### Metadata (V24-V29)
- sys_table, sys_column, sys_window, sys_tab, sys_window_field, sys_window_access, sys_menu

### Master Data (V19)
- md_business_partner, md_product, md_uom, md_uom_conversion, md_warehouse

### Transactions (V20)
- tx_order, tx_order_line, tx_invoice, tx_invoice_line, tx_payment, tx_shipment, tx_shipment_line, tx_material_receipt, tx_mr_line
