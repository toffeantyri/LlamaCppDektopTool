package ru.llama.tool.presentation.setting_screen

import com.arkivanov.decompose.value.Value

interface SettingComponent {
    val state: Value<SettingsState>
    fun onEvent(event: SettingsEvent)
}