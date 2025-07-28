package ru.llama.tool.presentation.setting_screen

import com.arkivanov.decompose.value.Value
import ru.llama.tool.presentation.setting_screen.models.SettingsState

interface SettingComponent {
    val viewModel: ISettingViewModel

    fun getAppSettingState(): Value<SettingsState>

    fun onEvent(event: SettingsEvent)
}