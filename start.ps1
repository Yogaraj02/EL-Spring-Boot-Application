# ========================================
#  College Event Management - Smart Start
# ========================================

Write-Host ""
Write-Host "  ======================================" -ForegroundColor Cyan
Write-Host "   College Event Management System" -ForegroundColor Cyan
Write-Host "  ======================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "  [1/2] Checking for port conflicts..." -ForegroundColor Yellow

$pids = (netstat -ano | Select-String ":8080 |:8081 " | ForEach-Object { ($_ -split '\s+')[-1] } | Select-Object -Unique | Where-Object { $_ -match '^\d+$' -and $_ -ne '0' })

if ($pids) {
    foreach ($p in $pids) {
        Write-Host "  Stopping PID $p..." -ForegroundColor Gray
        taskkill /F /PID $p /T | Out-Null
    }
    Start-Sleep -Seconds 1
    Write-Host "  ✔ Ports cleared." -ForegroundColor Green
} else {
    Write-Host "  ✔ No conflicts found." -ForegroundColor Green
}

Write-Host ""
Write-Host "  [2/2] Starting Spring Boot (http://localhost:8081)..." -ForegroundColor Yellow
Write-Host ""

mvn spring-boot:run
