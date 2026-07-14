#!/usr/bin/env python3
"""Project Monitor Dashboard — live project tracking from ai/ markdown files."""

import os, re, json, time, glob, logging, threading, queue, collections.abc
from http.server import HTTPServer, BaseHTTPRequestHandler
from urllib.parse import urlparse, parse_qs
from datetime import datetime, timezone, date
from collections import defaultdict

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
log = logging.getLogger("monitor")

AI_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
TASKS_DIR = os.path.join(AI_DIR, "tasks")
CHANGES_DIR = os.path.join(AI_DIR, "changes")
PRD_DIR = os.path.join(AI_DIR, "prd")
BOARD_FILE = os.path.join(AI_DIR, "PROJECT_BOARD.md")
PORT = int(os.environ.get("MONITOR_PORT", 3000))

HAVE_YAML = False
try:
    import yaml
    HAVE_YAML = True
except ImportError:
    pass

HAVE_WATCHDOG = False
try:
    from watchdog.observers import Observer
    from watchdog.events import FileSystemEventHandler
    HAVE_WATCHDOG = True
except ImportError:
    pass

sse_clients = []
sse_lock = threading.Lock()
data_cache = {}
data_cache_lock = threading.Lock()
last_modified = {}
poll_interval = 3

MIME_TYPES = {
    ".html": "text/html; charset=utf-8",
    ".css": "text/css; charset=utf-8",
    ".js": "application/javascript; charset=utf-8",
    ".png": "image/png",
    ".svg": "image/svg+xml",
    ".ico": "image/x-icon",
    ".json": "application/json",
}

class SafeEncoder(json.JSONEncoder):
    def default(self, o):
        if isinstance(o, (datetime, date)):
            return o.isoformat()
        return super().default(o)

    def encode(self, o):
        return super().encode(self._convert(o))

    def _convert(self, val):
        if isinstance(val, (datetime, date)):
            return val.isoformat()
        if isinstance(val, dict):
            return {str(k) if not isinstance(k, (str, int, float, bool, type(None))) else k: self._convert(v) for k, v in val.items()}
        if isinstance(val, list):
            return [self._convert(v) for v in val]
        if isinstance(val, set):
            return list(val)
        if isinstance(val, (int, float, bool, str, type(None))):
            return val
        try:
            return str(val)
        except Exception:
            return None

def _json_safe(val):
    if isinstance(val, (datetime, date)):
        return val.isoformat()
    if isinstance(val, dict):
        return {str(k) if not isinstance(k, (str, int, float, bool, type(None))) else k: _json_safe(v) for k, v in val.items()}
    if isinstance(val, list):
        return [_json_safe(v) for v in val]
    if isinstance(val, set):
        return list(val)
    if isinstance(val, (int, float, bool, str, type(None))):
        return val
    try:
        return str(val)
    except Exception:
        return None

def parse_frontmatter(text):
    m = re.match(r"^---\s*\n(.*?)\n---\s*\n", text, re.DOTALL)
    if not m:
        return {}, text
    raw = m.group(1)
    body = text[m.end():]
    if HAVE_YAML:
        try:
            result = yaml.safe_load(raw) or {}
            return _json_safe(result), body
        except Exception:
            pass
    fm = {}
    for line in raw.split("\n"):
        line = line.strip()
        if not line or line.startswith("#"):
            continue
        if ":" in line:
            k, _, v = line.partition(":")
            k = k.strip()
            v = v.strip()
            if v.startswith("[") and v.endswith("]"):
                fm[k] = [x.strip().strip('"').strip("'") for x in v[1:-1].split(",") if x.strip()]
            elif v.lower() == "true":
                fm[k] = True
            elif v.lower() == "false":
                fm[k] = False
            elif v.isdigit():
                fm[k] = int(v)
            else:
                try:
                    fm[k] = float(v)
                except ValueError:
                    fm[k] = v
    return _json_safe(fm), body

def list_files(dirpath, pattern="*.md"):
    if not os.path.isdir(dirpath):
        return []
    return sorted(glob.glob(os.path.join(dirpath, pattern)))

def parse_task_file(filepath):
    with open(filepath, "r") as f:
        text = f.read()
    fm, body = parse_frontmatter(text)
    fm["_file"] = os.path.basename(filepath)
    fm["_path"] = filepath
    return fm, body

def parse_board_tables(text):
    tables = {}
    current_section = None
    current_headers = []
    current_rows = []

    for line in text.split("\n"):
        if line.startswith("## "):
            if current_section and current_rows:
                tables[current_section] = {"headers": current_headers, "rows": current_rows}
            current_section = line[3:].strip()
            current_headers = []
            current_rows = []
            continue
        if current_section and line.strip().startswith("|") and line.strip().endswith("|"):
            cells = [c.strip() for c in line.strip().strip("|").split("|")]
            if not current_headers:
                current_headers = cells
            elif not re.match(r"^[\s\-\|:]+$", line.strip()):
                current_rows.append(cells)
    if current_section and current_rows:
        tables[current_section] = {"headers": current_headers, "rows": current_rows}
    return tables

def parse_stats_section(tables):
    stats = {}
    stats_section = tables.get("Statistics", {})
    for row in stats_section.get("rows", []):
        if len(row) >= 2:
            stats[row[0].strip()] = row[1].strip()
    return stats

def load_all_data():
    cache = {}

    cache["board_updated"] = datetime.now(timezone.utc).isoformat()

    if os.path.isfile(BOARD_FILE):
        with open(BOARD_FILE, "r") as f:
            board_text = f.read()
        fm, _ = parse_frontmatter(board_text)
        cache["board_meta"] = fm
        cache["board_tables"] = parse_board_tables(board_text)
        cache["stats"] = parse_stats_section(cache["board_tables"])
    else:
        cache["board_meta"] = {}
        cache["board_tables"] = {}
        cache["stats"] = {}

    tasks = {}
    for fp in list_files(TASKS_DIR):
        tid = os.path.basename(fp).replace(".md", "")
        try:
            fm, body = parse_task_file(fp)
            tasks[tid] = fm
        except Exception as e:
            log.warning("Failed to parse %s: %s", fp, e)
    cache["tasks"] = tasks

    changes = {}
    for fp in list_files(CHANGES_DIR):
        cid = os.path.basename(fp).replace(".md", "")
        try:
            fm, body = parse_task_file(fp)
            changes[cid] = fm
        except Exception as e:
            log.warning("Failed to parse %s: %s", fp, e)
    cache["changes"] = changes

    prds = {}
    for fp in list_files(PRD_DIR):
        try:
            fm, body = parse_task_file(fp)
            prd_id = fm.get("id", os.path.basename(fp).replace(".md", ""))
            prds[prd_id] = fm
        except Exception as e:
            log.warning("Failed to parse %s: %s", fp, e)
    cache["prds"] = prds

    agents = defaultdict(lambda: {"tasks": [], "completed": 0, "in_progress": 0, "total_hours": 0})
    for tid, t in tasks.items():
        owner = t.get("owner") or t.get("assigned_to") or "unassigned"
        agents[owner]["tasks"].append({
            "id": tid,
            "title": t.get("title", ""),
            "status": t.get("status", ""),
            "priority": t.get("priority", ""),
            "parent_prd": t.get("parent_prd", ""),
        })
        if t.get("status") == "COMPLETED":
            agents[owner]["completed"] += 1
        if t.get("status") in ("IN_DEVELOPMENT", "TESTING"):
            agents[owner]["in_progress"] += 1
        agents[owner]["total_hours"] += t.get("actual_hours", 0) or 0
    cache["agents"] = {k: dict(v) for k, v in agents.items()}

    for pid, p in cache.get("prds", {}).items():
        prd_tasks = [t for t in tasks.values() if t.get("parent_prd") == pid]
        total = len(prd_tasks)
        completed = sum(1 for t in prd_tasks if t.get("status") == "COMPLETED")
        p["_task_count"] = total
        p["_completed_count"] = completed
        if total:
            p["_completion_pct"] = round(completed / total * 100)
        else:
            p["_completion_pct"] = 0
        p["_statuses"] = defaultdict(int)
        for t in prd_tasks:
            p["_statuses"][t.get("status", "UNKNOWN")] += 1
        p["_statuses"] = dict(p["_statuses"])

    timeline = defaultdict(lambda: {"completed": [], "bugs_fixed": 0})
    for t in tasks.values():
        completed_date = t.get("completed") or t.get("updated")
        if completed_date is not None and t.get("status") == "COMPLETED":
            timeline[str(completed_date)]["completed"].append(t.get("id", ""))
    try:
        cache["timeline"] = {k: dict(v) for k, v in sorted(timeline.items())}
    except TypeError:
        cache["timeline"] = {k: dict(v) for k, v in timeline.items()}

    return cache

def build_aggregated(cache=None, board_tables=None, tasks=None, changes=None, prds=None, agents=None, stats=None, timeline=None):
    return cache

def reload_cache():
    global data_cache, last_modified
    new_data = load_all_data()
    with data_cache_lock:
        data_cache = new_data
    now = time.time()
    for fp in list_files(TASKS_DIR) + list_files(CHANGES_DIR) + list_files(PRD_DIR) + [BOARD_FILE]:
        try:
            last_modified[fp] = os.path.getmtime(fp)
        except OSError:
            pass
    log.info("Cache reloaded (%d tasks, %d changes, %d PRDs)", len(new_data["tasks"]), len(new_data["changes"]), len(new_data["prds"]))
    broadcast({"type": "reload", "data": new_data})
    return new_data

def check_for_changes():
    while True:
        time.sleep(poll_interval)
        changed = False
        for fp in list_files(TASKS_DIR) + list_files(CHANGES_DIR) + list_files(PRD_DIR) + [BOARD_FILE]:
            try:
                mtime = os.path.getmtime(fp)
                if fp not in last_modified or mtime > last_modified[fp]:
                    log.info("Change detected: %s", fp)
                    changed = True
            except OSError:
                pass
        if changed:
            reload_cache()

def broadcast(data):
    msg = f"data: {json.dumps(data, cls=SafeEncoder)}\n\n"
    with sse_lock:
        dead = []
        for q in sse_clients:
            try:
                q.put_nowait(msg)
            except Exception:
                dead.append(q)
        for q in dead:
            sse_clients.remove(q)


class SSEQueue:
    def __init__(self):
        self.q = queue.Queue()
    def get(self, timeout=30):
        try:
            return self.q.get(timeout=timeout)
        except queue.Empty:
            return None

class Handler(BaseHTTPRequestHandler):
    def log_message(self, fmt, *args):
        log.info(f"{self.client_address[0]} - {fmt}", *args)

    def do_GET(self):
        parsed = urlparse(self.path)
        path = parsed.path
        params = parse_qs(parsed.query)

        if path == "/events":
            self.handle_sse()
        elif path == "/api/board":
            self.send_json(data_cache)
        elif path == "/api/stats":
            self.send_json(data_cache.get("stats", {}))
        elif path == "/api/tasks":
            self.send_json(list(data_cache.get("tasks", {}).values()))
        elif path == "/api/prds":
            self.send_json(list(data_cache.get("prds", {}).values()))
        elif path == "/api/agents":
            self.send_json(data_cache.get("agents", {}))
        elif path == "/api/changes":
            self.send_json(list(data_cache.get("changes", {}).values()))
        elif path == "/api/timeline":
            self.send_json(data_cache.get("timeline", {}))
        elif path == "/api/board-tables":
            self.send_json(data_cache.get("board_tables", {}))
        elif path in ("/", "/index.html"):
            self.serve_static("dashboard.html")
        else:
            self.serve_static(path.lstrip("/"))

    def handle_sse(self):
        self.send_response(200)
        self.send_header("Content-Type", "text/event-stream")
        self.send_header("Cache-Control", "no-cache")
        self.send_header("Connection", "keep-alive")
        self.send_header("Access-Control-Allow-Origin", "*")
        self.end_headers()

        q = SSEQueue()
        with sse_lock:
            sse_clients.append(q)

        self.wfile.write(f"data: {json.dumps({'type': 'connected', 'data': data_cache})}\n\n".encode())
        self.wfile.flush()

        try:
            while True:
                msg = q.get(timeout=15)
                if msg:
                    self.wfile.write(msg.encode())
                    self.wfile.flush()
                else:
                    self.wfile.write(": keepalive\n\n".encode())
                    self.wfile.flush()
        except (BrokenPipeError, ConnectionResetError):
            pass
        finally:
            with sse_lock:
                if q in sse_clients:
                    sse_clients.remove(q)

    def send_json(self, data):
        self.send_response(200)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Cache-Control", "no-cache")
        self.end_headers()
        self.wfile.write(json.dumps(data, cls=SafeEncoder, indent=2).encode())

    def serve_static(self, filename):
        script_dir = os.path.dirname(os.path.abspath(__file__))
        filepath = os.path.join(script_dir, filename)
        if not os.path.isfile(filepath) or ".." in filename or filename.startswith("/"):
            self.send_error(404)
            return
        ext = os.path.splitext(filename)[1].lower()
        mime = MIME_TYPES.get(ext, "application/octet-stream")
        try:
            with open(filepath, "rb") as f:
                content = f.read()
            self.send_response(200)
            self.send_header("Content-Type", mime)
            self.send_header("Cache-Control", "no-cache")
            self.end_headers()
            self.wfile.write(content)
        except IOError:
            self.send_error(404)


def start_file_watcher():
    if HAVE_WATCHDOG:
        class ChangeHandler(FileSystemEventHandler):
            def on_any_event(self, event):
                if event.src_path.endswith(".md") and not event.is_directory:
                    log.info("Watchdog detected change: %s", event.src_path)
                    reload_cache()
        observer = Observer()
        observer.schedule(ChangeHandler(), AI_DIR, recursive=True)
        observer.start()
        log.info("File watcher: watchdog (recursive)")
        return observer
    else:
        t = threading.Thread(target=check_for_changes, daemon=True)
        t.start()
        log.info("File watcher: polling (every %ds)", poll_interval)
        return None

def main():
    reload_cache()
    watcher = start_file_watcher()

    server = HTTPServer(("0.0.0.0", PORT), Handler)
    log.info("=" * 60)
    log.info("  Project Monitor Dashboard")
    log.info("  http://localhost:%d", PORT)
    log.info("  Watching: %s", AI_DIR)
    log.info("=" * 60)

    try:
        server.serve_forever()
    except KeyboardInterrupt:
        log.info("Shutting down...")
        server.shutdown()
        if watcher:
            watcher.stop()

if __name__ == "__main__":
    main()
