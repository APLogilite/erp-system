#!/bin/bash
# Backend Start Script for Dynamic ERP

set -e

# Kill any process lingering on port 8081 from a previous run
# (common when mvn spring-boot:run is killed abruptly)
fuser -k 8081/tcp 2>/dev/null || true

# If PostgreSQL reports "too many clients", run this manually:
#   pkill -f "mvn spring-boot:run"
# This kills stale Maven processes whose PostgreSQL connections
# weren't released when the JVM was killed.

LOG_MODE=false
if [ "$1" = "--log" ]; then
  LOG_MODE=true
  echo "Starting Dynamic ERP Spring Boot Backend (logging to /tmp/erp-backend.log)..."
  nohup mvn spring-boot:run > /tmp/erp-backend.log 2>&1 &
  echo "Backend PID: $!"
  echo "Tail logs: tail -f /tmp/erp-backend.log"
else
  echo "Starting Dynamic ERP Spring Boot Backend..."
  mvn spring-boot:run
fi
