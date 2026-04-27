# College Event Management - GitHub Sync Script
Write-Host ">>> Starting Sync with GitHub..." -ForegroundColor Cyan

# Stage all changes
git add .

# Check if there are changes to commit
$status = git status --porcelain
if (-not $status) {
    Write-Host ">>> No changes detected. Your repository is already up to date." -ForegroundColor Yellow
    exit
}

# Prompt for commit message or use default
$msg = "Update: New features and fixes added on $(Get-Date -Format 'yyyy-MM-dd HH:mm')"
Write-Host ">>> Committing changes..." -ForegroundColor Gray
git commit -m "$msg"

# Push to main branch
Write-Host ">>> Pushing to GitHub (main)..." -ForegroundColor Gray
git push origin main

Write-Host ">>> SUCCESS: Your GitHub repository has been updated!" -ForegroundColor Green
Write-Host ">>> View it here: https://github.com/Yogaraj02/EL-Spring-Boot-Application" -ForegroundColor Blue
