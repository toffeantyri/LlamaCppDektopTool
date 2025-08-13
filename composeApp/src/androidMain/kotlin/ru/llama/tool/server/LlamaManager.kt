package ru.llama.tool.server

import android.content.Context
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.llama.tool.MainActivity
import java.io.File

private const val LLAMA_LOG = "LLAMA_LOG"


class LlamaManager(private val context: Context) {

    private var modelLoaded = false

    init {
        System.loadLibrary("native-lib") // Твой JNI-обёртку (напишем ниже)
    }

    init {
        println(LLAMA_LOG + getModelInfo())
        (context as MainActivity).lifecycleScope.launch {
            val modelPathResult = context.copyModelFromAssets()
            modelPathResult.onSuccess { modelPath ->
                Log.i(LLAMA_LOG, "LOAD MODEL success")
                loadModel(modelPath)
                println(LLAMA_LOG + "ModelInfo" + getModelInfo())

                withContext(Dispatchers.Default) {
                    val result = generateText("What is your name?", 1000)
                    Log.d(LLAMA_LOG, "gen RESULT $result")
                }
            }.onFailure { error ->
                Log.i(LLAMA_LOG, "LOAD MODEL ERROR $error")
            }
        }

        println(LLAMA_LOG + getModelInfo())
    }


    private suspend fun Context.copyModelFromAssets(): Result<String> =
        withContext(Dispatchers.IO) {
            val modelFileName = "model.gguf"
            val outFile = File(filesDir, modelFileName)

            if (outFile.exists()) {
                Log.i(LLAMA_LOG, "Model already exists: ${outFile.absolutePath}")
                return@withContext Result.success(outFile.absolutePath)
            }

            try {
                Log.i(LLAMA_LOG, "Copying model from assets to ${outFile.absolutePath}")
                assets.open(modelFileName).use { input ->
                    outFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                Log.i(LLAMA_LOG, "Model copied successfully")
                Result.success(outFile.absolutePath)
            } catch (e: Exception) {
                Log.e(LLAMA_LOG, "Failed to copy model from assets", e)
                Result.failure(e)
            }
        }

    fun isModelLoaded(): Boolean = modelLoaded

    // ——————— Нативные методы (JNI) ———————
    private external fun stringFromJNI(): String
    private external fun getModelInfo(): String
    private external fun unloadModel()
    private external fun generateText(prompt: String, maxTokens: Int): String
    private external fun loadModel(modelPath: String): Boolean

}