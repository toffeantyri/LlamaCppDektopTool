This is a Kotlin Multiplatform project targeting Android & Desktop.

### 📝 Description for dev

Connection with llama-server using Ktor SSE.
Saving dialogs using Room.
Saving application settings and dialog settings using DataStore.

desktop: open terminal/command line and run llama-server from llama.cpp as rest api (127.0.0.1:8080 by default)

android: open termux and run llama-server from llama.cpp as rest api (127.0.0.1:8080 by default)

Gradle run configuration : "composeApp:jvmRun -DmainClass=ru/llama/tool/MainKt --quiet"

if need desktop exe file: 
android studio: terminal: ./gradlew composeApp:runDistributable (looks logs see path)

### 🌐 USING 
Installation & Build

### 1. Install required packages

```bash
pkg update && pkg upgrade -y
pkg install git cmake clang wget
```

### 2. Clone `llama.cpp`

```bash
cd ~
git clone https://github.com/ggerganov/llama.cpp
cd llama.cpp
```

### 3. Build `llama-server`

```bash
rm -rf build && mkdir build && cd build

cmake .. \
  -DCMAKE_BUILD_TYPE=Release \
  -DBUILD_SHARED_LIBS=OFF \
  -DGGML_USE_CLBLAST=OFF \
  -DGGML_EXCLUDE_EXAMPLES=ON

cd tools/server
make -j$(nproc)
```

> ✅ After build, the binary will be at:  
> `~/llama.cpp/build/bin/llama-server`

## 📂 Download a GGUF Model

Create a models directory and download a quantized model:

```bash
mkdir -p ~/llama.cpp/models
cd ~/llama.cpp/models
```

### Example: TinyLlama (lightweight, good for low-end devices)

```bash
wget https://huggingface.co/TheBloke/TinyLlama-1.1B-1.6K-OpenOrca-GGUF/resolve/main/tinyllama-1.1b-1.6k-openorca.Q4_K_M.gguf
mv tinyllama-1.1b-1.6k-openorca.Q4_K_M.gguf your_model.gguf
```

> 🔍 More models: [Hugging Face — GGUF](https://huggingface.co/models?search=gguf)  
> Choose quantized models (e.g. `Q4_K_M`) for better performance on mobile.

For example:  Russian adapted model
https://huggingface.co/RefalMachine/RuadaptQwen3-4B-Instruct-GGUF.
Use Q3_K_S or better.

## ▶️ Start the Server

Run the server with:

```bash
~/llama.cpp/build/bin/llama-server -m ~/llama.cpp/models/your_model.gguf --host 127.0.0.1 --port 8080 -t 4 --parallel 4 -c 2048
```

### Parameter explanation:
- `-m` — path to the `.gguf` model file
- `--host 127.0.0.1` — bind to localhost (local access only)
- `--port 8080` — server port
- `-t 4` — number of CPU threads
- `--parallel 4` — max parallel requests
- `-c 2048` — context size in tokens


## ✅ Fully offline:
 No data leaves your device.  
> You have full control over privacy and performance.



## ⚠️ Disclaimer
This software is for educational purposes only. The author disclaims all responsibility 
for any damages or legal issues arising from its use.
