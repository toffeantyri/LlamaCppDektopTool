package ru.llama.tool.presentation.chat_screen.ai_chat_settings

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.llama.tool.domain.models.AiProperties

class AiChatSettingsComponentImpl(
    private val currentAiProperties: AiProperties,
    private val onCloseDialog: () -> Unit
) : AiChatSettingsComponent {

    override val currentAiProp: Value<AiProperties> = MutableValue(currentAiProperties)

    override fun onSaveClicked(newAiProps: AiProperties) {
        //todo
    }

    override fun onCancelClicked() = onCloseDialog()

    override fun onDismissDialog() = onCloseDialog()
}