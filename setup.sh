#!/bin/bash
# Root Setup Script for Dynamic ERP
# This script installs all frontend and backend prerequisites sequentially.

set -e

echo "=========================================================="
echo "      Dynamic ERP System — Full Stack Setup Script        "
echo "=========================================================="

# Determine project root directory
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${PROJECT_ROOT}"

# 1. Setup Backend
echo ""
echo ">>> Setting up Backend..."
echo "------------------------------------------"
cd backend
./setup.sh
cd "${PROJECT_ROOT}"

# 2. Setup Frontend
echo ""
echo ">>> Setting up Frontend..."
echo "------------------------------------------"
cd frontend
./setup.sh
cd "${PROJECT_ROOT}"

echo ""
echo "=========================================================="
echo "          Full Stack Setup Complete Successfully!         "
echo "=========================================================="
echo ""
echo "You can now run both servers using the start helpers:"
echo ""
echo "Backend Server:"
echo "  cd backend && ./start.sh"
echo ""
echo "Frontend Server:"
echo "  cd frontend && ./start.sh"
echo ""
echo "=========================================================="
