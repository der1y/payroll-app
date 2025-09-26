#!/usr/bin/env bash
set -euo pipefail
# run-backend.sh — start bundled backend (uses bundled jre/ or system java)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$SCRIPT_DIR/.."
if [ -d "$ROOT_DIR/jre" ]; then
  JAVA_EXE="$ROOT_DIR/jre/bin/java"
else
  JAVA_EXE="java"
fi
"$JAVA_EXE" --enable-preview -cp "$ROOT_DIR/payroll-app-1.0-SNAPSHOT.jar:$ROOT_DIR/dependency/*" org.example.Main serve
