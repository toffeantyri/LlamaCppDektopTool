package ru.llama.tool.presentation.setting_screen

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.llama.tool.data.preferences.preferances.IAppPreferences
import ru.llama.tool.domain.models.AiDialogProperties
import ru.llama.tool.presentation.setting_screen.models.AiDefaultSetting

class SettingViewModelImpl(
    private val coroutineScope: CoroutineScope,
    private val preferences: IAppPreferences
) : ISettingViewModel, InstanceKeeper.Instance {

    override val uiModel: Value<ISettingViewModel.UiModel> =
        MutableValue(ISettingViewModel.UiModel())

    override fun saveDefaultSystemPrompt(data: AiDefaultSetting) {
        coroutineScope.launch {
            runCatching {
                if (uiModel.value.aiSettings.value.defSystemPrompt != data.defSystemPrompt) {
                    preferences.setSystemPrompt(data.defSystemPrompt)
                }
                if (uiModel.value.aiSettings.value.baseUrl != data.baseUrl) {
                    preferences.setBaseUrl(data.baseUrl)
                }
            }.onSuccess {
                uiModel.value.aiSettings.value = data
            }.onFailure {
                println("saveDefaultSystemPrompt failed $it")
            }
        }
    }

    init {
        preferencesCollector()
    }


    private fun preferencesCollector() {
        coroutineScope.launch {
            val defSysPrompt = preferences.getSystemPrompt(AiDialogProperties.INITIAL_SYSTEM_PROMPT)
            val baseUrl = preferences.getBaseUrl()
            uiModel.value.aiSettings.value =
                uiModel.value.aiSettings.value.copy(
                    defSystemPrompt = defSysPrompt,
                    baseUrl = baseUrl
                )
        }
    }

}