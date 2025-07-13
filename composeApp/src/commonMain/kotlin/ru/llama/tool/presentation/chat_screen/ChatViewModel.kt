package ru.llama.tool.presentation.chat_screen

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ru.llama.tool.domain.models.EnumSender
import ru.llama.tool.domain.models.Message
import ru.llama.tool.domain.use_cases.ai_props_use_case.GetAiPropertiesUseCase
import ru.llama.tool.domain.use_cases.messaging_use_case.SendChatRequestUseCase

class ChatViewModel(
    private val coroutineScope: CoroutineScope,
    private val sendChatRequestUseCase: SendChatRequestUseCase,
    private val getAiPropertiesUseCase: GetAiPropertiesUseCase

) : InstanceKeeper.Instance, IChatViewModel {

    val uiModel: MutableStateFlow<ChatComponent.UiModel> = MutableStateFlow(ChatComponent.UiModel())

    private var idCounter = 0

    override fun onMessageSend() {
        var id = idCounter++

        val message = Message(
            content = uiModel.value.messageInput.value.trim(),
            sender = EnumSender.User,
            id = id
        )

        if (message.content.isNotBlank()) {
            uiModel.value.chatMessagesData.value += message
            uiModel.value.messageInput.value = ""

            coroutineScope.launch {
                sendChatRequestUseCase(message.copy(id = ++id))
                    .onStart { uiModel.value.isAiTyping.value = true }
                    .onCompletion { uiModel.value.isAiTyping.value = false }
                    .flowOn(Dispatchers.IO)
                    .collectLatest { aiResponse ->
                        if (aiResponse.id != id) {
                            uiModel.value.chatMessagesData.value += aiResponse
                        } else if (uiModel.value.chatMessagesData.value.firstOrNull {
                                it.id == aiResponse.id
                            } == null) {
                            uiModel.value.chatMessagesData.value += aiResponse
                        } else {
                            uiModel.value.chatMessagesData.value.firstOrNull {
                                it.id == aiResponse.id
                            }?.let { oldMessage ->
                                val oldIndex =
                                    uiModel.value.chatMessagesData.value.indexOf(oldMessage)
                                val newMessage = Message(
                                    id = id,
                                    sender = aiResponse.sender,
                                    content = oldMessage.content + aiResponse.content
                                )
                                uiModel.value.chatMessagesData.value[oldIndex] = newMessage
                            }
                        }
                    }
            }
        }
    }

    override fun onMessageInputChanged(input: String) {
        uiModel.value.messageInput.value = input
    }


    init {
        updateAiModelName()
    }

    fun updateAiModelName() {
        coroutineScope.launch {
            runCatching {
                uiModel.value.titleLoading.value = true
                getAiPropertiesUseCase.invoke()
            }.onSuccess { aiProps ->
                uiModel.value.aiProps.value = aiProps
                uiModel.value.titleLoading.value = false
            }.onFailure {
                uiModel.value.titleLoading.value = false
            }
        }
    }
}