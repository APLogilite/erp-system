#!/bin/bash
# Frontend Start Script for Dynamic ERP

set -e

# Add local Node.js and pnpm bin to path
export PATH="$(pwd)/.local/nodejs/bin:$PATH"

echo "Starting Dynamic ERP Frontend with local environment..."
pnpm dev
