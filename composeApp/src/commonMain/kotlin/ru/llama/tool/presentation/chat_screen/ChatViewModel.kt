package ru.llama.tool.presentation.chat_screen

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import ru.llama.tool.domain.models.EnumSender
import ru.llama.tool.domain.models.Message
import ru.llama.tool.domain.models.UiText
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
        // Добавляем системный промпт если это первое сообщение
        if (uiModel.value.chatMessagesData.value.isEmpty()) {
            uiModel.value.chatMessagesData.value += Message(
                sender = EnumSender.System,
                id = -1,
                content = uiModel.value.aiProps.value.systemPrompt
            )
        }

        val userMessageId = idCounter++
        val aiResponseId = idCounter++ // Заранее резервируем ID для ответа

        val message = Message(
            content = uiModel.value.messageInput.value.trim(),
            sender = EnumSender.User,
            id = userMessageId
        )

        if (message.content.isNotBlank()) {
            uiModel.value.chatMessagesData.value += message
            uiModel.value.messageInput.value = ""
            coroutineScope.launch {
                runCatching {
                    uiModel.value.isAiTyping.value = true
                    sendChatRequestUseCase(uiModel.value.chatMessagesData.value)
                        .onCompletion { uiModel.value.isAiTyping.value = false }
                        .flowOn(Dispatchers.IO)
                }.onSuccess { flowResult ->
                    flowResult.collectLatest { aiResponse ->
                        // Обновляем ID ответа, чтобы он соответствовал зарезервированному
                        val updatedResponse = aiResponse.copy(id = aiResponseId)

                        if (uiModel.value.chatMessagesData.value.none { it.id == updatedResponse.id }) {
                            uiModel.value.chatMessagesData.value += updatedResponse
                        } else {
                            uiModel.value.chatMessagesData.value.firstOrNull {
                                it.id == updatedResponse.id
                            }?.let { oldMessage ->
                                val oldIndex =
                                    uiModel.value.chatMessagesData.value.indexOf(oldMessage)
                                val newMessage = Message(
                                    id = updatedResponse.id,
                                    sender = updatedResponse.sender,
                                    content = oldMessage.content + updatedResponse.content
                                )
                                uiModel.value.chatMessagesData.value[oldIndex] = newMessage
                            }
                        }
                    }
                }.onFailure { error ->
                    uiModel.value.isAiTyping.value = false
                    val lastUserMessageIndex = uiModel.value.chatMessagesData.value.indexOfFirst {
                        it.id == userMessageId
                    }
                    val userMessage =
                        uiModel.value.chatMessagesData.value[lastUserMessageIndex].copy(
                            error = error.message ?: "Error"
                        )

                    uiModel.value.chatMessagesData.value[lastUserMessageIndex] = userMessage
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

    private fun updateAiModelName() {
        coroutineScope.launch {
            runCatching {
                uiModel.value.titleLoading.value = true
                getAiPropertiesUseCase.invoke()
            }.onSuccess { aiProps ->
                uiModel.value.aiProps.value = aiProps
                uiModel.value.titleLoading.value = false
            }.onFailure {
                uiModel.value.aiProps.value =
                    uiModel.value.aiProps.value.copy(modelName = UiText.StringValue("Unknown"))
                uiModel.value.titleLoading.value = false
            }
        }
    }
}