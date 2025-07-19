package ru.llama.tool.presentation.chat_screen.ai_chat_settings

import com.arkivanov.decompose.value.Value
import ru.llama.tool.domain.models.AiProperties

interface AiChatSettingsComponent {

    val currentAiProp: Value<AiProperties>

    fun onSaveClicked(newAiProps: AiProperties)

    fun onCancelClicked()

    fun onDismissDialog()

}