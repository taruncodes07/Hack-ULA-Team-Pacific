# Download AI Model Script
# This script downloads the SmolLM2 360M Q8_0 model from HuggingFace

$modelUrl = "https://huggingface.co/prithivMLmods/SmolLM2-360M-GGUF/resolve/main/SmolLM2-360M.Q8_0.gguf"
$outputPath = "app/src/main/assets/models/SmolLM2-360M.Q8_0.gguf"

Write-Host "Downloading SmolLM2 360M Q8_0 Model..." -ForegroundColor Cyan
Write-Host "Source: $modelUrl" -ForegroundColor Gray
Write-Host "Destination: $outputPath" -ForegroundColor Gray
Write-Host ""

# Check if file already exists
if (Test-Path $outputPath) {
    $fileSize = (Get-Item $outputPath).Length / 1MB
    Write-Host "WARNING: Model file already exists ($([math]::Round($fileSize, 2)) MB)" -ForegroundColor Yellow
    $response = Read-Host "Do you want to re-download? (y/n)"
    if ($response -ne "y" -and $response -ne "Y") {
        Write-Host "SUCCESS: Using existing model file." -ForegroundColor Green
        exit 0
    }
}

# Create directory if it doesn't exist
$directory = Split-Path -Parent $outputPath
if (-not (Test-Path $directory)) {
    New-Item -ItemType Directory -Path $directory -Force | Out-Null
    Write-Host "Created directory: $directory" -ForegroundColor Green
}

# Download the model with progress
try {
    Write-Host "Downloading... (this may take 5-10 minutes, ~119 MB)" -ForegroundColor Yellow
    Write-Host ""
    
    # Use Start-BitsTransfer for better progress display
    Import-Module BitsTransfer
    Start-BitsTransfer -Source $modelUrl -Destination $outputPath -Description "Downloading AI Model" -DisplayName "SmolLM2 360M"
    
    # Verify download
    if (Test-Path $outputPath) {
        $fileSize = (Get-Item $outputPath).Length / 1MB
        Write-Host ""
        Write-Host "SUCCESS: Download complete!" -ForegroundColor Green
        Write-Host "Model size: $([math]::Round($fileSize, 2)) MB" -ForegroundColor Green
        Write-Host "Location: $outputPath" -ForegroundColor Green
        Write-Host ""
        Write-Host "Model is now bundled with your app!" -ForegroundColor Cyan
        Write-Host "Users won't need to download anything." -ForegroundColor Cyan
    } else {
        throw "Download failed - file not found"
    }
    
} catch {
    Write-Host ""
    Write-Host "ERROR: Download failed: $_" -ForegroundColor Red
    Write-Host ""
    Write-Host "Alternative: Try downloading manually:" -ForegroundColor Yellow
    Write-Host "1. Open browser: $modelUrl" -ForegroundColor Gray
    Write-Host "2. Save file to: $outputPath" -ForegroundColor Gray
    exit 1
}
