package ru.llama.tool.data.data_sources.messaging_data_source

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.api.models.messages.MessageRequest
import ru.llama.tool.domain.models.AiDialogProperties
import ru.llama.tool.domain.models.Message

interface LlamaAiDataSource {

    suspend fun sendMessageToAi(
        messages: List<MessageRequest>,
        aiProps: AiDialogProperties
    ): Flow<Message>

}