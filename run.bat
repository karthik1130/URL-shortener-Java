@echo off
echo ========================================
echo URL Shortener - Quick Run Script
echo ========================================
echo.

REM Check if gradlew.bat exists
if exist gradlew.bat (
    echo Using Gradle Wrapper...
    echo.
    echo Building project...
    call gradlew.bat build
    if %ERRORLEVEL% NEQ 0 (
        echo Build failed! Please check the errors above.
        pause
        exit /b 1
    )
    echo.
    echo Starting application...
    echo Open http://localhost:8080 in your browser once it starts.
    echo Press Ctrl+C to stop the server.
    echo.
    call gradlew.bat bootRun
) else (
    echo Gradle wrapper not found!
    echo.
    echo Please do one of the following:
    echo 1. Install Gradle and run: gradle wrapper
    echo 2. Or install Gradle and run: gradle bootRun
    echo.
    echo Download Gradle from: https://gradle.org/install/
    echo.
    pause
)
