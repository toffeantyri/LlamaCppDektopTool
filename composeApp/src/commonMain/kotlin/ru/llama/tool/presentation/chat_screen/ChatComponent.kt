package ru.llama.tool.presentation.chat_screen

import kotlinx.coroutines.flow.StateFlow
import ru.llama.tool.domain.models.Message

interface ChatComponent {
    val chatMessages: StateFlow<List<Message>>
    val messageInput: StateFlow<String>
    val isAITyping: StateFlow<Boolean>

    fun onChatListOpenClicked()
    fun onMessageSend()
    fun onMessageInputChanged(input: String)
}