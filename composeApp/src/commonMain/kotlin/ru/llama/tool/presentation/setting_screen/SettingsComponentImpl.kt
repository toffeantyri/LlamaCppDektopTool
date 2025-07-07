package ru.llama.tool.presentation.setting_screen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.llama.tool.core.data_store.preferances.IAppPreferences

class SettingsComponentImpl(
    componentContext: ComponentContext,
    private val parentCoroutineScope: CoroutineScope,
    private val preferences: IAppPreferences,
) : SettingComponent, ComponentContext by componentContext, InstanceKeeper.Instance {


    private val _state = MutableValue(SettingsState(isDarkMode = preferences.getThemeIsDarkMode()))

    init {
        parentCoroutineScope.launch {
            preferences.getAppThemeIsDarkMode().collect {
                _state.value = _state.value.copy(isDarkMode = it)
            }
        }
    }

    override val state: Value<SettingsState> = _state

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.ToggleDarkMode -> {
                parentCoroutineScope.launch {
                    preferences.setThemeIsDarkMode(event.isDarkMode)
                }
            }
        }
    }
}