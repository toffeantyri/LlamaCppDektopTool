package ru.llama.tool.data.repository.get_ai_dialog

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.domain.models.AiDialogProperties

interface GetAiDialogRepository {

    suspend fun saveToDb(aiDialogProps: AiDialogProperties)

    suspend fun getDialogProperties(chatId: Int): Flow<AiDialogProperties>

}