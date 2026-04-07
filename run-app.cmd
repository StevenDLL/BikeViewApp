@echo off
setlocal
if defined JAVA_HOME goto run
for %%D in (
    "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot"
    "C:\Program Files\Eclipse Adoptium\jdk-21.0.6.7-hotspot"
    "C:\Program Files\Eclipse Adoptium\jdk-23.0.2.7-hotspot"
    "C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot"
    "C:\Program Files\Java\jdk-24"
) do (
    if exist "%%~D\bin\java.exe" (
set "JAVA_HOME=%%~D"
goto run
    )
)
echo JAVA_HOME is not set and no supported JDK was found automatically.
echo Install Java 17+ or set JAVA_HOME before launching the app.
exit /b 1
:run
echo Using JAVA_HOME=%JAVA_HOME%
call "%~dp0mvnw.cmd" clean javafx:run