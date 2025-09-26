param(
  [switch]$NoFrontend
)

# PowerShell wrapper to build the backend and start API server and frontend dev server.
# Run from the repository root in PowerShell (Windows).

Write-Host "Building backend (maven)..."
mvn -DskipTests dependency:copy-dependencies package

Write-Host "Starting backend (Java) ..."
$backendProc = Start-Process -FilePath java -ArgumentList @(
  "--enable-preview",
  "-cp",
  "target/classes;target/dependency/*",
  "org.example.Main",
  "serve"
) -PassThru
Write-Host "Backend started (PID=$($backendProc.Id)) -> http://localhost:8080"

if (-not $NoFrontend -and (Test-Path -Path "frontend")) {
  Write-Host "Starting frontend (npm)..."
  Push-Location frontend
  npm ci
  Start-Process -FilePath npm -ArgumentList @("run","dev") -PassThru | Out-Null
  Pop-Location
  Write-Host "Frontend started -> http://localhost:5173"
} else {
  Write-Host "Skipping frontend start."
}

Write-Host "Done. To stop the backend: Stop-Process -Id $($backendProc.Id) -Force"
