package ru.llama.tool.presentation.chat_screen.views.renaming_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

private const val MAX_CHAR_FOR_RENAMING = 30

@Composable
fun RenamingDialog(
    component: RenamingDialogComponent
) {

    val textFieldValue =
        remember { mutableStateOf(component.initialText.take(MAX_CHAR_FOR_RENAMING)) }

    Dialog(onDismissRequest = component::onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                // Заголовок диалога
                Text(
                    text = "Введите новое название диалога",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )


                // Поле ввода
                OutlinedTextField(
                    value = textFieldValue.value,
                    onValueChange = { newText ->
                        if (newText.length <= MAX_CHAR_FOR_RENAMING) {
                            textFieldValue.value = newText
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("Название") },
                    maxLines = 1,
                    supportingText = {
                        Text(
                            text = "${textFieldValue.value.length}/$MAX_CHAR_FOR_RENAMING",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge
                )

                // Кнопки
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Кнопка отмены
                    TextButton(
                        onClick = component::onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Отмена")
                    }

                    // Кнопка сохранения
                    Button(
                        onClick = {
                            component.onSave(textFieldValue.value)
                        },
                        enabled = textFieldValue.value.isNotBlank()
                    ) {
                        Text("Сохранить")
                    }
                }
            }
        }
    }
}