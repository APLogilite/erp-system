---
name: project-monitor
description: >-
  Use this skill when the user wants to start, view, or check the status of the
  real-time Project Monitor Dashboard (Kanban board, PRD progress, task status,
  and agent metrics) on port 3000.
---

# Project Monitor Dashboard

The Project Monitor is a real-time web dashboard that visualizes project tasks, PRD progress bars, Kanban status swimlanes, and agent metrics by monitoring markdown files under `ai/`.

---

## 1. Quick Start

To launch the dashboard server:

```bash
cd ai/agent/monitor
python3 server.py
```

The dashboard will be available at:
👉 **http://localhost:3000**

---

## 2. Features

- **Overview:** Total tasks, completion %, PRD progress bars, board statistics.
- **PRDs:** Per-PRD task breakdowns with completion tracking.
- **Kanban:** Tasks organized by status (Ready for Dev → In Development → Ready for Test → Testing → Completed).
- **Agents:** Workload distribution and task assignment.
- **Board:** Live view of `ai/agent/project-board.md`.
- **Live Updates:** File watcher detects changes to `ai/*.md` and broadcasts reload events via SSE.
