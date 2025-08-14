package ru.llama.tool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.retainedComponent
import ru.llama.tool.data.server.ILlamaManager
import ru.llama.tool.presentation.root.App
import ru.llama.tool.presentation.root.IRootComponent
import ru.llama.tool.presentation.root.RootComponentImpl
import ru.llama.tool.server.LlamaManagerImpl


class MainActivity : ComponentActivity() {


    private lateinit var rootComponent: IRootComponent
    private lateinit var llamaManagerImpl: ILlamaManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootComponent = retainedComponent { componentContext ->
            RootComponentImpl(
                componentContext = componentContext,

                )
        }
        enableEdgeToEdge()
        setContent { App(rootComponent) }
        llamaManagerImpl = LlamaManagerImpl(this)


    }


}