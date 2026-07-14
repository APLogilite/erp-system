#!/bin/bash
# Backend Start Script for Dynamic ERP

set -e

# Kill any stale Java/Maven processes from previous runs
# This ensures PostgreSQL connections are properly released,
# preventing "too many clients already" errors.
pkill -f "spring-boot:run|erp-system" 2>/dev/null || true
sleep 2

# Also kill any process lingering on port 8081
fuser -k 8081/tcp 2>/dev/null || true

echo "Starting Dynamic ERP Spring Boot Backend..."
mvn spring-boot:run
