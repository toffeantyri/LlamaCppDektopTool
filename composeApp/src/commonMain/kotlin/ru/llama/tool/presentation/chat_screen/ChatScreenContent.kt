package ru.llama.tool.presentation.chat_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.llama.tool.presentation.chat_screen.views.ChatTopBar
import ru.llama.tool.presentation.chat_screen.views.MessageInputPanel

@Composable
fun ChatScreenContent(component: ChatComponent) {
    val chatMessages by component.chatMessages.collectAsState()
    val messageInput by component.messageInput.collectAsState()

    Scaffold(
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
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(chatMessages) {
                    Text(text = it)
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
} 