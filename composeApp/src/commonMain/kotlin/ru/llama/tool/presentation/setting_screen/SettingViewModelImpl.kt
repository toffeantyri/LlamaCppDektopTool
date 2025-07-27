package ru.llama.tool.presentation.setting_screen

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper

class SettingViewModelImpl : ISettingViewModel, InstanceKeeper.Instance {

    override val uiModel: Value<ISettingViewModel.UiModel> =
        MutableValue(ISettingViewModel.UiModel())

    override fun onChangeSystemPrompt() {
        //todo
    }

}