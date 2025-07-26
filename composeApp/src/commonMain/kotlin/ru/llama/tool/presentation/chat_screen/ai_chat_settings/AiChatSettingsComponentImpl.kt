package ru.llama.tool.presentation.chat_screen.ai_chat_settings

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import ru.llama.tool.domain.models.AiDialogProperties

class AiChatSettingsComponentImpl(
    private val currentAiDialogProperties: AiDialogProperties,
    private val savePropertiesAction: (newAiChatProp: AiDialogProperties) -> Unit,
    private val onCloseDialog: () -> Unit
) : AiChatSettingsComponent {

    override val currentAiProp: Value<AiDialogProperties> = MutableValue(currentAiDialogProperties)

    override fun onSaveClicked(newAiProps: AiDialogProperties) {
        savePropertiesAction(newAiProps)
        onCloseDialog()
    }

    override fun onCancelClicked() = onCloseDialog()

    override fun onDismissDialog() = onCloseDialog()
}