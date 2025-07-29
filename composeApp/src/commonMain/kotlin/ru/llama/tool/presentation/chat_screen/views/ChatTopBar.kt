package ru.llama.tool.presentation.chat_screen.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.llama.tool.domain.models.UiText
import ru.llama.tool.presentation.utils.asString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    modelName: State<UiText>,
    aiTyping: State<Boolean>,
    aiLoading: State<Boolean>,
    onChatListOpenClicked: () -> Unit,
    onChatSettingOpenClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(
                            color = when {
                                modelName.value.asString() == "Unknown" -> Color.Red
                                aiLoading.value -> Color.Yellow
                                aiTyping.value -> Color.Cyan
                                else -> Color.Green
                            },
                            shape = CircleShape
                        )
                        .border(1.dp, Color.Gray, CircleShape)
                        .padding(horizontal = 4.dp)
                )

                Text(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    text = modelName.value.asString(),
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onChatListOpenClicked) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            if (modelName.value.asString() != "Unknown") {
                IconButton(onClick = onChatSettingOpenClicked, enabled = aiTyping.value.not()) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }
    )
} 