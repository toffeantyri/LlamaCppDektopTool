package ru.llama.tool.presentation.chat_screen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import ru.llama.tool.presentation.utils.componentCoroutineScope

class ChatComponentImpl(
    componentContext: ComponentContext,
    private val onChatListOpenAction: () -> Unit,
) : ChatComponent, ComponentContext by componentContext, KoinComponent {
    private val coroutineScope = componentContext.componentCoroutineScope()

    override val viewModel: ChatViewModel = componentContext.instanceKeeper.getOrCreate {
        ChatViewModel(
            coroutineScope = coroutineScope,
            sendChatRequestUseCase = get(),
            getAiPropertiesUseCase = get()
        )
    }


    override fun onChatListOpenClicked() = onChatListOpenAction()

    override fun onChatSettingOpen() {
        //todo - open dialog bottom sheet with settings
    }


} 