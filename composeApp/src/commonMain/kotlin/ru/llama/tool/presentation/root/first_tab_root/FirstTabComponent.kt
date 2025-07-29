package ru.llama.tool.presentation.root.first_tab_root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import ru.llama.tool.presentation.chat_screen.ChatComponent
import ru.llama.tool.presentation.root.IRootComponent.Child

interface FirstTabComponent {

    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {

        data class ChatContentChild(val component: ChatComponent) : Child

    }

}