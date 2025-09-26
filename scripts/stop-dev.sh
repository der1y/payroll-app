#!/usr/bin/env bash
set -euo pipefail

# Stop helper for the dev environment.
# - On Windows (Git Bash / MSYS) it will try to find the PID listening on 8080 and taskkill it.
# - On Unix-like, it will use lsof to find processes listening on 8080 and kill them.
# - It will also try to kill common frontend dev processes (vite/node) by name.

echo "Stopping dev servers..."
UNAME=$(uname -s 2>/dev/null || "")

if [[ "$UNAME" =~ MINGW|CYGWIN|MSYS ]]; then
  echo "Detected Windows environment (MSYS/Cygwin). Looking for PID listening on 8080..."
  PIDS=$(netstat -ano | grep 8080 | awk '{print $NF}' | sort -u || true)
  if [ -n "$PIDS" ]; then
    for pid in $PIDS; do
      echo "Killing PID $pid (taskkill)..."
      taskkill /PID $pid /F 2>/dev/null || true
    done
  else
    echo "No process listening on 8080 found."
  fi
else
  echo "Unix-like environment. Looking for processes listening on :8080..."
  if command -v lsof >/dev/null 2>&1; then
    PIDS=$(lsof -ti:8080 || true)
    if [ -n "$PIDS" ]; then
      echo "Killing PIDs: $PIDS"
      kill $PIDS 2>/dev/null || kill -9 $PIDS 2>/dev/null || true
    else
      echo "No processes listening on 8080 found."
    fi
  else
    echo "lsof not available; skipping 8080 lookup."
  fi
fi

# Try to kill frontend dev servers (vite / node)
if command -v pgrep >/dev/null 2>&1; then
  FRONT_PIDS=$(pgrep -f "node|vite" || true)
  if [ -n "$FRONT_PIDS" ]; then
    echo "Killing frontend PIDs: $FRONT_PIDS"
    kill $FRONT_PIDS 2>/dev/null || kill -9 $FRONT_PIDS 2>/dev/null || true
  else
    echo "No vite/node frontend processes found."
  fi
else
  echo "pgrep not available; skipping frontend process lookup."
fi

echo "Done."