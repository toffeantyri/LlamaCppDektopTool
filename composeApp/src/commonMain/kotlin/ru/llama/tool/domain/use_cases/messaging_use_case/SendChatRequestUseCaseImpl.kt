package ru.llama.tool.domain.use_cases.messaging_use_case

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.repository.ai_repo.LlamaAiRepository
import ru.llama.tool.domain.models.AiDialogProperties
import ru.llama.tool.domain.models.Message

class SendChatRequestUseCaseImpl(private val llamaAiRepository: LlamaAiRepository) :
    SendChatRequestUseCase {
    override suspend fun invoke(
        message: List<Message>,
        aiProps: AiDialogProperties
    ): Flow<Message> {
        return llamaAiRepository.sendMessage(message, aiProps)
    }
} 