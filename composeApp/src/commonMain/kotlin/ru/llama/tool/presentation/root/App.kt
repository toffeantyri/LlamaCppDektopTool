package ru.llama.tool.presentation.root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.llama.tool.presentation.root.first_tab_root.FirstTabContent
import ru.llama.tool.presentation.setting_screen.SettingsContent

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC6),
    tertiary = Color(0xFF3700B3),
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    secondary = Color(0xFF03DAC6),
    tertiary = Color(0xFF3700B3),
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun App(root: IRootComponent) {
    val appSettingsState by root.appSettingState.subscribeAsState()

    MaterialTheme(colorScheme = if (appSettingsState.isDarkMode) DarkColorScheme else LightColorScheme) {
        val childStack by root.stack.subscribeAsState()
        val selectedIndex by root.selectedIndex.subscribeAsState()

        Scaffold(
            modifier = Modifier/*.navigationBarsPadding()*/,
            bottomBar = {
                NavigationBar(modifier = Modifier) {
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
                    is IRootComponent.Child.ChatContentChild -> FirstTabContent(child.component)
                    is IRootComponent.Child.SettingContentChild -> SettingsContent(child.component)
                }
            }
        }
    }
}
