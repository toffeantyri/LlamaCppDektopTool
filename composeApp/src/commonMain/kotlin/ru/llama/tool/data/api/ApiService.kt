package ru.llama.tool.data.api

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.api.models.llama_props_dto.LlamaProperties
import ru.llama.tool.data.api.models.messages.MessageRequest
import ru.llama.tool.domain.models.Message

interface ApiService {

    suspend fun getModelProperties(): LlamaProperties
    suspend fun simpleRequestAi(message: MessageRequest): Flow<Message>

}