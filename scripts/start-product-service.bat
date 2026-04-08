@echo off
:: Navigate to the parent directory
cd /d %~dp0\..

echo Compiling product-service with Maven...
:: -pl targets just the product-service module. -am builds any dependencies it needs.
call mvn clean package -pl product-service -am -DskipTests

echo.
echo Rebuilding and starting product-service container...
docker compose up -d --build product-service

echo.
echo Product service successfully built and started!
