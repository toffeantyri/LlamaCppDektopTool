package ru.llama.tool.data.data_sources

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.api.ApiService
import ru.llama.tool.data.api.models.llama_models.LlamaResponseDto
import ru.llama.tool.data.api.models.llama_models.MessageRequest

class LlamaAiDataSourceImpl(private val api: ApiService) : LlamaAiDataSource {

    override suspend fun sendMessageToAi(message: MessageRequest): Flow<LlamaResponseDto> {
        return api.simpleRequestAi(message)
    }

}