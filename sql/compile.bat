
set OUTPUT=output.sql
if exist %OUTPUT% DEL /F %OUTPUT%
for /R %%f in (*.sql) do (
    for %%d in (%%~pf.) do set FOLDER=%%~nxd
    echo -------------------------- >> %OUTPUT%
    echo -- %FOLDER%\%%~nxf >> %OUTPUT%
    echo -------------------------- >> %OUTPUT%
    echo. >> %OUTPUT%
    type "%%f" >> %OUTPUT%
)

