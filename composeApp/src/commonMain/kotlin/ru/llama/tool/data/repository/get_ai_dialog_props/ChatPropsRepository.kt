package ru.llama.tool.data.repository.get_ai_dialog_props

import kotlinx.coroutines.flow.Flow
import ru.llama.tool.domain.models.AiDialogProperties

interface ChatPropsRepository {

    suspend fun savePropsToDb(aiDialogProps: AiDialogProperties)

    suspend fun deletePropsFromDb(chatId: Int)

    suspend fun getDialogProperties(chatId: Int): Flow<AiDialogProperties>


}