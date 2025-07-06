package ru.llama.tool.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ru.llama.tool.presentation.screen_a.ScreenAComponent
import ru.llama.tool.presentation.screen_b.SettingsComponent

interface IRootComponent {
    val stack: Value<ChildStack<*, Child>>
    val selectedIndex: Value<Int>
    fun onChatTabClicked()
    fun onSettingsTabClicked()

    sealed interface Child {
        data class ScreenA(val component: ScreenAComponent) : Child
        data class SettingScreenChild(val component: SettingsComponent) : Child
    }
} 