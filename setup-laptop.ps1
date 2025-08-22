# GigFinder - Laptop Setup Script
# Run this script to set up the project on your laptop

Write-Host "🚀 Setting up GigFinder on your laptop..." -ForegroundColor Green

# Check if PostgreSQL is running
Write-Host "📊 Checking PostgreSQL service..." -ForegroundColor Yellow
$pgService = Get-Service -Name "postgresql*" -ErrorAction SilentlyContinue
if ($pgService) {
    if ($pgService.Status -eq "Running") {
        Write-Host "✅ PostgreSQL is running" -ForegroundColor Green
    } else {
        Write-Host "⚠️  Starting PostgreSQL service..." -ForegroundColor Yellow
        Start-Service $pgService.Name
    }
} else {
    Write-Host "❌ PostgreSQL service not found. Please install PostgreSQL first." -ForegroundColor Red
    exit 1
}

# Create database if it doesn't exist
Write-Host "🗄️  Setting up database..." -ForegroundColor Yellow
$createDbScript = @"
DO `$`$
BEGIN
   IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'gigfinder') THEN
      CREATE DATABASE gigfinder;
   END IF;
END
`$`$;
"@

try {
    # Try to connect and create database
    psql -U postgres -c $createDbScript
    Write-Host "✅ Database 'gigfinder' is ready" -ForegroundColor Green
} catch {
    Write-Host "⚠️  Please ensure PostgreSQL is installed and accessible with user 'postgres'" -ForegroundColor Yellow
}

# Install backend dependencies
Write-Host "📦 Installing backend dependencies..." -ForegroundColor Yellow
if (Test-Path "pom.xml") {
    mvn clean install -DskipTests
    Write-Host "✅ Backend dependencies installed" -ForegroundColor Green
} else {
    Write-Host "❌ pom.xml not found in current directory" -ForegroundColor Red
}

# Install frontend dependencies
Write-Host "🎨 Installing frontend dependencies..." -ForegroundColor Yellow
if (Test-Path "frontend/package.json") {
    Set-Location frontend
    npm install
    Set-Location ..
    Write-Host "✅ Frontend dependencies installed" -ForegroundColor Green
} else {
    Write-Host "❌ frontend/package.json not found" -ForegroundColor Red
}

Write-Host "`n🎉 Setup complete! Next steps:" -ForegroundColor Green
Write-Host "1. Start backend: mvn spring-boot:run" -ForegroundColor Cyan
Write-Host "2. Start frontend: cd frontend && npm run dev" -ForegroundColor Cyan
Write-Host "3. Open browser: http://localhost:5173" -ForegroundColor Cyan
Write-Host "`n💡 Use the run.ps1 script to start both services together" -ForegroundColor Yellow
