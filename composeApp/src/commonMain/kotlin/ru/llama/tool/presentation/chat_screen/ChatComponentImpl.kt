package ru.llama.tool.presentation.chat_screen

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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

    private val _chatMessagesData =
        MutableStateFlow<SnapshotStateList<Message>>(mutableStateListOf())

    override val chatMessages: StateFlow<SnapshotStateList<Message>> =
        _chatMessagesData.asStateFlow()

    private val _messageInput = MutableStateFlow("")
    override val messageInput: StateFlow<String> = _messageInput.asStateFlow()

    private val _isAITyping = MutableStateFlow(false)
    override val isAITyping: StateFlow<Boolean> = _isAITyping.asStateFlow()

    private var counter = 0

    override fun onChatListOpenClicked() = onChatListOpenAction()

    override fun onMessageSend(userMessage: String) {
        var id = counter++

        val message = Message(
            content = userMessage,
            sender = EnumSender.User,
            id = id
        )

        if (message.content.isNotBlank()) {
            _chatMessagesData.value += message
            _messageInput.value = ""

            coroutineScope.launch {
                sendChatRequestUseCase(message.copy(id = ++id))
                    .onStart { _isAITyping.value = true }
                    .onCompletion { _isAITyping.value = false }
                    .flowOn(Dispatchers.IO)
                    .collectLatest { aiResponse ->
                        if (aiResponse.id != id) {
                            _chatMessagesData.value += aiResponse
                        } else if (_chatMessagesData.value.firstOrNull {
                                it.id == aiResponse.id
                            } == null) {
                            _chatMessagesData.value += aiResponse
                        } else {
                            _chatMessagesData.value.firstOrNull {
                                it.id == aiResponse.id
                            }?.let { oldMessage ->
                                val oldIndex = _chatMessagesData.value.indexOf(oldMessage)
                                val newMessage = Message(
                                    id = id,
                                    sender = aiResponse.sender,
                                    content = oldMessage.content + aiResponse.content
                                )
                                _chatMessagesData.value[oldIndex] = newMessage
                            }
                        }
                    }
            }
        }
    }

    override fun onMessageInputChanged(input: String) {
        _messageInput.value = input
    }
} 