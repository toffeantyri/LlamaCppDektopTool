package ru.llama.tool.data.data_sources

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.api.models.llama_models.MessageRequest
import ru.llama.tool.domain.models.Message

interface LlamaAiDataSource {

    suspend fun sendMessageToAi(message: MessageRequest): Flow<Message>

}