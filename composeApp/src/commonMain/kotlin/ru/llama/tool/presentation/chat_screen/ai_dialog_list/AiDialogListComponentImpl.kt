package ru.llama.tool.presentation.chat_screen.ai_dialog_list

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.llama.tool.domain.models.AIDialogChatDto
import ru.llama.tool.domain.use_cases.ChatInteractor

class AiDialogListComponentImpl(
    private val coroutineScope: CoroutineScope,
    private val chatInteractor: ChatInteractor,
    private val onDialogSelected: (chatId: Int) -> Unit,
    private val onCreateNewDialog: () -> Unit,
    private val onDismiss: () -> Unit
) : AiDialogListComponent {

    override val dialogs: Value<SnapshotStateList<AIDialogChatDto>> =
        MutableValue(mutableStateListOf())

    override fun onDialogSelected(chatId: Int) {
        onDialogSelected.invoke(chatId)
    }

    override fun onCreateNewDialogClicked() {
        onCreateNewDialog.invoke()
    }

    override fun onDismissDialogList() {
        onDismiss.invoke()
    }

    override fun onRenameDialogClicked(chatId: Int) {
        println("Переименовать диалог с ID: $chatId")
        // TODO: Implement rename dialog logic
    }

    override fun onDeleteDialogClicked(chatId: Int) {
        coroutineScope.launch {
            runCatching {
                chatInteractor.deleteChatFromDb(chatId)
            }.onSuccess {
                println("Успешно Удалить диалог с ID: $chatId")
                dialogs.value.removeIf { it.chatId == chatId }
            }.onFailure {
                println("Ошибка Удалить диалог с ID: $chatId")
            }
        }
    }

    init {
        loadChatList()
    }

    private fun loadChatList() {
        coroutineScope.launch {
            val chatList = chatInteractor.getAllChatsList()
            dialogs.value.clear()
            dialogs.value.addAll(chatList)
        }
    }
} 