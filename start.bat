@echo off
title Jingsai System

echo.
echo ============================================
echo   University Competition Management System
echo ============================================
echo.

cd /d "%~dp0backend"

if not exist "%~dp0backend\target\jingsai-1.0.0.jar" (
    echo [ERROR] backend\target\jingsai-1.0.0.jar not found!
    echo Please build first: cd backend ^&^& mvn clean package -DskipTests
    pause
    exit /b 1
)

echo Checking Java...
java -version 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java not found! Please install JDK 17 or later.
    pause
    exit /b 1
)

echo.
echo Starting server...
echo Log will be saved to app.log
echo.

java -jar "%~dp0backend\target\jingsai-1.0.0.jar" > app.log 2>&1

echo.
echo ============================================
echo Server stopped. Check app.log for errors.
echo ============================================
pause
