package ru.llama.tool.presentation.setting_screen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.llama.tool.data.preferences.preferances.IAppPreferences

class SettingsComponentImpl(
    componentContext: ComponentContext,
    private val parentCoroutineScope: CoroutineScope,
    private val preferences: IAppPreferences,
) : SettingComponent, ComponentContext by componentContext, InstanceKeeper.Instance {

    override val viewModel: ISettingViewModel = instanceKeeper.getOrCreate {
        SettingViewModelImpl()
    }

    override fun getAppSettingState(): Value<SettingsState> = viewModel.uiModel.value.darkModeState

    init {
        parentCoroutineScope.launch {
            preferences.getAppThemeIsDarkMode().collect {
                viewModel.uiModel.value.darkModeState.value =
                    viewModel.uiModel.value.darkModeState.value.copy(isDarkMode = it)
            }
        }
    }

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