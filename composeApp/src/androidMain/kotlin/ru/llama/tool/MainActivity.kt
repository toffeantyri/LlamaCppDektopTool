package ru.llama.tool

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.arkivanov.decompose.retainedComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.llama.tool.presentation.root.App
import ru.llama.tool.presentation.root.IRootComponent
import ru.llama.tool.presentation.root.RootComponentImpl
import java.io.File

private const val LLAMA_LOG = "LLAMA_LOG"

class MainActivity : ComponentActivity() {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    private external fun stringFromJNI(): String
    private external fun getModelInfo(): String
    private external fun unloadModel()
    private external fun generateText(prompt: String, maxTokens: Int): String
    private external fun loadModel(modelPath: String): Boolean


    private lateinit var rootComponent: IRootComponent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootComponent = retainedComponent { componentContext ->
            RootComponentImpl(
                componentContext = componentContext,

                )
        }
        enableEdgeToEdge()
        setContent { App(rootComponent) }

        println(stringFromJNI())

        println(LLAMA_LOG + getModelInfo())
        lifecycleScope.launch {
            val modelPathResult = copyModelFromAssets()
            modelPathResult.onSuccess { modelPath ->
                Log.i(LLAMA_LOG, "LOAD MODEL success")
                loadModel(modelPath)
                println(LLAMA_LOG + "ModelInfo" + getModelInfo())

                withContext(Dispatchers.Default) {
                    val result = generateText("Hello! How are you today?", 1000)
                    Log.d(LLAMA_LOG, "gen RESULT $result")
                }
            }.onFailure { error ->
                Log.i(LLAMA_LOG, "LOAD MODEL ERROR $error")
            }

        }

        println(LLAMA_LOG + getModelInfo())


    }

    override fun onDestroy() {
        unloadModel()
        super.onDestroy()
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

}