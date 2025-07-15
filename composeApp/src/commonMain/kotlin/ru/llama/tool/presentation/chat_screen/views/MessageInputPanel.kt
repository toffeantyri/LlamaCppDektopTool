package ru.llama.tool.presentation.chat_screen.views

import androidx.compose.foundation.layout.Arrangement
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

@Composable
fun MessageInputPanel(
    messageInput: State<String>,
    onMessageInputChanged: (String) -> Unit,
    onMessageSend: () -> Unit,
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
            placeholder = { Text("Введите ваше сообщение") },
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
            onClick = { onMessageSend() },
            enabled = isAiTyping.value.not(),
            colors = ButtonDefaults.buttonColors(disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            if (isAiTyping.value) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp).aspectRatio(1f),
                    color = Color.White
                )
            } else {
                Icon(
                    modifier = Modifier.size(20.dp).aspectRatio(1f),
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    tint = Color.Black,
                    contentDescription = "Send"
                )
            }
        }
    }
} 