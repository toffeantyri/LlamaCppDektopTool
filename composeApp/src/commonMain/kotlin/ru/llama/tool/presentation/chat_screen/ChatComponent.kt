package ru.llama.tool.presentation.chat_screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ru.llama.tool.core.EMPTY
import ru.llama.tool.domain.models.AiDialogProperties
import ru.llama.tool.domain.models.Message
import ru.llama.tool.domain.models.UiText
import ru.llama.tool.presentation.chat_screen.ai_chat_settings.AiChatSettingsComponent
import ru.llama.tool.presentation.chat_screen.ai_dialog_list.AiDialogListComponent
import ru.llama.tool.presentation.events.UiEvent
import ru.llama.tool.presentation.events.UpEventChat

interface ChatComponent {

    val viewModel: ChatViewModel
    val chatEventState: SharedFlow<UpEventChat>
    val uiEvent: StateFlow<UiEvent>
    val dialog: Value<ChildSlot<*, DialogChild>>

    val drawerComponent: AiDialogListComponent

    fun clearUiEvent()
    fun onChatSettingOpen()

    fun onDrawerOpened()

    fun onDrawerClosed()


    data class UiModel(
        val chatName: MutableState<String> = mutableStateOf(EMPTY),
        val chatId: MutableState<Long> = mutableLongStateOf(0L),
        val chatMessagesData: SnapshotStateList<Message> = mutableStateListOf(),
        val messageInput: MutableState<String> = mutableStateOf(EMPTY),
        val aiProps: MutableState<AiDialogProperties> =
            mutableStateOf(AiDialogProperties()),

        var messageId: Int = 0, //message id counter

        val isAiTyping: MutableState<Boolean> = mutableStateOf(false),
        val modelName: MutableState<UiText> = mutableStateOf(UiText.Empty),
        val titleLoading: MutableState<Boolean> = mutableStateOf(false)

    )

    @Stable
    sealed interface DialogChild {
        data class AiSettingDialogChild(val component: AiChatSettingsComponent) : DialogChild

    }
}