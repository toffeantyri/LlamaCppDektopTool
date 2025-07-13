package ru.llama.tool.data.data_sources.messaging_data_source

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.api.ApiService
import ru.llama.tool.data.api.models.messages.MessageRequest
import ru.llama.tool.domain.models.Message

class LlamaAiDataSourceImpl(private val api: ApiService) : LlamaAiDataSource {

    override suspend fun sendMessageToAi(message: MessageRequest): Flow<Message> {
        return api.simpleRequestAi(message)
    }

}