@echo off
title 高校竞赛管理系统

echo ========================================
echo    高校竞赛管理系统 - 启动脚本
echo ========================================
echo.

echo [1] 检查 Java 环境...
java -version >/dev/null 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 Java，请安装 JDK 17 或更高版本
    pause
    exit /b 1
)
echo [i] Java 环境正常

echo.
echo [2] 检查 Maven...
mvn -version >/dev/null 2>&1
if %errorlevel% neq 0 (
    echo [错误] 未找到 Maven，请先安装 Maven
    pause
    exit /b 1
)
echo [i] Maven 环境正常

echo.
echo [3] 构建项目...
cd /d "%~dp0backend"
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 (
    echo [错误] 项目构建失败，请检查错误信息
    cd /d "%~dp0"
    pause
    exit /b 1
)
echo [i] 项目构建成功

echo.
echo [4] 启动后端服务...
start "高校竞赛管理系统-后端" java -jar "%~dp0backend	arget\jingsai-1.0.0.jar"
echo [i] 后端服务启动中，请稍候...

echo.
echo [5] 等待服务就绪...
timeout /t 8 /nobreak >/dev/null

echo.
echo ========================================
echo    服务状态
echo ========================================
echo.
echo    系统首页:   http://localhost:8080
echo    H2控制台:   http://localhost:8080/h2-console
echo    API基础路径: http://localhost:8080/api
echo.
echo    测试账号 (密码均为 123456):
echo    - 管理员: admin
echo    - 教师:   T1001 / T1002 / T1003
echo    - 学生:   1001 / 1002 / 1003
echo.
echo ========================================

echo [i] 正在打开浏览器...
start "" "http://localhost:8080"

echo.
echo 按任意键关闭此窗口 (后端服务继续运行)...
pause >/dev/null
