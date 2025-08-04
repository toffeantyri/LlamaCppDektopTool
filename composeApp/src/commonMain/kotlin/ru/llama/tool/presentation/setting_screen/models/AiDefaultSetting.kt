package ru.llama.tool.presentation.setting_screen.models

import ru.llama.tool.core.EMPTY
import ru.llama.tool.domain.models.AiDialogProperties

data class AiDefaultSetting(
    val defSystemPrompt: String = AiDialogProperties.INITIAL_SYSTEM_PROMPT,
    val baseUrl: String = EMPTY
)
