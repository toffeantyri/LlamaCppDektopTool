package ru.llama.tool.presentation.chat_screen.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import llamacppdektoptool.composeapp.generated.resources.Res
import llamacppdektoptool.composeapp.generated.resources.exclamation_error
import llamacppdektoptool.composeapp.generated.resources.repeat_send
import llamacppdektoptool.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.stringResource
import ru.llama.tool.domain.models.EnumSender
import ru.llama.tool.domain.models.Message

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageItem(
    modifier: Modifier,
    message: Message,
    maxMessageWidth: Dp,
    onResendClicked: () -> Unit
) {

    var menuExpanded by remember { mutableStateOf(false) }
    var menuOffset by remember { mutableStateOf(Offset.Zero) }

    val density = LocalDensity.current

    val onOpenMessageMenu: (Offset) -> Unit = { offset ->
        menuOffset = offset
        menuExpanded = true
    }


    Column(modifier = modifier) {
        val isUserMessage = message.sender is EnumSender.User
        val isAiMessage = message.sender is EnumSender.AI

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            onOpenMessageMenu(offset)
                        }
                    )
                },
            horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {


            AnimatedVisibility(message.error != null) {
                Text(
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 18.2.sp,
                        letterSpacing = 0.42.sp
                    ),
                    text = stringResource(Res.string.exclamation_error),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.size(24.dp).background(
                        color = Color.Red.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(10.dp)
                    )
                )
            }

            if (message.sender is EnumSender.Error) {
                Text(
                    modifier = Modifier.fillMaxWidth().background(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                    text = message.sender.throwable ?: stringResource(Res.string.unknown_error),
                    maxLines = 1,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center

                )
            }

            if (isAiMessage) {
                SenderNameBox(
                    sender = message.sender,
                    dateTime = message.dateTime
                )
                Spacer(modifier = Modifier.width(8.dp))


                ContentCloud(
                    modifier = Modifier
                        .widthIn(max = maxMessageWidth)
                        .padding(horizontal = 4.dp),
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                    message = message
                )
            }

            if (isUserMessage) {
                ContentCloud(
                    modifier = Modifier
                        .widthIn(max = maxMessageWidth)
                        .padding(horizontal = 4.dp),
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    message = message
                )

                Spacer(modifier = Modifier.width(8.dp))

                SenderNameBox(
                    sender = message.sender,
                    dateTime = message.dateTime
                )
            }
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
            offset = with(density) {
                DpOffset(menuOffset.x.toDp(), menuOffset.y.toDp())
            }
        ) {
            if (message.error != null && message.sender == EnumSender.User) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(Res.string.repeat_send)) },
                    onClick = {
                        onResendClicked()
                        menuExpanded = false
                    }
                )
            }
        }

    }
}

@Composable
private fun SenderNameBox(
    sender: EnumSender,
    dateTime: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = sender.toString().first().toString(),
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 12.sp
            )
        }

        // Отображение времени для пользовательского сообщения
        Text(
            text = dateTime,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
