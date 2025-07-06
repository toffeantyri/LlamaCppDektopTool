package ru.llama.tool.presentation.root

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import llamacppdektoptool.composeapp.generated.resources.Res
import llamacppdektoptool.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import ru.llama.tool.getPlatform
import ru.llama.tool.presentation.screen_a.ScreenAComponent
import ru.llama.tool.presentation.screen_b.ScreenBComponent

@Composable
fun App(root: IRootComponent) {
    MaterialTheme {
        val childStack by root.stack.subscribeAsState()
        val selectedIndex by root.selectedIndex.subscribeAsState()

        Scaffold(bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedIndex == 0,
                    onClick = root::onChatTabClicked,
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Чат") },
                    label = { Text("Чат") }
                )
                NavigationBarItem(
                    selected = selectedIndex == 1,
                    onClick = root::onSettingsTabClicked,
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Настройки") },
                    label = { Text("Настройки") }
                )
            }
        }) { innerPadding ->
            Children(
                stack = childStack,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                animation = stackAnimation(animator = slide())
            ) {
                when (val child = it.instance) {
                    is IRootComponent.Child.ScreenA -> ScreenAContent(child.component)
                    is IRootComponent.Child.ScreenB -> ScreenBContent(child.component)
                }
            }
        }
    }
}

@Composable
fun ScreenAContent(component: ScreenAComponent) {
    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var showContent by remember { mutableStateOf(false) }
        Button(onClick = { showContent = !showContent }) {
            Text("Click me!")
        }
        AnimatedVisibility(showContent) {
            val greeting = remember { getPlatform() }
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painterResource(Res.drawable.compose_multiplatform), null)
                Text("Compose: $greeting")
            }
        }
    }
}

@Composable
fun ScreenBContent(component: ScreenBComponent) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Screen B Content")
    }
}