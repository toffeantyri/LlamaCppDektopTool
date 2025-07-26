package ru.llama.tool.data.api

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.api.models.llama_props_dto.HealthAiDto
import ru.llama.tool.data.api.models.llama_props_dto.LlamaProperties
import ru.llama.tool.data.api.models.messages.MessageRequest
import ru.llama.tool.domain.models.AiDialogProperties
import ru.llama.tool.domain.models.Message

interface ApiService {

    companion object {
        const val DISABLE_LOG = "DISABLE_LOG"

    }

    suspend fun getModelProperties(): LlamaProperties

    suspend fun simpleRequestAi(
        messages: List<MessageRequest>,
        aiProps: AiDialogProperties
    ): Flow<Message>

    suspend fun sseRequestAi(
        messages: List<MessageRequest>,
        aiProps: AiDialogProperties
    ): Flow<Message>

    suspend fun getHealthAi(): HealthAiDto

}