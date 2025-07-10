package ru.llama.tool.data.repository

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.api.models.llama_models.MessageRequest
import ru.llama.tool.data.data_sources.LlamaAiDataSource
import ru.llama.tool.domain.models.Message


class LlamaAiRepositoryImpl(private val llamaAiDataSource: LlamaAiDataSource) : LlamaAiRepository {

    override suspend fun sendMessage(message: Message): Flow<Message> {
        val request = llamaAiDataSource.sendMessageToAi(
            MessageRequest(
                content = message.content,
                role = message.sender.name.lowercase()
            )
        )

        return request
    }
} 