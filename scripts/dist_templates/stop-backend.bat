@echo off
REM stop-backend.bat — tries to kill process listening on 8080
necho Stopping backend (attempting to find PID listening on :8080)...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":8080"') do (
  echo Killing PID %%a
  taskkill /PID %%a /F
)
echo Done.
