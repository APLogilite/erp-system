# AI Code Agent Prompt — M8 Collaboration, Notifications, Documents, Search & Platform Services

You are a principal ERP architect.

Your task is to build the Platform Services layer that transforms the ERP from a collection of business modules into a collaborative enterprise platform.

IMPORTANT:

These services must be generic and reusable by every current and future module.

Do NOT build module-specific implementations.

Everything must integrate with:

- Metadata Engine
- Runtime Renderer
- Workflow Engine
- Permission Engine
- Event Bus
- Notification Engine
- Plugin System

All features must be metadata-driven whenever possible.

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
✓ M7 Analytics Platform

Current Goal:

Build Collaboration & Platform Services.

---

# TARGET OUTCOME

After M8:

✓ Notification Engine operational
✓ Email Engine operational
✓ In-App Notification Center operational
✓ Document Management operational
✓ Attachment Engine operational
✓ Activity Timeline operational
✓ Comment System operational
✓ Global Search operational
✓ Audit Viewer operational
✓ Platform Event Center operational

---

# MODULES

```txt
notifications
documents
search
audit
activities
comments
platform
```

Backend:

```txt
com.erp.modules.platform
```

Frontend:

```txt
src/modules/platform
```

---

# M8.1 — Notification Engine

Purpose:

Provide a unified notification framework for all ERP modules.

---

## Notification

Fields:

```txt
notificationId
title
message
type
priority
recipient
module
recordId
status
createdAt
readAt
```

Types:

```txt
INFO
SUCCESS
WARNING
ERROR
ACTION_REQUIRED
```

Delivery Channels:

```txt
In-App
Email
SMS (future)
Push (future)
Webhook (future)
```

---

# M8.2 — Notification Service

Create:

```txt
NotificationService
```

Responsibilities:

```txt
send()
broadcast()
markAsRead()
dismiss()
subscribe()
```

Integrate with:

```txt
Workflow Events
Inventory Events
Accounting Events
Plugin Events
```

---

# M8.3 — Email Engine

Purpose:

Generic outbound email service.

Support:

```txt
Templates
Attachments
HTML
Plain Text
Variables
Localization
```

Create:

```txt
EmailService
EmailTemplateService
```

Future support:

```txt
SMTP
Microsoft 365
Google Workspace
SES
```

---

# M8.4 — Document Management

Purpose:

Manage all ERP documents.

Support:

```txt
Upload
Versioning
Categories
Folders
Preview
Metadata
```

Document Fields:

```txt
fileName
mimeType
size
owner
module
recordId
version
checksum
```

---

# M8.5 — Attachment Engine

Allow attachments on every record.

Examples:

```txt
Sales Order
Invoice
Employee
Project
Asset
Manufacturing Order
```

Relations:

```txt
many2one -> any runtime model
```

Support:

```txt
Multiple attachments
Version history
Permissions
```

---

# M8.6 — Activity Timeline

Purpose:

Unified timeline across all modules.

Events:

```txt
Created
Updated
Workflow Transition
Comment Added
Attachment Uploaded
Notification Sent
Email Delivered
```

Support:

```txt
Chronological timeline
Filtering
Module-specific views
```

---

# M8.7 — Comment System

Purpose:

Enable collaboration.

Features:

```txt
Comments
Replies
Mentions
Reactions (future)
Attachments
```

Mention syntax:

```txt
@username
```

Notify mentioned users automatically.

---

# M8.8 — Global Search Engine

Create:

```txt
GlobalSearchService
```

Support:

```txt
Products
Customers
Orders
Invoices
Projects
Employees
Assets
Reports
Documents
```

Features:

```txt
Full-text search
Metadata search
Autocomplete
Recent searches
Saved searches
```

Future:

```txt
Elasticsearch/OpenSearch
```

---

# M8.9 — Audit Viewer

Purpose:

Visualize audit history.

Display:

```txt
Created
Updated
Field Changes
Workflow Changes
Permissions
Actions
```

Support:

```txt
Timeline
Diff View
Filters
Export
```

---

# M8.10 — Platform Event Center

Purpose:

Central event registry.

Events:

```txt
Workflow
Inventory
Accounting
Manufacturing
CRM
Projects
HR
Assets
Plugins
```

Support:

```txt
Publish
Subscribe
Replay (future)
Monitoring
```

---

# M8.11 — Runtime Integration

All platform services must integrate with:

```txt
Metadata Engine
Runtime Renderer
Workflow Engine
Permission Engine
Relation Engine
Plugin Engine
```

---

# M8.12 — Runtime Rendering Validation

Render entirely from metadata:

```txt
Notification Center
Document Browser
Activity Timeline
Comment Panel
Audit Viewer
Global Search Results
```

---

# M8.13 — Permissions

Roles:

User

```txt
View Notifications
Upload Attachments
Comment
Search
```

Manager

```txt
View Audit
Manage Documents
```

Administrator

```txt
Manage Templates
Manage Notifications
Manage Platform Settings
```

---

# M8.14 — Seed Data

Create:

Notifications

```txt
Workflow Approval
Purchase Order Created
Production Completed
```

Documents

```txt
Sample Attachments
Product Images
Employee Documents
```

Comments

```txt
Sales discussions
Project updates
```

Activity Timeline

```txt
Sample workflow history
```

---

# M8.15 — Acceptance Tests

Notifications

Expected:

```txt
Real-time notifications delivered.
```

---

Email

Expected:

```txt
Template email generated.
```

---

Documents

Expected:

```txt
Upload and versioning work.
```

---

Attachments

Expected:

```txt
Files linked to any ERP record.
```

---

Comments

Expected:

```txt
Mentions trigger notifications.
```

---

Search

Expected:

```txt
Global search returns cross-module results.
```

---

Audit

Expected:

```txt
Field changes displayed correctly.
```

---

Activity Timeline

Expected:

```txt
Events displayed chronologically.
```

---

Permissions

Expected:

```txt
Platform security enforced.
```

---

# SUCCESS CRITERIA

After M8:

```txt
Metadata
      ↓
Platform Services
      ↓
Notifications
Documents
Search
Audit
Collaboration
```

The ERP now supports:

✓ Enterprise Collaboration
✓ Notification Center
✓ Email Templates
✓ Document Management
✓ Attachments
✓ Activity Timeline
✓ Global Search
✓ Audit Viewer
✓ Platform Event Center

---

# FINAL DELIVERABLE

Produce:

✓ Notification Engine
✓ Email Engine
✓ Document Management
✓ Attachment Engine
✓ Activity Timeline
✓ Comment System
✓ Global Search
✓ Audit Viewer
✓ Platform Event Center
✓ Metadata Definitions
✓ Runtime UI
✓ Permissions
✓ Seed Data
✓ End-to-End Validation

This prepares the platform for:

# M9 — Integration Platform, API Gateway, Import/Export & External Connectivity