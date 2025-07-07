package ru.llama.tool.presentation.chat_screen

import kotlinx.coroutines.flow.StateFlow

interface ChatComponent {
    val chatMessages: StateFlow<List<String>>
    val messageInput: StateFlow<String>

    fun onChatListOpenClicked()
    fun onMessageSend(message: String)
    fun onMessageInputChanged(input: String)
}