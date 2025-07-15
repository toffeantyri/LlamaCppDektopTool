package ru.llama.tool.presentation.chat_screen.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.text.style.TextOverflow
import ru.llama.tool.domain.models.AiProperties
import ru.llama.tool.presentation.utils.asString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    aiProps: State<AiProperties>,
    onChatListOpenClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                aiProps.value.modelName.asString(),
                maxLines = 1,
                overflow = TextOverflow.StartEllipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onChatListOpenClicked) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        }
    )
} 