package ru.llama.tool.data.repository.llama_repo

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.data.api.models.messages.MessageRequest
import ru.llama.tool.data.data_sources.messaging_data_source.LlamaAiDataSource
import ru.llama.tool.domain.models.AiDialogProperties
import ru.llama.tool.domain.models.Message


class LlamaAiRepositoryImpl(private val llamaAiDataSource: LlamaAiDataSource) : LlamaAiRepository {

    override suspend fun sendMessage(
        messages: List<Message>,
        aiProps: AiDialogProperties
    ): Flow<Message> {
        val request = llamaAiDataSource.sendMessageToAi(
            messages.mapIndexed { index, message ->
                val content = if (index != messages.lastIndex) message.content
                else message.content + if (aiProps.thinkingEnabled) "/think" else "/no_think"
                MessageRequest(
                    content = content,
                    role = message.sender.toString().lowercase(),
                    id = message.id
                )
            }, aiProps
        )
        return request
    }
} 