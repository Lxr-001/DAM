@echo off
set WORK_DIR=%1
set PROTECTED_DIR=%WORK_DIR%\ProtectedDLL
if not exist %PROTECTED_DIR% (
    md %PROTECTED_DIR%
)
cd %WORK_DIR%
REM DO DOTNET_REACTOR
for %%s in (*.dll *.exe) do (
DOTNET_REACTOR -file "%%s" -targetfile "ProtectedDLL\%%s"
)