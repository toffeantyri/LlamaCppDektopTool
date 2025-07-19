package ru.llama.tool.presentation.chat_screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow
import ru.llama.tool.core.EMPTY
import ru.llama.tool.domain.models.AiProperties
import ru.llama.tool.domain.models.Message
import ru.llama.tool.domain.models.UiText

interface ChatComponent {

    val viewModel: ChatViewModel

    fun onChatListOpenClicked()

    fun onChatSettingOpen()


    data class UiModel(
        val chatMessagesData: MutableStateFlow<SnapshotStateList<Message>> = MutableStateFlow(
            mutableStateListOf()
        ),
        val messageInput: MutableState<String> = mutableStateOf(EMPTY),
        val isAiTyping: MutableState<Boolean> = mutableStateOf(false),

        val aiProps: MutableState<AiProperties> = mutableStateOf(AiProperties(UiText.Empty)),
        val titleLoading: MutableState<Boolean> = mutableStateOf(false)

    )
}