# ============================================
# College Event Management - START Script
# ============================================

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host "  College Event Management System" -ForegroundColor Cyan
Write-Host "  http://localhost:8000" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Kill any existing Java process on port 8000
Write-Host ">> Checking port 8000..." -ForegroundColor Yellow
$netstatLines = netstat -ano | Select-String ":8000\s" | Where-Object { $_ -match "LISTENING" }
foreach ($line in $netstatLines) {
    $parts = ($line.ToString().Trim() -split '\s+')
    $procId = $parts[-1]
    if ($procId -match '^\d+$' -and $procId -ne '0') {
        Write-Host ">> Killing old process (PID: $procId)..." -ForegroundColor Red
        taskkill /PID $procId /F | Out-Null
        Start-Sleep -Milliseconds 500
    }
}
Write-Host ">> Port 8000 is free." -ForegroundColor Green
Write-Host ""
Write-Host ">> Starting server... Press Ctrl+C to stop." -ForegroundColor Cyan
Write-Host ""

# Step 2: Launch Maven as a tracked process
$mvnProcess = Start-Process -FilePath "mvn" `
    -ArgumentList "spring-boot:run" `
    -WorkingDirectory $PSScriptRoot `
    -NoNewWindow `
    -PassThru

# Step 3: Wait and handle Ctrl+C cleanly
try {
    $mvnProcess.WaitForExit()
} finally {
    Write-Host ""
    Write-Host ">> Shutting down server..." -ForegroundColor Yellow

    # Kill the maven process tree
    if (-not $mvnProcess.HasExited) {
        taskkill /PID $mvnProcess.Id /T /F 2>$null | Out-Null
    }

    # Also kill any remaining Java process on port 8000
    Start-Sleep -Milliseconds 500
    $remaining = netstat -ano | Select-String ":8000\s" | Where-Object { $_ -match "LISTENING" }
    foreach ($line in $remaining) {
        $parts = ($line.ToString().Trim() -split '\s+')
        $procId = $parts[-1]
        if ($procId -match '^\d+$' -and $procId -ne '0') {
            taskkill /PID $procId /F 2>$null | Out-Null
        }
    }

    Write-Host ">> Server stopped. Port 8000 is now free." -ForegroundColor Green
    Write-Host ""
}
