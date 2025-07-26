package ru.llama.tool.presentation.chat_screen

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.llama.tool.core.EMPTY
import ru.llama.tool.domain.models.AIDialogChatDto
import ru.llama.tool.domain.models.AiDialogProperties
import ru.llama.tool.domain.models.EnumSender
import ru.llama.tool.domain.models.Message
import ru.llama.tool.domain.models.UiText
import ru.llama.tool.domain.use_cases.ChatInteractor
import ru.llama.tool.domain.use_cases.chat_property_interactor.ChatPropsInteractor
import ru.llama.tool.domain.use_cases.llama_props_use_case.GetLlamaPropertiesUseCase
import ru.llama.tool.domain.use_cases.messaging_use_case.SendChatRequestUseCase
import ru.llama.tool.presentation.chat_screen.utils.extractErrorMessage503LoadingModel
import ru.llama.tool.presentation.events.UpEventChat
import kotlin.time.Duration.Companion.seconds

class ChatViewModel(
    private val inChatEvent: SharedFlow<UpEventChat>,
    private val coroutineScope: CoroutineScope,
    private val sendChatRequestUseCase: SendChatRequestUseCase,
    private val getLlamaPropertiesUseCase: GetLlamaPropertiesUseCase,
    private val chatPropsInteractor: ChatPropsInteractor,
    val chatInteractor: ChatInteractor,
) : InstanceKeeper.Instance, IChatViewModel {

    val uiModel: MutableStateFlow<ChatComponent.UiModel> = MutableStateFlow(ChatComponent.UiModel())

    private val saveDialogTrigger = MutableSharedFlow<Unit>()

    private var messageJob: Job? = null

    private var idCounter = 0


    override fun onMessageSend() {
        // Добавляем системный промпт если это первое сообщение
        if (uiModel.value.chatMessagesData.isEmpty()) {
            uiModel.value.chatMessagesData += Message(
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
            uiModel.value.chatMessagesData += message
            uiModel.value.messageInput.value = ""
            messageJob?.cancel()
            messageJob = null
            messageJob = coroutineScope.launch {
                runCatching {
                    uiModel.value.isAiTyping.value = true
                    sendChatRequestUseCase(
                        uiModel.value.chatMessagesData,
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
                            saveDialogTrigger.emit(Unit) //save dialog trigger
                        }
                        .flowOn(Dispatchers.IO)
                }.onSuccess { flowResult ->
                    flowResult.onEach { aiResponse ->
                        // Обновляем ID ответа, чтобы он соответствовал зарезервированному
                        val updatedResponse = aiResponse.copy(id = aiResponseId)

                        if (uiModel.value.chatMessagesData.none { it.id == updatedResponse.id }) {
                            uiModel.value.chatMessagesData += updatedResponse
                        } else {
                            uiModel.value.chatMessagesData.firstOrNull {
                                it.id == updatedResponse.id
                            }?.let { oldMessage ->
                                val oldIndex =
                                    uiModel.value.chatMessagesData.indexOf(oldMessage)
                                val newMessage = Message(
                                    id = updatedResponse.id,
                                    sender = updatedResponse.sender,
                                    content = oldMessage.content + updatedResponse.content
                                )
                                uiModel.value.chatMessagesData[oldIndex] = newMessage
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
        val lastUserMessageIndex = uiModel.value.chatMessagesData.indexOfFirst {
            it.id == userMessageId
        }
        val userMessage =
            uiModel.value.chatMessagesData[lastUserMessageIndex].copy(
                error = error.message ?: "Error"
            )

        uiModel.value.chatMessagesData[lastUserMessageIndex] = userMessage

    }

    override fun stopMessageGen() {
        messageJob?.cancel()
        messageJob = null
    }

    override fun onMessageInputChanged(input: String) {
        uiModel.value.messageInput.value = input
    }


    private fun saveCurrentDialog() {
        coroutineScope.launch {
            val currentChat = AIDialogChatDto(
                chatId = uiModel.value.chatId.value,
                chatName = uiModel.value.chatName.value,
                messages = uiModel.value.chatMessagesData,
                date = EMPTY
            )
            val savedChatId = chatInteractor.saveChatToDb(currentChat)
            uiModel.value.chatId.value = savedChatId
        }
    }




    @OptIn(FlowPreview::class)
    private fun saveDialogTriggerCollector() {
        coroutineScope.launch {
            saveDialogTrigger
                .debounce(3.seconds)
                .collect {
                    saveCurrentDialog()
                }
        }
    }


    private fun updateAiModelNameWorker() {
        coroutineScope.launch {
            runCatching {
                uiModel.value.titleLoading.value = true
                getLlamaPropertiesUseCase.invoke()
            }.onSuccess { aiProps ->
                println("D/AiModelName onSuccess ${aiProps.modelName}")
                uiModel.value.modelName.value = aiProps.modelName
                uiModel.value.titleLoading.value = false
                delay(30.seconds)
                updateAiModelNameWorker()
            }.onFailure { error ->
                if (error is ServerResponseException) {
                    val message = extractErrorMessage503LoadingModel(error.message)
                    uiModel.value.modelName.value = UiText.StringValue(message ?: "Unknown")
                } else {
                    uiModel.value.modelName.value = UiText.StringValue("Unknown")
                    uiModel.value.titleLoading.value = false
                }
                delay(5.seconds)
                updateAiModelNameWorker()
            }
        }
    }

    private fun updateAiDialogProperties(newChatId: Long) {
        coroutineScope.launch {
            chatPropsInteractor.getChatProperty(newChatId)
                .collect { properties ->
                    uiModel.value.aiProps.value = properties
                }
        }
    }

    private fun updateChatDialogBy(newChatId: Long) {
        if (newChatId == AiDialogProperties.DEFAULT_ID) {
            println("D/CHAT VM: createNewDialog")
            with(uiModel.value) {
                chatId.value = AiDialogProperties.DEFAULT_ID
                chatName.value = EMPTY
                messageInput.value = EMPTY
                chatMessagesData.clear()
            }
        } else {
            coroutineScope.launch {
                chatInteractor.getDialogChat(newChatId)
                    .collect { chat ->
                        println("D/CHAT VM: collect chat $chat")
                        with(uiModel.value) {
                            chatId.value = chat.chatId
                            chatName.value = chat.chatName
                            chatMessagesData.clear()
                            chatMessagesData.addAll(chat.messages)
                        }
                    }
            }
        }
    }

    private fun chatEventCollector() {
        coroutineScope.launch {
            println("D/CHAT VM: init Event collector")
            inChatEvent.collect { event ->
                println("D/CHAT VM: EVENT $event")
                with(uiModel.value) {
                    when (event) {
                        is UpEventChat.CreateNewDialog -> {
                            updateAiDialogProperties(AiDialogProperties.DEFAULT_ID)
                            updateChatDialogBy(AiDialogProperties.DEFAULT_ID)
                        }

                        is UpEventChat.SelectDialogBy -> {
                            updateAiDialogProperties(event.chatId)
                            updateChatDialogBy(event.chatId)
                        }
                    }
                }
            }
        }
    }


    init {
        chatEventCollector()
        updateAiModelNameWorker()
        saveDialogTriggerCollector()
    }
}