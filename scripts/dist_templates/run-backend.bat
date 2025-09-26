@echo off
REM run-backend.bat — start the backend using bundled JRE if present or system java
setlocal
set SCRIPT_DIR=%~dp0
set ROOT=%SCRIPT_DIR%..
set JRE=%SCRIPT_DIR%..\jre\bin\java.exe
if exist "%JRE%" (
  set JAVA_EXE=%JRE%
) else (
  set JAVA_EXE=java
)
"%JAVA_EXE%" --enable-preview -cp "%SCRIPT_DIR%..\payroll-app-1.0-SNAPSHOT.jar;%SCRIPT_DIR%..\dependency\*" org.example.Main serve
pause
