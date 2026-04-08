@echo off
:: Navigate to the parent directory
cd /d %~dp0\..

echo.
echo Stopping old order-service container (if running)...
docker compose stop order-service
docker compose rm -f order-service
echo Order service successfully stopped!

