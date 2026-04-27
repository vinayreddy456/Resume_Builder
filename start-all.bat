@echo off
echo AI Resume Optimizer - Starting Full Application
echo ============================================
echo.

REM Check if Java is available
java -version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 from: https://adoptium.net/
    pause
    exit /b 1
)

REM Check if Node.js is available
node --version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ERROR: Node.js is not installed or not in PATH
    echo Please install Node.js from: https://nodejs.org/
    pause
    exit /b 1
)

echo Prerequisites check passed!
echo.

echo Starting Backend Server...
echo Backend will be available at: http://localhost:8080
echo.
start "Backend Server" cmd /k "mvn spring-boot:run"

echo Waiting for backend to start...
timeout /t 10 /nobreak >nul

echo.
echo Starting Frontend Development Server...
echo Frontend will be available at: http://localhost:3000
echo.
cd frontend
start "Frontend Server" cmd /k "npm start"

echo.
echo ============================================
echo AI Resume Optimizer is starting up...
echo.
echo Access Points:
echo - Frontend: http://localhost:3000
echo - Backend API: http://localhost:8080/api
echo - H2 Console: http://localhost:8080/api/h2-console
echo.
echo Please wait a moment for both services to fully start.
echo.

pause
