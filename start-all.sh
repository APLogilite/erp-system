#!/bin/bash
# ============================================================
# ERP System — Start All Services
# ============================================================
# Starts both the backend and frontend dev servers in the
# background, showing logs from both.
#
# Usage:
#   bash start-all.sh              # normal start
#   bash start-all.sh --setup      # reset DB first, then start
# ============================================================

set -e

DIR="$(cd "$(dirname "$0")" && pwd)"

echo "========================================"
echo " ERP System — Starting All Services"
echo "========================================"

# Optional: reset database
if [ "$1" = "--setup" ]; then
  echo ""
  echo ">>> Running full setup (DB reset + migrate)..."
  bash "$DIR/backend/db-reset.sh"
fi

# Kill any existing processes
pkill -f "spring-boot:run" 2>/dev/null || true
pkill -f "vite" 2>/dev/null || true
sleep 2

# Start backend
echo ""
echo ">>> Starting Backend (port 8081)..."
cd "$DIR/backend"
mvn spring-boot:run > /tmp/erp-backend.log 2>&1 &
BACKEND_PID=$!
echo "  Backend PID: $BACKEND_PID"

# Wait for backend to be ready
echo "  Waiting for backend to start..."
for i in $(seq 1 60); do
  if curl -s http://localhost:8081/api/v1/runtime/menu > /dev/null 2>&1; then
    echo "  Backend is ready!"
    break
  fi
  sleep 2
done

# Start frontend
echo ""
echo ">>> Starting Frontend (port 5173)..."
cd "$DIR/frontend"
./.local/nodejs/bin/pnpm dev > /tmp/erp-frontend.log 2>&1 &
FRONTEND_PID=$!
echo "  Frontend PID: $FRONTEND_PID"

sleep 3

echo ""
echo "========================================"
echo " Both servers are running!"
echo "========================================"
echo " Frontend : http://localhost:5173"
echo " Backend  : http://localhost:8081"
echo ""
echo " Logs:"
echo "  Backend : tail -f /tmp/erp-backend.log"
echo "  Frontend: tail -f /tmp/erp-frontend.log"
echo ""
echo " To stop: pkill -f 'spring-boot:run' && pkill -f 'vite'"
echo "========================================"

# Trap to handle Ctrl+C gracefully
trap "echo ''; echo 'Stopping services...'; kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; exit 0" INT TERM

# Keep script running
wait
