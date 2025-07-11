package ru.llama.tool.presentation.chat_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.llama.tool.domain.models.EnumSender
import ru.llama.tool.presentation.chat_screen.views.ChatTopBar
import ru.llama.tool.presentation.chat_screen.views.MessageInputPanel
import ru.llama.tool.presentation.utils.onKeyEnter

@Composable
fun ChatScreenContent(component: ChatComponent) {
    val chatMessages by component.chatMessages.collectAsState()
    val messageInput by component.messageInput.collectAsState()


    val focusRequester = remember { FocusRequester() }

    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()



    Scaffold(
        modifier = Modifier.onKeyEnter(focusRequester) {
            scope.launch {
                scrollState.scrollToItem(chatMessages.lastIndex)
            }
            component.onMessageSend()
        },
        topBar = {
            ChatTopBar(onChatListOpenClicked = component::onChatListOpenClicked)
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
                    state = scrollState
                ) {
                    items(chatMessages) { message ->
                        val isUserMessage = message.sender == EnumSender.User
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            if (!isUserMessage) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .align(Alignment.Bottom),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = message.sender.name.first().toString(),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            Surface(
                                modifier = Modifier
                                    .widthIn(max = maxMessageWidth)
                                    .padding(horizontal = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = if (isUserMessage) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = message.content,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            if (isUserMessage) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondary)
                                        .align(Alignment.Bottom),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = message.sender.name.first().toString(),
                                        color = MaterialTheme.colorScheme.onSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shadowElevation = 8.dp
            ) {
                MessageInputPanel(
                    messageInput = messageInput,
                    onMessageInputChanged = component::onMessageInputChanged,
                    onMessageSend = component::onMessageSend
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
} 