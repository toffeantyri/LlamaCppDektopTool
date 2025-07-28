package ru.llama.tool.domain.use_cases.messaging_use_case

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.domain.models.AiDialogProperties
import ru.llama.tool.domain.models.Message

interface SendChatRequestUseCase {
    suspend operator fun invoke(message: List<Message>, aiProps: AiDialogProperties): Flow<Message>
} 