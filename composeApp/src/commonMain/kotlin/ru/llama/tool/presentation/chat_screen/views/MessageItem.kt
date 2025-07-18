package ru.llama.tool.presentation.chat_screen.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.llama.tool.domain.models.EnumSender
import ru.llama.tool.domain.models.Message

@Composable
fun MessageItem(modifier: Modifier, message: Message, maxMessageWidth: Dp) {

    Column(modifier = modifier) {
        val isUserMessage = message.sender is EnumSender.User
        val isAiMessage = message.sender is EnumSender.AI

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
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
                    text = "!",
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
                    text = message.sender.throwable.message ?: "Unknown error",
                    maxLines = 1,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center

                )
            }

            if (isAiMessage) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .align(Alignment.Bottom),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message.sender.toString().first().toString(),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
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
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondary)
                        .align(Alignment.Bottom),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message.sender.toString().first().toString(),
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}