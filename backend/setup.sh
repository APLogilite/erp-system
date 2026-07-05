#!/bin/bash
# Backend Setup Script for Dynamic ERP
# This script verifies prerequisites and downloads dependencies.

set -e

echo "Setting up backend environment..."

# 1. Verify Java Installation
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed. Please install Java 17+ and try again."
    exit 1
fi

# Extract major Java version
JAVA_VERSION_FULL=$(java -version 2>&1 | head -n 1)
echo "Java installation found: ${JAVA_VERSION_FULL}"

# 2. Verify Maven Installation
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven (mvn) is not installed. Please install Maven 3.6+ and try again."
    exit 1
fi

echo "Maven installation found: $(mvn -version | head -n 1)"

# 3. Clean and build backend packages to download all POM dependencies
echo "Building backend and downloading dependencies..."
mvn clean install -DskipTests

echo "Backend environment setup complete!"
echo "To start the backend server, run: ./start.sh"
