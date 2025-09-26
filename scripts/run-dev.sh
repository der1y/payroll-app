#!/usr/bin/env bash
set -euo pipefail

# Small wrapper to build the Java backend, copy dependencies, start the API server,
# and (optionally) start the frontend dev server. Run this from the repository root.

# Determine classpath separator for the current environment
SEP=":"
UNAME=$(uname -s 2>/dev/null || echo "")
if [[ "$UNAME" =~ MINGW|CYGWIN|MSYS ]]; then
  SEP=";"
fi

echo "Building backend (maven)..."
mvn -DskipTests dependency:copy-dependencies package

echo "Starting backend (Java) ..."
java --enable-preview -cp "target/classes${SEP}target/dependency/*" org.example.Main serve &
BACK_PID=$!

# Give the server a moment to start
sleep 1

echo "Backend started (PID=$BACK_PID) -> http://localhost:8080"

# Start frontend in background if frontend directory exists
if [ -d "frontend" ]; then
  echo "Starting frontend (vite)..."
  (cd frontend && npm ci && npm run dev) &
  FRONT_PID=$!
  echo "Frontend started (PID=$FRONT_PID) -> http://localhost:5173"
else
  echo "No frontend folder found, skipping frontend start."
fi

echo "Run status: backend PID=$BACK_PID${FRONT_PID:+, frontend PID=$FRONT_PID}"
echo "To stop processes: kill <PID> (or use taskkill /PID <PID> /F on Windows)"
