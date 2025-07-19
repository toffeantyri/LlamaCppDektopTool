package ru.llama.tool.presentation.chat_screen.ai_chat_settings

import ru.llama.tool.domain.models.AiProperties

interface AiChatSettingsComponent {


    fun onSaveClicked(newAiProps: AiProperties)

    fun onCancelClicked()

    fun onDismissDialog()

}