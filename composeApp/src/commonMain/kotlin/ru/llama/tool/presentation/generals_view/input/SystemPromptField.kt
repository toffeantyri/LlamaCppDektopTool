package ru.llama.tool.presentation.generals_view.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SystemPromptField(
    labelText: String,
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        label = { Text(labelText) },
        modifier = modifier.fillMaxWidth()
    )

}

