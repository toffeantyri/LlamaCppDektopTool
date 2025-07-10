package ru.llama.tool.data.api

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.api.models.llama_models.LlamaResponseDto
import ru.llama.tool.data.api.models.llama_models.MessageRequest
import ru.llama.tool.domain.models.Message

interface ApiService {

    suspend fun simpleRequestAi(message: MessageRequest): Flow<LlamaResponseDto>

    suspend fun simpleRequestAi2(message: MessageRequest): Flow<Message>

}