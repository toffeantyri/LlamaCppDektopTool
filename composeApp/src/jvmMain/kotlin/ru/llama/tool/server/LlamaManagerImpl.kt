package ru.llama.tool.server

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.llama.tool.data.server.ILlamaManager
import java.io.File

// Desktop реализация (jvmMain/desktopMain)
class DesktopLlamaManager : ILlamaManager {

    private var isLoaded = false
    private var modelInfo: ILlamaManager.ModelInfo? = null
    private var currentModelPath: String = ""

    override suspend fun loadLangModel(sourcePath: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val modelFile = File(sourcePath)
                if (!modelFile.exists()) {
                    return@withContext Result.failure(IllegalArgumentException("Model file not found: $sourcePath"))
                }

                currentModelPath = sourcePath
                modelInfo = ILlamaManager.ModelInfo(
                    modelName = modelFile.name,
                    modelPath = sourcePath,
                    fileSize = modelFile.length(),
                    isLoaded = false
                )

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun initialize(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Инициализация llama.cpp с путем к файлу
            //todo initializeNative(currentModelPath)
            isLoaded = true
            modelInfo = modelInfo?.copy(isLoaded = true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generate(
        prompt: String,
        options: ILlamaManager.GenerationOptions
    ): Result<String> {
        return try {
            // Вызов нативного кода или процесса
            val result = "" // todo generateNative(prompt, options)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun release(): Result<Unit> {
        return try {
            // Освобождение ресурсов
//            releaseNative() todo
            isLoaded = false
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isModelLoaded(): Boolean = isLoaded

    override fun getLangModelInfo(): ILlamaManager.ModelInfo? = modelInfo


}