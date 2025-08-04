package ru.llama.tool.presentation.chat_screen.views

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.llama.tool.domain.models.Message

@Composable
fun ContentCloud(modifier: Modifier, backgroundColor: Color, message: Message) {

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        SelectionContainer {
            Text(
                text = message.content.trimStart(),
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}