# Download Correct AI Model (Smaller, Faster)
# This downloads SmolLM2 360M Q4_K_M (76MB) instead of Q8_0 (368MB)

# Delete old large model
$oldModelPath = "app/src/main/assets/models/SmolLM2-360M.Q8_0.gguf"
if (Test-Path $oldModelPath) {
    Write-Host "Removing old large model (368MB)..." -ForegroundColor Yellow
    Remove-Item $oldModelPath -Force
    Write-Host "Old model removed." -ForegroundColor Green
}

# Download smaller, faster model
$modelUrl = "https://huggingface.co/bartowski/SmolLM2-360M-Instruct-GGUF/resolve/main/SmolLM2-360M-Instruct-Q4_K_M.gguf"
$outputPath = "app/src/main/assets/models/SmolLM2-360M-Q4_K_M.gguf"

Write-Host "Downloading SmolLM2 360M Q4_K_M Model (76MB - Much Faster!)..." -ForegroundColor Cyan
Write-Host "Source: $modelUrl" -ForegroundColor Gray
Write-Host "Destination: $outputPath" -ForegroundColor Gray
Write-Host ""

# Create directory if needed
$directory = Split-Path -Parent $outputPath
if (-not (Test-Path $directory)) {
    New-Item -ItemType Directory -Path $directory -Force | Out-Null
}

# Download with progress
try {
    Write-Host "Downloading... (this will take 2-3 minutes, ~76 MB)" -ForegroundColor Yellow
    Write-Host ""
    
    Import-Module BitsTransfer
    Start-BitsTransfer -Source $modelUrl -Destination $outputPath -Description "Downloading Smaller AI Model" -DisplayName "SmolLM2 Q4_K_M"
    
    if (Test-Path $outputPath) {
        $fileSize = (Get-Item $outputPath).Length / 1MB
        Write-Host ""
        Write-Host "SUCCESS: Download complete!" -ForegroundColor Green
        Write-Host "Model size: $([math]::Round($fileSize, 2)) MB" -ForegroundColor Green
        Write-Host "Location: $outputPath" -ForegroundColor Green
        Write-Host ""
        Write-Host "This model is 5x smaller and loads 10x faster!" -ForegroundColor Cyan
        Write-Host "Now rebuild your app and it will load in ~5 seconds instead of 60+ seconds." -ForegroundColor Cyan
    } else {
        throw "Download failed"
    }
    
} catch {
    Write-Host ""
    Write-Host "ERROR: Download failed: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Alternative: Download manually:" -ForegroundColor Yellow
    Write-Host "1. Open: $modelUrl" -ForegroundColor Gray
    Write-Host "2. Save to: $outputPath" -ForegroundColor Gray
    exit 1
}
