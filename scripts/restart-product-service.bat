@echo off

set SCRIPTS_DIR=%~dp0

echo Restarting Product Service...

call "%SCRIPTS_DIR%stop-product-service.bat"
call "%SCRIPTS_DIR%start-product-service.bat"

echo Restart sequence complete!