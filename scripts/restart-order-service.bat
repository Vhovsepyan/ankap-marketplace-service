@echo off

set SCRIPTS_DIR=%~dp0

echo Restarting Order Service...

call "%SCRIPTS_DIR%stop-order-service.bat"
call "%SCRIPTS_DIR%start-order-service.bat"

echo Restart sequence complete!