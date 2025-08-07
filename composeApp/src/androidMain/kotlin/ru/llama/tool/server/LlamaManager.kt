package ru.llama.tool.server

import android.content.Context
import java.io.File

class LlamaManager(private val context: Context) {

    private var modelLoaded = false
    private val libFile = File(context.filesDir, "jniLibs/arm64-v8a/libllama.so")

    init {
        System.loadLibrary("native-lib") // Твой JNI-обёртку (напишем ниже)
    }

    /**
     * Копирует libllama.so из assets в internal storage
     */
    private fun installLibrary() {
        if (libFile.exists()) return

        context.assets.open("jniLibs/arm64-v8a/libllama.so").use { input ->
            libFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        libFile.setExecutable(true, true)
    }

    /**
     * Загружает модель через нативный код
     * @param modelPath — путь к .gguf файлу
     * @return true, если модель загружена
     */
    fun loadModel(modelPath: String): Boolean {
        try {
            installLibrary()
            System.load(libFile.absolutePath) // Загружаем libllama.so

            modelLoaded = nativeLoadModel(modelPath)
            return modelLoaded
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * Генерирует ответ на промпт
     * @param prompt — входной текст
     * @param maxTokens — макс. длина ответа
     * @return сгенерированный текст или null
     */
    fun generate(prompt: String, maxTokens: Int = 256): String? {
        return if (modelLoaded) {
            nativeGenerate(prompt, maxTokens)
        } else {
            null
        }
    }

    /**
     * Освобождает ресурсы модели
     */
    fun unloadModel() {
        if (modelLoaded) {
            nativeUnloadModel()
            modelLoaded = false
        }
    }

    /**
     * Проверяет, загружена ли модель
     */
    fun isModelLoaded(): Boolean = modelLoaded

    // ——————— Нативные методы (JNI) ———————
    private external fun nativeLoadModel(modelPath: String): Boolean
    private external fun nativeGenerate(prompt: String, maxTokens: Int): String?
    private external fun nativeUnloadModel()
}