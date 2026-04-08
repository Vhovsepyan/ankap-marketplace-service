@echo off
:: Navigate to the parent directory
cd /d %~dp0\..

echo Compiling order-service with Maven...
:: -pl targets just the order-service module. -am builds any dependencies it needs.
call mvn clean package -pl order-service -am -DskipTests

echo.
echo Rebuilding and starting order-service container...
docker compose up -d --build order-service

echo.
echo Order service successfully built and started!
