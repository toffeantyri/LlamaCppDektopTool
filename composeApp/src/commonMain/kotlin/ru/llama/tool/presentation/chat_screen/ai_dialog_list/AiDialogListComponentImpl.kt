package ru.llama.tool.presentation.chat_screen.ai_dialog_list

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnStart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.llama.tool.domain.models.AIDialogChatDto
import ru.llama.tool.domain.use_cases.ChatInteractor

class AiDialogListComponentImpl(
    private val componentContext: ComponentContext,
    private val coroutineScope: CoroutineScope,
    private val chatInteractor: ChatInteractor,
    private val onDialogSelectedAction: (chatId: Long) -> Unit,
    private val onCreateNewDialog: () -> Unit,
) : AiDialogListComponent, ComponentContext by componentContext {

    override val dialogs: Value<SnapshotStateList<AIDialogChatDto>> =
        MutableValue(mutableStateListOf())

    override fun onDialogSelected(chatId: Long) = onDialogSelectedAction(chatId)

    override fun onCreateNewDialogClicked() {
        onCreateNewDialog()
    }

    override fun onDeleteDialogClicked(chatId: Long) {
        coroutineScope.launch {
            runCatching {
                chatInteractor.deleteChatFromDb(chatId)
            }.onSuccess {
                dialogs.value.removeIf { it.chatId == chatId }
            }.onFailure {
                println("Ошибка Удалить диалог с ID: $chatId")
            }
        }
    }

    init {
        componentContext.lifecycle.doOnStart {
            coroutineScope.launch {
                loadChatList()
            }
        }
    }

    private suspend fun loadChatList() {
        val chatList = chatInteractor.getAllChatsList()
        dialogs.value.clear()
        dialogs.value.addAll(chatList)
    }
} 