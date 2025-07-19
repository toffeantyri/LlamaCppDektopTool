package ru.llama.tool.presentation.chat_screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.MutableStateFlow
import ru.llama.tool.core.EMPTY
import ru.llama.tool.domain.models.AiDialogProperties
import ru.llama.tool.domain.models.Message
import ru.llama.tool.domain.models.UiText
import ru.llama.tool.presentation.chat_screen.ai_chat_settings.AiChatSettingsComponent

interface ChatComponent {

    val viewModel: ChatViewModel

    val dialog: Value<ChildSlot<*, DialogChild>>

    fun onChatListOpenClicked()

    fun onChatSettingOpen()


    data class UiModel(
        val chatMessagesData: MutableStateFlow<SnapshotStateList<Message>> = MutableStateFlow(
            mutableStateListOf()
        ),
        val messageInput: MutableState<String> = mutableStateOf(EMPTY),
        val isAiTyping: MutableState<Boolean> = mutableStateOf(false),

        val modelName: MutableState<UiText> = mutableStateOf(UiText.Empty),
        val aiProps: MutableState<AiDialogProperties> = mutableStateOf(AiDialogProperties(id = -1)),
        val titleLoading: MutableState<Boolean> = mutableStateOf(false)

    )

    @Stable
    sealed interface DialogChild {
        data class AiSettingDialogChild(val component: AiChatSettingsComponent) : DialogChild

        data object DialogListDialogChild : DialogChild
    }
}