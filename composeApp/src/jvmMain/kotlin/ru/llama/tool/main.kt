package ru.llama.tool

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.essenty.statekeeper.StateKeeperDispatcher
import llamacppdektoptool.composeapp.generated.resources.Res
import llamacppdektoptool.composeapp.generated.resources.app_icon
import org.jetbrains.compose.resources.painterResource
import ru.delivery.client.utils.runOnUiThread
import ru.llama.tool.data.preferences.initializeDataStore
import ru.llama.tool.di.initKoin
import ru.llama.tool.presentation.root.App
import ru.llama.tool.presentation.root.RootComponentImpl
import ru.llama.tool.utils.DesktopLifecycleManager
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener

fun main() = application {
    val stateKeeper = StateKeeperDispatcher()
    val lifecycleManager = DesktopLifecycleManager(stateKeeper)


    initKoin(enableNetworkLogs = false)
    initializeDataStore()

    val rootComponent = runOnUiThread {
        RootComponentImpl(
            componentContext = lifecycleManager.componentContext
        )
    }
    val windowState = rememberWindowState(width = 500.dp, height = 700.dp)
    Window(
        icon = painterResource(Res.drawable.app_icon),
        onCloseRequest = {
            lifecycleManager.onDestroy()
            exitApplication()
        },
        state = windowState,
        title = "Local AI Tool"
    ) {

        LaunchedEffect(window, lifecycleManager) {
            if (window != null) {
                val focusListener = object : WindowFocusListener {
                    override fun windowGainedFocus(p0: WindowEvent?) {
                        lifecycleManager.handleWindowState(isVisible = true, isFocused = true)
                    }

                    override fun windowLostFocus(p0: WindowEvent?) {
                        lifecycleManager.handleWindowState(isVisible = true, isFocused = false)
                    }
                }

                val windowListener = object : WindowAdapter() {
                    override fun windowIconified(e: WindowEvent?) {
                        lifecycleManager.handleWindowState(isVisible = false, isFocused = false)
                    }

                    override fun windowDeiconified(e: WindowEvent?) {
                        lifecycleManager.handleWindowState(
                            isVisible = true,
                            isFocused = window.isFocused
                        )
                    }

                    override fun windowActivated(e: WindowEvent?) {
                        lifecycleManager.handleWindowState(isVisible = true, isFocused = true)
                    }

                    override fun windowDeactivated(e: WindowEvent?) {
                        lifecycleManager.handleWindowState(isVisible = true, isFocused = false)
                    }
                }

                window.addWindowFocusListener(focusListener)
                window.addWindowListener(windowListener)

                lifecycleManager.handleWindowState(isVisible = true, isFocused = window.isFocused)

            }


        }


        window.minimumSize = Dimension(400, 600)
        App(root = rootComponent)
    }
}