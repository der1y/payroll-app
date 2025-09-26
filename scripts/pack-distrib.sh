#!/usr/bin/env bash
set -euo pipefail

# pack-distrib.sh
# Build the project, collect artifacts and create a distributable ZIP in ./dist
# Usage:
#   ./scripts/pack-distrib.sh [PATH_TO_JRE_FOLDER]
# If PATH_TO_JRE_FOLDER is provided, it will be copied into the ZIP under 'jre/'.

JRE_PATH="${1-}"
ROOT_DIR=$(pwd)
PKG_DIR="$ROOT_DIR/dist/package"
DIST_ZIP="$ROOT_DIR/dist/payroll-app-distrib.zip"

echo "Building project (maven)..."
mvn -DskipTests dependency:copy-dependencies package

echo "Preparing package directory: $PKG_DIR"
rm -rf "$PKG_DIR"
mkdir -p "$PKG_DIR"

# Copy jar and dependencies
cp target/payroll-app-1.0-SNAPSHOT.jar "$PKG_DIR/"
cp -r target/dependency "$PKG_DIR/"

# Copy helper start/stop scripts
mkdir -p "$PKG_DIR/scripts"
cp -r scripts/dist_templates/* "$PKG_DIR/scripts/"

# Optionally include frontend build (if exists)
if [ -d frontend/dist ]; then
  echo "Including frontend/dist (pre-built)"
  mkdir -p "$PKG_DIR/frontend"
  cp -r frontend/dist/* "$PKG_DIR/frontend/"
fi

# Optionally include a JRE if provided
if [ -n "$JRE_PATH" ]; then
  if [ -d "$JRE_PATH" ]; then
    echo "Including JRE from $JRE_PATH"
    cp -r "$JRE_PATH" "$PKG_DIR/jre"
  else
    echo "Warning: provided JRE_PATH does not exist: $JRE_PATH"
  fi
else
  echo "No JRE path provided. You can include a JRE by passing its folder path as the first argument."
fi

# Create the distribution archive
mkdir -p "$ROOT_DIR/dist"
if command -v zip >/dev/null 2>&1; then
  echo "Creating ZIP: $DIST_ZIP"
  (cd "$ROOT_DIR/dist" && zip -r "payroll-app-distrib.zip" package)
  echo "Created $DIST_ZIP"
else
  echo "zip not found; creating tar.gz instead"
  (cd "$ROOT_DIR/dist" && tar -czf payroll-app-distrib.tar.gz package)
  echo "Created dist/payroll-app-distrib.tar.gz"
fi

echo "Package ready in ./dist/"
