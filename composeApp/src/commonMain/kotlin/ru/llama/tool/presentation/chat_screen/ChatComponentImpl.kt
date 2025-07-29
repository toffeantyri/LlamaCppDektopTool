package ru.llama.tool.presentation.chat_screen

import androidx.compose.runtime.Stable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.navigate
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import ru.llama.tool.domain.use_cases.ChatInteractor
import ru.llama.tool.presentation.chat_screen.ai_chat_settings.AiChatSettingsComponentImpl
import ru.llama.tool.presentation.chat_screen.ai_dialog_list.AiDialogListComponent
import ru.llama.tool.presentation.chat_screen.ai_dialog_list.AiDialogListComponentImpl
import ru.llama.tool.presentation.events.UiEvent
import ru.llama.tool.presentation.events.UpEventChat
import ru.llama.tool.presentation.utils.componentCoroutineScope

class ChatComponentImpl(
    componentContext: ComponentContext
) : ChatComponent, ComponentContext by componentContext, KoinComponent {

    private val coroutineScope = componentContext.componentCoroutineScope()
    private val chatInteractor: ChatInteractor = get()
    private val _chatEventState = MutableSharedFlow<UpEventChat>(replay = 0)
    override val chatEventState: SharedFlow<UpEventChat> get() = _chatEventState
    private val _uiEvent = MutableStateFlow<UiEvent>(UiEvent.Initial)

    override val uiEvent: StateFlow<UiEvent> get() = _uiEvent


    override val viewModel: ChatViewModel = componentContext.instanceKeeper.getOrCreate {
        ChatViewModel(
            inChatEvent = chatEventState,
            coroutineScope = coroutineScope,
            sendChatRequestUseCase = get(),
            getLlamaPropertiesUseCase = get(),
            chatPropsInteractor = get(),
            chatInteractor = chatInteractor
        )
    }

    private val slotNavigation = SlotNavigation<DialogConfig>()

    override val dialog: Value<ChildSlot<*, ChatComponent.DialogChild>> = childSlot(
        source = slotNavigation, serializer = DialogConfig.serializer(),
        handleBackButton = true,
        childFactory = ::createChildDialog,
    )

    private val drawerLifecycle = LifecycleRegistry(initialState = Lifecycle.State.CREATED)

    override val drawerComponent: AiDialogListComponent =
        AiDialogListComponentImpl(
            componentContext = childContext(
                key = "chatListDrawer",
                lifecycle = drawerLifecycle
            ),
            chatInteractor = viewModel.chatInteractor,
            onDialogSelectedAction = { selectedChatID ->
                coroutineScope.launch {
                    _uiEvent.emit(UiEvent.CloseDrawer)
                    _chatEventState.emit(UpEventChat.SelectDialogBy(selectedChatID))
                }
            },
            onCreateNewDialog = ::onCreateNewEmptyDialog,
            coroutineScope = coroutineScope,
        )

    private fun onCreateNewEmptyDialog() {
        coroutineScope.launch {
            _uiEvent.emit(UiEvent.CloseDrawer)
            _chatEventState.emit(UpEventChat.CreateNewDialog)
        }

    }

    override fun clearUiEvent() {
        coroutineScope.launch {
            _uiEvent.emit(UiEvent.Initial)
        }
    }

    @Stable
    private fun createChildDialog(
        config: DialogConfig,
        childComponentContext: ComponentContext
    ): ChatComponent.DialogChild {
        return when (config) {
            is DialogConfig.AiSettingDialogConfig -> ChatComponent.DialogChild.AiSettingDialogChild(
                component = AiChatSettingsComponentImpl(
                    componentContext = childComponentContext,
                    currentAiDialogProperties = viewModel.uiModel.value.aiProps.value,
                    onCloseDialog = { slotNavigation.navigate { null } },
                    savePropertiesAction = viewModel::saveProperties
                )
            )
        }
    }


    override fun onChatSettingOpen() {
        slotNavigation.navigate { DialogConfig.AiSettingDialogConfig }
    }


    override fun onDrawerOpened() {
        drawerLifecycle.resume()
    }

    override fun onDrawerClosed() {
        drawerLifecycle.stop()
    }


    @Serializable
    private sealed class DialogConfig {
        @Serializable
        data object AiSettingDialogConfig : DialogConfig()

    }


    init {
        coroutineScope.launch {
            _chatEventState.emit(UpEventChat.CreateNewDialog)
        }

        componentContext.lifecycle.apply {
//            doOnPause { println("Lifecycle doOnPause") }
//            doOnStop { println("Lifecycle doOnStop") }
//            doOnStart { println("Lifecycle doOnStart") }
//            doOnCreate { println("Lifecycle doOnCreate") }
//            doOnDestroy { println("Lifecycle doOnDestroy") }
//            doOnResume { println("Lifecycle doOnResume") }
        }
    }

} 