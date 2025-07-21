package ru.llama.tool.presentation.chat_screen.ai_dialog_list

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.decompose.value.Value
import ru.llama.tool.domain.models.AIDialogChatDto

interface AiDialogListComponent {

    val dialogs: Value<SnapshotStateList<AIDialogChatDto>>

    fun onDialogSelected(chatId: Int)

    fun onCreateNewDialogClicked()

    fun onRenameDialogClicked(chatId: Int)

    fun onDeleteDialogClicked(chatId: Int)

} 