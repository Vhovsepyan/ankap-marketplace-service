@echo off
echo 🐳 Restarting Ankap Marketplace Infrastructure...

echo.
echo 🛑 Stopping and removing existing containers...
docker compose down

echo.
echo 🚀 Starting containers in the background...
docker compose up -d

echo.
echo 📋 Current container status:
docker ps

echo.
echo ✅ Docker infrastructure successfully restarted!
pause