@echo off
set JAR_HOME=%~dp0
start javaw -jar %JAR_HOME%\weight-1.0-SNAPSHOT.jar --spring.profiles.active=prod
exit

@REM @ECHO OFF
@REM %1 start mshta vbscript:createobject("wscript.shell").run("""%~0"" ::",0)(window.close)&&exit
@REM start /b java -jar weight-1.0-SNAPSHOT.jar --spring.profiles.active=prod