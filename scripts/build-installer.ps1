param(
  [string]$JrePath
)

# Build and package, then build NSIS installer (Windows)
if (-not (Get-Command makensis -ErrorAction SilentlyContinue)) {
  Write-Host "makensis not found. Please install NSIS and ensure 'makensis' is on PATH."; exit 1
}

Write-Host "Creating package..."
.\pack-distrib.ps1 -JrePath $JrePath

Write-Host "Building NSIS installer..."
# NSIS script located at scripts/installer.nsi
makensis scripts\installer.nsi

Write-Host "Installer created at dist\\payroll-app-installer.exe"
