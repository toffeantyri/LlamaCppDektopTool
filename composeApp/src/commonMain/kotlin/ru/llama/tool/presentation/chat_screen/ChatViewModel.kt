package ru.llama.tool.presentation.chat_screen

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.llama.tool.core.io
import ru.llama.tool.domain.models.EnumSender
import ru.llama.tool.domain.models.Message
import ru.llama.tool.domain.models.UiText
import ru.llama.tool.domain.use_cases.ChatInteractor
import ru.llama.tool.domain.use_cases.chat_property_interactor.ChatPropsInteractor
import ru.llama.tool.domain.use_cases.llama_props_use_case.GetLlamaPropertiesUseCase
import ru.llama.tool.domain.use_cases.messaging_use_case.SendChatRequestUseCase

class ChatViewModel(
    private val chatId: Int?,
    private val coroutineScope: CoroutineScope,
    private val sendChatRequestUseCase: SendChatRequestUseCase,
    private val getLlamaPropertiesUseCase: GetLlamaPropertiesUseCase,
    private val chatPropsInteractor: ChatPropsInteractor,
    val chatInteractor: ChatInteractor,
    private val onChangeCurrentChatId: (newChatId: Int) -> Unit,
) : InstanceKeeper.Instance, IChatViewModel {

    val uiModel: MutableStateFlow<ChatComponent.UiModel> = MutableStateFlow(ChatComponent.UiModel())

    private var messageJob: Job? = null

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
            messageJob?.cancel()
            messageJob = null
            messageJob = coroutineScope.launch {
                runCatching {
                    uiModel.value.isAiTyping.value = true
                    sendChatRequestUseCase(
                        uiModel.value.chatMessagesData.value,
                        uiModel.value.aiProps.value
                    )
                        .catch {
                            println("VM catch $it")
                            setErrorMessage(userMessageId, it)
                            uiModel.value.isAiTyping.value = false
                        }
                        .onCompletion {
                            println("VM onCompletion $it")
                            uiModel.value.isAiTyping.value = false
                        }
                        .flowOn(Dispatchers.IO)
                }.onSuccess { flowResult ->
                    flowResult.onEach { aiResponse ->
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
                    }.launchIn(this)
                }.onFailure { error ->
                    println("onFailure $error")
                    setErrorMessage(userMessageId, error)
                }

            }
        }
    }

    private fun setErrorMessage(userMessageId: Int, error: Throwable) {

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

    override fun stopMessageGen() {
        messageJob?.cancel()
        messageJob = null
    }

    override fun onMessageInputChanged(input: String) {
        uiModel.value.messageInput.value = input
    }


    init {
        initChatDialog()
    }


    private suspend fun updateAiModelName() {
        runCatching {
            uiModel.value.titleLoading.value = true
            getLlamaPropertiesUseCase.invoke()
        }.onSuccess { aiProps ->
            uiModel.value.modelName.value = aiProps.modelName
            uiModel.value.titleLoading.value = false
        }.onFailure {
            uiModel.value.modelName.value = UiText.StringValue("Unknown")
            uiModel.value.titleLoading.value = false
        }
    }

    private suspend fun updateAiDialogProperties(toNextAction: suspend () -> Unit) {
        if (chatId != null) {
            chatPropsInteractor.getChatProperty(chatId).collect { properties ->
                uiModel.value.aiProps.value = properties
                println("VM $properties")
                toNextAction()
            }
        } else {
            toNextAction()
        }
    }


    private fun initChatDialog() {
        coroutineScope.launch {
            launch(Dispatchers.io()) {
                updateAiDialogProperties(
                    toNextAction = { updateAiModelName() }
                )
            }
        }
    }
}