# 🧠 LlamaTool — Local LLM Client for Android & Desktop

![1000064834](https://github.com/user-attachments/assets/6ee3f184-8ad0-455e-9838-7ba007e255f8) ![1000064837](https://github.com/user-attachments/assets/600f7604-1baa-442f-a771-069bfd3c362b)
![1000064838](https://github.com/user-attachments/assets/441c8d06-b7e4-4bb9-a02f-62360d8f2e25) ![1000064836](https://github.com/user-attachments/assets/203fc67b-162e-4d48-801e-307adf4932ac)


A Kotlin Multiplatform desktop and mobile application for interacting with **locally running LLMs** via `llama-server` from [`llama.cpp`](https://github.com/ggerganov/llama.cpp).  
Run large language models **completely offline** on your device — no internet, no cloud, no privacy concerns.

Perfect for developers, researchers, and privacy-focused users who want full control over their AI experience.

> 🔒 Fully offline. Your data never leaves your device.

---

## 🚀 Features

- ✅ **Cross-platform**: Runs on **Android** and **Desktop** (JVM) from a single codebase
- 💬 **Chat interface**: Save and manage conversations with persistent dialogs
- 🔄 **SSE Streaming**: Real-time token streaming via Server-Sent Events (Ktor + SSE)
- 🗂️ **Local Storage**:
  - Dialogs saved with **Room Database** (Android)
  - App & dialog settings stored in **DataStore** (Android) and **Preferences** (Desktop)
- 🌐 **Local API Only**: Connects to `llama-server` running on `127.0.0.1:8080`
- 🛠️ **No backend required**: Works entirely offline after setup

---

## 📱 Use Cases

- Run private, secure AI assistants on your phone or laptop
- Experiment with open-source LLMs (e.g. LLaMA, Qwen, Mistral, Phi)
- Learn how LLMs work without relying on cloud APIs
- Develop custom prompts, fine-tuned workflows, or role-playing bots
- Ideal for environments with no internet or high privacy requirements

---

## 🖥️ Supported Platforms

| Platform  | Supported | Notes |
|---------|-----------|-------|
| Android | ✅ Yes    | Requires Termux to run `llama-server` |
| Desktop (JVM) | ✅ Yes | Runs on Windows, Linux |

Built with **Kotlin Multiplatform**, **Jetpack Compose**, and **Ktor**.

---

## ⚙️ How It Works

The app **does not run the model itself**. Instead, it acts as a lightweight client that:

1. Connects to `llama-server` (from `llama.cpp`) via HTTP
2. Sends prompts and receives responses using **Server-Sent Events (SSE)** for smooth streaming
3. Saves chat history and settings locally
4. Provides a clean, responsive UI on both mobile and desktop

You must run `llama-server` separately on your device.

---

## 📦 Requirements

### For Android:
- Android device (7.0+)
- [Termux](https://f-droid.org/packages/com.termux/) (from F-Droid)
- At least 4–6 GB RAM (depending on model)
- Sufficient storage (5–10 GB)

### For Desktop:
- Java 17+ installed
- tested OS (Windows, Linux)
- `llama-server` running locally (can be built on the same machine or WSL)

---

## 🛠️ Setup `llama-server` (Prerequisite)

> The app requires `llama-server` to be running at `http://127.0.0.1:8080`.



## 💬 For dev
Gradle run configuration : "composeApp:jvmRun -DmainClass=ru/llama/tool/MainKt --quiet"

if need desktop exe file: 
android studio: terminal: ./gradlew composeApp:runDistributable (looks logs see path)


### 🌐 USING 

For android : using Termux.

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
