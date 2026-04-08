@echo off
:: Navigate to the parent directory
cd /d %~dp0\..

echo.
echo Stopping old product-service container (if running)...
docker compose stop product-service
docker compose rm -f product-service
echo Product service successfully stopped!

