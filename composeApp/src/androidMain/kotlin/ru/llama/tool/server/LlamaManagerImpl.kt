package ru.llama.tool.server

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.llama.tool.MainActivity
import ru.llama.tool.data.server.ILlamaManager
import java.io.File

private const val LLAMA_LOG = "LLAMA_LOG"


class LlamaManagerImpl(private val context: Context) : ILlamaManager {

    private var isModelLoaded = false

    private var cachedModelInfo: ILlamaManager.ModelInfo? = null

    private var internalModelPath: String = ""


    init {
        System.loadLibrary("native-lib") // Твой JNI-обёртку (напишем ниже)
    }

    init {
        println(LLAMA_LOG + getModelInfo())
        (context as MainActivity).lifecycleScope.launch {
            val modelFileName = "asset://model.gguf"
            loadLangModelFromUri(modelFileName.toUri())
        }
        println(LLAMA_LOG + getModelInfo())
    }


    private suspend fun Context.copyModelFromAssets(
        sourcePath: String,
        targetFile: File
    ): Result<String> = withContext(Dispatchers.IO) {
        if (targetFile.exists()) {
            Log.i(LLAMA_LOG, "Model already exists: ${targetFile.absolutePath}")
            return@withContext Result.success(targetFile.absolutePath)
        }
        try {
            Log.i(LLAMA_LOG, "Copying model from assets to ${targetFile.absolutePath}")
            assets.open(sourcePath).use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Log.i(LLAMA_LOG, "Model copied successfully")
            Result.success(targetFile.absolutePath)
        } catch (e: Exception) {
            Log.e(LLAMA_LOG, "Failed to copy model from assets", e)
            Result.failure(e)
        }
    }

    // В androidMain - расширение для Context
    private fun Context.copyModelFromFile(sourcePath: String, targetFile: File) {
        val sourceFile = File(sourcePath)
        sourceFile.inputStream().use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    // Или более универсальный метод:
    private fun Context.copyModelFromUri(uri: Uri, targetFile: File) {
        val inputStream = contentResolver.openInputStream(uri) ?: return
        inputStream.use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    // Обновленная реализация loadLangModel для Android
    @SuppressLint("UseKtx")
    override suspend fun loadLangModel(sourcePath: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val fileName = sourcePath.substringAfterLast("/")
                val targetFile = File(context.filesDir, "models/$fileName")

                if (!targetFile.exists()) {
                    targetFile.parentFile?.mkdirs()

                    when {
                        // Если путь начинается с "content://" - это URI
                        sourcePath.startsWith("content://") -> {
                            val uri = sourcePath.toUri()
                            context.copyModelFromUri(uri, targetFile)
                        }
                        // Если путь начинается с "asset://" - из assets
                        sourcePath.startsWith("asset://") -> {
                            val assetPath = sourcePath.substringAfter("asset://")
                            context.copyModelFromAssets(assetPath, targetFile)
                        }
                        // Иначе обычный файловый путь
                        else -> {
                            context.copyModelFromFile(sourcePath, targetFile)
                        }
                    }
                }

                internalModelPath = targetFile.absolutePath
                cachedModelInfo = ILlamaManager.ModelInfo(
                    modelName = fileName,
                    modelPath = internalModelPath,
                    fileSize = targetFile.length(),
                    isLoaded = false
                )

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Альтернативный метод для работы с Uri напрямую (может быть полезен)
    suspend fun loadLangModelFromUri(uri: Uri): Result<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val fileName = uri.lastPathSegment ?: "model.gguf"
                val targetFile = File(context.filesDir, "models/$fileName")

                if (!targetFile.exists()) {
                    targetFile.parentFile?.mkdirs()
                    context.copyModelFromUri(uri, targetFile)
                }

                internalModelPath = targetFile.absolutePath
                cachedModelInfo = ILlamaManager.ModelInfo(
                    modelName = fileName,
                    modelPath = internalModelPath,
                    fileSize = targetFile.length(),
                    isLoaded = false
                )

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun initialize(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            // Инициализация llama.cpp с internalModelPath
            // Вызов нативного кода
            loadModel(internalModelPath)
            isModelLoaded = true
            cachedModelInfo = cachedModelInfo?.copy(isLoaded = true)
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
            // Вызов нативного кода для генерации
            val result = generateText(prompt, options.maxTokens)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun release(): Result<Unit> {
        return try {
            // Освобождение ресурсов
            unloadModel()
            isModelLoaded = false
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isModelLoaded(): Boolean = isModelLoaded

    override fun getLangModelInfo(): ILlamaManager.ModelInfo? = cachedModelInfo

    // ——————— Нативные методы (JNI) ———————
    private external fun stringFromJNI(): String
    private external fun getModelInfo(): String
    private external fun unloadModel()
    private external fun generateText(prompt: String, maxTokens: Int): String
    private external fun loadModel(modelPath: String): Boolean

}