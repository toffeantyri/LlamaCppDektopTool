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
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import ru.llama.tool.presentation.chat_screen.ai_chat_settings.AiChatSettingsComponentImpl
import ru.llama.tool.presentation.chat_screen.ai_dialog_list.AiDialogListComponent
import ru.llama.tool.presentation.chat_screen.ai_dialog_list.AiDialogListComponentImpl
import ru.llama.tool.presentation.utils.componentCoroutineScope

class ChatComponentImpl(
    componentContext: ComponentContext,
    private val chatId: Int?,
    private val changeCurrentChatId: (newChatId: Int) -> Unit,
    createNewChat: () -> Unit
) : ChatComponent, ComponentContext by componentContext, KoinComponent {
    private val coroutineScope = componentContext.componentCoroutineScope()

    override val viewModel: ChatViewModel = componentContext.instanceKeeper.getOrCreate {
        ChatViewModel(
            chatId = chatId,
            coroutineScope = coroutineScope,
            sendChatRequestUseCase = get(),
            getLlamaPropertiesUseCase = get(),
            chatPropsInteractor = get(),
            chatInteractor = get(),
            onChangeCurrentChatId = changeCurrentChatId
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
            onDialogSelected = { /*todo*/ },
            onCreateNewDialog = createNewChat,
            coroutineScope = coroutineScope,
        )


    @Stable
    private fun createChildDialog(
        config: DialogConfig,
        childComponentContext: ComponentContext
    ): ChatComponent.DialogChild {
        return when (config) {
            is DialogConfig.AiSettingDialogConfig -> ChatComponent.DialogChild.AiSettingDialogChild(
                component = AiChatSettingsComponentImpl(
                    currentAiDialogProperties = viewModel.uiModel.value.aiProps.value,
                    onCloseDialog = { slotNavigation.navigate { null } }
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

} 