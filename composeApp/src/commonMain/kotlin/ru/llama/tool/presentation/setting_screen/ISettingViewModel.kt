package ru.llama.tool.presentation.setting_screen

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

interface ISettingViewModel {

    val uiModel: Value<UiModel>

    fun onChangeSystemPrompt()

    data class UiModel(
        val darkModeState: MutableValue<SettingsState> = MutableValue(SettingsState())
    )

}