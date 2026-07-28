#!/bin/bash
# Frontend Start Script for Dynamic ERP

set -e

# Add local Node.js and pnpm bin to path
export PATH="$(pwd)/.local/nodejs/bin:$PATH"

LOG_MODE=false
if [ "$1" = "--log" ]; then
  LOG_MODE=true
  echo "Starting Dynamic ERP Frontend (logging to /tmp/erp-frontend.log)..."
  nohup pnpm dev > /tmp/erp-frontend.log 2>&1 &
  echo "Frontend PID: $!"
  echo "Tail logs: tail -f /tmp/erp-frontend.log"
else
  echo "Starting Dynamic ERP Frontend with local environment..."
  pnpm dev
fi
