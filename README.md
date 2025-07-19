This is a Kotlin Multiplatform project targeting Desktop.

Gradle run configuration : "composeApp:jvmRun -DmainClass=ru/llama/tool/MainKt --quiet"

@echo off
cd /d "%~dp0"
llama\llama-server.exe -m models\Q4_0.gguf -c 2048 --temp 0.7 --top-k 40 --parallel 4
pause
