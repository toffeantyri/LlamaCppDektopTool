package ru.llama.tool.data.repository

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.domain.models.Message

interface LlamaAiRepository {
    suspend fun sendMessage(message: Message): Flow<Message>
}