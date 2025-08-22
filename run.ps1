# GigFinder - Development Server Startup Script
# This script starts both backend and frontend servers

Write-Host "🚀 Starting GigFinder Development Servers..." -ForegroundColor Green

# Check if PostgreSQL is running first
Write-Host "📊 Checking PostgreSQL..." -ForegroundColor Yellow
$pgService = Get-Service -Name "postgresql*" -ErrorAction SilentlyContinue
if ($pgService -and $pgService.Status -eq "Running") {
    Write-Host "✅ PostgreSQL is running" -ForegroundColor Green
} else {
    Write-Host "❌ PostgreSQL is not running. Please start it first or run setup-laptop.ps1" -ForegroundColor Red
    exit 1
}

# Start Backend (Spring Boot)
Write-Host "📊 Starting Backend Server (Spring Boot)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "mvn spring-boot:run" -WindowStyle Normal

# Wait a moment for backend to initialize
Start-Sleep -Seconds 5

# Start Frontend (Vite React)
Write-Host "🎨 Starting Frontend Server (Vite + React)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd frontend; npm run dev" -WindowStyle Normal

# Wait and then open browser
Start-Sleep -Seconds 3
Write-Host "🌐 Opening browser..." -ForegroundColor Yellow
Start-Process "http://localhost:5173"

Write-Host "`n✅ Both servers are running!" -ForegroundColor Green
Write-Host "Backend API: http://localhost:8080/api" -ForegroundColor Cyan
Write-Host "Frontend UI: http://localhost:5173" -ForegroundColor Cyan
Write-Host "`n💡 Press Ctrl+C in each terminal to stop the servers" -ForegroundColor Yellow

# Ensure Maven is available (use local tools folder if not installed system-wide)
function Ensure-Maven {
    try {
        & mvn -v | Out-Null
        return "mvn"
    } catch {
        $tools = Join-Path $PSScriptRoot "tools"
        if (!(Test-Path $tools)) { New-Item -ItemType Directory -Force -Path $tools | Out-Null }

        $existing = Get-ChildItem $tools -Directory -Filter "apache-maven-*" -ErrorAction SilentlyContinue | Select-Object -First 1
        if (-not $existing) {
            Write-Host "[GigFinder] Downloading Maven 3.9.9..."
            $zip = Join-Path $tools "maven.zip"
            Invoke-WebRequest -Uri "https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip" -OutFile $zip
            Expand-Archive -LiteralPath $zip -DestinationPath $tools -Force
            Remove-Item $zip -Force
            $existing = Get-ChildItem $tools -Directory -Filter "apache-maven-*" | Select-Object -First 1
        }

        $env:PATH = (Join-Path $existing.FullName "bin") + ";" + $env:PATH
        return (Join-Path $existing.FullName "bin\mvn.cmd")
    }
}

# Optionally ensure DB exists if psql is available
function Ensure-Database {
    $psql = "C:\\Program Files\\PostgreSQL\\17\\bin\\psql.exe"
    if (Test-Path $psql) {
        try {
            Write-Host "[GigFinder] Ensuring database 'gigfinder' exists..."
            $env:PGPASSWORD = $DbPassword
            $exists = & $psql -U $DbUser -h localhost -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname='gigfinder'"
            if (-not $exists) {
                & $psql -U $DbUser -h localhost -d postgres -c "CREATE DATABASE gigfinder;"
            }
        } catch {
            Write-Warning "[GigFinder] Skipping DB ensure step: $($_.Exception.Message)"
        }
    } else {
        Write-Host "[GigFinder] Skipping DB ensure step (psql not found at $psql)."
    }
}

Ensure-Database

$mvnCmd = Ensure-Maven

Write-Host "[GigFinder] Starting Spring Boot on port $Port ..."
& $mvnCmd -q -DskipTests spring-boot:run


