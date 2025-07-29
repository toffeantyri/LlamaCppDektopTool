package ru.llama.tool.presentation.chat_screen.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import llamacppdektoptool.composeapp.generated.resources.Res
import llamacppdektoptool.composeapp.generated.resources.input_placeholder
import llamacppdektoptool.composeapp.generated.resources.send
import llamacppdektoptool.composeapp.generated.resources.stop
import org.jetbrains.compose.resources.stringResource

@Composable
fun MessageInputPanel(
    messageInput: State<String>,
    onMessageInputChanged: (String) -> Unit,
    onMessageSend: () -> Unit,
    onMessageStopGen: () -> Unit,
    isAiTyping: State<Boolean>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = messageInput.value,
            onValueChange = onMessageInputChanged,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp, max = 150.dp),
            shape = RoundedCornerShape(10.dp),
            placeholder = { Text(stringResource(Res.string.input_placeholder)) },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = false,
            maxLines = 5,
            minLines = 1,
            enabled = !isAiTyping.value
        )
        Button(
            modifier = Modifier.padding(horizontal = 8.dp).padding(vertical = 4.dp),
            onClick = { if (isAiTyping.value) onMessageStopGen() else onMessageSend() },
            colors = ButtonDefaults.buttonColors(disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            if (isAiTyping.value) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp).aspectRatio(1f),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Icon(
                        modifier = Modifier.size(18.dp).aspectRatio(1f),
                        imageVector = Icons.Rounded.Stop,
                        tint = Color.White,
                        contentDescription = stringResource(Res.string.stop)
                    )
                }
            } else {
                Icon(
                    modifier = Modifier.size(20.dp).aspectRatio(1f),
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    tint = Color.Black,
                    contentDescription = stringResource(Res.string.send)
                )
            }
        }
    }
} 