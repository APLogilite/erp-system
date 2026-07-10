#!/bin/bash
# Backend Start Script for Dynamic ERP

set -e

# Kill any stale process lingering on port 8081 from a previous run
# (common when mvn spring-boot:run is killed abruptly)
fuser -k 8081/tcp 2>/dev/null || true

echo "Starting Dynamic ERP Spring Boot Backend..."
mvn spring-boot:run
