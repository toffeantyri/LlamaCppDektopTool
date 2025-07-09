package ru.llama.tool.domain

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.repository.LlamaAiRepository
import ru.llama.tool.domain.models.Message

class SendChatRequestUseCaseImpl(private val llamaAiRepository: LlamaAiRepository) :
    SendChatRequestUseCase {
    override suspend fun invoke(message: Message): Flow<Message> {
        return llamaAiRepository.sendMessage(message)
    }
} 