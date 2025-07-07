package ru.llama.tool.presentation.chat_screen.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(onChatListOpenClicked: () -> Unit) {
    TopAppBar(
        title = { Text("Chat") },
        navigationIcon = {
            IconButton(onClick = onChatListOpenClicked) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        }
    )
} 