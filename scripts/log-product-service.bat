@echo off
:: Navigate to the parent directory
cd /d %~dp0\..

echo.
docker compose logs product-service

