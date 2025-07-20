package ru.llama.tool.presentation.chat_screen.ai_dialog_list

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.llama.tool.domain.models.AIDialogChatDto

class AiDialogListComponentImpl(
    private val onDialogSelected: (chatId: Int) -> Unit,
    private val onCreateNewDialog: () -> Unit,
    private val onDismiss: () -> Unit
) : AiDialogListComponent {

    override val dialogs: Value<SnapshotStateList<AIDialogChatDto>> = MutableValue(
        mutableStateListOf(
            AIDialogChatDto(1, "Диалог 1", emptyList()),
            AIDialogChatDto(2, "Диалог 2", emptyList())
        )
    )

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
        println("Удалить диалог с ID: $chatId")
        // TODO: Implement delete dialog logic
    }
} 