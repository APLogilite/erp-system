#!/bin/bash
# ============================================================
# ERP System — Full Setup Script
# ============================================================
# Runs db-reset.sh to create a fresh database, then starts the
# application.
#
# Usage:  bash setup.sh
# ============================================================

set -e

DIR="$(cd "$(dirname "$0")" && pwd)"

echo "========================================"
echo " ERP System — Full Setup"
echo "========================================"
echo ""

# Step 1: Reset database
echo ">>> Step 1/2: Resetting database..."
bash "$DIR/db-reset.sh"

# Step 2: Start application
echo ""
echo ">>> Step 2/2: Starting application..."
bash "$DIR/start.sh"
