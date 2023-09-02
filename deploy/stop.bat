@echo off
SET port=8080
for /f "usebackq tokens=1-5" %%a in (`netstat -ano ^| findstr %port%`) do (
    if [%%d] EQU [LISTENING] (
        set pid=%%e
    )
)
echo close : %port%  %pid%
if not "%pid%" == "" taskkill /f /pid %pid%

exit