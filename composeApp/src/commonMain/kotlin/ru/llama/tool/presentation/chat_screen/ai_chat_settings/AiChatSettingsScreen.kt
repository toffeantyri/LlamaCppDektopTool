package ru.llama.tool.presentation.chat_screen.ai_chat_settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatSettingsScreen(
    component: AiChatSettingsComponent
) {
    val currentProperties by component.currentAiProp.subscribeAsState()

    var systemPrompt by remember(currentProperties) { mutableStateOf(currentProperties.systemPrompt) }
    var temperature by remember(currentProperties) { mutableDoubleStateOf(currentProperties.temperature) }
    var maxTokens by remember(currentProperties) { mutableFloatStateOf(currentProperties.maxTokens.toFloat()) }
    var topP by remember(currentProperties) { mutableFloatStateOf(currentProperties.topP.toFloat()) }

    val windowInsetsBottom =
        WindowInsets.Companion.navigationBars.asPaddingValues().calculateBottomPadding()

    ModalBottomSheet(
        onDismissRequest = component::onDismissDialog,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = { WindowInsets(bottom = windowInsetsBottom /*+ 80.dp*/) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedTextField(
                value = systemPrompt,
                onValueChange = { systemPrompt = it },
                label = { Text("Системный промпт") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Температура: ${String.format("%.1f", temperature)}")
            Slider(
                value = temperature.toFloat(),
                onValueChange = { temperature = it.toDouble() },
                valueRange = 0.1f..1.0f,
                steps = 9, // 0.1, 0.2, ..., 1.0
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Макс. токенов: ${maxTokens.toInt()}")
            Slider(
                value = maxTokens,
                onValueChange = { maxTokens = it },
                valueRange = 100f..1500f,
                steps = 140, // 100, 110, ..., 1500 (increment by 10)
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Top P: ${String.format("%.1f", topP)}")
            Slider(
                value = topP,
                onValueChange = { topP = it },
                valueRange = 0.1f..1.0f,
                steps = 9, // 0.1, 0.2, ..., 1.0
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = component::onCancelClicked) {
                    Text("Отмена")
                }
                Button(onClick = {
                    val updatedProperties = currentProperties.copy(
                        systemPrompt = systemPrompt,
                        temperature = temperature,
                        maxTokens = maxTokens.toInt(),
                        topP = topP.toDouble()
                    )
                    component.onSaveClicked(updatedProperties)
                }) {
                    Text("Сохранить")
                }
            }
        }
    }
}
