@echo off
REM ============================================
REM  PathPilot FX - Build JAR Script
REM ============================================

cd /d %~dp0

REM --- Configure your JavaFX SDK path here ---
set JAVAFX_SDK=C:\Users\singh\Downloads\openjfx-24.0.2_windows-x64_bin-sdk\javafx-sdk-24.0.2\lib

echo === Building PathPilotFX ===

REM Step 1: Compile Java source
echo Compiling...
javac --module-path "%JAVAFX_SDK%" --add-modules javafx.controls -cp "lib/*" PathPilotFX.java
if errorlevel 1 (
    echo [ERROR] Compilation failed.
    pause
    exit /b
)

REM Step 2: Create manifest
echo Main-Class: PathPilotFX > manifest.txt

REM Step 3: Package into JAR
jar cmf manifest.txt PathPilotFX.jar PathPilotFX*.class

if errorlevel 1 (
    echo [ERROR] JAR creation failed.
    pause
    exit /b
)

echo [SUCCESS] Build complete: PathPilotFX.jar
echo Run it with: java --module-path "%JAVAFX_SDK%" --add-modules javafx.controls -cp "PathPilotFX.jar;lib/*" PathPilotFX
pause
