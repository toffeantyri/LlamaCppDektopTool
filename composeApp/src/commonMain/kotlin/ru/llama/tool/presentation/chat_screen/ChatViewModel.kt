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
import kotlinx.coroutines.flow.first
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

    override fun onRepeatMessageSend() {
        val lastIndex = uiModel.value.chatMessagesData.lastIndex
        val lastMessage = uiModel.value.chatMessagesData[lastIndex]
        if (lastMessage.sender != EnumSender.User) return

        uiModel.value.chatMessagesData[lastIndex] = lastMessage.copy(error = null)
        val lastUserID = lastMessage.id
        val aiResponseId = (lastUserID + 1)// Заранее резервируем ID для ответа

        handleMessage(userMessageId = lastUserID, aiResponseId = aiResponseId)
    }

    override fun onMessageSend() {
        //перед каждым запросом - системный промпт
        uiModel.value.chatMessagesData.removeIf { it.sender == EnumSender.System }
        uiModel.value.chatMessagesData += Message(
            sender = EnumSender.System,
            id = -1,
            content = uiModel.value.aiProps.value.systemPrompt
        )

        val userMessageId = uiModel.value.messageId++
        val aiResponseId = uiModel.value.messageId++ // Заранее резервируем ID для ответа

        val message = Message(
            content = uiModel.value.messageInput.value.trim(),
            sender = EnumSender.User,
            id = userMessageId
        )

        if (message.content.isNotBlank()) {
            uiModel.value.chatMessagesData += message
            uiModel.value.messageInput.value = ""

            handleMessage(
                userMessageId = userMessageId,
                aiResponseId = aiResponseId
            )

        }


    }


    private fun handleMessage(userMessageId: Int, aiResponseId: Int) {
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

    override fun saveProperties(newProp: AiDialogProperties) {
        coroutineScope.launch {
            val currentChatId = uiModel.value.chatId.value
            chatPropsInteractor.saveChatProperty(currentChatId, newProp)
            uiModel.value.aiProps.value = newProp
        }
    }

    private fun setErrorMessage(userMessageId: Int, error: Throwable) {
        uiModel.value.isAiTyping.value = false
        val lastUserMessageIndex = uiModel.value.chatMessagesData.indexOfLast {
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

    private fun saveCurrentDialog(onSuccess: () -> Unit) {
        coroutineScope.launch {
            if (uiModel.value.chatMessagesData.last().sender != EnumSender.AI) return@launch
            val currentChat = AIDialogChatDto(
                chatId = uiModel.value.chatId.value,
                chatName = uiModel.value.chatName.value,
                messages = uiModel.value.chatMessagesData,
                date = EMPTY
            )
            val savedChatId = chatInteractor.saveChatToDb(currentChat)
            uiModel.value.chatId.value = savedChatId
            onSuccess()
        }
    }

    private fun updateAiDialogProperties(newChatId: Long) {
        coroutineScope.launch {
            val props = chatPropsInteractor.getChatProperty(newChatId).first()
            uiModel.value.aiProps.value = props
        }
    }

    private fun updateChatDialogBy(newChatId: Long) {
        if (newChatId == AiDialogProperties.DEFAULT_ID) {
            with(uiModel.value) {
                chatId.value = AiDialogProperties.DEFAULT_ID
                chatName.value = EMPTY
                messageInput.value = EMPTY
                chatMessagesData.clear()
                messageId = 0
            }
        } else {
            coroutineScope.launch {
                chatInteractor.getDialogChat(newChatId).first().let { chat ->

                    val oldId: Int = (chat.messages.findLast {
                        it.sender == EnumSender.AI
                    }?.id?.plus(1) ?: 0)

                    with(uiModel.value) {
                        chatId.value = chat.chatId
                        chatName.value = chat.chatName
                        chatMessagesData.clear()
                        chatMessagesData.addAll(chat.messages)
                        uiModel.value.messageId = oldId
                    }
                }
            }
        }
    }

    private fun chatEventCollector() {
        coroutineScope.launch {
            inChatEvent.collect { event ->
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

    @OptIn(FlowPreview::class)
    private fun saveDialogTriggerCollector() {
        coroutineScope.launch {
            saveDialogTrigger
                .debounce(0.seconds)
                .collect {
                    saveCurrentDialog {
                        saveProperties(uiModel.value.aiProps.value)
                    }

                }
        }
    }


    private fun updateAiModelNameWorker() {
        coroutineScope.launch {
            runCatching {
                uiModel.value.titleLoading.value = true
                getLlamaPropertiesUseCase.invoke()
            }.onSuccess { aiProps ->
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

    init {
        chatEventCollector()
        updateAiModelNameWorker()
        saveDialogTriggerCollector()
    }


}