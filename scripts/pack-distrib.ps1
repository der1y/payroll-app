param(
  [string]$JrePath
)

# PowerShell packager: builds and creates dist\payroll-app-distrib.zip
Write-Host "Building project (maven)..."
mvn -DskipTests dependency:copy-dependencies package

$root = Get-Location
$pkg = Join-Path $root "dist\package"
$distZip = Join-Path $root "dist\payroll-app-distrib.zip"

if (Test-Path $pkg) { Remove-Item $pkg -Recurse -Force }
New-Item -ItemType Directory -Path $pkg -Force | Out-Null

Copy-Item -Path "target\payroll-app-1.0-SNAPSHOT.jar" -Destination $pkg
Copy-Item -Recurse -Path "target\dependency" -Destination $pkg

# copy helper scripts
New-Item -ItemType Directory -Path (Join-Path $pkg "scripts") -Force | Out-Null
Copy-Item -Recurse -Path "scripts\dist_templates\*" -Destination (Join-Path $pkg "scripts") -Force

# Optionally include frontend/dist
if (Test-Path "frontend\dist") {
  New-Item -ItemType Directory -Path (Join-Path $pkg "frontend") -Force | Out-Null
  Copy-Item -Recurse -Path "frontend\dist\*" -Destination (Join-Path $pkg "frontend") -Force
}

if ($JrePath) {
  if (Test-Path $JrePath) {
    Write-Host "Including JRE from $JrePath"
    Copy-Item -Recurse -Path $JrePath -Destination (Join-Path $pkg "jre") -Force
  } else {
    Write-Warning "JRE path not found: $JrePath"
  }
} else {
  Write-Host "No JRE path provided. You can pass -JrePath <path> to include one in the package."
}

if (Test-Path (Join-Path $root "dist")) { Remove-Item (Join-Path $root "dist") -Recurse -Force }
New-Item -ItemType Directory -Path (Join-Path $root "dist") -Force | Out-Null

Write-Host "Creating ZIP: $distZip"
Compress-Archive -Path (Join-Path $pkg "*") -DestinationPath $distZip -Force
Write-Host "Package created: $distZip"
