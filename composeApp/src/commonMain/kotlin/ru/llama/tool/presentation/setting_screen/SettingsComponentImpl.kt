package ru.llama.tool.presentation.setting_screen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import ru.llama.tool.data.preferences.preferances.IAppPreferences
import ru.llama.tool.presentation.setting_screen.models.SettingsState
import ru.llama.tool.presentation.utils.componentCoroutineScope

class SettingsComponentImpl(
    componentContext: ComponentContext,
    private val parentCoroutineScope: CoroutineScope,
    private val preferences: IAppPreferences,
) : SettingComponent, ComponentContext by componentContext, InstanceKeeper.Instance, KoinComponent {

    private val coroutineScope = componentContext.componentCoroutineScope()

    override val viewModel: ISettingViewModel = instanceKeeper.getOrCreate {
        SettingViewModelImpl(coroutineScope = coroutineScope, preferences = get())
    }

    override fun getAppSettingState(): Value<SettingsState> =
        viewModel.uiModel.value.appSettingState

    init {
        parentCoroutineScope.launch {
            preferences.getAppThemeIsDarkMode().collect {
                viewModel.uiModel.value.appSettingState.value =
                    viewModel.uiModel.value.appSettingState.value.copy(isDarkMode = it)
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