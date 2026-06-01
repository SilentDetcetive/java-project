@echo off
chcp 65001 >nul
cd /d "F:\code\VS code\Java\hotel system"

echo ================================
echo   StayHub 酒店管理系统
echo ================================
echo.
echo 正在编译...

javac *.java -encoding UTF-8
if %errorlevel% neq 0 (
    echo.
    echo 编译失败，请检查错误信息。
    pause
    exit /b %errorlevel%
)

echo 编译成功，正在启动...
echo.
start "" javaw -Dfile.encoding=UTF-8 StayHubUI
