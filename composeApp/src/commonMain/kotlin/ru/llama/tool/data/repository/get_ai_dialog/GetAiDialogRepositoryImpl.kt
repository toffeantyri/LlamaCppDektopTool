package ru.llama.tool.data.repository.get_ai_dialog

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.llama.tool.data.room.ai_properties_db.AiPropertiesDao
import ru.llama.tool.data.room.ai_properties_db.AiPropertiesEntity
import ru.llama.tool.domain.models.AiDialogProperties

class GetAiDialogRepositoryImpl(private val dao: AiPropertiesDao) : GetAiDialogRepository {

    override suspend fun saveToDb(aiDialogProps: AiDialogProperties) {
        return if (aiDialogProps.id != AiDialogProperties.DEFAULT_ID) {
            dao.insert(
                AiPropertiesEntity(
                    id = aiDialogProps.id,
                    systemPrompt = aiDialogProps.systemPrompt,
                    temperature = aiDialogProps.temperature,
                    topP = aiDialogProps.topP,
                    maxTokens = aiDialogProps.maxTokens
                )
            )
        } else Unit
    }


    override suspend fun getDialogProperties(chatId: Int): Flow<AiDialogProperties> {
        return dao.getDataBy(chatId).map { entity ->
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