@echo off
:: Navigate to the parent directory (project root)
cd /d %~dp0\..

echo Restarting Ankap Marketplace Infrastructure...

echo.
echo Stopping and removing existing containers...
docker compose down

echo.
echo Building the project...
call mvn clean package -DskipTests

echo.
echo Rebuilding and starting ALL containers...
docker compose up -d --build

echo.
echo Infrastructure successfully restarted!
pause