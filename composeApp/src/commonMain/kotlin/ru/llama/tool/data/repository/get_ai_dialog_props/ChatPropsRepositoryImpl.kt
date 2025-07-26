package ru.llama.tool.data.repository.get_ai_dialog_props

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.llama.tool.data.data_sources.local_ai_dialog_props_data_source.AiDialogPropsDataSource
import ru.llama.tool.data.room.ai_properties_db.AiPropertiesEntity
import ru.llama.tool.domain.models.AiDialogProperties

class ChatPropsRepositoryImpl(
    private val propsDataSource: AiDialogPropsDataSource,
) : ChatPropsRepository {

    override suspend fun savePropsToDb(aiDialogProps: AiDialogProperties) {
        return propsDataSource.saveToDb(
            AiPropertiesEntity(
                id = aiDialogProps.id,
                systemPrompt = aiDialogProps.systemPrompt,
                temperature = aiDialogProps.temperature,
                topP = aiDialogProps.topP,
                maxTokens = aiDialogProps.maxTokens
            )
        )
    }


    override suspend fun deletePropsFromDb(chatId: Long) {
        return propsDataSource.deleteFromDb(chatId)
    }


    override suspend fun getDialogProperties(chatId: Long): Flow<AiDialogProperties> {
        return propsDataSource.getDialogProperties(chatId).map { entity ->
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