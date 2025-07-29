package ru.llama.tool.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ru.llama.tool.presentation.root.first_tab_root.FirstTabComponent
import ru.llama.tool.presentation.setting_screen.SettingComponent
import ru.llama.tool.presentation.setting_screen.models.SettingsState

interface IRootComponent {

    val stack: Value<ChildStack<*, Child>>
    val selectedIndex: Value<Int>
    val appSettingState: Value<SettingsState>
    fun onChatTabClicked()
    fun onSettingsTabClicked()

    sealed interface Child {
        data class ChatContentChild(val component: FirstTabComponent) : Child
        data class SettingContentChild(val component: SettingComponent) : Child
    }
} 