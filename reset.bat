@echo off
title Reset Database

echo ============================================
echo   One-Time Database Reset
echo ============================================
echo.
echo This will delete the old database and create
echo fresh test data on next startup.
echo Only run this ONCE if you have no data.
echo.

cd /d "%~dp0backend"

if exist "data\jingsai.mv.db" (
    echo Deleting old database...
    del /f /q "data\jingsai.mv.db"
    del /f /q "data\jingsai.lock.db" 2>nul
    echo Done. Fresh data will be created on next start.
) else (
    echo No existing database found. Fresh start anyway.
)

echo.
echo Starting server...
start "Jingsai" java -jar "%~dp0jingsai-1.0.0.jar"

echo.
echo URL: http://localhost:8080
echo.
echo Accounts (password: 123456):
echo   Admin:   admin
echo   Teacher: T1001
echo   Student: 1001
echo.
pause
