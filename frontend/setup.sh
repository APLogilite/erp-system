#!/bin/bash

# Frontend Setup Script for Dynamic ERP
# This script sets up the local Node.js and pnpm environment

set -e

echo "Setting up frontend environment..."

# Create .local directory if it doesn't exist
mkdir -p .local/nodejs

# Download and install Node.js locally
NODE_VERSION="22.13.0"
NODE_DIST="node-v${NODE_VERSION}-linux-x64"
NODE_URL="https://nodejs.org/dist/v${NODE_VERSION}/${NODE_DIST}.tar.xz"

echo "Downloading Node.js ${NODE_VERSION}..."
curl -o node.tar.xz "${NODE_URL}"

echo "Extracting Node.js..."
tar -xf node.tar.xz --strip-components=1 -C .local/nodejs
rm node.tar.xz

# Install pnpm globally using the local Node.js
echo "Installing pnpm..."
PATH=./.local/nodejs/bin:$PATH ./.local/nodejs/bin/npm install -g pnpm

# Install project dependencies
echo "Installing dependencies..."
PATH=./.local/nodejs/bin:$PATH ./.local/nodejs/bin/pnpm install

echo "Frontend environment setup complete!"
echo "You can now run: ./.local/bin/pnpm dev"