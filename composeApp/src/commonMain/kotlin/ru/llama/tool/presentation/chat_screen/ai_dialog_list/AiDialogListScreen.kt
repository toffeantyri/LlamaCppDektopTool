package ru.llama.tool.presentation.chat_screen.ai_dialog_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun AiDialogListScreen(
    component: AiDialogListComponent
) {
    val dialogs by component.dialogs.subscribeAsState()

    Surface(
        modifier = Modifier
            .fillMaxWidth(0.9f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Ваши диалоги",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = component::onCreateNewDialogClicked,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Создать новый диалог")
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(dialogs) { dialog ->
                    var expanded by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { component.onDialogSelected(dialog.chatId) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = dialog.chatName,
                                style = MaterialTheme.typography.titleMedium
                            )

                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Действия")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Переименовать") },
                                    onClick = {
                                        expanded = false
                                        component.onRenameDialogClicked(dialog.chatId)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Удалить") },
                                    onClick = {
                                        expanded = false
                                        component.onDeleteDialogClicked(dialog.chatId)
                                    }
                                )
                            }
                        }
                    }
                }
            }


        }
    }
}
