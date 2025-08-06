package ru.llama.tool

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.retainedComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.llama.tool.presentation.root.App
import ru.llama.tool.presentation.root.IRootComponent
import ru.llama.tool.presentation.root.RootComponentImpl
import ru.llama.tool.server.LlamaServerManager
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {

    private lateinit var rootComponent: IRootComponent

    private lateinit var server: LlamaServerManager
    private lateinit var modelPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootComponent = retainedComponent { componentContext ->
            RootComponentImpl(
                componentContext = componentContext,

                )
        }
        enableEdgeToEdge()
        setContent { App(rootComponent) }


        // Копируем модель
        val modelFile = File(filesDir, "model.gguf")
        if (!modelFile.exists()) {
            assets.open("model.gguf").use { input ->
                FileOutputStream(modelFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
        modelPath = modelFile.absolutePath

        // Создаём менеджер сервера
        server = LlamaServerManager(this)

        // Запускаем сервер
        GlobalScope.launch(Dispatchers.IO) {
            if (server.startServer(modelPath)) {
                Log.d("LLaMAa", "Сервер запущен! http://127.0.0.1:8080")
            } else {
                Log.e("LLaMAa", "Ошибка запуска сервера")
            }
        }
    }

    override fun onDestroy() {
        server.stopServer()
        super.onDestroy()
    }

}