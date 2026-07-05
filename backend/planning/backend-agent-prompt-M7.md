# AI Code Agent Prompt — M7 Business Intelligence, Reporting & Analytics Platform

You are a principal ERP architect.

Your task is to build the Enterprise Analytics Platform for the metadata-driven ERP.

IMPORTANT:

This is NOT just a reporting module.

This phase builds the complete Business Intelligence layer that allows every module to expose analytics through metadata.

The platform must support:

- Dynamic Dashboards
- KPI Engine
- Report Engine
- Pivot Analysis
- Chart Engine
- Scheduled Reports
- Ad-hoc Queries
- Drill Down Analytics

Everything must be metadata-driven.

No report should require writing Java or React code.

---

# CONTEXT

Completed:

✓ Phase 0 – Architecture Freeze
✓ T1–T6 Frontend Runtime
✓ B1–B5 Backend Runtime
✓ M1 Foundation Modules
✓ M2 Sales & Inventory
✓ M3 Purchasing
✓ M4 Accounting
✓ M5 Manufacturing
✓ M6 Enterprise Modules

Current Goal:

Build Business Intelligence Platform.

---

# TARGET OUTCOME

After M7:

✓ Dashboard Engine operational
✓ KPI Engine operational
✓ Report Engine operational
✓ Pivot Engine operational
✓ Chart Engine operational
✓ Ad-hoc Query Builder operational
✓ Scheduled Reports operational
✓ Drill-down Analytics operational

---

# MODULES

```txt
analytics
reporting
dashboard
kpi
pivot
```

Backend:

```txt
com.erp.modules.analytics
```

Frontend:

```txt
src/modules/analytics
```

---

# M7.1 — Dashboard Engine

Purpose:

Provide configurable dashboards rendered entirely from metadata.

---

## Dashboard

Fields:

```txt
dashboardCode
name
description
layout
isDefault
roles
```

Relations:

```txt
one2many -> widgets
```

---

## Dashboard Widget

Support:

```txt
KPI Card
Chart
Grid
Pivot
Report
HTML Widget
Markdown Widget
```

---

Supported layouts:

```txt
Responsive Grid
Rows
Columns
Nested Layouts
```

---

# M7.2 — KPI Engine

Create:

```txt
KPIEngine
```

Responsibilities:

```txt
calculate KPI
cache KPI
refresh KPI
compare periods
```

Examples:

```txt
Sales Today
Revenue
Inventory Value
Production Efficiency
Open Tickets
Purchase Amount
```

---

# M7.3 — Report Engine

Create:

```txt
ReportEngine
```

Support:

```txt
Tabular reports
Grouped reports
Summary reports
Detail reports
Master-detail reports
```

---

Output formats:

```txt
PDF
Excel
CSV
JSON
```

---

# M7.4 — Pivot Engine

Support:

```txt
Rows
Columns
Measures
Filters
Calculated fields
```

Examples:

```txt
Sales by Customer
Sales by Product
Inventory by Warehouse
Revenue by Month
```

---

# M7.5 — Chart Engine

Support:

```txt
Bar
Line
Area
Pie
Donut
Scatter
Bubble
Radar
Heatmap
Treemap
```

Charts must be generated from metadata definitions.

---

# M7.6 — Ad-hoc Query Builder

Create:

```txt
QueryBuilderService
```

Support:

```txt
Select models
Choose fields
Apply filters
Grouping
Sorting
Aggregations
```

Output:

```txt
Grid
Chart
Pivot
Export
```

---

# M7.7 — Drill Down Engine

Support:

Example:

```txt
Revenue KPI
↓
Sales Orders
↓
Sales Order
↓
Customer
```

Every visualization should support configurable drill-down paths.

---

# M7.8 — Scheduled Reports

Create:

```txt
ReportScheduler
```

Support:

```txt
Daily
Weekly
Monthly
Custom Cron
```

Delivery:

```txt
Email
Notification
File Storage
```

---

# M7.9 — Analytics Metadata

Each analytics artifact must have metadata:

```txt
DashboardDefinition
WidgetDefinition
ReportDefinition
KPIDefinition
ChartDefinition
PivotDefinition
```

No hardcoded dashboards.

---

# M7.10 — Cross Module Analytics

Support analytics for:

```txt
Sales
Purchasing
Inventory
Manufacturing
Accounting
CRM
Projects
HR
Assets
Service
```

---

# M7.11 — Runtime Rendering Validation

The Runtime Renderer should dynamically render:

```txt
Dashboards
Charts
Reports
KPIs
Pivots
```

using metadata only.

---

# M7.12 — Permissions

Roles:

Business User

```txt
View Dashboards
Run Reports
```

Manager

```txt
Create Reports
Create Dashboards
```

Analyst

```txt
Build Pivot Reports
Create KPIs
```

Admin

```txt
Full Access
```

---

# M7.13 — Seed Data

Create sample:

Dashboards:

```txt
Executive Dashboard
Sales Dashboard
Inventory Dashboard
Finance Dashboard
Manufacturing Dashboard
```

Reports:

```txt
Sales Summary
Inventory Aging
Purchase Analysis
General Ledger
Production Efficiency
```

KPIs:

```txt
Revenue
Profit
Inventory Value
Open Orders
On-time Delivery
```

---

# M7.14 — Acceptance Tests

Dashboard

Expected:

```txt
Widgets rendered dynamically.
```

---

Report

Expected:

```txt
Report generated from metadata.
```

---

Pivot

Expected:

```txt
Interactive pivot works.
```

---

Charts

Expected:

```txt
Charts generated from metadata.
```

---

Drill Down

Expected:

```txt
Navigation reaches source records.
```

---

Scheduling

Expected:

```txt
Reports generated automatically.
```

---

Permissions

Expected:

```txt
Analytics security enforced.
```

---

# SUCCESS CRITERIA

After M7:

```txt
Metadata
      ↓
Analytics Engine
      ↓
Dashboards
Reports
KPIs
Charts
Pivots
```

The ERP now supports:

✓ Executive Dashboards
✓ Operational Dashboards
✓ Business Intelligence
✓ Analytics
✓ Reporting
✓ Scheduled Reports
✓ Self-Service Reporting

---

# FINAL DELIVERABLE

Produce:

✓ Dashboard Engine
✓ KPI Engine
✓ Report Engine
✓ Pivot Engine
✓ Chart Engine
✓ Query Builder
✓ Drill Down Engine
✓ Scheduled Reporting
✓ Analytics Metadata
✓ Runtime Dashboard Rendering
✓ Permissions
✓ Seed Data
✓ End-to-End Validation

This prepares the platform for:

# M8 — Collaboration, Notifications, Documents, Search & Platform Services