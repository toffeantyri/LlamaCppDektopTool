This is a Kotlin Multiplatform project targeting Android & Desktop.

Connection with llama-server using Ktor SSE.
Saving dialogs using Room.
Saving application settings and dialog settings using DataStore.


desktop: open terminal/command line and run llama-server from llama.cpp as rest api (127.0.0.1:8080 by default)

android: open termux and run llama-server from llama.cpp as rest api (127.0.0.1:8080 by default)

Gradle run configuration : "composeApp:jvmRun -DmainClass=ru/llama/tool/MainKt --quiet"

if need desktop exe file: 
android studio: terminal: ./gradlew composeApp:runDistributable (looks logs see path)