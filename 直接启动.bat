@echo off
title 高校竞赛管理系统 - 直接启动

echo.
echo ============================================
echo   高校竞赛管理系统 - 直接启动（免编译）
echo ============================================
echo.
echo 正在启动服务...

cd /d "%~dp0backend"
if not exist "%~dp0jingsai-1.0.0.jar" (
    echo [错误] 未找到 jingsai-1.0.0.jar，请确认文件存在！
    pause
    exit /b 1
)

start "高校竞赛管理系统" java -jar "%~dp0jingsai-1.0.0.jar"

echo 服务启动中，请稍候...
echo.
echo 测试账号：
echo   学生: 1001 / 123456
echo   教师: T1001 / 123456
echo   管理员: admin / 123456
echo.
echo 服务启动后浏览器访问: http://localhost:8080
echo.
echo 按任意键关闭本窗口...
pause >/dev/null
