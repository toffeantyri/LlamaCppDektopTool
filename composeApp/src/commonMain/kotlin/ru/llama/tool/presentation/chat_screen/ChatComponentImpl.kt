package ru.llama.tool.presentation.chat_screen

import com.arkivanov.decompose.ComponentContext

class ChatComponentImpl(componentContext: ComponentContext) : ChatComponent,
    ComponentContext by componentContext {

    override fun onNextScreenClicked() {
        // Handle navigation to next screen
    }
} 