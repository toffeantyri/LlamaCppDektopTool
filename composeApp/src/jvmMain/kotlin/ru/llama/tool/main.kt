package ru.llama.tool

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.statekeeper.StateKeeperDispatcher
import llamacppdektoptool.composeapp.generated.resources.Res
import llamacppdektoptool.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import ru.delivery.client.utils.runOnUiThread
import ru.llama.tool.presentation.root.App
import ru.llama.tool.presentation.root.RootComponentImpl
import java.awt.Dimension

fun main() = application {
    val lifecycle = LifecycleRegistry()
    val stateKeeper = StateKeeperDispatcher()

    val rootComponent = runOnUiThread {
        RootComponentImpl(
            componentContext =
                DefaultComponentContext(
                    lifecycle,
                    stateKeeper
                )
        )
    }
    val windowState = rememberWindowState(width = 500.dp, height = 700.dp)
    Window(
        icon = painterResource(Res.drawable.compose_multiplatform),
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Local AI Tool"
    ) {
        window.minimumSize = Dimension(400, 600)
        App(root = rootComponent)
    }
}