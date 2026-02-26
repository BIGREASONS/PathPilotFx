@echo off
REM ============================================
REM  PathPilot FX - Compile and Run Script
REM ============================================
REM  Set JAVAFX_SDK to your JavaFX SDK lib folder
REM  Download from: https://openjfx.io/
REM ============================================

cd /d %~dp0

REM --- Configure your JavaFX SDK path here ---
set JAVAFX_SDK=C:\Users\singh\Downloads\openjfx-24.0.2_windows-x64_bin-sdk\javafx-sdk-24.0.2\lib

echo Compiling PathPilotFX.java...
javac --module-path "%JAVAFX_SDK%" --add-modules javafx.controls -cp "lib/*" PathPilotFX.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed!
    pause
    exit /b
)

echo Running PathPilotFX...
java --module-path "%JAVAFX_SDK%" --add-modules javafx.controls -cp ".;lib/*" PathPilotFX

pause
