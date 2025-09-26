param()

# PowerShell stop helper for dev environment
Write-Host "Stopping backend listening on port 8080 (if any)..."
try {
  $net = netstat -ano | Select-String ":8080"
  if ($net) {
    $pids = $net -replace '^.*\s(\d+)$','$1' | Sort-Object -Unique
    foreach ($pid in $pids) {
      Write-Host "Stopping PID $pid"
      Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
    }
  } else {
    Write-Host "No processes found listening on 8080."
  }
} catch {
  Write-Warning "Failed to query netstat: $_"
}

Write-Host "Attempting to stop vite/node frontend processes..."
Get-Process -Name node -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.Id -Force }
Get-Process -Name npm -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.Id -Force }
Write-Host "Done."