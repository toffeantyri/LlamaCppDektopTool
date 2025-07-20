package ru.llama.tool.data.repository.llama_repo

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.domain.models.AiDialogProperties
import ru.llama.tool.domain.models.Message

interface LlamaAiRepository {
    suspend fun sendMessage(messages: List<Message>, aiProps: AiDialogProperties): Flow<Message>
}