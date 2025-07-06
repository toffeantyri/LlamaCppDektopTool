package ru.llama.tool.presentation.screen_b

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper

class SettingsComponentImpl(componentContext: ComponentContext) : SettingsComponent,
    ComponentContext by componentContext, InstanceKeeper.Instance {

    private val _state = MutableValue(ApplicationSettingsState())

    override val state: Value<ApplicationSettingsState> = _state

    override fun onEvent(event: SettingsScreenEvent) {
        when (event) {
            is SettingsScreenEvent.ToggleDarkMode -> {
                _state.value = _state.value.copy(isDarkMode = event.isDarkMode)
            }
        }
    }
} 