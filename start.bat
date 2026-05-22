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

echo Starting server...
start "Jingsai" java -jar "%~dp0backend\target\jingsai-1.0.0.jar"

echo Server is starting, please wait...
echo.
echo URL: http://localhost:8080
echo.
echo Accounts (password: 123456):
echo   Admin:   admin
echo   Teacher: T1001
echo   Student: 1001
echo.
pause
