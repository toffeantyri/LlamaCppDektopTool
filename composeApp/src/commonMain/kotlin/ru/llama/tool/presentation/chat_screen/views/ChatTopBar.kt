package ru.llama.tool.presentation.chat_screen.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.llama.tool.domain.models.UiText
import ru.llama.tool.presentation.utils.asString

@Composable
fun ChatTopBar(
    modelName: State<UiText>,
    aiTyping: State<Boolean>,
    aiLoading: State<Boolean>,
    onChatListOpenClicked: () -> Unit,
    onChatSettingOpenClicked: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Навигационная иконка (меню)
            IconButton(
                onClick = onChatListOpenClicked,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu"
                )
            }

            // Индикатор состояния и название модели
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    modifier = Modifier.padding(start = 8.dp).sizeIn(minWidth = 200.dp),
                    text = modelName.value.asString(),
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis,
                    textAlign = TextAlign.End
                )
            }

            // Кнопка настроек (если модель известна)
            if (modelName.value.asString() != "Unknown") {
                IconButton(
                    onClick = onChatSettingOpenClicked,
                    enabled = aiTyping.value.not(),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        }
    }
}