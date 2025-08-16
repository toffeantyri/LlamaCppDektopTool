package ru.llama.tool.presentation.setting_screen.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.llama.tool.presentation.setting_screen.models.LangModelInfo

@Composable
fun ModelCard(
    selectedModel: LangModelInfo?,
    isModelLoading: Boolean,
    isModelRunning: Boolean,
    onModelSelected: (String) -> Unit,
    onStartModel: () -> Unit,
    onStopModel: () -> Unit,
    onOpenFileManager: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(
                enabled = selectedModel == null && !isModelLoading
            ) {
                if (selectedModel == null && !isModelLoading) {
                    onOpenFileManager()
                }
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selectedModel == null)
                MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        if (selectedModel == null) {
            // Пустая карточка для выбора модели
            EmptyModelCard(onOpenFileManager)
        } else {
            // Карточка с выбранной моделью
            SelectedModelCard(
                modelInfo = selectedModel,
                isModelLoading = isModelLoading,
                isModelRunning = isModelRunning,
                onStartModel = onStartModel,
                onStopModel = onStopModel
            )
        }
    }
}

@Composable
private fun EmptyModelCard(
    onOpenFileManager: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.UploadFile,
            contentDescription = "Выбрать модель",
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Выберите модель GGUF",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Нажмите для выбора файла .gguf",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun SelectedModelCard(
    modelInfo: LangModelInfo,
    isModelLoading: Boolean,
    isModelRunning: Boolean,
    onStartModel: () -> Unit,
    onStopModel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Заголовок с названием модели
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Memory,
                contentDescription = "Модель",
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = modelInfo.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Информация о модели
        ModelInfoRow(
            icon = Icons.Default.Info,
            label = "Размер файла",
            value = modelInfo.size
        )

        Spacer(modifier = Modifier.height(8.dp))

        ModelInfoRow(
            icon = Icons.Default.Category,
            label = "Архитектура",
            value = modelInfo.architecture
        )

        Spacer(modifier = Modifier.height(8.dp))

        ModelInfoRow(
            icon = Icons.Default.Settings,
            label = "Путь",
            value = modelInfo.path,
            isPath = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопки управления
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Статус модели
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = when {
                                isModelRunning -> Color.Green
                                isModelLoading -> Color.Yellow
                                else -> Color.Gray
                            },
                            shape = RoundedCornerShape(50)
                        )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = when {
                        isModelRunning -> "Запущена"
                        isModelLoading -> "Загрузка..."
                        else -> "Остановлена"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 12.sp
                )
            }

            // Кнопки управления
            Row {
                if (isModelRunning) {
                    IconButton(
                        onClick = onStopModel,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Остановить"
                        )
                    }
                } else {
                    IconButton(
                        onClick = onStartModel,
                        enabled = !isModelLoading,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        if (isModelLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Запустить"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    isPath: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )

            Text(
                text = if (isPath) {
                    // Обрезаем длинный путь
                    if (value.length > 40) {
                        "...${value.takeLast(37)}"
                    } else value
                } else value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


expect fun openFilePicker(onFileSelected: (String?) -> Unit)