package ru.llama.tool.presentation.chat_screen.ai_chat_settings

import com.arkivanov.decompose.value.Value
import ru.llama.tool.domain.models.AiDialogProperties

interface AiChatSettingsComponent {

    val currentAiProp: Value<AiDialogProperties>

    fun onSaveClicked(newAiProps: AiDialogProperties)

    fun onCancelClicked()

    fun onDismissDialog()

}