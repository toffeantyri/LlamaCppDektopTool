package ru.llama.tool.presentation.chat_screen

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.llama.tool.domain.SendChatRequestUseCase
import ru.llama.tool.domain.models.EnumSender
import ru.llama.tool.domain.models.Message
import ru.llama.tool.presentation.utils.componentCoroutineScope

class ChatComponentImpl(
    componentContext: ComponentContext,
    private val onChatListOpenAction: () -> Unit
) : ChatComponent, ComponentContext by componentContext, KoinComponent {

    private val coroutineScope = componentContext.componentCoroutineScope()

    private val sendChatRequestUseCase: SendChatRequestUseCase by inject()

    private val _chatMessages = MutableStateFlow<List<Message>>(emptyList())
    override val chatMessages: StateFlow<List<Message>> = _chatMessages.asStateFlow()

    private val _messageInput = MutableStateFlow("")
    override val messageInput: StateFlow<String> = _messageInput.asStateFlow()

    private val _isAITyping = MutableStateFlow(false)
    override val isAITyping: StateFlow<Boolean> = _isAITyping.asStateFlow()

    override fun onChatListOpenClicked() = onChatListOpenAction()

    override fun onMessageSend(userMessage: String) {
        val message = Message(
            content = userMessage,
            sender = EnumSender.User
        )
        if (message.content.isNotBlank()) {
            _chatMessages.value += message
            _messageInput.value = ""

            coroutineScope.launch {
                sendChatRequestUseCase(message)
                    .onStart { _isAITyping.value = true }
                    .onCompletion { _isAITyping.value = false }
                    .flowOn(Dispatchers.IO)
                    .collectLatest { aiResponse ->
                        _chatMessages.value += aiResponse
                    }
            }
        }
    }

    override fun onMessageInputChanged(input: String) {
        _messageInput.value = input
    }
} 