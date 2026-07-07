#!/bin/bash
# Stop both backend and frontend servers for Dynamic ERP

echo "Stopping Dynamic ERP servers..."

# Kill backend (Spring Boot)
BACKEND_PID=$(lsof -ti tcp:8081 2>/dev/null)
if [ -n "$BACKEND_PID" ]; then
  kill $BACKEND_PID 2>/dev/null && echo "  Backend (PID $BACKEND_PID) stopped." || echo "  Backend not running."
else
  echo "  Backend not running."
fi

# Kill frontend (Vite)
FRONTEND_PID=$(lsof -ti tcp:5173 2>/dev/null)
if [ -n "$FRONTEND_PID" ]; then
  kill $FRONTEND_PID 2>/dev/null && echo "  Frontend (PID $FRONTEND_PID) stopped." || echo "  Frontend not running."
else
  echo "  Frontend not running."
fi

echo "Done."
