package ru.llama.tool.presentation.chat_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import ru.llama.tool.presentation.chat_screen.ai_dialog_list.AiDialogListScreen
import ru.llama.tool.presentation.chat_screen.views.ChatTopBar
import ru.llama.tool.presentation.chat_screen.views.MessageInputPanel
import ru.llama.tool.presentation.chat_screen.views.MessageItem
import ru.llama.tool.presentation.utils.onKeyEnter

@Composable
fun ChatScreenContent(component: ChatComponent) {

    val coroutineScope = rememberCoroutineScope()

    val uiModel by component.viewModel.uiModel.collectAsState()
    val chatMessages = uiModel.chatMessagesData.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberLazyListState()
    val dialogSlot = component.dialog.subscribeAsState()

    val drawerState = rememberDrawerState(DrawerValue.Closed)


    ModalNavigationDrawer(
        drawerContent = {
            val item = dialogSlot.value.child?.instance
            if (item is ChatComponent.DialogChild.DialogListDialogChild) {
                AiDialogListScreen(item.component)
            }
        },
        drawerState = drawerState,
    ) {
        Scaffold(
            modifier = Modifier
                .onKeyEnter(focusRequester) {
                    component.viewModel.onMessageSend()
                }
                .imePadding(),
            topBar = {
                ChatTopBar(
                    modelName = uiModel.modelName,
                    aiLoading = uiModel.isAiTyping,
                    onChatListOpenClicked = component::onChatListOpenClicked,
                    onChatSettingOpenClicked = component::onChatSettingOpen,
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding()),
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
                        items(chatMessages.value.asReversed()) { message ->
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

        // Синхронизация drawerState с dialogSlot
        LaunchedEffect(dialogSlot.value) {
            val isDialogListOpen =
                dialogSlot.value.child?.instance is ChatComponent.DialogChild.DialogListDialogChild

            if (isDialogListOpen) {
                // Открытие drawer только если он ещё закрыт
                if (drawerState.isClosed) {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                }
            } else {
                if (drawerState.isOpen) {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                }
            }
        }

        // Обработка закрытия drawer (в т.ч. по scrim)
        LaunchedEffect(drawerState.isClosed) {
            if (drawerState.isClosed && dialogSlot.value.child != null) {
                component.closeDialogSlot()
            }
        }


        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}