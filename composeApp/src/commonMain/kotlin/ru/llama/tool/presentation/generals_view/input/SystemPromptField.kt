package ru.llama.tool.presentation.generals_view.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

private const val MAX_SYSTEM_PROMPT_LEN = 200

@Composable
fun SystemPromptField(
    labelText: String,
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit
) {

    val textFieldValue = remember(value) { mutableStateOf(value.take(MAX_SYSTEM_PROMPT_LEN)) }
    val onTextChange: (String) -> Unit = remember {
        {
            if (textFieldValue.value.length <= MAX_SYSTEM_PROMPT_LEN) {
                onValueChanged(it)
            }
        }
    }

    OutlinedTextField(
        value = textFieldValue.value,
        onValueChange = onTextChange,
        label = { Text(labelText) },
        modifier = modifier.fillMaxWidth(),
        supportingText = {
            Text(
                text = "${textFieldValue.value.length}/$MAX_SYSTEM_PROMPT_LEN",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
        }
    )

}

