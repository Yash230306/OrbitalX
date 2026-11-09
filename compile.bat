@echo off
cd /d "%~dp0"
setlocal EnableDelayedExpansion
if not exist out mkdir out
if exist out\sources.args del out\sources.args
for /r src\main\java %%f in (*.java) do (
    set "SOURCE=%%f"
    set "SOURCE=!SOURCE:\=/!"
    echo "!SOURCE!">>out\sources.args
)
javac --release 17 -encoding UTF-8 -d out @out\sources.args
if errorlevel 1 exit /b 1
echo Compiled OK
