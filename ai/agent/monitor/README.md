# Project Monitor Dashboard

A lightweight, real-time dashboard for tracking project tasks, PRDs, agents, and progress — powered by the markdown files in the `ai/` directory.

## Features

- **Overview** — Total tasks, completion %, PRD progress bars, board statistics
- **PRDs** — Per-PRD task breakdowns with completion tracking and expandable task lists
- **Kanban** — Tasks organized by status swimlanes (Ready for Dev → In Development → Ready for Test → Testing → Completed)
- **Agents** — Who's working on what, workload distribution, completion rates
- **Board** — Raw `PROJECT_BOARD.md` sections rendered as tables
- **Live Updates** — File watcher detects changes to `ai/*.md` files and pushes updates to the browser via Server-Sent Events

## Requirements

- Python 3.8+
- Optional: `pyyaml`, `watchdog` (auto-installed by `start.sh`)

## Quick Start

```bash
cd ai/monitor
./start.sh
```

Then open **http://localhost:3000** in your browser.

## Manual Start

```bash
cd ai/monitor
pip3 install pyyaml watchdog   # optional but recommended
python3 server.py
```

## Configuration

| Environment Variable | Default | Description |
|---------------------|---------|-------------|
| `MONITOR_PORT` | `3000` | HTTP server port |

## How It Works

1. On startup, `server.py` parses all markdown files in `ai/` (tasks, changes, PRDs, and `PROJECT_BOARD.md`)
2. A file watcher (watchdog if available, polling fallback) monitors for changes
3. When a file changes, the cache is rebuilt and a `reload` event is broadcast to all connected browsers via SSE
4. The dashboard renders 5 views from the same data snapshot

## Data Sources

| Source | Path | Contents |
|--------|------|----------|
| Project Board | `ai/agent/project-board.md` | Swimlanes, PRD status, stats |
| Tasks | `ai/project/tasks/*.md` | Individual task frontmatter (status, owner, priority, PRD, hours) |
| Changes | `ai/project/changes/*.md` | Change report metadata (developer, duration) |
| PRDs | `ai/project/prd/*.md` | PRD metadata (version, status, task count) |

## File Structure

```
ai/monitor/
├── server.py        # HTTP server + parsers + SSE + file watcher
├── dashboard.html   # Single-page dashboard UI
├── start.sh         # Launcher script
└── README.md        # This file
```
