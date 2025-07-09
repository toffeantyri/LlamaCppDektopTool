package ru.llama.tool.domain

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.domain.models.Message

interface SendChatRequestUseCase {
    suspend operator fun invoke(message: Message): Flow<Message>
} 