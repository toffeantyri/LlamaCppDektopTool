package ru.llama.tool.presentation.setting_screen.view

import androidx.compose.runtime.Composable
import ru.llama.tool.presentation.setting_screen.models.LangModelInfo

expect fun openFilePicker(onFileSelected: (String?) -> Unit, activity: Any? = null)

@Composable
expect fun ModelCard(
    selectedModel: LangModelInfo?,
    isModelLoading: Boolean,
    isModelRunning: Boolean,
    onModelSelected: (String) -> Unit,
    onStartModel: () -> Unit,
    onStopModel: () -> Unit,
    onOpenFileManager: (String) -> Unit
)



