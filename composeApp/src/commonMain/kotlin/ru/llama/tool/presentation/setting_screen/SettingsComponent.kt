package ru.llama.tool.presentation.setting_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import ru.llama.tool.presentation.generals_view.input.SystemPromptField


sealed interface SettingsEvent {
    data class ToggleDarkMode(val isDarkMode: Boolean) : SettingsEvent
}

@Composable
fun SettingsContent(component: SettingComponent, modifier: Modifier = Modifier) {

    val uiModel by component.viewModel.uiModel.subscribeAsState()
    val aiSettings by uiModel.aiSettings.subscribeAsState()

    val defaultSystemPrompt =
        remember(aiSettings.defSystemPrompt) { mutableStateOf(aiSettings.defSystemPrompt) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Настройки",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Раздел Приложение
        Text(
            "Приложение",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Темная тема", color = MaterialTheme.colorScheme.onBackground)
            Switch(
                checked = uiModel.appSettingState.value.isDarkMode,
                onCheckedChange = { newValue ->
                    component.onEvent(
                        SettingsEvent.ToggleDarkMode(newValue)
                    )
                }
            )
        }

        // Раздел Аккаунт
        Text(
            "Аккаунт",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Информация об аккаунте",
            modifier = Modifier.padding(horizontal = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Раздел Настройки ИИ
        Text(
            "Настройки ИИ",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        OutlinedTextField(
            value = "127.0.0.1:8080",
            onValueChange = { },
            label = { Text("test base url") },
            modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp),
            maxLines = 1
        )


        SystemPromptField(
            value = defaultSystemPrompt.value,
            onValueChanged = { defaultSystemPrompt.value = it },
            labelText = "Дефолтный системный промпт",
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        )

        AnimatedVisibility(uiModel.aiSettings.value.defSystemPrompt != defaultSystemPrompt.value) {
            Button(
                onClick = {
                    val updatedProperties = uiModel.aiSettings.value.copy(
                        defSystemPrompt = defaultSystemPrompt.value
                    )
                    component.viewModel.saveDefaultSystemPrompt(updatedProperties)
                }) {
                Text("Сохранить")
            }
        }

        // Раздел About
        Text(
            "О приложении",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            "Версия приложения, лицензии и т.д.",
            modifier = Modifier.padding(horizontal = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
} 