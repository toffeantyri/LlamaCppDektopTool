package ru.llama.tool.presentation.chat_screen

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChatComponentImpl(
    componentContext: ComponentContext,
    private val onChatListOpenAction: () -> Unit
) : ChatComponent, ComponentContext by componentContext {

    private val _chatMessages = MutableStateFlow<List<Message>>(emptyList())
    override val chatMessages: StateFlow<List<Message>> = _chatMessages.asStateFlow()

    private val _messageInput = MutableStateFlow("")
    override val messageInput: StateFlow<String> = _messageInput.asStateFlow()

    override fun onChatListOpenClicked() = onChatListOpenAction()

    override fun onMessageSend(message: String) {
        if (message.isNotBlank()) {
            _chatMessages.value =
                _chatMessages.value + Message(content = message, sender = Sender.User)
            _messageInput.value = ""
        }
    }

    override fun onMessageInputChanged(input: String) {
        _messageInput.value = input
    }
} 