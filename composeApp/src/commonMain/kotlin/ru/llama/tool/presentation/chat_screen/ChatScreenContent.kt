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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import ru.llama.tool.presentation.chat_screen.views.ChatTopBar
import ru.llama.tool.presentation.chat_screen.views.MessageInputPanel
import ru.llama.tool.presentation.chat_screen.views.MessageItem
import ru.llama.tool.presentation.utils.onKeyEnter

@Composable
fun ChatScreenContent(component: ChatComponent) {

    val uiModel by component.viewModel.uiModel.collectAsState()

    val chatMessages = uiModel.chatMessagesData.collectAsState()

    val focusRequester = remember { FocusRequester() }

    val scrollState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.onKeyEnter(focusRequester) {
            component.viewModel.onMessageSend()
        }.imePadding(),
        topBar = {
            ChatTopBar(
                aiProps = uiModel.aiProps,
                onChatListOpenClicked = component::onChatListOpenClicked
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
                    isAiTyping = uiModel.isAiTyping

                )
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}