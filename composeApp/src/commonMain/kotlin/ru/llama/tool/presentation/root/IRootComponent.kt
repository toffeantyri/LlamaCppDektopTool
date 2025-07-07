package ru.llama.tool.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ru.llama.tool.presentation.chat_screen.ChatComponent
import ru.llama.tool.presentation.setting_screen.SettingComponent
import ru.llama.tool.presentation.setting_screen.SettingsState

interface IRootComponent {
    val stack: Value<ChildStack<*, Child>>
    val selectedIndex: Value<Int>
    val appSettingState: Value<SettingsState>
    fun onChatTabClicked()
    fun onSettingsTabClicked()

    sealed interface Child {
        data class ChatContentChild(val component: ChatComponent) : Child
        data class SettingContentChild(val component: SettingComponent) : Child
    }
} 