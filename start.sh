#!/bin/bash
# Start both backend and frontend servers for Dynamic ERP
# Runs both in parallel — Ctrl+C stops both

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "=========================================================="
echo "      Dynamic ERP System — Starting All Servers           "
echo "=========================================================="
echo ""
echo "  Backend  → http://localhost:8081"
echo "  Frontend → http://localhost:5173"
echo ""

cleanup() {
  echo ""
  echo "Shutting down all servers..."
  kill $BACKEND_PID $FRONTEND_PID 2>/dev/null || true
  wait $BACKEND_PID $FRONTEND_PID 2>/dev/null || true
  echo "All servers stopped."
}
trap cleanup EXIT INT TERM

# Start backend
cd "$PROJECT_ROOT/backend"
mvn spring-boot:run &
BACKEND_PID=$!

# Start frontend
cd "$PROJECT_ROOT/frontend"
export PATH="$(pwd)/.local/nodejs/bin:$PATH"
pnpm dev &
FRONTEND_PID=$!

cd "$PROJECT_ROOT"

echo "Servers starting in background..."
echo "  Backend PID:  $BACKEND_PID"
echo "  Frontend PID: $FRONTEND_PID"
echo ""
echo "Press Ctrl+C to stop both servers."

wait
