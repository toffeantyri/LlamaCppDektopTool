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
            messages.map { message ->
                MessageRequest(
                    content = message.content,
                    role = message.sender.toString().lowercase(),
                    id = message.id
                )
            }, aiProps
        )
        return request

//        delay(2000)
//        return flow {
//            repeat(20){
//                delay(30)
//                emit(Message(
//                    sender = EnumSender.AI,
//                    id = message.id,
//                    content = "Кто нибудь помогите мне выбраться из локального окружения"
//                ))
//            }
//        }
    }
} 