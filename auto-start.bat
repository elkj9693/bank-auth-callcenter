@echo off
chcp 65001 > nul
echo ===========================================
echo   Premium Bank System - Auto Starter
echo ===========================================

echo [1/3] Starting Database Containers...
docker-compose -p premium-bank up -d
if %errorlevel% neq 0 (
    echo Docker startup failed. Please check Docker Desktop.
    pause
    exit /b
)

echo [2/3] Starting Backend Services (WAS)...
start "Continue Bank WAS (8081)" cmd /k "cd continue-bank\continue-was && gradlew bootRun"
start "Auth Trustee WAS (8082)" cmd /k "cd auth-trustee\auth-was && gradlew bootRun"
start "Call Center WAS (8080)" cmd /k "cd callcenter-trustee\callcenter-was && gradlew bootRun"

echo [3/3] Starting Frontend Services (Web)...
start "Continue Web (5174)" cmd /k "cd continue-bank\continue-web && npm install && npm run dev"
start "Auth Web (5173)" cmd /k "cd auth-trustee\auth-web && npm install && npm run dev"
start "Call Center Web (5175)" cmd /k "cd callcenter-trustee\callcenter-web && npm install && npm run dev"

echo ===========================================
echo   All systems validated and starting...
echo   Please wait for the Spring Boot apps to initialize.
echo ===========================================
pause
