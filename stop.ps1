# ============================================
# College Event Management - STOP Script
# ============================================

Write-Host ""
Write-Host ">> Stopping server on port 8000..." -ForegroundColor Yellow

$found = $false
$netstatLines = netstat -ano | Select-String ":8000\s" | Where-Object { $_ -match "LISTENING" }
foreach ($line in $netstatLines) {
    $parts = ($line.ToString().Trim() -split '\s+')
    $procId = $parts[-1]
    if ($procId -match '^\d+$' -and $procId -ne '0') {
        taskkill /PID $procId /T /F 2>$null | Out-Null
        Write-Host ">> Killed process PID: $procId" -ForegroundColor Red
        $found = $true
    }
}

if ($found) {
    Write-Host ">> Server stopped. Port 8000 is now free." -ForegroundColor Green
} else {
    Write-Host ">> No server was running on port 8000." -ForegroundColor Cyan
}
Write-Host ""
