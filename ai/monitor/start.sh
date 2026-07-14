#!/bin/bash
set -e
DIR="$(cd "$(dirname "$0")" && pwd)"

echo "🔧 Project Monitor Dashboard"
echo "============================"
echo ""

if ! command -v python3 &>/dev/null; then
  echo "Error: python3 is required but not found."
  exit 1
fi

if python3 -c "import yaml" 2>/dev/null; then
  echo "✓ pyyaml found"
else
  echo "Installing pyyaml..."
  pip3 install pyyaml --quiet 2>&1 | tail -1
fi

if python3 -c "import watchdog" 2>/dev/null; then
  echo "✓ watchdog found (file watching)"
else
  echo "Installing watchdog for live file watching..."
  pip3 install watchdog --quiet 2>&1 | tail -1
fi

echo ""
echo "Starting server at http://localhost:${MONITOR_PORT:-3000}"
echo "Watching: $DIR/.."
echo "Press Ctrl+C to stop"
echo ""

cd "$DIR"
exec python3 server.py
