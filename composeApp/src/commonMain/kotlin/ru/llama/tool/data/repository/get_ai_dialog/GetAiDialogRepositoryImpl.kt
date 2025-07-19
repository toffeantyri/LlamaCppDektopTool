package ru.llama.tool.data.repository.get_ai_dialog

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.llama.tool.data.room.ai_properties_db.AiPropertiesDao
import ru.llama.tool.domain.models.AiDialogProperties

class GetAiDialogRepositoryImpl(private val dao: AiPropertiesDao) : GetAiDialogRepository {

    override suspend fun getDialogProperties(chatId: Int): Flow<AiDialogProperties> {
        return dao.getDataBy(chatId).map { entity ->
            println("dao entity : $entity")
            if (entity != null) {
                AiDialogProperties(
                    id = chatId,
                    systemPrompt = entity.systemPrompt,
                    temperature = entity.temperature,
                    topP = entity.topP,
                    maxTokens = entity.maxTokens
                )
            } else AiDialogProperties(chatId)
        }
    }
}