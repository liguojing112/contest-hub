@echo off
title MySQL Connection Check

echo.
echo ============================================
echo   MySQL Connection Diagnostic Tool
echo ============================================
echo.

set MYSQL=

REM Check PATH first
where mysql >nul 2>&1
if %ERRORLEVEL%==0 (
    set MYSQL=mysql
    goto :found
)

REM Check common install paths
for %%d in (
    "C:\Program Files\MySQL\MySQL Server 8.0\bin"
    "C:\Program Files\MySQL\MySQL Server 8.4\bin"
    "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin"
    "C:\xampp\mysql\bin"
    "D:\xampp\mysql\bin"
    "C:\phpstudy\MySQL\bin"
    "D:\phpstudy\MySQL\bin"
    "C:\Program Files\MariaDB 10.11\bin"
) do (
    if exist %%d\mysql.exe (
        set "MYSQL=%%d\mysql.exe"
        goto :found
    )
)

REM Search D:\ and C:\ for mysql.exe
echo Searching for mysql.exe...
for /f "delims=" %%f in ('dir /s /b "D:\mysql.exe" 2^>nul ^| findstr /i "bin\\mysql.exe$"') do (
    set "MYSQL=%%f"
    goto :found
)
for /f "delims=" %%f in ('dir /s /b "C:\mysql.exe" 2^>nul ^| findstr /i "bin\\mysql.exe$"') do (
    set "MYSQL=%%f"
    goto :found
)

echo.
echo Can't find mysql.exe automatically.
echo.
echo Please search for mysql.exe in File Explorer,
echo then drag the file into this window, or type its full path:
echo.
set /p MYSQL_PATH="mysql.exe path: "
if exist "%MYSQL_PATH%" (
    set "MYSQL=%MYSQL_PATH%"
    goto :test
)
echo File not found, please try again.
pause
exit /b 1

:test

:found
echo MySQL client: %MYSQL%
echo.

sc query MySQL80 >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [WARN] MySQL80 service is not running!
    echo Run: net start MySQL80
    echo.
)

echo Testing connections...
echo.

echo [Test 1] No password...
%MYSQL% -u root -e "SELECT 'OK' AS result;" 2>nul
if %ERRORLEVEL%==0 (
    echo [OK] MySQL root has NO password!
    echo.
    echo Run this to set password to root123:
    echo   %MYSQL% -u root -e "ALTER USER 'root'@'localhost' IDENTIFIED BY 'root123'; FLUSH PRIVILEGES;"
    echo Then double-click start.bat
    echo.
    pause
    exit /b 0
)
echo [FAIL]

echo [Test 2] Password: root123...
%MYSQL% -u root -proot123 -e "SELECT 'OK' AS result;" 2>nul
if %ERRORLEVEL%==0 (
    echo [OK] Password is root123 - matches project config!
    echo Just double-click start.bat to launch.
    echo.
    pause
    exit /b 0
)
echo [FAIL]

echo [Test 3] Password: 123456...
%MYSQL% -u root -p123456 -e "SELECT 'OK' AS result;" 2>nul
if %ERRORLEVEL%==0 (
    echo [OK] Password is 123456!
    echo.
    echo To start: run "start.bat 123456" or edit backend\application.yml
    echo.
    pause
    exit /b 0
)
echo [FAIL]

echo [Test 4] Password: root...
%MYSQL% -u root -proot -e "SELECT 'OK' AS result;" 2>nul
if %ERRORLEVEL%==0 (
    echo [OK] Password is root!
    echo.
    echo To start: run "start.bat root" or edit backend\application.yml
    echo.
    pause
    exit /b 0
)
echo [FAIL]

echo.
echo ============================================
echo   All common passwords FAILED!
echo ============================================
echo.
echo You need to reset your MySQL root password.
echo.
echo Method: Use MySQL Installer or Workbench to reset,
echo or run the following commands as administrator:
echo   1. Stop MySQL80 service
echo   2. Run: mysqld --skip-grant-tables --console
echo   3. In another cmd: mysql -u root
echo      FLUSH PRIVILEGES;
echo      ALTER USER 'root'@'localhost' IDENTIFIED BY 'root123';
echo   4. Restart MySQL80 service
echo.
pause
