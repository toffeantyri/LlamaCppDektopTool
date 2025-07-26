package ru.llama.tool.presentation.chat_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import kotlinx.coroutines.launch
import ru.llama.tool.presentation.chat_screen.ai_chat_settings.AiChatSettingsScreen
import ru.llama.tool.presentation.chat_screen.ai_dialog_list.AiDialogListScreen
import ru.llama.tool.presentation.chat_screen.views.ChatTopBar
import ru.llama.tool.presentation.chat_screen.views.MessageInputPanel
import ru.llama.tool.presentation.chat_screen.views.MessageItem
import ru.llama.tool.presentation.utils.onKeyEnter

@Suppress("UnusedBoxWithConstraintsScope")
@Composable
fun ChatScreenContent(component: ChatComponent) {

    val coroutineScope = rememberCoroutineScope()

    val uiModel by component.viewModel.uiModel.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberLazyListState()
    val dialogSlot = component.dialog.subscribeAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    DisposableEffect(drawerState.isOpen) {
        if (drawerState.isOpen) {
            component.onDrawerOpened()
        } else {
            component.onDrawerClosed()
        }
        onDispose {}
    }

    dialogSlot.value.child?.also { child ->
        when (val item = child.instance) {
            is ChatComponent.DialogChild.AiSettingDialogChild -> AiChatSettingsScreen(item.component)
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            AiDialogListScreen(component.drawerComponent)
        },
        drawerState = drawerState,
    ) {
        Scaffold(
            modifier = Modifier
                .onKeyEnter(focusRequester) {
                    component.viewModel.onMessageSend()
                },
            contentWindowInsets = WindowInsets(0.dp),
            topBar = {
                ChatTopBar(
                    modelName = uiModel.modelName,
                    aiTyping = uiModel.isAiTyping,
                    aiLoading = uiModel.titleLoading,
                    onChatListOpenClicked = { coroutineScope.launch { drawerState::open.invoke() } },
                    onChatSettingOpenClicked = component::onChatSettingOpen,
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BoxWithConstraints(modifier = Modifier.weight(1f)) {
                    val maxMessageWidth = maxWidth * 0.7f
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        state = scrollState,
                        reverseLayout = true
                    ) {
                        items(uiModel.chatMessagesData.asReversed()) { message ->
                            MessageItem(
                                modifier = Modifier,
                                message = message,
                                maxMessageWidth = maxMessageWidth
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shadowElevation = 8.dp
                ) {
                    MessageInputPanel(
                        messageInput = uiModel.messageInput,
                        onMessageInputChanged = component.viewModel::onMessageInputChanged,
                        onMessageSend = component.viewModel::onMessageSend,
                        isAiTyping = uiModel.isAiTyping,
                        onMessageStopGen = component.viewModel::stopMessageGen
                    )
                }
            }
        }


        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}