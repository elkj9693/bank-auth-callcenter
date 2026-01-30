# Premium Startup Script for Windows

Write-Host ">>> Starting Infrastructure (Docker)..." -ForegroundColor Cyan
docker-compose -p premium-bank up -d

Write-Host ">>> Starting Backends (WAS)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'continue-bank/continue-was'; ./gradlew bootRun"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'auth-trustee/auth-was'; ./gradlew bootRun"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'callcenter-trustee/callcenter-was'; ./gradlew bootRun"

Write-Host ">>> Starting Frontends (Web)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'continue-bank/continue-web'; npm install; npm run dev"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'auth-trustee/auth-web'; npm install; npm run dev"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'callcenter-trustee/callcenter-web'; npm install; npm run dev"

Write-Host ">>> All services are starting in separate windows." -ForegroundColor Green
