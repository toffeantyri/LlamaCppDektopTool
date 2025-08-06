package ru.llama.tool.server

import android.content.Context
import java.io.File

class LlamaServerManager(private val context: Context) {

    private var process: Process? = null
    private val executableFile = File(context.filesDir, "llama_server.so")

    /**
     * Копирует бинарник из assets в filesDir и делает исполняемым
     */
    private fun installBinary() {
        if (executableFile.exists()) return

        context.assets.open("llama_server.so").use { input ->
            executableFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        executableFile.setExecutable(true, true)
    }

    /**
     * Запускает llama-server с моделью
     * @param modelPath — полный путь к .gguf файлу
     */
    fun startServer(modelPath: String): Boolean {
        try {
            installBinary()

            val builder = ProcessBuilder(
                executableFile.absolutePath,
                "--model", modelPath,
                "--port", "8080",
                "--host", "127.0.0.1",
                "--n-gpu-layers", "0" // CPU-only, можно увеличить при поддержке GPU
            )
            builder.directory(context.filesDir) // рабочая директория
            builder.redirectErrorStream(true)

            process = builder.start()

            // Логируем вывод сервера
            Thread {
                process?.inputStream?.bufferedReader()?.use { reader ->
                    reader.forEachLine { line ->
                        println("LLaMAa: $line")
                    }
                }
            }.start()

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * Останавливает сервер
     */
    fun stopServer() {
        process?.destroy()
        process = null
    }

    /**
     * Проверяет, запущен ли сервер
     */
    fun isRunning(): Boolean = process?.isAlive == true
}