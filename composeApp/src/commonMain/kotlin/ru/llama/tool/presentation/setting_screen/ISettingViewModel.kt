package ru.llama.tool.presentation.setting_screen

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.llama.tool.presentation.setting_screen.models.AiDefaultSetting
import ru.llama.tool.presentation.setting_screen.models.SettingsState

interface ISettingViewModel {

    val uiModel: Value<UiModel>

    fun onChangeSystemPrompt(value: String)

    data class UiModel(
        val appSettingState: MutableValue<SettingsState> = MutableValue(SettingsState()),
        val aiSettings: MutableValue<AiDefaultSetting> = MutableValue(AiDefaultSetting())
    )

}