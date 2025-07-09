package ru.llama.tool.presentation.chat_screen

import kotlinx.coroutines.flow.StateFlow
import ru.llama.tool.domain.models.Message

interface ChatComponent {
    val chatMessages: StateFlow<List<Message>>
    val messageInput: StateFlow<String>

    fun onChatListOpenClicked()
    fun onMessageSend(message: String)
    fun onMessageInputChanged(input: String)
}