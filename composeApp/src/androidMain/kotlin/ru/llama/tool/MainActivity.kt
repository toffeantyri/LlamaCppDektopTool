package ru.llama.tool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.retainedComponent
import ru.llama.tool.presentation.root.App
import ru.llama.tool.presentation.root.IRootComponent
import ru.llama.tool.presentation.root.RootComponentImpl

class MainActivity : ComponentActivity() {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    private external fun stringFromJNI(): String

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

        println(stringFromJNI()) // → "Hello from C++!"



    }

    override fun onDestroy() {
        super.onDestroy()
    }

}